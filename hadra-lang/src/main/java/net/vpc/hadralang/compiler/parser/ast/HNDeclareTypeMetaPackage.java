package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.*;
import net.vpc.hadralang.compiler.core.JModuleId;

import java.util.*;

public class HNDeclareTypeMetaPackage extends HNDeclareType {

//    public HNDeclareTypeMetaPackage setModuleId(JModuleId moduleId) {
//        this.moduleId = moduleId;
//        setPackageName("");
//        String pname = JStringUtils.isBlank(moduleId.getArtifact()) ? "" : moduleId.getArtifact();
//        String cname = JStringUtils.capitalize(pname);
//        setNameToken(JTokenUtils.createTokenIdPointer(new JToken(), JStringUtils.isBlank(cname) ? "MyApp" : cname));
//        String mpackageName = JStringUtils.isBlank(moduleId.getGroup()) ? "" : moduleId.getGroup();
//        StringBuilder sb = new StringBuilder();
//        if (!JStringUtils.isBlank(mpackageName)) {
//            sb.append(mpackageName);
//        }
//        if (!JStringUtils.isBlank(pname)) {
//            if (sb.length() > 0) {
//                sb.append(".");
//            }
//            sb.append(pname);
//        }
//        setMetaPackageName(sb.toString());
//        return this;
//    }


//    public String getVersion() {
//        return version;
//    }
//
//    public HNDeclareTypeMetaPackage setVersion(String version) {
//        this.version = version;
//        return this;
//    }

}
