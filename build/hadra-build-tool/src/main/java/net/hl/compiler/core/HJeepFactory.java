package net.hl.compiler.core;

import net.hl.compiler.core.types.HLJTypes;
import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.DefaultJeepFactory;
import net.thevpc.jeep.impl.functions.JFunctionsImpl;
import net.thevpc.jeep.impl.functions.JMultipleInvokableMatchFound;
import net.thevpc.jeep.util.JTypeUtils;
import net.hl.compiler.core.invokables.HCallerInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.thevpc.nuts.NSession;

public class HJeepFactory extends DefaultJeepFactory {

    private NSession session;

    public HJeepFactory(NSession session) {
        this.session = session;
    }

    @Override
    public JFunctions createFunctions(JContext context) {
        JContext p = context.parent();
        return new HLFunctions(context, p);
    }

    @Override
    public JCompilerLog createLog(JContext context) {
        return new HCompilerLog(session);
    }

    @Override
    public JTypes createTypes(JContext context, ClassLoader classLoader) {
        return new HLJTypes(context, classLoader);
    }

    private static class HLFunctions extends JFunctionsImpl {

        public HLFunctions(JContext context, JContext p) {
            super(context, p == null ? null : p.functions());
        }

        @Override
        public JInvokable resolveBestMatch(JCallerInfo callerInfo, JInvokable[] invokables, Function<JTypePattern, JConverter[]> convertersSupplier, JTypePattern[] argTypes, JTypePattern returnType) {
            HCallerInfo hCallerInfo = (callerInfo instanceof HCallerInfo) ? ((HCallerInfo) callerInfo) : null;
            JInvokableCost[] result = resolveMatches(true, invokables, convertersSupplier, argTypes, returnType);
            if (result.length == 0) {
                return null;
            }
            if (result.length > 1) {
                JInvokable[] error = new JInvokable[result.length];
                for (int i = 0; i < error.length; i++) {
                    error[i] = result[i].getInvokable();
                }
                CallerInfoComparator comparator = new CallerInfoComparator(hCallerInfo);
                Arrays.sort(error, comparator);
                List<JInvokable> reorderedResults = new ArrayList<>();
                reorderedResults.add(error[0]);
                for (int i = 1; i < error.length; i++) {
                    if (comparator.compare(error[0], error[i]) == 0) {
                        reorderedResults.add(error[i]);
                    } else {
                        break;
                    }
                }
                if (reorderedResults.size() > 1) {
                    throw new JMultipleInvokableMatchFound(
                            JTypeUtils.sig(invokables[0].getSignature().name(), argTypes, false, true),
                            reorderedResults.toArray(new JInvokable[0]));
                }
                return reorderedResults.get(0);
            }
            return result[0].getInvokable();
        }

        private static class CallerInfoComparator implements Comparator<JInvokable> {

            private final HCallerInfo hCallerInfo;

            public CallerInfoComparator(HCallerInfo hCallerInfo) {
                this.hCallerInfo = hCallerInfo;
            }

            @Override
            public int compare(JInvokable o1, JInvokable o2) {
                String s1 = o1.getSourceName()==null?"":o1.getSourceName();
                String s2 = o2.getSourceName()==null?"":o2.getSourceName();
                if (!s1.equals(s2)) {
                    if (s1.endsWith(".hl") && !s2.endsWith(".hl")) {
                        return -1;
                    }
                    if (s2.endsWith(".hl") && !s1.endsWith(".hl")) {
                        return 1;
                    }
                    if (hCallerInfo != null) {
                        if (s1.equals(hCallerInfo.getSource())) {
                            return -1;
                        }
                        if (s2.equals(hCallerInfo.getSource())) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
        }
    }

}
