package net.hl.compiler.index;

import net.hl.compiler.ast.HNDeclareType;
import net.hl.compiler.ast.HNExtends;
import net.hl.compiler.utils.HSharedUtils;
import net.thevpc.jeep.JIndexDocument;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HIndexedClass implements HIndexedElement {
    private String simpleName;
    private String simpleName2;
    private String fullName;
    private String declaringType;
    private String packageName;
    private String[] superTypes;
    private String[] imports;
    private String[] exports;
    private AnnInfo[] annotations;
    private String source;

    public HIndexedClass(JType type, String source) {
        fullName = type.getName();
        simpleName = type.getSimpleName();
        simpleName2 = simpleName;
        JType nn = type.getDeclaringType();
        while (nn != null) {
            simpleName2 = nn.getName() + "." + simpleName2;
            nn = type.getDeclaringType();
        }

        if (type.getDeclaringType() != null) {
            declaringType = type.getDeclaringType().getName();
        } else {
            declaringType = "";
        }
        packageName = type.getPackageName();
        this.source = source;
        this.annotations = type.getAnnotations().stream().map(AnnInfo::new).toArray(AnnInfo[]::new);
        imports = new String[0];
        List<String> superTypesList = new ArrayList<>();
        for (JType extend : type.getParents()) {
            superTypesList.add(extend.getName());
        }
        this.superTypes = superTypesList.toArray(new String[0]);
        this.exports = new String[0]; //fix me;
    }

    public HIndexedClass(HNDeclareType type, String source) {
        fullName = type.getFullName();
        simpleName = type.getName();
        simpleName2 = simpleName;
        HNDeclareType nn = type.getDeclaringType();
        while (nn != null) {
            simpleName2 = nn.getName() + "." + simpleName2;
            nn = type.getDeclaringType();
        }

        if (type.getDeclaringType() != null) {
            declaringType = type.getDeclaringType().getFullName();
        } else {
            declaringType = "";
        }
        packageName = type.getFullPackage();
        this.source = source;
        this.annotations = Arrays.stream(type.getAnnotations()).map(AnnInfo::new).toArray(AnnInfo[]::new);
        imports = HSharedUtils.getImports(type);
        List<String> superTypesList = new ArrayList<>();
        for (HNExtends extend : type.getExtends()) {
            superTypesList.add(extend.getFullName());
        }
        this.superTypes = superTypesList.toArray(new String[0]);
        this.exports = new String[0]; //fix me;
    }

    public HIndexedClass(String simpleName, String simpleName2, String fullName, String declaringType, String packageName, String[] superTypes, String[] imports, AnnInfo[] annotations, String source) {
        this.simpleName = simpleName;
        this.simpleName2 = simpleName2;
        this.fullName = fullName;
        this.declaringType = declaringType;
        this.packageName = packageName;
        this.superTypes = superTypes;
        this.imports = imports;
        this.annotations = Arrays.stream(annotations).toArray(AnnInfo[]::new);
        this.source = source;
        this.exports = new String[0]; //fix me;
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

    public AnnInfo[] getAnnotations() {
        return annotations;
    }

    public String getSource() {
        return source;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc = new DefaultJIndexDocument(getFullName());
        doc.add("fullName", fullName, true);
        doc.add("declaringType", declaringType, true);
        String ns = packageName;
        doc.add("package", ns, true);
        while (!ns.isEmpty()) {
            doc.add("packages", ns, true);
            int dot = ns.lastIndexOf('.');
            if (dot >= 0) {
                ns = ns.substring(0, dot);
            } else {
                ns = "";
            }
        }
        doc.add("simpleName", simpleName, true);
        doc.add("simpleName2", simpleName2, true);
        doc.add("annotations", Arrays.asList(annotations).toString(), false);
        doc.add("source", String.valueOf(source), false);
        doc.add("imports", String.join(";", Arrays.asList(imports)), false);

        doc.add("superTypes", String.join(";", Arrays.asList(superTypes)), true);
        for (String superType : superTypes) {
            doc.add("superType", superType, true);
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
                ", annotations=" + Arrays.asList(annotations).toString() +
                ", source='" + source + '\'' +
                '}';
    }
}
