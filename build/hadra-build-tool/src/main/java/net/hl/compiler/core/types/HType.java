package net.hl.compiler.core.types;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.types.DefaultJObject;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.impl.types.DefaultJAnnotationInstanceList;
import net.vpc.common.jeep.impl.types.DefaultJConstructor;
import net.vpc.common.jeep.impl.types.DefaultJModifierList;
import net.vpc.common.jeep.impl.types.DefaultJType;

import java.util.Arrays;

public class HType extends DefaultJType {

    public static final JAnnotationInstance PUBLIC = new JPrimitiveModifierAnnotationInstance("public");
    public static final JAnnotationInstance PRIVATE = new JPrimitiveModifierAnnotationInstance("private");
    public static final JAnnotationInstance PROTECTED = new JPrimitiveModifierAnnotationInstance("protected");
    public static final JAnnotationInstance STATIC = new JPrimitiveModifierAnnotationInstance("static");
    public static final JAnnotationInstance INTERFACE = new JPrimitiveModifierAnnotationInstance("interface");
    public static final JAnnotationInstance ANNOTATION = new JPrimitiveModifierAnnotationInstance("annotation");
    public static final JAnnotationInstance ENUM = new JPrimitiveModifierAnnotationInstance("enum");
    public static final JAnnotationInstance EXCEPTION = new JPrimitiveModifierAnnotationInstance("exception");

    public HType(String name, JTypes types) {
        super(name, types);
    }

    protected JConstructor createDefaultConstructor() {
        DefaultJConstructor constructor = (DefaultJConstructor) createConstructor(JSignature.of(getName(), new JType[0]),
                new String[0],
                new JInvoke() {
                    @Override
                    public Object invoke(JInvokeContext context) {
                        return new DefaultJObject(HType.this);
                    }
                }, new JModifier[]{
                        DefaultJModifierList.PUBLIC
                }, new JAnnotationInstance[]{
                        JPrimitiveModifierAnnotationInstance.PUBLIC
                }
        );
        constructor.setSourceName("<generated>");
        return constructor;
    }

    @Override
    public JConstructor createConstructor(JSignature signature, String[] argNames, JInvoke handler, JModifier[] modifiers, JAnnotationInstance[] annotations) {
        signature = signature.setName(getName());
        HConstructor m = new HConstructor();
        m.setDeclaringType(this);
        m.setTypes(getTypes());
        ((DefaultJModifierList) m.getModifiers()).addAll(modifiers);
        m.setArgNames(Arrays.copyOf(argNames, argNames.length));
        ((DefaultJAnnotationInstanceList) m.getAnnotations()).addAll(annotations);
        m.setHandler(handler);
        m.setGenericSignature(signature);
        return m;
    }


    @Override
    public boolean isInterface() {
        return getAnnotations().contains(INTERFACE);
    }

}
