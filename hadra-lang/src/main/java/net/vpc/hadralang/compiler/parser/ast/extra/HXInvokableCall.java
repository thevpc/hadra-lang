/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast.extra;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.hadralang.compiler.parser.ast.HNNodeId;
import net.vpc.hadralang.compiler.parser.ast.HNode;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HXInvokableCall extends HNode {

    private JInvokable invokable;
    private HNode base;
    private HNode[] args;
    public HXInvokableCall() {
        super(HNNodeId.X_INVOKABLE_CALL);
    }

    public HXInvokableCall(JInvokable invokable, HNode base, HNode[] args, JToken startToken, JToken endToken) {
        this();
        setInvokable(invokable);
        setBase(base);
        setArgs(args);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getArgs());
    }


    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(args);
    }


    public HNode[] getArgs() {
        return args;
    }

    public HXInvokableCall setArgs(HNode[] args) {
        this.args = JNodeUtils.bind(this,args,"args");
        return this;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public HXInvokableCall setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public HNode getBase() {
        return base;
    }

    public HXInvokableCall setBase(HNode base) {
        this.base = JNodeUtils.bind(this,base,"base");
        return this;
    }

    @Override
    public String toString() {
        String n = invokable.name();
        StringBuilder sb = new StringBuilder().append(n).append("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String sargi = args[i].toString();
            sb.append(sargi);
        }
        sb.append(")");
        return sb.toString();
    }

    public JNode get(int index) {
        return args[index];
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HXInvokableCall) {
            HXInvokableCall o = (HXInvokableCall) node;
            this.invokable = (o.invokable);
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
            this.args = JNodeUtils.bindCopy(this, copyFactory, o.args,HNode.class);
        }
    }

}
