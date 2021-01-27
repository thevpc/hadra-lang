package net.hl.compiler.utils;

import net.thevpc.jeep.JToken;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.util.JTokenUtils;
import net.hl.compiler.core.HadraLanguage;

import java.util.HashMap;
import java.util.Map;

public class HTokenUtils {
    private static JTokenDef[] jTokenDefs=null;
    private static Map<String,JTokenDef> jTokenDefsMapByLayout=null;
    public static JToken createToken(String image) {
        JTokenDef u = getjTokenDefsMapByLayout().get(image);
        if(u!=null){
            JToken t = JTokenUtils.createUnknownToken(-1, image);
            t.def=u;
            t.sval=image;
            return t;
        }
//        switch (image) {
//            case "null":
//            case "void":
//            case "int":
//            case "long":
//            case "float":
//            case "byte":
//            case "short":
//            case "char":
//            case "boolean":
//            case "double": {
//                return JTokenUtils.createKeywordToken(image);
//            }
//            case "=":
//            case ":":
//            case "^":
//            case "*":
//            case "+":
//            case ".": {
//                return JTokenUtils.createOpToken(image);
//            }
//        }
        return JTokenUtils.createWordToken(image);
    }

    public static Map<String, JTokenDef> getjTokenDefsMapByLayout() {
        if(jTokenDefsMapByLayout==null){
            jTokenDefsMapByLayout=new HashMap<>();
            for (JTokenDef jTokenDef : getjTokenDefs()) {
                jTokenDefsMapByLayout.put(jTokenDef.imageLayout,jTokenDef);
            }
        }
        return jTokenDefsMapByLayout;
    }

    public static JTokenDef[] getjTokenDefs() {
        if(jTokenDefs==null){
            jTokenDefs=new HadraLanguage().tokens().tokenDefinitions();
        }
        return jTokenDefs;
    }
}
