package net.vpc.hadralang.compiler.index;

import net.vpc.common.jeep.JCompilationUnit;
import net.vpc.common.jeep.JIndexDocument;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareType;
import net.vpc.hadralang.compiler.parser.ast.HNExtends;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.*;

public class HLIndexedClass implements HLIndexedElement{
    private String simpleName;
    private String simpleName2;
    private String fullName;
    private String declaringType;
    private String packageName;
    private String[] superTypes;
    private String[] imports;
    private String[] exports;
    private int modifiers;
    private String source;
    public HLIndexedClass(JType type,String source) {
        fullName=type.name();
        simpleName=type.simpleName();
        simpleName2=simpleName;
        JType nn=type.declaringType();
        while(nn!=null){
            simpleName2=nn.name()+"."+simpleName2;
            nn=type.declaringType();
        }

        if(type.declaringType()!=null) {
            declaringType =type.declaringType().name();
        }else{
            declaringType="";
        }
        packageName =type.packageName();
        this.source=source;
        modifiers=type.modifiers();
        imports=new String[0];
        List<String> superTypesList=new ArrayList<>();
        for (JType extend : type.parents()) {
            superTypesList.add(extend.name());
        }
        this.superTypes=superTypesList.toArray(new String[0]);
        this.exports=new String[0]; //fix me;
    }
    public HLIndexedClass(HNDeclareType type,String source) {
        fullName=type.getFullName();
        simpleName=type.getName();
        simpleName2=simpleName;
        HNDeclareType nn=type.getDeclaringType();
        while(nn!=null){
            simpleName2=nn.getName()+"."+simpleName2;
            nn=type.getDeclaringType();
        }

        if(type.getDeclaringType()!=null) {
            declaringType =type.getDeclaringType().getFullName();
        }else{
            declaringType="";
        }
        packageName =type.getFullPackage();
        this.source=source;
        modifiers=type.getModifiers();
        imports=HUtils.getImports(type);
        List<String> superTypesList=new ArrayList<>();
        for (HNExtends extend : type.getExtends()) {
            superTypesList.add(extend.getFullName());
        }
        this.superTypes=superTypesList.toArray(new String[0]);
        this.exports=new String[0]; //fix me;
    }

    public HLIndexedClass(String simpleName, String simpleName2, String fullName, String declaringType, String packageName, String[] superTypes, String[] imports, int modifiers, String source) {
        this.simpleName = simpleName;
        this.simpleName2 = simpleName2;
        this.fullName = fullName;
        this.declaringType = declaringType;
        this.packageName = packageName;
        this.superTypes = superTypes;
        this.imports = imports;
        this.modifiers = modifiers;
        this.source = source;
        this.exports=new String[0]; //fix me;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getSimpleName2() {
        return simpleName2;
    }

    public String getDeclaringType() {
        return declaringType;
    }

    public String getPackageName() {
        return packageName;
    }

    public String[] getSuperTypes() {
        return superTypes;
    }

    public String[] getImports() {
        return imports;
    }

    public String[] getExports() {
        return exports;
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getSource() {
        return source;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc=new DefaultJIndexDocument(getFullName());
        doc.add("fullName",fullName,true);
        doc.add("declaringType",declaringType,true);
        String ns= packageName;
        doc.add("package", ns,true);
        while(!ns.isEmpty()) {
            doc.add("packages", ns,true);
            int dot=ns.indexOf('.');
            if(dot>=0){
                ns=ns.substring(0,dot);
            }else{
                ns="";
            }
        }
        doc.add("simpleName",simpleName,true);
        doc.add("simpleName2",simpleName2,true);
        doc.add("modifiers",String.valueOf(modifiers),false);
        doc.add("source",String.valueOf(source),false);
        doc.add("imports",String.join(";", Arrays.asList(imports)),false);

        doc.add("superTypes",String.join(";", Arrays.asList(superTypes)),true);
        for (String superType : superTypes) {
            doc.add("superType",superType,true);
        }
        return doc;
    }

    @Override
    public String getId() {
        return getFullName();
    }

    @Override
    public String toString() {
        return "HLIndexedClass{" +
                "simpleName='" + simpleName + '\'' +
                ", simpleName2='" + simpleName2 + '\'' +
                ", fullName='" + fullName + '\'' +
                ", declaringType='" + declaringType + '\'' +
                ", package='" + packageName + '\'' +
                ", superTypes=" + Arrays.toString(superTypes) +
                ", imports=" + Arrays.toString(imports) +
                ", exports=" + Arrays.toString(exports) +
                ", modifiers=" + modifiers +
                ", source='" + source + '\'' +
                '}';
    }
}
