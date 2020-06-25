package net.hl.compiler.stages;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JMethod;
import net.hl.compiler.core.invokables.HLJCompilerContext;

import java.util.ArrayList;
import java.util.List;

public class HLCStageUtils {
    public static List<JField> getNoTypeFields(HLJCompilerContext compilerContext) {
        List<JField> list = (List<JField>) compilerContext.metaPackageType().userObjects().get("NoTypeFields");
        if(list==null){
            list=new ArrayList<>();
            compilerContext.metaPackageType().userObjects().put("NoTypeFields",list);
        }
        return list;
    }
    public static List<JMethod> getNoTypeMethods(HLJCompilerContext compilerContext) {
        List<JMethod> list = (List<JMethod>) compilerContext.metaPackageType().userObjects().get("NoTypeMethods");
        if(list==null){
            list=new ArrayList<>();
            compilerContext.metaPackageType().userObjects().put("NoTypeMethods",list);
        }
        return list;
    }
}
