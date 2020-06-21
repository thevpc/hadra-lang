package net.vpc.hadralang.compiler.index;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JIndexDocument;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;

public class HLIndexedField implements HLIndexedElement {
    private String declaringType;
    private String type;
    private String name;
    private String id;
    private String[] imports;
    private int modifiers;
    private String source;

    public HLIndexedField(JField item, String source) {
        this.declaringType = item.declaringType().getName();
        this.type = item.type()==null?"":item.type().getName();
        this.name = item.name();
        this.modifiers = item.modifiers();
        this.imports= new String[0];
        this.source=source;
        this.id = (this.declaringType==null) ? name : (this.declaringType + "." + name);
    }

    public HLIndexedField(String declaringType, String type, String name, String[] imports, int modifiers, String source) {
        this.declaringType = declaringType;
        this.type = type;
        this.name = name;
        this.imports = imports;
        this.modifiers = modifiers;
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

    public int getModifiers() {
        return modifiers;
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
        doc.add("modifiers", String.valueOf(modifiers),false);
        doc.add("imports", String.join(";",imports),false);
        doc.add("source", source,false);
        return doc;
    }

    @Override
    public String getId() {
        return id;
    }
}
