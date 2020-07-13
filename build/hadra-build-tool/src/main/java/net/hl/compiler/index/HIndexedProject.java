package net.hl.compiler.index;

import net.vpc.common.jeep.JIndexDocument;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;

public class HIndexedProject implements HIndexedElement {
    private String id;
    private String moduleId;
    private String source;
    private String[] dependencyIds;
    private String[] dependencyFiles;

    public HIndexedProject(String id, String moduleId, String source, String[] dependencyIds,String[] dependencyFiles) {
        this.id = id;
        this.moduleId = moduleId;
        this.source = source;
        this.dependencyIds = dependencyIds;
        this.dependencyFiles = dependencyFiles;
    }

    public String[] getDependencyIds() {
        return dependencyIds;
    }

    public String[] getDependencyFiles() {
        return dependencyFiles;
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
        doc.add("projectRoot", getId(),true);
        doc.add("moduleId",getModuleId(),true);
        doc.add("source", source,true);
        doc.add("dependencyFiles",String.join(";",dependencyFiles),true);
        doc.add("dependencyIds",String.join(";",dependencyIds),true);
        return doc;
    }
}
