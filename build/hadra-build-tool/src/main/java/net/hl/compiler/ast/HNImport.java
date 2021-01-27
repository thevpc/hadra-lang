package net.hl.compiler.ast;

import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.compiler.DefaultJImportInfo;
import net.thevpc.jeep.JNodeFindAndReplace;

import java.util.Collections;
import java.util.List;

public class HNImport extends HNode {
    private String value;

    private HNImport() {
        super(HNNodeId.H_IMPORT);
    }
    public HNImport(String value, JToken token, JToken endtoken) {
        super(HNNodeId.H_IMPORT);
        this.value = value;
        setStartToken(token);
        setEndToken(endtoken);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }



    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNImport) {
            HNImport o =(HNImport) node;
//            this.XXX=bindCopy(o.XXX);
            this.value=(o.value);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Collections.emptyList();
    }

    public String getValue() {
        return value;
    }
    public JImportInfo getJImportInfo() {
        return new DefaultJImportInfo(value, getStartToken());
    }

    @Override
    public String toString() {
        return "import "+ value;
    }
}
