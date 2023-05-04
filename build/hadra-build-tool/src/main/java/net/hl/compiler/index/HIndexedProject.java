package net.hl.compiler.index;

import java.util.Arrays;
import net.hl.compiler.utils.DepIdAndFile;
import net.thevpc.jeep.JIndexDocument;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;

public class HIndexedProject implements HIndexedElement {

    private String id;
    private String moduleId;
    private String source;
    private DepIdAndFile[] dependencies;

    public HIndexedProject(String id, String moduleId, String source, DepIdAndFile[] dependencies) {
        this.id = id;
        this.moduleId = moduleId;
        this.source = source;
        this.dependencies = dependencies;
        if(source==null){
            throw new IllegalArgumentException("Missing source");
        }
        if(moduleId==null){
            throw new IllegalArgumentException("Missing moduleId");
        }
    }

    public DepIdAndFile[] getDependencies() {
        return dependencies;
    }

    //
//    private String toStr(HNTokenSuite v) {
//        if (v == null) {
//            return "";
//        }
//        StringBuilder sb = new StringBuilder();
//        for (JToken token : v.getTokens()) {
//            if (token.isString()) {
//                sb.append(token.sval);
//            } else {
//                sb.append(token.image);
//            }
//        }
//        return sb.toString();
//    }
    public String getModuleId() {
        return moduleId;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public JIndexDocument toDocument() {
        JIndexDocument doc = new DefaultJIndexDocument(getId());
        doc.add("projectRoot", getId(), true);
        doc.add("moduleId", getModuleId(), true);
        doc.add("source", source, true);
        doc.add("dependencyFiles", String.join(";", Arrays.stream(dependencies).map(x -> x.getFile()).toArray(String[]::new)), true);
        doc.add("dependencyIds", String.join(";", Arrays.stream(dependencies).map(x -> x.getId()).toArray(String[]::new)), true);
        return doc;
    }
}
