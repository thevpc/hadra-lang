package net.hl.compiler.index;

import net.vpc.common.jeep.util.JTypeUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

class JavaMethodVisitor extends MethodVisitor {
    private final String methodName;
    private final String[] paramTypes;
    private final int access;
    private final String returnType;
    private List<String> parameterNames;
    private boolean staticMethod;
    private DefaultHLIndexer defaultHLIndexer;
    private String source;
    private String currentFullName;


    public JavaMethodVisitor(DefaultHLIndexer defaultHLIndexer,
                             String source,
                             String currentFullName,
                             String methodName, String[] paramTypes, int access, String returnType) {
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
        int modifiers = JavaClassVisitor.parseModifiers(access);
        if(methodName.equals("<init>")){
            defaultHLIndexer.indexConstructor0(new HLIndexedConstructor(
                    methodName, (String[]) parameterNames.toArray(new String[0]), paramTypes, new String[0],
                    currentFullName, modifiers, source
            ));
        }else {
//            System.out.println("VISIT : "+currentFullName+"."+methodName);
            //if("println".equals(methodName)) {
            if(!JTypeUtils.isSynthetic(modifiers)) {
//                if(methodName.contains("$")){
//                    System.out.println(modifiers);
//                }
                defaultHLIndexer.indexMethod0(new HLIndexedMethod(
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
