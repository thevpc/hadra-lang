package net.hl.compiler.core;

import net.thevpc.jeep.core.JTokenState;
import net.thevpc.jeep.impl.JEnumDefinition;
import net.thevpc.jeep.impl.JEnumTypeRegistry;

public class HTokenState extends JTokenState {

    public static final int STATE_DEFAULT = 1;
    public static final int STATE_STRING_INTERP_TEXT = 2;
    public static final int STATE_STRING_INTERP_VAR = 3;
    public static final int STATE_STRING_INTERP_CODE = 4;

    public static final JEnumDefinition<HTokenState> _ET = JEnumTypeRegistry.INSTANCE.register(HTokenState.class)
            .addConstIntFields(HTokenState.class, f -> f.getName().startsWith("STATE_"));

    public static class Enums {

        public static final HTokenState STATE_DEFAULT = _ET.valueOf("STATE_DEFAULT");
        public static final HTokenState STATE_STRING_INTERP_TEXT = _ET.valueOf("STATE_STRING_INTERP_TEXT");
        public static final HTokenState STATE_STRING_INTERP_VAR = _ET.valueOf("STATE_STRING_INTERP_VAR");
        public static final HTokenState STATE_STRING_INTERP_CODE = _ET.valueOf("STATE_STRING_INTERP_CODE");
    }

    private HTokenState(JEnumDefinition type, String name, int value) {
        super(type, name, value);
    }
}
