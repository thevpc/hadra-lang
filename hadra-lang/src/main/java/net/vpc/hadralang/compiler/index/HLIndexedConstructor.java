package net.vpc.hadralang.compiler.index;

import net.vpc.common.jeep.JConstructor;
import net.vpc.common.jeep.JIndexDocument;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareIdentifier;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareInvokable;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HLIndexedConstructor implements HLIndexedElement{
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
    public HLIndexedConstructor(HNDeclareInvokable m) {
        name=m.getNameToken().sval;
        declaringType=m.getDeclaringType().getFullName();
        returnType=declaringType;
        modifiers=m.getModifiers();
        List<String> pn=new ArrayList<>();
        List<String> pt=new ArrayList<>();
        for (HNDeclareIdentifier argument : m.getArguments()) {
            pn.add(argument.getIdentifierName());
            pt.add(argument.getIdentifierTypeName().getTypename().fullName());
        }
        parameterNames=pn.toArray(new String[0]);
        parameterTypes=pt.toArray(new String[0]);
        imports= HUtils.getImports(m);
        signature=JNameSignature.of(name,parameterTypes);
        source=HUtils.getSourceName(m);
        id=declaringType+"."+signature.toString();
    }
    public HLIndexedConstructor(JConstructor m,String source) {
        name=m.name();
        declaringType=m.declaringType().name();
        returnType=declaringType;
        modifiers=m.modifiers();
        parameterNames=m.argNames();
        parameterTypes= Arrays.stream(m.argTypes()).map(JType::name).toArray(String[]::new);
        imports= new String[0];
        signature=JNameSignature.of(name,parameterTypes);
        this.source=source;
        id=declaringType+"."+signature.toString();
    }
    public HLIndexedConstructor(String name, String[] parameterNames, String[] parameterTypes, String[] imports, String declaringType, int modifiers, String source) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
        this.imports = imports;
        this.returnType = declaringType;
        this.declaringType = declaringType;
        this.modifiers = modifiers;
        this.source = source;
        this.signature=JNameSignature.of(name,parameterTypes);
        id=declaringType+"."+signature.toString();
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
    public String getId() {
        return id;
    }
}
