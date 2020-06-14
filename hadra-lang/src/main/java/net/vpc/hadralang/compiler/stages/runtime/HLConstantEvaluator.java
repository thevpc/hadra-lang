package net.vpc.hadralang.compiler.stages.runtime;

import net.vpc.common.jeep.JMethod;
import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.types.host.HostJArray;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.stdlib.*;
import net.vpc.hadralang.stdlib.ext.HJavaDefaultOperators;
import net.vpc.hadralang.stdlib.ext.RangeExtensions;

import java.util.*;
import java.util.stream.*;

public class HLConstantEvaluator extends HLEvaluator {
    public static final HLConstantEvaluator INSTANCE = new HLConstantEvaluator();

    @Override
    public Object evaluate(JNode node, JInvokeContext context) {
        switch (((HNode)node).id()) {
            case H_DECLARE_INVOKABLE:
            case H_DECLARE_IDENTIFIER:
            case H_DECLARE_TYPE:
            case H_WHILE:
            case H_FOR:
            case H_ASSIGN:
            case H_ARRAY_CALL:
            case H_OBJECT_NEW:
            case H_THIS:
            case H_META_IMPORT_PACKAGE:
            case H_LAMBDA_EXPR:
            case H_BREAK:
            case H_CONTINUE:
            case H_RETURN:
            case H_ARRAY_NEW:{
                throw new JParseException("unable to evaluate constant value : "+node);
            }

            case H_INVOKE_METHOD: {
                HNMethodCall a = (HNMethodCall) node;
                JInvokable m = a.getMethod();
                if(m instanceof JMethod && ((JMethod)m).isStatic()
                    && (
                            ((JMethod)m).declaringType().name().equals(HJavaDefaultOperators.class.getName())
                        || ((JMethod)m).declaringType().name().equals(RangeExtensions.class.getName()))
                ){
                    //fallthrough
                } else {
                    throw new JParseException("unable to evaluate constant value : "+node);
                }
                break;
            }
//            case H_FIELD: {
//                HNField a = (HNField) node;
//                JField f = a.getField();
//                if (f.isStatic() && f.isFinal()) {
//                    //fallthrough
//                } else {
//                    throw new JParseException("unable to evaluate constant value : "+node);
//                }
//                break;
//            }
            case H_INVOKER_CALL: {
                if(node instanceof HNInvokerCall){
                    JInvokablePrefilled impl = ((HNInvokerCall) node).impl();
                    JInvokable ii = impl.getInvokable();
                    if(ii instanceof JMethod){
                        JMethod m=(JMethod) ii;
                        if (m.isStatic()) {
                            if(
                                    m.declaringType().name().equals(HJavaDefaultOperators.class.getName())
                                            ||
                                            m.declaringType().name().equals(RangeExtensions.class.getName())){
                                //should i limit methods to call?
                                //fallthrough
                            }else{
                                throw new JParseException("unable to evaluate constant value : "+node);
                            }
                        } else {
                            throw new JParseException("unable to evaluate constant value : "+node);
                        }
                    }else{
                        throw new JParseException("unable to evaluate constant value : "+node);
                    }
                }else {
                    throw new JParseException("unable to evaluate constant value : " + node);
                }
                break;
            }
        }
        return super.evaluate(node, context);
    }

