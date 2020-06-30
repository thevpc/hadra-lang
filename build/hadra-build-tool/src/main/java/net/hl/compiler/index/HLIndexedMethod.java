package net.hl.compiler.index;

import net.vpc.common.jeep.JIndexDocument;
import net.vpc.common.jeep.JMethod;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.hl.compiler.ast.HNDeclareIdentifier;
import net.hl.compiler.ast.HNDeclareInvokable;
import net.hl.compiler.utils.HUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HLIndexedMethod implements HLIndexedElement{
    private String name;
    private String[] parameterNames;
    private String[] parameterTypes;
    private String[] imports;
    private String returnType;
    private String declaringType;
    private JNameSignature signature;
    private int modifiers;
    private String source;
    private String id;
    public HLIndexedMethod(HNDeclareInvokable m) {
        name=m.getNameToken().sval;
        if(m.getReturnTypeName()==null){
            returnType="";
        }else{
            returnType=m.getReturnTypeName().getTypename().fullName();
        }
        declaringType=m.getDeclaringType()==null?"":m.getDeclaringType().getFullName();
        modifiers=m.getModifiers();
        List<String> pn=new ArrayList<>();
        List<String> pt=new ArrayList<>();
        for (HNDeclareIdentifier argument : m.getArguments()) {
            pn.add(argument.getIdentifierName());
            pt.add(argument.getIdentifierTypeNode().getTypename().fullName());
        }
        parameterNames=pn.toArray(new String[0]);
        parameterTypes=pt.toArray(new String[0]);
        imports= HUtils.getImports(m);
        source=HUtils.getSourceName(m);
        this.signature=JNameSignature.of(name,parameterTypes);
        id=declaringType==null?signature.toString():(declaringType+"."+signature.toString());
    }

    public HLIndexedMethod(JMethod m,String source) {
        name=m.getName();
        if(m.getReturnType()==null){
            returnType="";
        }else{
            returnType=m.getReturnType().getName();
        }
        declaringType=m.getDeclaringType()==null?"":m.getDeclaringType().getName();
        modifiers=m.getModifiers();
        parameterNames=m.getArgNames();
        parameterTypes=Arrays.stream(m.getArgTypes()).map(JType::getName).toArray(String[]::new);
        imports= new String[0];
        this.source=source;
        this.signature=JNameSignature.of(name,parameterTypes);
        id=declaringType==null?signature.toString():(declaringType+"."+signature.toString());
    }

    public HLIndexedMethod(String name, String[] parameterNames, String[] parameterTypes, String[] imports, String returnType, String declaringType, int modifiers, String source) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
        this.imports = imports;
        this.returnType = returnType;
        this.declaringType = declaringType;
        this.modifiers = modifiers;
        this.source = source;
        this.signature=JNameSignature.of(name,parameterTypes);
        id=declaringType==null?signature.toString():(declaringType+"."+signature.toString());
    }

    @Override
    public String toString() {
        return "HLIndexedMethod{" +
                "name='" + name + '\'' +
                ", parameterNames=" + Arrays.toString(parameterNames) +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", imports=" + Arrays.toString(imports) +
                ", returnType='" + returnType + '\'' +
                ", declaringType='" + declaringType + '\'' +
                ", signature=" + signature +
                ", modifiers=" + modifiers +
                ", source='" + source + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public String[] getImports() {
        return imports;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getDeclaringType() {
        return declaringType;
    }

    public JNameSignature getSignature() {
        return signature;
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getSource() {
        return source;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc=new DefaultJIndexDocument(id);
        doc.add("id",id,true);
        doc.add("name",name,true);
        doc.add("returnType",returnType,true);
        doc.add("declaringType",declaringType,true);
        doc.add("signature",signature.toString(),true);
        doc.add("modifiers",String.valueOf(modifiers),false);
        doc.add("source",source,false);
        doc.add("parameterNames",String.join(";",parameterNames),false);
        doc.add("parameterTypes",String.join(";",parameterTypes),false);
        doc.add("imports",String.join(";",imports),false);
        return doc;
    }

    @Override
    public String getId() {
        return id;
    }
}
