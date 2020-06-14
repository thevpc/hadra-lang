package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.ArrayList;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

public class HNExtends extends HNode {
    private int modifiers;
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
    public List<JNode> childrenNodes() {
        return (List) this.arguments;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNExtends) {
            HNExtends o =(HNExtends) node;
            this.modifiers=o.modifiers;
            this.packageName =o.packageName;
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

    public int getModifiers() {
        return modifiers;
    }

    public HNExtends setModifiers(int modifiers) {
        this.modifiers = modifiers;
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
        sb.append(HUtils.modifiersToString(modifiers));
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