    private Iterator resolveIteratorOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof Iterator) {
            return (Iterator) a;
        }
        if (a instanceof Iterable) {
            return ((Iterable) a).iterator();
        }
        Stream q = resolveStreamOrNull(a);
        if (q != null) {
            return q.iterator();
        }
        return null;
    }

    private Iterable resolveIterableOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof Iterable) {
            return (Iterable) a;
        }
        if (a instanceof boolean[]) {
            return () -> {
                boolean[] x = (boolean[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof byte[]) {
            return () -> {
                byte[] x = (byte[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof short[]) {
            return () -> {
                short[] x = (short[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof char[]) {
            return () -> {
                char[] x = (char[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof CharSequence) {
            return () -> ((CharSequence) a).chars().mapToObj(i -> (char) i).iterator();
        } else if (a instanceof int[]) {
            return () -> Arrays.stream((int[]) a).boxed().iterator();
        } else if (a instanceof long[]) {
            return () -> Arrays.stream((long[]) a).boxed().iterator();
        } else if (a instanceof float[]) {
            return () -> {
                float[] x = (float[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof double[]) {
            return () -> Arrays.stream((double[]) a).boxed().iterator();
        } else if (a.getClass().isArray()) {
            return () -> Arrays.stream((Object[]) a).iterator();
        } else if (a instanceof IntRange) {
            return () -> ((IntRange) a).stream().iterator();
        }
        return null;
    }

    private Stream resolveStreamOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof boolean[]) {
            boolean[] x = (boolean[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof byte[]) {
            byte[] x = (byte[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof short[]) {
            short[] x = (short[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof char[]) {
            char[] x = (char[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof CharSequence) {
            return ((CharSequence) a).chars().mapToObj(i -> (char) i);
        } else if (a instanceof int[]) {
            return Arrays.stream((int[]) a).boxed();
        } else if (a instanceof long[]) {
            return Arrays.stream((long[]) a).boxed();
        } else if (a instanceof float[]) {
            float[] x = (float[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof double[]) {
            return Arrays.stream((double[]) a).boxed();
        } else if (a.getClass().isArray()) {
            return Arrays.stream((Object[]) a);
        } else if (a instanceof Iterable) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize((((Iterable) a).iterator()), Spliterator.ORDERED),
                    false);
        } else if (a instanceof Iterator) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize((Iterator) a, Spliterator.ORDERED),
                    false);
        } else if (a instanceof Stream) {
            return (Stream) a;
        } else if (a instanceof IntStream) {
            return ((IntStream) a).boxed();
        } else if (a instanceof LongStream) {
            return ((LongStream) a).boxed();
        } else if (a instanceof DoubleStream) {
            return ((DoubleStream) a).boxed();
        } else if (a instanceof IntRange) {
            return ((IntRange) a).stream().boxed();
        }
        return null;
    }

    private JArray evaluateArray(JNode expr, JInvokeContext jContext) {
        Object a = evaluate(expr, jContext);
        JTypes types = jContext.context().types();
        if (a == null) {
            return null;
        }
        if (a.getClass().isArray()) {
            return new HostJArray(a, types.forName(a.getClass().getName()));
        }
        if (a instanceof Iterable) {
            JType componentType = null;
            List<Object> all = new ArrayList<>();
            for (Object o : ((Iterable) a)) {
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
                all.add(o);
            }
            return new HostJArray(new Object[0], componentType);
        }
        if (a instanceof Iterator) {
            List<Object> all = new ArrayList<>();
            Iterator i = (Iterator) a;
            JType componentType = null;
            while (i.hasNext()) {
                Object o = i.next();
                all.add(i);
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
            }
            return new HostJArray(new Object[0], componentType);
        }
        if (a instanceof Stream) {
            Object[] b = ((Stream) a).toArray();
            JType componentType = null;
            for (Object o : b) {
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
            }
            return new HostJArray(b, componentType);
        }
        throw new JEvalException("Expected an iterable type but found " + a.getClass().getName());
    }

    private Iterable iter(Object val, InitValueConstraint c) {
        Iterable i = resolveIterableOrNull(val);
        if (i != null) {
            return i;
        }
        if (val != null) {
            throw new IllegalArgumentException("Cannot resolve iterator from " + val.getClass().getSimpleName());
        }
        return null;
    }

    private boolean evaluateBoolean(JNode node, JInvokeContext context) {
        Object v = evaluate(node, context);
        if (v instanceof Boolean) {
            return ((Boolean) v);
        }
        return false;
    }

    public interface InvokeRunnable {
        void invoke(JInvokeContext context);
    }

    private static class Loop {
        private String label;
        private Iterable iter;
        private String varName;
        private JType type;
        private JNode bodyNode;
        private JNode filter;
        private JNode[] incs;
        private Loop bodyLoop;

        public Loop(String label,Iterable iter, String varName, JType type, JNode bodyNode, JNode filter, JNode[] incs) {
            this.label = label;
            this.iter = iter;
            this.varName = varName;
            this.type = type;
            this.bodyNode = bodyNode;
            this.filter = filter;
            this.incs = incs;
        }

        public Loop(String label,Iterable iter, String varName, JType type, Loop bodyLoop, JNode filter, JNode[] incs) {
            this.label = label;
            this.iter = iter;
            this.varName = varName;
            this.type = type;
            this.bodyLoop = bodyLoop;
            this.filter = filter;
            this.incs = incs;
        }

        public Object run(JInvokeContext context) {
            JInvokeContext cc = context.builder().context(context.context().newContext()).build();
            Iterator iter = this.iter.iterator();
            cc.context().vars().declareVar(varName, type, null);
            runInit(cc);
            Object ret=null;
            while (iter.hasNext()) {
                Object item = iter.next();
                cc.context().vars().setValue(varName, item);
                runItem(context, item);
                if (filter != null) {
                    Boolean b = (Boolean) cc.evaluate(filter);
                    if (!b.booleanValue()) {
                        continue;
                    }
                }
                try {
                    if (bodyNode != null) {
                        ret = cc.evaluate(bodyNode);
                    } else if (bodyLoop != null) {
                        ret = bodyLoop.run(cc);
                    }
                }catch (InternalBreak b){
                    if(b.getLabel()==null || b.getLabel().equals(label)){
                        break;
                    }else{
                        throw b;
                    }
                }catch (InternalContinue b){
                    if(b.getLabel()==null || b.getLabel().equals(label)){
                        //continue;
                    }else{
                        throw b;
                    }
                }
                for (JNode inc : incs) {
                    ret= cc.evaluate(inc);
                }
            }
            return ret;
        }

        public void runInit(JInvokeContext context) {

        }

        public void runItem(JInvokeContext context, Object item) {

        }
    }
    public static class InternalBreak extends RuntimeException{
        private String label;

        public InternalBreak(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
    public static class InternalContinue extends RuntimeException{
        private String label;

        public InternalContinue(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
    public static class InternalReturn extends RuntimeException{
        private Object value;

        public InternalReturn(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}