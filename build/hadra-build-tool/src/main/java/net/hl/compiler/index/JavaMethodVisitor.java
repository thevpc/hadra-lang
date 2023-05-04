package net.hl.compiler.index;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class JavaMethodVisitor extends MethodVisitor {
    private final String methodName;
    private final String[] paramTypes;
    private final int access;
    private final String returnType;
    private List<String> parameterNames;
    private boolean staticMethod;
    private HIndexerImpl defaultHLIndexer;
    private String source;
    private String currentFullName;
    private List<HIndexedMethod> indexedMethods;
    private List<HIndexedConstructor> indexedConstructors;


    public JavaMethodVisitor(HIndexerImpl defaultHLIndexer,
                             String source,
                             String currentFullName,
                             String methodName, String[] paramTypes, int access, String returnType
            ,List<HIndexedMethod> indexedMethods,
                             List<HIndexedConstructor> indexedConstructors
    ) {
        super(Opcodes.ASM6);
        this.defaultHLIndexer = defaultHLIndexer;
        this.source = source;
        this.currentFullName = currentFullName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.access = access;
        this.returnType = returnType;
        this.parameterNames = new ArrayList<>();
        this.staticMethod =(access&Opcodes.ACC_STATIC)!=0;
        this.indexedMethods =indexedMethods;
        this.indexedConstructors =indexedConstructors;
    }

    @Override
    public void visitParameter(String name, int access) {
        parameterNames.add(name);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if(parameterNames.size()<paramTypes.length){
            if(index==0){
                if(staticMethod){
                    parameterNames.add(name);
                }
            }else{
                parameterNames.add(name);
            }
        }
    }

    @Override
    public void visitEnd() {
        AnnInfo[] modifiers = JavaClassVisitor.parseModifiers(access);
        if(methodName.equals("<init>")){
            indexedConstructors.add(new HIndexedConstructor(
                    methodName, (String[]) parameterNames.toArray(new String[0]), paramTypes, new String[0],
                    currentFullName, modifiers, source
            ));
        }else {
//            System.out.println("VISIT : "+currentFullName+"."+methodName);
            //if("println".equals(methodName)) {
            if(Arrays.stream(modifiers).noneMatch(x->x.getName().equals("synthetic"))) {
//                if(methodName.contains("$")){
//                    System.out.println(modifiers);
//                }
                indexedMethods.add(new HIndexedMethod(
                        methodName, (String[]) parameterNames.toArray(new String[0]), paramTypes, new String[0],
                        returnType, currentFullName, modifiers, source
                ));
            }else{
//                System.err.println("IGNORED "+(new HLIndexedMethod(
//                        methodName, (String[]) parameterNames.toArray(new String[0]), paramTypes, new String[0],
//                        returnType, currentFullName, modifiers, source
//                )).toString());
            }
            //}
        }
    }
}
