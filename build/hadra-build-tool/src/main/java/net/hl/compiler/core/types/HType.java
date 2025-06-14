package net.hl.compiler.core.types;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.DefaultJObject;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.*;
import net.thevpc.jeep.util.JTypeUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class HType extends DefaultJType {

    public static final DefaultJAnnotationInstance STRICTFP = new DefaultJAnnotationInstance("strictfp");
    public static final DefaultJAnnotationInstance TRANSIENT = new DefaultJAnnotationInstance("transient");
    public static final DefaultJAnnotationInstance VOLATILE = new DefaultJAnnotationInstance("volatile");
    public static final DefaultJAnnotationInstance SYNCHRONIZED = new DefaultJAnnotationInstance("synchronized");
    public static final DefaultJAnnotationInstance STATIC = new DefaultJAnnotationInstance("static");
    public static final DefaultJAnnotationInstance PUBLIC = new DefaultJAnnotationInstance("public");
    public static final DefaultJAnnotationInstance PROTECTED = new DefaultJAnnotationInstance("protected");
    public static final DefaultJAnnotationInstance PRIVATE = new DefaultJAnnotationInstance("private");
    public static final DefaultJAnnotationInstance NATIVE = new DefaultJAnnotationInstance("native");
    public static final DefaultJAnnotationInstance INTERFACE = new DefaultJAnnotationInstance("interface");
    public static final DefaultJAnnotationInstance FINAL = new DefaultJAnnotationInstance("final");
    public static final DefaultJAnnotationInstance ABSTRACT = new DefaultJAnnotationInstance("abstract");
    public static final DefaultJAnnotationInstance ENUM = new DefaultJAnnotationInstance("enum");
    public static final DefaultJAnnotationInstance ANNOTATION = new DefaultJAnnotationInstance("annotation");
    public static final DefaultJAnnotationInstance EXCEPTION = new DefaultJAnnotationInstance("exception");
    public static final DefaultJAnnotationInstance CONST = new DefaultJAnnotationInstance("const");
    public static final DefaultJAnnotationInstance SPECIAL_DEFAULT_CONSTRUCTOR = new DefaultJAnnotationInstance("default-constructor");


    public HType(String name, JTypeKind kind,JTypes types) {
        super(name, kind, types);
        switch (kind.getValue()){
            case JTypeKind.Ids.ANNOTATION:{
                ((DefaultJAnnotationInstanceList)getAnnotations()).add(ANNOTATION);
                break;
            }
            case JTypeKind.Ids.ENUM:{
                ((DefaultJAnnotationInstanceList)getAnnotations()).add(ENUM);
                break;
            }
            case JTypeKind.Ids.EXCEPTION:{
                ((DefaultJAnnotationInstanceList)getAnnotations()).add(EXCEPTION);
                break;
            }
            case JTypeKind.Ids.INTERFACE:{
                ((DefaultJAnnotationInstanceList)getAnnotations()).add(INTERFACE);
                break;
            }
        }
        switch (getKind().getValue()){
            case JTypeKind.Ids.ENUM:{
                setSuperType(getTypes().forName(Enum.class.getName()));
                break;
            }
            case JTypeKind.Ids.CLASS:{
                setSuperType(getTypes().forName(Object.class.getName()));
                break;
            }
            case JTypeKind.Ids.EXCEPTION:{
                setSuperType(getTypes().forName(RuntimeException.class.getName()));
                break;
            }
            case JTypeKind.Ids.ANNOTATION:{
                addInterface(getTypes().forName(Annotation.class.getName()));
                break;
            }
        }
    }

    public boolean isInterface() {
        return getAnnotations().contains(INTERFACE);
    }

    @Override
    public JType getSuperType() {
        JType t = super.getSuperType();
        if(t!=null){
            return t;
        }
        switch (getKind().getValue()){
            case JTypeKind.Ids.CLASS:{
                return JTypeUtils.forObject(getTypes());
            }
            case JTypeKind.Ids.ENUM:{
                return getTypes().forName(Enum.class.getName());
            }
        }
        return t;
    }

    protected JConstructor createDefaultConstructor() {
        DefaultJConstructor constructor = (DefaultJConstructor) createConstructor(JSignature.of(getName(), new JType[0]),
                new String[0], (JInvokeContext context) -> new DefaultJObject(HType.this), new JModifier[]{
                        DefaultJModifierList.PUBLIC
                }, new JAnnotationInstance[]{
                        HType.PUBLIC
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
        ((DefaultJModifierList) m.getModifiers()).addAll(modifiers);
        m.setArgNames(Arrays.copyOf(argNames, argNames.length));
        ((DefaultJAnnotationInstanceList) m.getAnnotations()).addAll(annotations);
        m.setHandler(handler);
        m.setGenericSignature(signature);
        return m;
    }

}
