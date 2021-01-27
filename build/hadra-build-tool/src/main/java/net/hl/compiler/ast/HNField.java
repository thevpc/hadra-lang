//package net.hl.compiler.parser.ast;
//
//import net.thevpc.jeep.*;
//import net.thevpc.jeep.util.JNodeUtils;
//import net.thevpc.jeep.util.JTokenUtils;
//import net.thevpc.jeep.JNodeFindAndReplace;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class HNField extends HNode {
//    private JToken fieldName;
//    private JNode instanceNode;
//
//    private JField field;
//    private JTypeName declaringTypeName;
//    private boolean nullableInstance;
//
//    private HNField() {
//        super(HNNodeId.H_FIELD);
//    }
//
//    public HNField(JNode instanceNode, JField field, JToken token) {
//        super(HNNodeId.H_FIELD);
//        this.field = field;
//        this.fieldName = field==null?null: JTokenUtils.createWordToken(field.name()); // field?name();
//        this.declaringTypeName = field==null?null:field.declaringType().typeName(); // field?name();
//        setInstanceNode(instanceNode);
//        setType(field==null?null:field.type());
//        setStartToken(token);
//    }
//
//    public HNField(JNode instanceNode, JToken fieldName, JTypeName declaringTypeName, JType fieldType, JToken startToken, JToken endToken) {
//        super(HNNodeId.H_FIELD);
//        this.fieldName = fieldName;
//        setInstanceNode(instanceNode);
//        this.declaringTypeName = declaringTypeName;
//        this.setType(fieldType);
//        this.setStartToken(token);
//        this.setEndToken(endToken);
//    }
//
//    public boolean isNullableInstance() {
//        return nullableInstance;
//    }
//
//    public HNField setNullableInstance(boolean nullableInstance) {
//        this.nullableInstance = nullableInstance;
//        return this;
//    }
//
//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getInstanceNode,this::setInstanceNode);
//    }
//
//    @Override
//    public List<JNode> childrenNodes() {
//        return Arrays.asList(instanceNode);
//    }
//
//    public HNField setInstanceNode(JNode instanceNode) {
//        this.instanceNode=JNodeUtils.bind(this,instanceNode,"instance");
//        return this;
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof HNField) {
//            HNField o =(HNField) node;
//            this.instanceNode=bindCopy(o.instanceNode);
//            this.fieldName =(o.fieldName);
//            this.field=(o.field);
//            this.nullableInstance=(o.nullableInstance);
//        }
//    }
//
//    public JToken getFieldNameToken() {
//        return fieldName;
//    }
//    public String getFieldName() {
//        return fieldName.image;
//    }
//
//
//    public JNode getInstanceNode() {
//        return instanceNode;
//    }
//
//    @Override
//    public String toString() {
//        if(field!=null){
//            if(field.isStatic()){
//                return field.declaringType().name()
//                        +"."+fieldName.image;
//            }else{
//                if(instanceNode==null){
//                    return String.valueOf("(this)")
//                            +(nullableInstance?"?":".")
//                            + fieldName.image;
//                }else {
//                    return String.valueOf(instanceNode)
//                            + (nullableInstance?"?":".") + fieldName.image;
//                }
//            }
//        }
//        return String.valueOf(instanceNode)
//                +"."+fieldName.image;
//    }
//
//    public JField getField() {
//        return field;
//    }
//
//    public HNField setField(JField field) {
//        this.field = field;
//        if (field != null) {
//            setType(field.type());
//        }
//        return this;
//    }
//
//    public JTypeName getDeclaringTypeName() {
//        return declaringTypeName;
//    }
//
//    public HNField setDeclaringTypeName(JTypeName declaringTypeName) {
//        this.declaringTypeName = declaringTypeName;
//        return this;
//    }
//}
