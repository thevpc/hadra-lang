package net.hl.compiler.index;

import net.hl.compiler.ast.HNAnnotationCall;
import net.hl.compiler.ast.HNIdentifier;
import net.hl.compiler.ast.HNTypeTokenSpecialAnnotation;
import net.hl.compiler.ast.HNode;
import net.thevpc.jeep.JAnnotationInstance;
import net.thevpc.jeep.JAnnotationInstanceField;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnInfo {
    private String fullName;
    private Map<String,AnnValue> values=new LinkedHashMap<>();

    public AnnInfo() {
    }
    public AnnInfo(String name) {
        this.fullName=name;
    }

    public AnnInfo(String fullName, Map<String, AnnValue> values) {
        this.fullName = fullName;
        this.values = values;
    }

    //TODO FIX ME
    public AnnInfo(HNAnnotationCall a) {
        if(a.getName() instanceof HNTypeTokenSpecialAnnotation){
            this.fullName=((HNTypeTokenSpecialAnnotation)(a.getName())).getStartToken().sval;
        }else{
            throw new IllegalArgumentException("unsupported");
        }
        for (HNode arg : a.getArgs()) {
            throw new IllegalArgumentException("FIX ME");
        }
    }
    //TODO FIX ME
    public AnnInfo(JAnnotationInstance a) {
        this.fullName=a.getName();
        for (JAnnotationInstanceField arg : a.getFields()) {
            values.put(arg.getName(),new AnnValue(arg.getValue()));
        }
    }

    public String getName() {
        return fullName;
    }

    public AnnInfo setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public Map<String, AnnValue> getValues() {
        return values;
    }

    public AnnInfo setValues(Map<String, AnnValue> values) {
        this.values = values;
        return this;
    }
}
