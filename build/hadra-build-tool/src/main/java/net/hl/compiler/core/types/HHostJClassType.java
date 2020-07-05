package net.hl.compiler.core.types;

import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.impl.types.DefaultJAnnotationInstanceList;
import net.vpc.common.jeep.impl.types.host.HostJClassType;
import net.vpc.common.jeep.util.JeepUtils;

import java.lang.reflect.Modifier;

public class HHostJClassType extends HostJClassType {

    public HHostJClassType(Class hostType, JTypes types) {
        super(hostType, types);
    }

    protected void applyModifiers(int modifiers) {
        DefaultJAnnotationInstanceList modifiersList = (DefaultJAnnotationInstanceList) getAnnotations();
        addJavaModifiers(modifiers, modifiersList);
    }

    public void addJavaModifiers(int modifiers, DefaultJAnnotationInstanceList modifiersList) {
        int[] modifierRef = new int[]{modifiers};
        while (modifierRef[0] != 0) {
            if (JeepUtils.consumeModifier(modifierRef, Modifier.ABSTRACT)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.ABSTRACT);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.FINAL)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.FINAL);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.INTERFACE)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.INTERFACE);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.NATIVE)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.NATIVE);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PRIVATE)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.PRIVATE);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PROTECTED)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.PROTECTED);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PUBLIC)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.PUBLIC);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.STATIC)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.STATIC);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.SYNCHRONIZED)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.SYNCHRONIZED);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.VOLATILE)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.VOLATILE);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.TRANSIENT)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.TRANSIENT);
            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.STRICT)) {
                modifiersList.add(JPrimitiveModifierAnnotationInstance.STRICTFP);
            } else {
                ///
            }
        }
        if(hostType().isInterface() && !modifiersList.contains(JPrimitiveModifierAnnotationInstance.INTERFACE)){
            modifiersList.add(JPrimitiveModifierAnnotationInstance.INTERFACE);
        }
        if(hostType().isEnum() && !modifiersList.contains(JPrimitiveModifierAnnotationInstance.ENUM)){
            modifiersList.add(JPrimitiveModifierAnnotationInstance.ENUM);
        }
        if(hostType().isAnnotation() && !modifiersList.contains(JPrimitiveModifierAnnotationInstance.ANNOTATION)){
            modifiersList.add(JPrimitiveModifierAnnotationInstance.ANNOTATION);
        }
        if(Throwable.class.isAssignableFrom(hostType()) && !modifiersList.contains(JPrimitiveModifierAnnotationInstance.EXCEPTION)){
            modifiersList.add(JPrimitiveModifierAnnotationInstance.EXCEPTION);
        }
    }
}
