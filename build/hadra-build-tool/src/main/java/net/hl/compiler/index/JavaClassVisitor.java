package net.hl.compiler.index;

import net.thevpc.jeep.impl.functions.JNameSignature;
import net.thevpc.jeep.util.JTypeUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;

import java.util.*;

class JavaClassVisitor extends ClassVisitor {
    private final HIndexerImpl defaultHLIndexer;
    private String source;
    private JavaClassNames currentFullName;
    private List<String> superTypes;
    private int classAccess;
    private Set<String> visitedMethods = new LinkedHashSet<>();
    private List<HIndexedMethod> indexedMethods = new ArrayList<>();
    private List<HIndexedField> indexedFields = new ArrayList<>();
    private List<HIndexedConstructor> indexedConstructors = new ArrayList<>();
    private List<AnnInfo> annotations = new ArrayList<>();

    public JavaClassVisitor(HIndexerImpl defaultHLIndexer, String source) {
        super(Opcodes.ASM9);
        this.defaultHLIndexer = defaultHLIndexer;
        this.source = source;
    }


    static AnnInfo[] parseModifiers(int access) {
        List<AnnInfo> mods = new ArrayList<>();
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            mods.add(new AnnInfo("abstract"));
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            mods.add(new AnnInfo("private"));
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            mods.add(new AnnInfo("protected"));
        }
        if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            mods.add(new AnnInfo("annotation"));
        }
        if ((access & Opcodes.ACC_BRIDGE) != 0) {
            mods.add(new AnnInfo("bridge"));
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            mods.add(new AnnInfo("deprecated"));
        }
        if ((access & Opcodes.ACC_ENUM) != 0) {
            mods.add(new AnnInfo("enum"));
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            mods.add(new AnnInfo("final"));
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            mods.add(new AnnInfo("interface"));
        }
        if ((access & Opcodes.ACC_TRANSIENT) != 0) {
            mods.add(new AnnInfo("transient"));
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            mods.add(new AnnInfo("native"));
        }
        if ((access & Opcodes.ACC_VOLATILE) != 0) {
            mods.add(new AnnInfo("volatile"));
        }
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
            mods.add(new AnnInfo("synchronized"));
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            mods.add(new AnnInfo("static"));
        }
        if ((access & Opcodes.ACC_STRICT) != 0) {
            mods.add(new AnnInfo("strictfp"));
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            mods.add(new AnnInfo("synthetic"));
        }
        if ((access & Opcodes.ACC_MANDATED) != 0) {
            mods.add(new AnnInfo("mandated"));
        }
        if ((access & Opcodes.ACC_MODULE) != 0) {
            mods.add(new AnnInfo("module"));
        }
        if ((access & Opcodes.ACC_SUPER) != 0) {
            mods.add(new AnnInfo("super"));
        }
        if ((access & Opcodes.ACC_TRANSITIVE) != 0) {
            mods.add(new AnnInfo("transitive"));
        }
        if ((access & Opcodes.ACC_OPEN) != 0) {
            mods.add(new AnnInfo("open"));
        }
        if ((access & Opcodes.ACC_RECORD) != 0) {
            mods.add(new AnnInfo("record"));
        }
        if ((access & Opcodes.ACC_STATIC_PHASE) != 0) {
            mods.add(new AnnInfo("staticphase"));
        }
//        if ((access & Opcodes.ACC_PUBLIC) != 0) {
//            modifiers |= Modifier.PUBLIC;
//        }
//        if ((access & Opcodes.ACC_PROTECTED) != 0) {
//            modifiers |= Modifier.PROTECTED;
//        }
//        if ((access & Opcodes.ACC_PRIVATE) != 0) {
//            modifiers |= Modifier.PRIVATE;
//        }
//        if ((access & Opcodes.ACC_PRIVATE) != 0) {
//            modifiers |= Modifier.PRIVATE;
//        }
        return mods.toArray(new AnnInfo[0]);//modifiers;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        superTypes = new ArrayList<>();
        if (signature != null) {
            new SignatureReader(signature).accept(new SignatureVisitorProcessor(
                    x->{
                        // TODO FIX ME
                    }
            ));
        }
        if (superName != null) {
            superTypes.add(JavaClassNames.parseFullNameFromPath(superName).fullName);
        }
        if (interfaces != null) {
            for (String anInterface : interfaces) {
                superTypes.add(JavaClassNames.parseFullNameFromPath(anInterface).fullName);
            }
        }
        currentFullName = JavaClassNames.parseFullNameFromPath(name);

    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new MyAnnotationVisitor(
                JavaClassNames.parseFullNameFromEncoded(descriptor).fullName
                , visible, x -> annotations.add(x));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }
    //        @Override
//        public void visitInnerClass(String name, String outerName, String innerName, int access) {
//            super.visitInnerClass(name, outerName, innerName, access);
//        }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!JTypeUtils.isSynthetic(access)) {
            AnnInfo[] modifiers = parseModifiers(access);
            indexedFields.add(new HIndexedField(
                    this.currentFullName.fullName, JavaClassNames.parseInternalForm(desc), name, new String[0], modifiers,
                    source
            ));
        }
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//            index(source, "Method", new HLIndexedMethod(
//                    parseFullName(name), parseFullName(signature), name,new String[0],parseModifiers(access),
//                    source
//            ));
        int endPar = desc.indexOf(')');
        String returnType = JavaClassNames.parseInternalForm(desc.substring(endPar + 1));
        String[] parameterTypes = JavaClassNames.parseInternalFormList(desc.substring(1, endPar));
        String id = JNameSignature.of(name, parameterTypes).toString();
        if (!visitedMethods.contains(id)) {
//            System.out.println("VISIT_00 "+returnType+" "+id+" :: "+access+" "+name+" "+desc+" "+signature+" "+exceptions);
            visitedMethods.add(id);
            return new JavaMethodVisitor(defaultHLIndexer, source, this.currentFullName.fullName,
                    name, parameterTypes, access, returnType, indexedMethods, indexedConstructors);
        } else {
//            System.out.println("IGNORED_00 "+returnType+" "+id+" :: "+access+" "+name+" "+desc+" "+signature+" "+exceptions);
        }
        return null;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        List<AnnInfo> annotations1 = new ArrayList<>(Arrays.asList(parseModifiers(classAccess)));
        annotations1.addAll(annotations);
        defaultHLIndexer.indexType0(
                new HIndexedClass(
                currentFullName.simpleName,
                currentFullName.simpleName2,
                this.currentFullName.fullName, currentFullName.declaringName == null ? "" : currentFullName.declaringName,
                currentFullName.packageName, (String[]) superTypes.toArray(new String[0]),
                new String[0],
                annotations1.toArray(new AnnInfo[0]),
                source
        ));
        for (HIndexedField indexedField : indexedFields) {
            defaultHLIndexer.indexField0(indexedField);
        }
        for (HIndexedConstructor indexConstructor : indexedConstructors) {
            defaultHLIndexer.indexConstructor0(indexConstructor);
        }
        for (HIndexedMethod indexedMethod : indexedMethods) {
            defaultHLIndexer.indexMethod0(indexedMethod);
        }
    }

}
