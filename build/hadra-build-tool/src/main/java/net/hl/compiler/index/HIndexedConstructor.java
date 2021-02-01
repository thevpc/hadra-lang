package net.hl.compiler.index;

import net.thevpc.jeep.JConstructor;
import net.thevpc.jeep.JIndexDocument;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;
import net.thevpc.jeep.impl.functions.JNameSignature;
import net.hl.compiler.ast.HNDeclareIdentifier;
import net.hl.compiler.ast.HNDeclareInvokable;
import net.hl.compiler.utils.HSharedUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HIndexedConstructor implements HIndexedElement{
    private String name;
    private String[] parameterNames;
    private String[] parameterTypes;
    private String[] imports;
    private String returnType;
    private String declaringType;
    private JNameSignature signature;
    private String annotations;
    private String source;
    private String id;
    public HIndexedConstructor(HNDeclareInvokable m) {
        name=m.getNameToken().sval;
        declaringType=m.getDeclaringType().getFullName();
        returnType=declaringType;
        annotations = Arrays.stream(m.getAnnotations()).map(Object::toString).collect(Collectors.joining(" "));
        List<String> pn=new ArrayList<>();
        List<String> pt=new ArrayList<>();
        for (HNDeclareIdentifier argument : m.getArguments()) {
            pn.add(argument.getIdentifierName());
            pt.add(argument.getIdentifierTypeNode().getTypename().fullName());
        }
        parameterNames=pn.toArray(new String[0]);
        parameterTypes=pt.toArray(new String[0]);
        imports= HSharedUtils.getImports(m);
        signature=JNameSignature.of(name,parameterTypes);
        source=HSharedUtils.getSourceName(m);
        id=declaringType+"."+signature.toString();
    }
    public HIndexedConstructor(JConstructor m,String source) {
        name=m.getName();
        declaringType=m.getDeclaringType().getName();
        returnType=declaringType;
        annotations = m.getAnnotations().stream().map(Object::toString).collect(Collectors.joining(" "));
        parameterNames=m.getArgNames();
        parameterTypes= Arrays.stream(m.getArgTypes()).map(JType::getName).toArray(String[]::new);
        imports= new String[0];
        signature=JNameSignature.of(name,parameterTypes);
        this.source=source;
        id=declaringType+"."+signature.toString();
    }
    public HIndexedConstructor(String name, String[] parameterNames, String[] parameterTypes, String[] imports, String declaringType, String[] annotations, String source) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
        this.imports = imports;
        this.returnType = declaringType;
        this.declaringType = declaringType;
        this.annotations = Arrays.stream(annotations).collect(Collectors.joining(" "));
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
        doc.add("annotations",String.valueOf(annotations),false);
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

    public String getAnnotations() {
        return annotations;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }
}
