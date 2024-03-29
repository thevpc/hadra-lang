package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.util.JNodeUtils;
import net.thevpc.jeep.JNodeFindAndReplace;
import net.hl.compiler.utils.HSharedUtils;

import java.util.ArrayList;
import java.util.List;
import net.thevpc.jeep.JNodeCopyFactory;

public class HNExtends extends HNode {
    private String packageName;
    private String name;
    private List<HNode> arguments = new ArrayList<>();

    private HNExtends() {
        super(HNNodeId.H_EXTENDS);
    }
    public HNExtends(String name, JToken startToken, JToken endToken) {
        super(HNNodeId.H_EXTENDS);
        this.name = name;
        setStartToken(startToken);
        setEndToken(endToken);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getArguments());
    }

    public String getFullName(){
        StringBuilder sb=new StringBuilder();
        if(getPackageName()!=null){
            sb.append(getPackageName());
            sb.append(".");
        }
        sb.append(getName());
        return sb.toString();
    }


    @Override
    public List<JNode> getChildrenNodes() {
        return (List) this.arguments;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNExtends) {
            HNExtends o =(HNExtends) node;
            this.packageName =o.packageName;
            this.name =o.name;
            this.arguments= JNodeUtils.copy(o.arguments);
        }
    }

    public List<HNode> getArguments() {
        return arguments;
    }

    public void addArguments(HNode argument) {
        this.arguments.add(JNodeUtils.bind(this,argument,"arguments",arguments.size()));
    }

    public HNExtends setArguments(List<HNode> arguments) {
        this.arguments = JNodeUtils.bind(this,arguments,"arguments");
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public HNExtends setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getName() {
        return name;
    }

    public HNExtends setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return toString("");
    }

//    @Override
    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(HNAnnotationList.nonNull(getAnnotations()));
        if (sb.length() > 0) {
            sb.append(" ");
        }
        if (packageName != null) {
            sb.append(packageName);
            sb.append(".");
        }
        sb.append(name);
        sb.append("(");
        boolean first=true;
        for (HNode argument : arguments) {
            if(first){
                first=false;
            }else{
                sb.append(", ");
            }
            sb.append(argument.toString());
        }
        sb.append(")");
        return sb.toString();
    }

}
