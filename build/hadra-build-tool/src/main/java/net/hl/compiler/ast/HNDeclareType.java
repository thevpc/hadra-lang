package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JStringUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.hl.compiler.utils.HUtils;

import java.util.*;

public class HNDeclareType extends HNode implements HNDeclare {
    private List<HNExtends> extendsList = new ArrayList<>();
    private JToken extendsSepToken;
    private List<HNDeclareIdentifier> mainConstructorArgs;
    private String packageName;
    private JToken nameToken;
    private HNode body;
    private JType jType;
    private HNDeclareType nonInternalDeclaringType;
    private HNDeclareType declaringType;
    private boolean internalType;
    private boolean immediateBody;
    private String globalName;
    private String metaPackageName;

    public HNDeclareType() {
        super(HNNodeId.H_DECLARE_TYPE);
    }

    public HNDeclareType(JToken token) {
        this();
        setStartToken(token);
    }


    public JToken getExtendsSepToken() {
        return extendsSepToken;
    }

    public HNDeclareType setExtendsSepToken(JToken extendsSepToken) {
        this.extendsSepToken = extendsSepToken;
        return this;
    }


    public HNDeclareType setExtendsList(List<HNExtends> extendsList) {
        this.extendsList = JNodeUtils.bind(this,extendsList, "extendsList");
        return this;
    }

    public boolean isInternalType() {
        return internalType;
    }

    public HNDeclareType setInternalType(boolean internalType) {
        this.internalType = internalType;
        return this;
    }

    public String getGlobalName() {
        return globalName;
    }

    public HNDeclareType setGlobalName(String globalName) {
        this.globalName = globalName;
        return this;
    }

    public String getMetaPackage() {
        return metaPackageName;
    }

    public HNDeclareType setMetaPackageName(String metaPackageName) {
        this.metaPackageName = metaPackageName;
        return this;
    }

    public JType getjType() {
        return jType;
    }

    public HNDeclareType setjType(JType jType) {
        this.jType = jType;
        return this;
    }

    public List<HNDeclareIdentifier> getMainConstructorArgs() {
        return mainConstructorArgs;
    }

    public HNDeclareType setMainConstructorArgs(List<HNDeclareIdentifier> mainConstructorArgs) {
        this.mainConstructorArgs = JNodeUtils.bind(this,mainConstructorArgs, "mainConstructorArgs");
        return this;
    }

    public List<HNExtends> getExtends() {
        return extendsList;
    }

    public HNDeclareType setExtends(List<HNExtends> arguments) {
        this.extendsList = JNodeUtils.bind(this,arguments, "arguments");
        return this;
    }

    public String getFullPackage() {
        if (getDeclaringType() != null) {
            return getDeclaringType().getFullPackage();
        }
        StringBuilder sb = new StringBuilder();
        if (!JStringUtils.isBlank(getMetaPackage())) {
            sb.append(getMetaPackage());
        }
        String packageName = getPackageName();
        if (!JStringUtils.isBlank(packageName)) {
            if(sb.length()>0) {
                sb.append(".");
            }
            sb.append(packageName);
        }
        return sb.toString();
    }

    public String getFullName() {
        if (getDeclaringType() != null) {
            return getDeclaringType().getFullName() + "." + getName();
        }
        StringBuilder sb = new StringBuilder();
        if (!JStringUtils.isBlank(getMetaPackage())) {
            sb.append(getMetaPackage());
        }
        String packageName = getPackageName();
        if (!JStringUtils.isBlank(packageName)) {
            if (sb.length()>0) {
                sb.append(".");
            }
            sb.append(packageName);
        }
        if (sb.length()>0) {
            sb.append(".");
        }
        sb.append(getName());
        return sb.toString();
    }

    public String getPackageName() {
        return packageName;
    }

    public HNDeclareType setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public JToken getNameToken() {
        return nameToken;
    }

    public HNDeclareType setNameToken(JToken name) {
        this.nameToken = name;
        return this;
    }

    public String getName() {
        return nameToken == null ? null : nameToken.image;
    }

    public HNode getBody() {
        return body;
    }

    public HNDeclareType setBody(HNode body) {
        this.body=JNodeUtils.bind(this,body, "body");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HNAnnotationList.nonNull(getAnnotations()));
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append("class");
        sb.append(" ");
        if (packageName != null && packageName.length() > 0) {
            sb.append(packageName);
            sb.append(".");
        }
        sb.append(nameToken == null ? "" : nameToken.image);

        if (mainConstructorArgs != null) {
            sb.append("(");
            boolean first = true;
            for (HNDeclareIdentifier argument : mainConstructorArgs) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(argument.toString());
            }
            sb.append(")");
        }
        List<HNDeclareIdentifier> mainConstructorArgs = getMainConstructorArgs();
        if (mainConstructorArgs != null) {
            sb.append("(");
            boolean first = true;
            for (HNDeclareIdentifier argument : mainConstructorArgs) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(argument.toString());
            }
            sb.append(")");
        }

        if (extendsList != null && extendsList.size() > 0) {
            sb.append(" extends ");
            boolean first = true;
            for (HNExtends item : extendsList) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(item.toString());
            }
        }
        sb.append("{");
        if (body != null) {
            if (body instanceof HNBlock) {
                HNBlock bb = (HNBlock) body;
                if (bb.getStatements().size() > 0) {
                    sb.append("\n");
                    sb.append(JeepUtils.indent(HNBlock.toString(bb.getStatements().toArray(new HNode[0]), false)));
                }
            } else {
                sb.append(body == null ? "" : JeepUtils.indent(body.toString()));
            }
        }
        sb.append("\n}");
        return sb.toString();
    }


    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDeclareType) {
            HNDeclareType o = (HNDeclareType) node;
            this.extendsList = JNodeUtils.bindCopy(this, copyFactory, o.extendsList);
            this.mainConstructorArgs = JNodeUtils.bindCopy(this, copyFactory, o.mainConstructorArgs);
            this.body = JNodeUtils.bindCopy(this, copyFactory, o.body);
            this.packageName = o.packageName;
            this.metaPackageName = o.metaPackageName;
            this.nameToken = JNodeUtils.copy(o.nameToken);
            this.jType = o.jType;
            this.internalType = o.internalType;
            this.immediateBody = o.immediateBody;
            this.globalName = o.globalName;
            this.declaringType = o.declaringType; //without copy or bind
            this.nonInternalDeclaringType = o.nonInternalDeclaringType;//without copy or bind
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        if (this.mainConstructorArgs != null) {
            li.addAll(this.mainConstructorArgs);
        }
        li.addAll(this.extendsList);

        li.add(this.body);

        return li;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getExtends());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getMainConstructorArgs());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getBody, this::setBody);
    }

    public HNDeclareType getDeclaringType() {
        return declaringType;
    }

    public HNDeclareType setDeclaringType(HNDeclareType declaringType) {
        this.declaringType = declaringType;
        return this;
    }

    public boolean isImmediateBody() {
        return immediateBody;
    }

    public HNDeclareType setImmediateBody(boolean immediateBody) {
        this.immediateBody = immediateBody;
        return this;
    }

    public HNDeclareType getNonInternalDeclaringType() {
        return nonInternalDeclaringType;
    }

    public HNDeclareType setNonInternalDeclaringType(HNDeclareType nonInternalDeclaringType) {
        this.nonInternalDeclaringType = nonInternalDeclaringType;
        return this;
    }
}
