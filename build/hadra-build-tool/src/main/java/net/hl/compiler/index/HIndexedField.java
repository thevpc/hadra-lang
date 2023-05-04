package net.hl.compiler.index;

import net.thevpc.jeep.JField;
import net.thevpc.jeep.JIndexDocument;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HIndexedField implements HIndexedElement {
    private String declaringType;
    private String type;
    private String name;
    private String id;
    private String[] imports;
    private AnnInfo[] annotations;
    private String source;

    public HIndexedField(JField item, String source) {
        this.declaringType = item.getDeclaringType().getName();
        this.type = item.type()==null?"":item.type().getName();
        this.name = item.name();
        this.annotations = item.getAnnotations().stream().map(AnnInfo::new).toArray(AnnInfo[]::new);
        this.imports= new String[0];
        this.source=source;
        this.id = (this.declaringType==null) ? name : (this.declaringType + "." + name);
    }

    public HIndexedField(String declaringType, String type, String name, String[] imports, AnnInfo[] annotations, String source) {
        this.declaringType = declaringType;
        this.type = type;
        this.name = name;
        this.imports = imports;
        this.annotations = Arrays.stream(annotations).toArray(AnnInfo[]::new);
        this.source = source;
        this.id = (this.declaringType==null) ? name : (this.declaringType + "." + name);
    }

    public String getDeclaringType() {
        return declaringType;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String[] getImports() {
        return imports;
    }

    public AnnInfo[] getAnnotations() {
        return annotations;
    }

    public String getSource() {
        return source;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc = new DefaultJIndexDocument(id);
        doc.add("id", id,true);
        doc.add("name", name,true);
        doc.add("type", type,true);
        doc.add("declaringType", declaringType,true);
        doc.add("annotations", Arrays.asList(annotations).toString(),false);
        doc.add("imports", String.join(";",imports),false);
        doc.add("source", source,false);
        return doc;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "HIndexedField{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
