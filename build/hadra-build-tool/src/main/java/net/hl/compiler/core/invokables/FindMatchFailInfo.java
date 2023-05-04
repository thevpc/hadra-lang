package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.log.JSourceMessage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class FindMatchFailInfo {
    private boolean error;
    private String desc;
    private String signatureString;
    private LinkedHashSet<String> alternatives = new LinkedHashSet<>();
    private LinkedHashSet<String> imports = new LinkedHashSet<>();
    private LinkedHashSet<JInvokable> available = new LinkedHashSet<>();
    private LinkedHashSet<JConverter> searchedConverters=new LinkedHashSet<>();
    private HLJCompilerContext.ConversionTrace conversions;

    private List<JSourceMessage> errorMessages=new ArrayList<>();

    public String getSignatureString() {
        return signatureString;
    }

    public FindMatchFailInfo setSignatureString(String signatureString) {
        this.signatureString = signatureString;
        return this;
    }

    public FindMatchFailInfo(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    

    public boolean isError() {
        return error;
    }

    public HLJCompilerContext.ConversionTrace getConversions() {
        return conversions;
    }

    public FindMatchFailInfo setConversions(HLJCompilerContext.ConversionTrace conversions) {
        this.conversions = conversions;
        return this;
    }

    public FindMatchFailInfo setError(boolean error) {
        this.error = error;
        return this;
    }

    public void addImport(String type) {
        imports.add(type);
    }
    public void addAlternative(String kind, String value) {
        StringBuilder sb = new StringBuilder(20);
        sb.append(kind);
        while(sb.length()<20){
            sb.append(' ');
        }
        sb.append(':');
        sb.append(value);
        alternatives.add(sb.toString());
    }

    public String header(){
        StringBuilder sb = new StringBuilder();
        sb.append("failed to match");
        if (desc != null) {
            sb.append(" ").append(desc);
        } else {
            sb.append(" ").append("method");
        }
        if(signatureString!=null){
            sb.append(" ").append(signatureString);
        }
        return sb.toString();
    }

    public void fail(JOnError fail, JCompilerLog log, String errorId, String group, String message, JToken location) {
        JSourceMessage em = buildMessage(errorId, null, message, location);
        errorMessages.add(em);
        switch (fail) {
            case NULL:
                return;
            case TRACE: {
                log.add(em);
                return;
            }
            case EXCEPTION: {
                log.add(em);
                throw new JParseException(header());
            }
        }
        throw new JParseException(header());
    }

    private JSourceMessage buildMessage(String errorId, String group, String extraMessage, JToken location){
        if(errorId==null){
            errorId="S044";
        }
        StringBuilder sb = new StringBuilder(header());
        if(extraMessage!=null){
            sb.append("\n\t").append(extraMessage);
        }
        boolean someText=false;
        if(imports.size()>0) {
            someText=true;
            sb.append("\n\twas looking into the following types (according to applicable imports) :");
            for (String imp : imports) {
                sb.append("\n\t\t").append(imp);
            }
            if(alternatives.size()>0) {
                sb.append("\n\tfor one of the following alternatives:");
                for (String alt : alternatives) {
                    sb.append("\n\t\t").append(alt);
                }
            }
        }else{
            if(alternatives.size()>0){
                someText=true;
                if(alternatives.size()==1) {
                    sb.append("\n\twas looking for :");
                }else{
                    sb.append("\n\twas looking for one of the following alternatives:");
                }
                for (String alt : alternatives) {
                    sb.append("\n\t\t").append(alt);
                }
            }
        }
        if (available.isEmpty()) {
            if(someText){
                if(alternatives.size()<=1) {
                    sb.append("\n\tbut nothing was available.");
                }else{
                    sb.append("\n\tbut none was available.");
                }
            }
        } else {
            if(someText) {
                sb.append("\n\tthe following do not apply : ");
                for (JInvokable alt : available) {
                    sb.append("\n\t\t").append(alt);
                }
            }else{
                sb.append("\n\tthe following do not apply : ");
                for (JInvokable alt : available) {
                    sb.append("\n\t\t").append(alt);
                }
            }
        }
        if (getSearchedConverters().size() > 0) {
            sb.append("\n\tapplicable conversions were : ");
            for (JConverter alt : getSearchedConverters()) {
                sb.append("\n\t\t").append(alt);
            }
        }
        return JSourceMessage.error(errorId, null, location, sb.toString());
    }

    public void fail(JOnError fail,JCompilerLog log, JToken location) {
        fail(fail,log,null, null,null, location);
    }

    public void addAvailable(JInvokable jInvokable) {
        this.available.add(jInvokable);
    }

    public LinkedHashSet<JConverter> getSearchedConverters() {
        return searchedConverters;
    }

    public FindMatchFailInfo setSearchedConverters(LinkedHashSet<JConverter> searchedConverters) {
        this.searchedConverters = searchedConverters;
        return this;
    }
}
