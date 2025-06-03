//package net.hl.compiler.core.types;
//
//import net.thevpc.jeep.JTypes;
//import net.thevpc.jeep.impl.types.DefaultJAnnotationInstanceList;
//import net.thevpc.jeep.impl.types.host.HostJClassType;
//import net.thevpc.jeep.util.JeepUtils;
//
//import java.lang.reflect.Modifier;
//
//public class HHostJClassType extends HostJClassType {
//
//    public HHostJClassType(Class hostType, JTypes types) {
//        super(hostType, types);
//    }
//
//    protected void applyModifiers(int modifiers) {
//        DefaultJAnnotationInstanceList modifiersList = (DefaultJAnnotationInstanceList) getAnnotations();
//        addJavaModifiers(modifiers, modifiersList);
//    }
//
//    public void addJavaModifiers(int modifiers, DefaultJAnnotationInstanceList modifiersList) {
//        int[] modifierRef = new int[]{modifiers};
//        while (modifierRef[0] != 0) {
//            if (JeepUtils.consumeModifier(modifierRef, Modifier.ABSTRACT)) {
//                modifiersList.add(HType.ABSTRACT);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.FINAL)) {
//                modifiersList.add(HType.FINAL);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.INTERFACE)) {
//                modifiersList.add(HType.INTERFACE);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.NATIVE)) {
//                modifiersList.add(HType.NATIVE);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PRIVATE)) {
//                modifiersList.add(HType.PRIVATE);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PROTECTED)) {
//                modifiersList.add(HType.PROTECTED);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.PUBLIC)) {
//                modifiersList.add(HType.PUBLIC);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.STATIC)) {
//                modifiersList.add(HType.STATIC);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.SYNCHRONIZED)) {
//                modifiersList.add(HType.SYNCHRONIZED);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.VOLATILE)) {
//                modifiersList.add(HType.VOLATILE);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.TRANSIENT)) {
//                modifiersList.add(HType.TRANSIENT);
//            } else if (JeepUtils.consumeModifier(modifierRef, Modifier.STRICT)) {
//                modifiersList.add(HType.STRICTFP);
//            } else {
//                ///
//            }
//        }
//        if(getHostType().isInterface() && !modifiersList.contains(HType.INTERFACE)){
//            modifiersList.add(HType.INTERFACE);
//        }
//        if(getHostType().isEnum() && !modifiersList.contains(HType.ENUM)){
//            modifiersList.add(HType.ENUM);
//        }
//        if(getHostType().isAnnotation() && !modifiersList.contains(HType.ANNOTATION)){
//            modifiersList.add(HType.ANNOTATION);
//        }
//        if(Throwable.class.isAssignableFrom(getHostType()) && !modifiersList.contains(HType.EXCEPTION)){
//            modifiersList.add(HType.EXCEPTION);
//        }
//    }
//}
