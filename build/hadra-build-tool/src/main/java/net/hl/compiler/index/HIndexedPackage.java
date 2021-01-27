package net.hl.compiler.index;

import net.thevpc.jeep.JIndexDocument;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;

import java.util.ArrayList;
import java.util.List;

public class HIndexedPackage implements HIndexedElement{
    private String name;
    private String parent;
    private String fullName;
    private String source;

    public HIndexedPackage(String source, String name) {
        this.fullName = name;
        int i=name.lastIndexOf('.');
        if(i>=0){
            this.name=name.substring(i+1);
            this.parent=name.substring(0,i);
        }else{
            this.name=fullName;
            this.parent="";
        }
        this.source=source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc = new DefaultJIndexDocument(name);
        doc.add("fullName",fullName,true);
        doc.add("name",name,true);
        doc.add("parent",parent,true);
        doc.add("source",source,false);

        String t=parent;
        while(t.length()>0){
            doc.add("parents",t,true);
            int i=t.lastIndexOf('.');
            if(i>=0) {
                t=t.substring(0, i);
            }else{
                break;
            }
        }
        return doc;
    }

    public String[] getParents() {
        List<String> parents=new ArrayList<>();
        String t=parent;
        while(t.length()>0){
            parents.add(t);
            int i=t.lastIndexOf('.');
            if(i>=0) {
                t=t.substring(0, i);
            }else{
                break;
            }
        }
        return parents.toArray(new String[0]);
    }

    public String getParent() {
        return parent;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return fullName;
    }

    @Override
    public String toString() {
        return "HLIndexedPackage{" +
                "fullName='" + fullName + '\'' +
                '}';
    }
}
