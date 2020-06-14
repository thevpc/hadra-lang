package net.vpc.hadralang.compiler.stages.generators.java;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.core.elements.*;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.stages.HLCStage;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class HLCStage08JavaTransform implements HLCStage {

    public static final Logger LOG = Logger.getLogger(HLCStage08JavaTransform.class.getName());
    private boolean showFinalErrors = false;
    private boolean inPreprocessor = false;
    private boolean inMetaPackage = false;
    private HLJCompilerContext2 compilerContext0;
    private JNodeCopyFactory copyFactory = new JNodeCopyFactory() {
        @Override
        public JNode copy(JNode other) {
            return processNode((HNode) other, compilerContext0);
        }
    };

    public HLCStage08JavaTransform(boolean inPreprocessor) {
        this.inPreprocessor = inPreprocessor;
    }

    @Override
    public void processProject(HLProject project, HLCOptions options) {
        JavaNodes.of(project).setMetaPackage(
                (HNDeclareType) copyFactory.copy(project.getMetaPackageType())
        );
        inMetaPackage = true;
//        JCompilationUnit cu0 = project.getCompilationUnits()[0];
//        HLJCompilerContext compilerContextBase0 = project.newCompilerContext(cu0);
//        HNode node0 = (HNode) compilerContextBase0.node();
//        JNode node20 = processNode(node0, compilerContextBase0);

        inMetaPackage = false;
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            HLJCompilerContext compilerContextBase = project.newCompilerContext(compilationUnit);
            HNode node = compilerContextBase.node();
            HNode node2 = processNode(node, compilerContext0 = new HLJCompilerContext2(compilerContextBase));
            //JavaNodes.of(project).getSourceNodes().add(node2);

        }
    }

    public HNode processNode(HNode node, HLJCompilerContext2 compilerContext) {
        if (node == null) {
            return null;
        }
        switch (node.id()) {
            case H_IDENTIFIER: {
                return onIdentifier((HNIdentifier) node, compilerContext);
            }
            case H_OP_UNARY: {
                return onOpUnaryCall((HNOpUnaryCall) node, compilerContext);
            }
            case H_OP_BINARY: {
                return onOpBinaryCall((HNOpBinaryCall) node, compilerContext);
            }
            case H_DECLARE_IDENTIFIER: {
                return onDeclareIdentifier((HNDeclareIdentifier) node, compilerContext);
            }
            case H_DECLARE_INVOKABLE: {
                return onDeclareInvokable((HNDeclareInvokable) node, compilerContext);
            }
            case H_ARRAY_NEW: {
                return onArrayNew((HNArrayNew) node, compilerContext);
            }
            case H_PARS_POSTFIX: {
                return onParsPostfix((HNParsPostfix) node, compilerContext);
            }
            case H_ASSIGN: {
                return onAssign((HNAssign) node, compilerContext);
            }
            case H_BRACKETS: {
                return onBrackets((HNBrackets) node, compilerContext);
            }
            case H_BRACKETS_POSTFIX: {
                return onBracketsPostfix((HNBracketsPostfix) node, compilerContext);
            }
            case H_OBJECT_NEW: {
                return onObjectNew((HNObjectNew) node, compilerContext);
            }
            case H_TUPLE: {
                return onTuple((HNTuple) node, compilerContext);
            }
            case H_OP_DOT: {
                return onOpDot((HNOpDot) node, compilerContext);
            }
            case H_OP_COALESCE: {
                return onOpCoalesce((HNOpCoalesce) node, compilerContext);
            }
            case H_DECLARE_TYPE: {
                return onDeclareType((HNDeclareType) node, compilerContext);
            }
            case H_LAMBDA_EXPR: {
                return onLambdaExpression((HNLambdaExpression) node, compilerContext);
            }
            case H_PARS: {
                return onPars((HNPars) node, compilerContext);
            }
            case H_IS: {
                return onIs((HNIs) node, compilerContext);
            }
            case H_IF_WHEN_DO: {
                return onWhenDoBranchNode((HNIf.WhenDoBranchNode) node, compilerContext);
            }
            case H_IF: {
                return onIf((HNIf) node, compilerContext);
            }
            case H_WHILE: {
                return onWhile((HNWhile) node, compilerContext);
            }
            case H_FOR: {
                return onFor((HNFor) node, compilerContext);
            }
            case H_CONTINUE: {
                return onContinue((HNContinue) node, compilerContext);
            }
            case H_BREAK: {
                return onBreak((HNBreak) node, compilerContext);
            }
            case H_DECLARE_TOKEN_IDENTIFIER: {
                return onDeclareTokenIdentifier((HNDeclareTokenIdentifier) node, compilerContext);
            }
            case H_DECLARE_TOKEN_LIST: {
                return onDeclareTokenList((HNDeclareTokenList) node, compilerContext);
            }
            case H_DECLARE_TOKEN_TUPLE: {
                return onDeclareTokenTuple((HNDeclareTokenTuple) node, compilerContext);
            }
            case H_SWITCH: {
                return onSwitch((HNSwitch) node, compilerContext);
            }
            case H_SWITCH_CASE: {
                return onSwitchCase((HNSwitch.SwitchCase) node, compilerContext);
            }
            case H_SWITCH_IS: {
                return onSwitchIs((HNSwitch.SwitchIs) node, compilerContext);
            }
            case H_SWITCH_IF: {
                return onSwitchIf((HNSwitch.SwitchIf) node, compilerContext);
            }
            case H_RETURN: {
                return onReturn((HNReturn) node, compilerContext);
            }
            case H_DOT_CLASS: {
                return onDotClass((HNDotClass) node, compilerContext);
            }
            case H_BLOCK: {
                return onBlock((HNBlock) node, compilerContext);
            }
            case H_THIS:
            case H_LITERAL_DEFAULT:
            case H_LITERAL:
            case H_TYPE_TOKEN:
            case H_STRING_INTEROP:
            case H_IMPORT: {
                //do nothing
                return copy0(node);
            }
            /////////////////////////////////////////
            case H_META_IMPORT_PACKAGE: {
                return onMetaImportPackage((HNMetaImportPackage) node, compilerContext);
            }
        }
        //in stage 1 wont change node instance
        throw new JShouldNeverHappenException("Unsupported node class in " + getClass().getSimpleName() + ": " + node.getClass().getSimpleName());
//        return node;
    }

    private HNode onBlock(HNBlock node, HLJCompilerContext2 compilerContext) {
        switch (node.getBlocType()) {
            case GLOBAL_BODY: {
//                HNBlock newBlock = new HNBlock(node.getBlocType(), new HNode[0], null, null);
                for (HNode statement : node.getStatements()) {
                    HNode hs = (HNode) statement;
                    JavaNodes jn = JavaNodes.of(compilerContext.project());
                    switch (hs.id()) {
                        case H_DECLARE_TYPE: {
                            HNDeclareType r = (HNDeclareType) processNode(hs, compilerContext);
                            JSource s0 = HUtils.getSource(hs);
                            HUtils.setSource(r, s0);
                            jn.getTopLevelTypes().add(r);
                            break;
                        }

                        case H_DECLARE_INVOKABLE: {
                            JSource s0 = HUtils.getSource(hs);
                            if (s0 != null) {
                                jn.getMetaPackageSources().add(s0);
                            }
                            HNBlock metaBody = (HNBlock) jn.getMetaPackage().getBody();
                            HNDeclareInvokable r = (HNDeclareInvokable) processNode(hs, compilerContext);
                            r.setModifiers(HUtils.publifyModifiers(r.getModifiers() | Modifier.STATIC));
                            if(metaBody==null){
                                metaBody=new HNBlock(HNBlock.BlocType.CLASS_BODY, new HNode[0],null,null);
                                jn.getMetaPackage().setBody(metaBody);
                            }
                            metaBody.add(r);
                            break;
                        }

                        case H_DECLARE_META_PACKAGE: {
                            //ignore
                            break;
                        }
                        case H_DECLARE_IDENTIFIER: {
                            //always dissociate declaration and assignement as the metaPackage may be run
                            //multiple times
                            JSource s0 = HUtils.getSource(hs);
                            if (s0 != null) {
                                jn.getMetaPackageSources().add(s0);
                            }
                            Supplier<HNBlock> dissociateBlock = () -> getOrCreateRunModuleMethodBody(compilerContext);
                            HNDeclareIdentifier hs0 = (HNDeclareIdentifier) hs;
                            HNode left = (HNode) copyFactory.copy(hs0.getIdentifierToken());
                            HNode right = hs0.getInitValue() == null ? null : (HNode) copyFactory.copy(hs0.getInitValue());
                            _fillBlockAssign(left, right,
                                    (HNBlock) jn.getMetaPackage().getBody(), dissociateBlock,
                                    isAssignRequireTempVar(left, right), true, true, compilerContext);
                            //ignore
                            break;
                        }
                        default: {
                            JSource s0 = HUtils.getSource(hs);
                            if (s0 != null) {
                                jn.getMetaPackageSources().add(s0);
                            }
                            HNBlock body = getOrCreateRunModuleMethodBody(compilerContext);
                            HNode n2 = processNode(hs, compilerContext.withNewParent(body));
                            if (n2.id() == HNNodeId.H_BLOCK && ((HNBlock) n2).getBlocType() == HNBlock.BlocType.EXPR_GROUP) {
                                for (HNode s2 : ((HNBlock) n2).getStatements()) {
                                    body.add(s2);
                                }
                            } else {
                                body.add(n2);
                            }
                        }

                    }
                }
                return null;
            }
            case CLASS_BODY: {
                HNBlock newBlock = new HNBlock(node.getBlocType(), new HNode[0], null, null);
                boolean dissociateInstance = false;
                boolean dissociateStatic = false;
                for (HNode statement : node.getStatements()) {
                    HNode hs = (HNode) statement;
                    switch (hs.id()) {
                        case H_DECLARE_TYPE: {
                            newBlock.add(processNode(hs, compilerContext));
                            break;
                        }
                        case H_DECLARE_INVOKABLE: {
                            newBlock.add(processNode(hs, compilerContext));
                            break;
                        }
                        case H_DECLARE_META_PACKAGE: {
                            //ignore
                            break;
                        }
                        case H_DECLARE_IDENTIFIER: {
                            //need to check when to dissociate.
                            // as a first reflexion, we should dissociate when these conditions apply
                            // + there is a statement (other than declarations) BEFORE this declaration
                            HNDeclareIdentifier hs0 = (HNDeclareIdentifier) hs;
                            boolean dissociate=hs0.isStatic()?dissociateStatic:dissociateInstance;
                            Supplier<HNBlock> dissociateBlock = null;
                            HNBlock newBlock0 = newBlock;
                            if (dissociate) {
                                dissociateBlock = () -> getOrCreateSpecialInitializer(newBlock, compilerContext,hs0.isStatic());
                            }
                            HNode left = (HNode) copyFactory.copy(hs0.getIdentifierToken());
                            HNode right = hs0.getInitValue() == null ? null : (HNode) copyFactory.copy(hs0.getInitValue());
                            _fillBlockAssign(left, right,
                                    newBlock0, dissociateBlock, isAssignRequireTempVar(left, right), true, false, compilerContext);
                            break;
                        }

                        case H_ASSIGN: {
                            //need to check when to dissociate.
                            // as a first reflexion, we should dissociate when these conditions apply
                            // + there is a statement (other than declarations) BEFORE this declaration
                            HNAssign hs0 = (HNAssign) hs;
                            if(hs0.isStatic()){
                                dissociateStatic=true;
                            }else{
                                dissociateInstance=true;
                            }
                            HNode left = (HNode) copyFactory.copy(hs0.getLeft());
                            HNode right = hs0.getRight() == null ? null : (HNode) copyFactory.copy(hs0.getRight());
                            _fillBlockAssign(left, right,
                                    getOrCreateSpecialInitializer(newBlock, compilerContext,hs0.isStatic())
                                    , null, isAssignRequireTempVar(left, right), true, false, compilerContext);
                            break;
                        }

                        default: {
                            dissociateInstance=true;
                            HNBlock body = getOrCreateSpecialInitializer(newBlock, compilerContext,false);
                            body.add(processNode(hs, compilerContext));
                        }

                    }
                }
                return newBlock;
            }

            case LOCAL_BLOC:
            case METHOD_BODY:
            case INSTANCE_INITIALIZER:
            case STATIC_INITIALIZER:
                {
                HNBlock newBlock = new HNBlock(node.getBlocType(), new HNode[0], null, null);
                for (HNode statement : node.getStatements()) {
                    HNode hs = (HNode) statement;
                    switch (hs.id()) {
                        case H_DECLARE_TYPE: {
                            newBlock.add(processNode(hs, compilerContext));
                            break;
                        }
                        case H_DECLARE_INVOKABLE: {
                            throw new JFixMeLaterException();
//                            newBlock.add(processNode(hs, compilerContext));
//                            break;
                        }
                        case H_DECLARE_META_PACKAGE: {
                            //ignore
                            break;
                        }
                        case H_DECLARE_IDENTIFIER: {
                            HNDeclareIdentifier hs0 = (HNDeclareIdentifier) hs;
                            HNode left = (HNode) copyFactory.copy(hs0.getIdentifierToken());
                            HNode right = hs0.getInitValue() == null ? null : (HNode) copyFactory.copy(hs0.getInitValue());
                            _fillBlockAssign(left, right,
                                    newBlock, null, isAssignRequireTempVar(left, right), false, false, compilerContext);
                            //ignore
                            break;
                        }

                        default: {
                            HNode n2 = processNode(hs, compilerContext);
                            if (n2.id() == HNNodeId.H_BLOCK && ((HNBlock) n2).getBlocType() == HNBlock.BlocType.EXPR_GROUP) {
                                for (HNode s2 : ((HNBlock) n2).getStatements()) {
                                    newBlock.add(s2);
                                }
                            } else {
                                newBlock.add(n2);
                            }
                            break;
                        }

                    }
                }
                return newBlock;
            }
            case UNKNOWN:
            case PACKAGE_BODY:
            case IMPORT_BLOC: {
                throw new JShouldNeverHappenException();
            }
            default: {
                throw new JShouldNeverHappenException();
            }
        }
    }

    private void _onDeclareIdentifier(HNDeclareIdentifier hs, boolean dissociate, boolean publify, boolean statify, HNBlock newBlock, Supplier<HNBlock> dissociatedBlock, HLJCompilerContext2 compilerContext) {
        HNDeclareIdentifier nhs0 = (HNDeclareIdentifier) hs;
        for (HNDeclareTokenIdentifier jNode : HNodeUtils.flatten(nhs0.getIdentifierToken())) {
            if (dissociate) {
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        (HNDeclareTokenIdentifier) jNode,
                        null,
                        new HNTypeToken(nhs0.getIdentifierType(), null),
                        nhs0.getAssignOperator(),
                        null, null
                );
                if (statify) {
                    nhs1.setModifiers(nhs1.getModifiers() | Modifier.STATIC);
                }
                if (publify) {
                    nhs1.setModifiers(HUtils.publifyModifiers(nhs1.getModifiers()));
                }

                newBlock.add(nhs1);
                if (nhs0.getInitValue() != null) {
                    HNBlock body = dissociatedBlock.get();
                    HNAssign hass = new HNAssign(new HNIdentifier(jNode.getToken()), nhs0.getAssignOperator(), nhs0.getInitValue(), null, null);
                    hass.setElement(new HNElementAssign(nhs0.getIdentifierType()));
                    body.add(hass);
                }
            } else {
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        (HNDeclareTokenIdentifier) jNode,
                        nhs0.getInitValue(),
                        (HNTypeToken) copyFactory.copy(nhs0.getIdentifierTypeName()),
                        nhs0.getAssignOperator(),
                        null, null
                );
                newBlock.add(nhs1);
            }
        }
    }

    private HNode onDotClass(HNDotClass node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onReturn(HNReturn node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onSwitchIf(HNSwitch.SwitchIf node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onSwitchIs(HNSwitch.SwitchIs node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onSwitchCase(HNSwitch.SwitchCase node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onSwitch(HNSwitch node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onDeclareTokenTuple(HNDeclareTokenTuple node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onDeclareTokenList(HNDeclareTokenList node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode lookupDeclarationStatement(HNDeclareTokenIdentifier node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onDeclareTokenIdentifier(HNDeclareTokenIdentifier node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onWhile(HNWhile node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onFor(HNFor node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onContinue(HNContinue node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onBreak(HNBreak node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onIs(HNIs node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onWhenDoBranchNode(HNIf.WhenDoBranchNode node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onIf(HNIf node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onPars(HNPars node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onDeclareType(HNDeclareType node, HLJCompilerContext2 compilerContext) {
        HNDeclareType copy = (HNDeclareType) copy0(node);
        copy.setPackageName(copy.getFullPackage());
        copy.setMetaPackageName(null);
        copy.setModifiers(HUtils.publifyModifiers(copy.getModifiers()));
        return copy;
    }

    private HNode onMetaImportPackage(HNMetaImportPackage node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onOpCoalesce(HNOpCoalesce node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onTuple(HNTuple node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onBrackets(HNBrackets node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode copy0(JNode node) {
        return (HNode) node.copy(copyFactory);
    }

    private HNode onBracketsPostfix(HNBracketsPostfix node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onObjectNew(HNObjectNew node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onAssign(HNAssign node, HLJCompilerContext2 compilerContext) {
        HNBlock b = new HNBlock(HNBlock.BlocType.EXPR_GROUP, new HNode[0], null, null);
        HNode left = (HNode) copyFactory.copy(node.getLeft());
        HNode right = (HNode) copyFactory.copy(node.getRight());
        _fillBlockAssign(left, right, b, null,
                isAssignRequireTempVar(left, right),
                false, false, compilerContext
        );
        if (b.getStatements().size() == 1) {
            return (HNode) b.getStatements().get(0);
        }
        return b;
    }

    private HNode onDeclareIdentifier(HNDeclareIdentifier node, HLJCompilerContext2 compilerContext) {
        HNBlock b = new HNBlock(HNBlock.BlocType.EXPR_GROUP, new HNode[0], null, null);
        HNode left = (HNode) copyFactory.copy(node.getIdentifierToken());
        HNode right = (HNode) (node.getInitValue() == null ? null : copyFactory.copy(node.getInitValue()));
        _fillBlockAssign(left, right, b, null,
                isAssignRequireTempVar(left, right),
                false, false, compilerContext
        );
        if (b.getStatements().size() == 1) {
            return (HNode) b.getStatements().get(0);
        }
        return b;
//        return copy0(node);
    }

    private HNode onParsPostfix(HNParsPostfix node, HLJCompilerContext2 compilerContext) {
        HNParsPostfix m = (HNParsPostfix) copy0(node);
        if(m.getElement().getKind()==HNElementKind.METHOD){
            JInvokable i = ((HNElementMethod) m.getElement()).getInvokable();
            if(i instanceof JMethod){
                JMethod jm=(JMethod)i;
                if(jm.isStatic()){
                    if(node.parentNode()!=null) {
                        switch (node.parentNode().id()) {
                            case H_OP_DOT: {
                                return m;
                            }
                        }
                    }
                    return new HNOpDot(
                            new HNTypeToken(jm.declaringType(),null),
                            HNodeUtils.createToken("."),
                            m,
                            null,null
                    ).setElement(m.getElement());
                }
            }
        }
        return m;
    }

    private HNode onArrayNew(HNArrayNew node, HLJCompilerContext2 compilerContext) {
        return copy0(node);

    }

    private HNode onDeclareInvokable(HNDeclareInvokable node, HLJCompilerContext2 compilerContext) {
        HNDeclareInvokable copy = (HNDeclareInvokable) copy0(node);
        HNode body = processNode((HNode) copy.getBody(), compilerContext);
        if(copy.isImmediateBody() && !JTypeUtils.isVoid(copy.getReturnType())){
            copy.setBody(
                    new HNReturn(body,null,null)
            );
        }else {
            copy.setBody(body);
        }
        return copy;
    }

    private HNode onOpBinaryCall(HNOpBinaryCall node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onOpUnaryCall(HNOpUnaryCall node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onLambdaExpression(HNLambdaExpression node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    protected HNode onIdentifier(HNIdentifier node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    protected HNode onOpDot(HNOpDot node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    ///////////////////////////////////////////////:
    public HNDeclareInvokable getRunModuleMethod(HLJCompilerContext2 compilerContext) {
        HNDeclareType meta = JavaNodes.of(compilerContext.project()).getMetaPackage();
        HNBlock b = (HNBlock) meta.getBody();
        for (HNDeclareInvokable ii : b.findDeclaredInvokables()) {
            if (ii.getSignature() != null && ii.getSignature().toString().equals("runModule()")) {
                return ii;
            }
        }
        return null;
    }

    public HNBlock getOrCreateRunModuleMethodBody(HLJCompilerContext2 compilerContext) {
        return (HNBlock) (getOrCreateRunModuleMethod(compilerContext).getBody());
    }

    public HNDeclareInvokable getOrCreateRunModuleMethod(HLJCompilerContext2 compilerContext) {
        HNDeclareType meta = JavaNodes.of(compilerContext.project()).getMetaPackage();
        HNDeclareInvokable m = getRunModuleMethod(compilerContext);
        if (m == null) {
            HNDeclareInvokable ii = new HNDeclareInvokable(
                    JTokenUtils.createTokenIdPointer(new JToken(), "runModule"),
                    null,
                    null
            );
            ii.setSignature(JNameSignature.of("runModule()"));
            ii.setElement(new HNElementMethod("runModule", null));
            ii.setReturnTypeName(compilerContext.base.createSpecialTypeToken("void"));
            ii.setDeclaringType(meta);
            ii.setBody(new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[0], ii.startToken(), ii.endToken()));
//            ii.buildInvokable();
            ii.setModifiers(HUtils.STATIC | HUtils.PUBLIC);
            HNBlock b = (HNBlock) meta.getBody();
            b.add(ii);
//            DefaultJType dt = (DefaultJType) compilerContext.getOrCreateType(meta);
//            JMethod runModuleMethod = dt.addMethod(
//                    JSignature.of(compilerContext.types(), ii.getSignature()),
//                    new String[0], JTypeUtils.forVoid(compilerContext.types()),
//                    new BodyJInvoke(ii), ii.getModifiers(), false
//            );
//            ii.setInvokable(runModuleMethod);
            m = ii;
        }
        return m;
    }

    public HNBlock getSpecialInitializer(HNBlock classTypeBody, HLJCompilerContext2 compilerContext,boolean isStatic) {
        for (HNode statement : classTypeBody.getStatements()) {
            if (statement instanceof HNBlock) {
                HNBlock specInit = (HNBlock) statement;
                if(isStatic){
                    if (specInit.getBlocType()== HNBlock.BlocType.STATIC_INITIALIZER && specInit.isSetUserObject("SPECIAL_INITIALIZER")) {
                        return specInit;
                    }
                }else {
                    if (specInit.getBlocType()== HNBlock.BlocType.INSTANCE_INITIALIZER && specInit.isSetUserObject("SPECIAL_INITIALIZER")) {
                        return specInit;
                    }
                }
            }
        }
        return null;
    }

    public HNBlock getOrCreateSpecialInitializer(HNBlock classTypeBody, HLJCompilerContext2 compilerContext,boolean isStatic) {
        HNBlock m = getSpecialInitializer(classTypeBody, compilerContext,isStatic);
        if (m == null) {
            m = new HNBlock(
                    isStatic?HNBlock.BlocType.STATIC_INITIALIZER:HNBlock.BlocType.INSTANCE_INITIALIZER,
                    new HNode[0], null, null);
            m.setUserObject("SPECIAL_INITIALIZER");
            classTypeBody.add(m);
        }
        return m;
    }

    private boolean isAssignRequireTempVar(HNode left, HNode right) {
        switch (left.id()) {
            case H_TUPLE:
                return true;
            case H_DECLARE_TOKEN_TUPLE:
                return true;
        }
        return false;
    }

    private void _fillBlockAssign(HNode left, HNode right, HNBlock block, Supplier<HNBlock> dissociatedBlock, boolean requireTempVar, boolean isField, boolean isStatic,
                                  HLJCompilerContext2 compilerContext) {
        JToken dot = HNodeUtils.createToken(".");
        JToken assignOp = HNodeUtils.createToken("=");
        if (right != null && requireTempVar) {
            HNBlock valDeclContext = null;
            boolean fieldTemp = isField;
            if (dissociatedBlock == null) {
                if (compilerContext.newParent != null) {
                    valDeclContext = compilerContext.newParent;
                } else {
                    valDeclContext = block;
                }
//                vadDeclContext = block;
            } else {
                valDeclContext = dissociatedBlock.get();
            }
            if (valDeclContext instanceof HNBlock) {
                HNBlock bb = (HNBlock) valDeclContext;
                if (bb.getBlocType() == HNBlock.BlocType.METHOD_BODY
                        || bb.getBlocType() == HNBlock.BlocType.LOCAL_BLOC
                        || bb.getBlocType() == HNBlock.BlocType.STATIC_INITIALIZER
                        || bb.getBlocType() == HNBlock.BlocType.INSTANCE_INITIALIZER
                ) {
                    fieldTemp = false;
                }
            }
            String newVarName = compilerContext.base.nextVarName2(null, valDeclContext);
            JToken newVarToken = HNodeUtils.createToken(newVarName);
            JTypeOrLambda rightTypeOrLambda = right.getElement().getTypeOrLambda();
            if (rightTypeOrLambda.isLambda()) {
                rightTypeOrLambda = left.getElement().getTypeOrLambda();
            }
            if (!fieldTemp || dissociatedBlock == null || right == null) {
                HNDeclareIdentifier decl = new HNDeclareIdentifier(
                        (HNDeclareToken) new HNDeclareTokenIdentifier(newVarToken)
                        .setElement(new HNElementLocalVar(newVarName).setEffectiveType(rightTypeOrLambda.getType()))
                        , right,
                        new HNTypeToken(rightTypeOrLambda.getType(), null), assignOp, null, null);
                int modifiers = 0;
                if (fieldTemp) {
                    modifiers |= (isField) ? Modifier.PRIVATE : 0;
                    if (isStatic) {
                        modifiers |= Modifier.STATIC;
                    }
                }
                decl.setModifiers(modifiers);
                valDeclContext.add(decl);
                right = new HNIdentifier(newVarToken)
                        .setElement(
                                isField ? new HNElementField(newVarName).setEffectiveType(rightTypeOrLambda.getType())
                                        : new HNElementLocalVar(newVarName).setEffectiveType(rightTypeOrLambda.getType())
                        );
            } else {
                HNDeclareIdentifier decl = new HNDeclareIdentifier(
                        (HNDeclareToken) new HNDeclareTokenIdentifier(newVarToken)
                        .setElement(new HNElementLocalVar(newVarName).setEffectiveType(rightTypeOrLambda.getType()))
                        , null,
                        new HNTypeToken(rightTypeOrLambda.getType(), null), assignOp, null, null);
                int modifiers = 0;
                if (fieldTemp) {
                    modifiers |= (isField) ? Modifier.PRIVATE : 0;
                    if (isStatic) {
                        modifiers |= Modifier.STATIC;
                    }
                }
                decl.setModifiers(modifiers);
                HNBlock body = valDeclContext;//dissociatedBlock.get();

                body.add(decl);

                HNIdentifier newRight = (HNIdentifier) new HNIdentifier(newVarToken)
                        .setElement(
                                isField ? new HNElementField(newVarName).setEffectiveType(rightTypeOrLambda.getType())
                                        : new HNElementLocalVar(newVarName).setEffectiveType(rightTypeOrLambda.getType())
                        );

                HNAssign hass = new HNAssign((HNode) copyFactory.copy(newRight), assignOp, right, null, null);
                hass.setElement(new HNElementAssign(rightTypeOrLambda.getType()));
                body.add(hass);

                right = newRight;

            }
        }
        if (left instanceof HNTuple) {
            HNTuple t = (HNTuple) left;
            HNode[] items = t.getItems();
            for (int i = 1; i <= items.length; i++) {
                HNode n = (HNode) items[i - 1];
                String tupleMemberName = "_" + i;
                HNIdentifier tupleMember = new HNIdentifier(HNodeUtils.createToken(tupleMemberName));
                tupleMember.setElement(new HNElementField(tupleMemberName));
                _fillBlockAssign(n, new HNOpDot(right, dot, tupleMember, null, null), block, dissociatedBlock, false, isField, isStatic, compilerContext);
            }
        } else if (left instanceof HNIdentifier) {
            HNIdentifier leftId = (HNIdentifier) left;
            JType identifierType = leftId.getElement().getType();
            HNAssign hass = new HNAssign(left, HNodeUtils.createToken("="), right, null, null);

            hass.setElement(new HNElementAssign(identifierType));
            block.add(hass);
        } else if (left instanceof HNDeclareTokenIdentifier) {
            if (dissociatedBlock != null) {
                HNDeclareTokenIdentifier leftId = (HNDeclareTokenIdentifier) left;
                JType identifierType = leftId.getIdentifierType();
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        (HNDeclareTokenIdentifier) left,
                        null,
                        new HNTypeToken(identifierType, null),
                        assignOp,
                        null, null
                );
                if (isStatic) {
                    nhs1.setModifiers(nhs1.getModifiers() | Modifier.STATIC);
                }
                if (isField) {
                    nhs1.setModifiers(HUtils.publifyModifiers(nhs1.getModifiers()));
                }

                block.add(nhs1);
                if (right != null) {
                    HNBlock body = dissociatedBlock.get();
                    HNAssign hass = new HNAssign(new HNIdentifier(leftId.getToken()), assignOp, right, null, null);
                    hass.setElement(new HNElementAssign(identifierType));
                    body.add(hass);
                }
            } else {
                HNDeclareTokenIdentifier leftId = (HNDeclareTokenIdentifier) left;
                JType identifierType = leftId.getIdentifierType();
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        (HNDeclareTokenIdentifier) left,
                        right,
                        new HNTypeToken(identifierType, null),
                        assignOp,
                        null, null
                );
                if (isStatic) {
                    nhs1.setModifiers(nhs1.getModifiers() | Modifier.STATIC);
                }
                if (isField) {
                    nhs1.setModifiers(HUtils.publifyModifiers(nhs1.getModifiers()));
                }

                block.add(nhs1);
            }
        } else if (left instanceof HNDeclareTokenTuple) {
            HNDeclareTokenTuple leftTuple = (HNDeclareTokenTuple) left;
            HNode[] items = leftTuple.getItems();
            for (int i = 1; i <= items.length; i++) {
                HNode n = (HNode) items[i - 1];
                String tupleMemberName = "_" + i;
                HNIdentifier tupleMember = new HNIdentifier(HNodeUtils.createToken(tupleMemberName));
                tupleMember.setElement(new HNElementField(tupleMemberName));
                _fillBlockAssign(n, new HNOpDot(right, dot, tupleMember, null, null), block, dissociatedBlock, false, isField, isStatic, compilerContext);
            }
        } else if (left instanceof HNDeclareTokenList) {
            if (dissociatedBlock != null) {
                HNDeclareTokenList leftId = (HNDeclareTokenList) left;
                JType identifierType = leftId.getIdentifierType();
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        leftId,
                        null,
                        new HNTypeToken(identifierType, null),
                        assignOp,
                        null, null
                );
                if (isStatic) {
                    nhs1.setModifiers(nhs1.getModifiers() | Modifier.STATIC);
                }
                if (isField) {
                    nhs1.setModifiers(HUtils.publifyModifiers(nhs1.getModifiers()));
                }

                block.add(nhs1);
                if (right != null) {
                    HNode first=null;
                    for (HNDeclareTokenIdentifier object : leftId.getItems()) {
                        HNode hnIdentifier = new HNIdentifier(object.getToken())
                                .setElement(
                                        isField ? new HNElementField(object.getName()).setEffectiveType(identifierType)
                                                : new HNElementLocalVar(object.getName()).setEffectiveType(identifierType)
                                );
                        if(first==null || right instanceof HNLiteral) {
                            first=hnIdentifier;
                            _fillBlockAssign(hnIdentifier, right, dissociatedBlock.get(), null, false, isField, isStatic, compilerContext);
                        }else{
                            _fillBlockAssign(hnIdentifier, first, dissociatedBlock.get(), null, false, isField, isStatic, compilerContext);
                        }
                    }
                }
            } else {
                HNDeclareTokenList leftId = (HNDeclareTokenList) left;
                JType identifierType = leftId.getIdentifierType();
                HNDeclareIdentifier nhs1 = new HNDeclareIdentifier(
                        (HNDeclareTokenList) left,
                        right,
                        new HNTypeToken(identifierType, null),
                        assignOp,
                        null, null
                );
                if (isStatic) {
                    nhs1.setModifiers(nhs1.getModifiers() | Modifier.STATIC);
                }
                if (isField) {
                    nhs1.setModifiers(HUtils.publifyModifiers(nhs1.getModifiers()));
                }

                block.add(nhs1);
            }
        } else {
            throw new JShouldNeverHappenException();
        }
    }

    public static class HLJCompilerContext2 {

        HLJCompilerContext base;
        HNBlock newParent;

        public HLJCompilerContext2(HLJCompilerContext base) {
            this.base = base;
        }

        public HLJCompilerContext2(HLJCompilerContext base, HNBlock newParent) {
            this.base = base;
            this.newParent = newParent;
        }

        public HLProject project() {
            return base.project();
        }

        public HLJCompilerContext2 withNewParent(HNBlock newParent) {
            return new HLJCompilerContext2(base, newParent);
        }

    }

}
