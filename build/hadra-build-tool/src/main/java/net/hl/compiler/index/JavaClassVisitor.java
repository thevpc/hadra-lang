package net.hl.compiler.index;

import net.vpc.common.jeep.JShouldNeverHappenException;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.util.JTypeUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

class JavaClassVisitor extends ClassVisitor {
    private final DefaultHLIndexer defaultHLIndexer;
    private String source;
    private String currentFullName;
    private Set<String> visitedMethods=new LinkedHashSet<>();

    public JavaClassVisitor(DefaultHLIndexer defaultHLIndexer, String source) {
        super(Opcodes.ASM8);
        this.defaultHLIndexer = defaultHLIndexer;
        this.source = source;
    }


    private String parseSimpleName(String name){
        int x=name.lastIndexOf('/');
        if(x>=0){
            name=name.substring(x+1);
        }
        return name;
    }
    private String readFullName2List(Reader name){
        int a=0;
        StringBuilder ename=new StringBuilder();
        try {
            int c=name.read();
            if(c==-1){
                return null;
            }
            while(c=='['){
                a++;
                c=name.read();
            }
            switch (c){
                    case 'Z': {
                        ename.append("boolean");
                        break;
                    }
                    case 'C': {
                        ename.append("char");
                        break;
                    }
                    case 'B': {
                        ename.append("byte");
                        break;
                    }
                    case 'S': {
                        ename.append("short");
                        break;
                    }
                    case 'I': {
                        ename.append("int");
                        break;
                    }
                    case 'F': {
                        ename.append("float");
                        break;
                    }
                    case 'J': {
                        ename.append("long");
                        break;
                    }
                    case 'D': {
                        ename.append("double");
                        break;
                    }
                    case 'V': {
                        ename.append("void");
                        break;
                    }
                    case 'L': {
                        boolean loop=true;
                        while(loop){
                            try {
                                c=name.read();
                            } catch (IOException e) {
                                c=-1;
                            }
                            switch (c){
                                case -1:
                                case ';':{
                                    loop=false;
                                    break;
                                }
                                case '/':
                                case '$':
                                    {
                                    ename.append('.');
                                    break;
                                }
                                default:{
                                    ename.append((char)c);
                                }
                            }
                        }
                        break;
                    }
                    default:{
                        throw new JShouldNeverHappenException("unexpected "+((char)c));
                    }
            }
            if(a>0){
                for (int i = 0; i < a; i++) {
                    ename.append("[]");
                }
            }
            return ename.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error");
        }
    }

    private String[] parseFullName2List(String name){
        List<String> all=new ArrayList<>();
        String s;
        StringReader r=new StringReader(name);
        while((s=readFullName2List(r))!=null){
            all.add(s);
        }
        return all.toArray(new String[0]);
    }
    private String parseFullName2(String name){
        StringReader r=new StringReader(name);
        return readFullName2List(r);
    }

    private JavaClassNames parseFullName(String name){
        JavaClassNames cn=new JavaClassNames();
        String s = name.replace('/', '.');
        int x=s.lastIndexOf('$');
        if(x>=0){
            cn.simpleName=s.substring(x+1);
            cn.fullName=s.replace('$', '.');
            int y=s.lastIndexOf('.');
            if(y>=0){
                cn.simpleName2=cn.fullName.substring(y+1);
            }else{
                cn.simpleName2=cn.fullName;
            }
            cn.declaringName= cn.fullName.substring(0, x);
            if(y>=0){
                cn.packageName =cn.fullName.substring(0,y);
            }else{
                cn.packageName ="";
            }
        }else{
            x=s.lastIndexOf('.');
            if(x>=0){
                cn.fullName=s;
                cn.simpleName=s.substring(x+1);
                cn.packageName =s.substring(0,x);
//                    cn.declaringName=null;
            }else{
                cn.packageName ="";
                cn.simpleName=s;
                cn.fullName=s;
            }
            cn.simpleName2=cn.simpleName;
        }
        return cn;
    }

    static String[] parseModifiers(int access){
        List<String> mods=new ArrayList<>();
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            mods.add("abstract");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            mods.add("private");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            mods.add("protected");
        }
        if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            mods.add("annotation");
        }
        if ((access & Opcodes.ACC_BRIDGE) != 0) {
            mods.add("bridge");
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            mods.add("deprecated");
        }
        if ((access & Opcodes.ACC_ENUM) != 0) {
            mods.add("enum");
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            mods.add("final");
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            mods.add("interface");
        }
        if ((access & Opcodes.ACC_TRANSIENT) != 0) {
            mods.add("transient");
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            mods.add("native");
        }
        if ((access & Opcodes.ACC_VOLATILE) != 0) {
            mods.add("volatile");
        }
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
            mods.add("synchronized");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            mods.add("static");
        }
        if ((access & Opcodes.ACC_STRICT) != 0) {
            mods.add("strictfp");
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            mods.add("synthetic");
        }
        if ((access & Opcodes.ACC_MANDATED) != 0) {
            mods.add("mandated");
        }
        if ((access & Opcodes.ACC_MODULE) != 0) {
            mods.add("module");
        }
        if ((access & Opcodes.ACC_SUPER) != 0) {
            mods.add("super");
        }
        if ((access & Opcodes.ACC_TRANSITIVE) != 0) {
            mods.add("transitive");
        }
        if ((access & Opcodes.ACC_OPEN) != 0) {
            mods.add("open");
        }
        if ((access & Opcodes.ACC_RECORD) != 0) {
            mods.add("record");
        }
        if ((access & Opcodes.ACC_STATIC_PHASE) != 0) {
            mods.add("staticphase");
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
        return mods.toArray(new String[0]);//modifiers;
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        List<String> superTypes=new ArrayList<>();
        if(superName!=null){
            superTypes.add(parseFullName(superName).fullName);
        }
        if(interfaces!=null) {
            for (String anInterface : interfaces) {
                superTypes.add(parseFullName(anInterface).fullName);
            }
        }
        JavaClassNames currentFullName = parseFullName(name);
        this.currentFullName = currentFullName.fullName;
        defaultHLIndexer.indexType0(new HLIndexedClass(
                currentFullName.simpleName,
                currentFullName.simpleName2,
                this.currentFullName, currentFullName.declaringName==null?"":currentFullName.declaringName,
                currentFullName.packageName,(String[]) superTypes.toArray(new String[0]),
                new String[0],
                parseModifiers(access),
                source
                ));
    }

//        @Override
//        public void visitInnerClass(String name, String outerName, String innerName, int access) {
//            super.visitInnerClass(name, outerName, innerName, access);
//        }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if(!JTypeUtils.isSynthetic(access)) {
            String[] modifiers = parseModifiers(access);
            defaultHLIndexer.indexField0(new HLIndexedField(
                    currentFullName, parseFullName2(desc), name, new String[0], modifiers,
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
        String returnType=parseFullName2(desc.substring(endPar+1));
        String[] parameterTypes=parseFullName2List(desc.substring(1, endPar));
        String id = JNameSignature.of(name, parameterTypes).toString();
        if(!visitedMethods.contains(id)) {
//            System.out.println("VISIT_00 "+returnType+" "+id+" :: "+access+" "+name+" "+desc+" "+signature+" "+exceptions);
            visitedMethods.add(id);
            return new JavaMethodVisitor(defaultHLIndexer, source, currentFullName, name, parameterTypes, access, returnType);
        }else{
//            System.out.println("IGNORED_00 "+returnType+" "+id+" :: "+access+" "+name+" "+desc+" "+signature+" "+exceptions);
        }
        return null;
    }

}
