package net.hl.compiler.stages;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JMethod;
import net.hl.compiler.core.invokables.HLJCompilerContext;

import java.util.ArrayList;
import java.util.List;

public class HStageUtils {
    public static List<JField> getNoTypeFields(HLJCompilerContext compilerContext) {
        List<JField> list = (List<JField>) compilerContext.getMetaPackageType().getUserObjects().get("NoTypeFields");
        if(list==null){
            list=new ArrayList<>();
            compilerContext.getMetaPackageType().getUserObjects().put("NoTypeFields",list);
        }
        return list;
    }
    public static List<JMethod> getNoTypeMethods(HLJCompilerContext compilerContext) {
        List<JMethod> list = (List<JMethod>) compilerContext.getMetaPackageType().getUserObjects().get("NoTypeMethods");
        if(list==null){
            list=new ArrayList<>();
            compilerContext.getMetaPackageType().getUserObjects().put("NoTypeMethods",list);
        }
        return list;
    }
}
