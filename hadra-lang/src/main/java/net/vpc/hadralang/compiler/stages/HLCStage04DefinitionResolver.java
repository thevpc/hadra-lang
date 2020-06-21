package net.vpc.hadralang.compiler.stages;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.impl.types.DefaultJType;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.hadralang.compiler.core.elements.*;
import net.vpc.hadralang.compiler.core.invokables.BodyJInvoke;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.index.HLIndexedClass;
import net.vpc.hadralang.compiler.index.HLIndexedConstructor;
import net.vpc.hadralang.compiler.index.HLIndexedField;
import net.vpc.hadralang.compiler.index.HLIndexedMethod;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HTypeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HLCStage04DefinitionResolver extends HLCStageType2 {

    public static final Logger LOG = Logger.getLogger(HLCStage04DefinitionResolver.class.getName());
    private boolean inPreprocessor = false;

    public HLCStage04DefinitionResolver(boolean inPreprocessor) {
        this.inPreprocessor = inPreprocessor;
    }

    public boolean processCompilerStageCurrent(String simpleName, HNode node, HLJCompilerContext compilerContext) {
        switch (node.id()) {
            case H_IDENTIFIER: {
                return onIdentifier((HNIdentifier) node, compilerContext);
            }
            case H_PARS: {
                return onPars((HNPars) node, compilerContext);
            }
            case H_PARS_POSTFIX: {
                return onParsPostfix((HNParsPostfix) node, compilerContext);
            }
            case H_BRACKETS: {
                return onBrackets((HNBrackets) node, compilerContext);
            }
            case H_BRACKETS_POSTFIX: {
                return onBracketsPostfix((HNBracketsPostfix) node, compilerContext);
            }
            case H_OP_DOT: {
                return onOpDot((HNOpDot) node, compilerContext);
            }
            case H_LITERAL: {
                return onLiteral((HNLiteral) node, compilerContext);
            }
            case H_LITERAL_DEFAULT: {
                return onLiteralDefault((HNLiteralDefault) node);
            }
            case H_BLOCK: {
                return onBlock((HNBlock) node, compilerContext);
            }
            case H_DECLARE_TYPE: {
                return onDeclareType((HNDeclareType) node, compilerContext);
            }
            case H_DECLARE_IDENTIFIER: {
                return onDeclareIdentifier((HNDeclareIdentifier) node, compilerContext);
            }
            case H_DECLARE_INVOKABLE: {
                return onDeclareInvokable((HNDeclareInvokable) node, compilerContext);
            }
            case H_TYPE_TOKEN: {
                return onTypeToken((HNTypeToken) node, compilerContext);
            }
            case H_OP_UNARY: {
                return onOpUnaryCall((HNOpUnaryCall) node, compilerContext);
            }
            case H_OP_BINARY: {
                return onOpBinaryCall((HNOpBinaryCall) node, compilerContext);
            }
            case H_ARRAY_NEW: {
                return onArrayNew((HNArrayNew) node, compilerContext);
            }
            case H_ASSIGN: {
                return onAssign((HNAssign) node, compilerContext);
            }
            case H_OBJECT_NEW: {
                return onObjectNew((HNObjectNew) node, compilerContext);
            }
            case H_TUPLE: {
                return onTuple((HNTuple) node, compilerContext);
            }
            case H_THIS: {
                return onThis((HNThis) node, compilerContext);
            }
            case H_SUPER: {
                return onSuper((HNSuper) node, compilerContext);
            }
            case H_STRING_INTEROP: {
                return onStringInterop((HNStringInterop) node, compilerContext);
            }
            case H_OP_COALESCE: {
                return onOpCoalesce((HNOpCoalesce) node, compilerContext);
            }
            case H_LAMBDA_EXPR: {
                return onLambdaExpression((HNLambdaExpression) node, compilerContext);
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
            case H_DOT_THIS: {
                return onDotThis((HNDotThis) node, compilerContext);
            }
            case H_EXTENDS: {
                return onExtends((HNExtends) node, compilerContext);
            }
            case H_META_PACKAGE_GROUP:
            case H_META_PACKAGE_ARTIFACT:
            case H_META_PACKAGE_VERSION:
            case H_META_PACKAGE_ID:
            case H_IMPORT: {
                node.setElement(new HNElementNonExpr());
                return true;
            }
            case H_DECLARE_META_PACKAGE: {
                //wont happen
                throw new JShouldNeverHappenException();
            }

            /////////////////////////////////////////
            case H_META_IMPORT_PACKAGE: {
                return onMetaImportPackage((HNMetaImportPackage) node, compilerContext);
            }

            case H_ARRAY_CALL:
            case X_INVOKABLE_CALL: {
                throw new JShouldNeverHappenException();
            }
        }
        //in stage 1 wont change node instance
        throw new JShouldNeverHappenException("Unsupported node class in " + getClass().getSimpleName() + ": " + node.getClass().getSimpleName());
//        compilerContext.log().error("S---", getClass().getSimpleName() + ": unsupported node class : " + node.getClass().getSimpleName(), node.startToken());
//        return false;
    }

    private boolean onExtends(HNExtends node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
        return true;
    }

    private boolean onDotClass(HNDotClass node, HLJCompilerContext compilerContext) {
        JType tv = node.getTypeRefName().getTypeVal();
        node.setElement(new HNElementExpr(tv == null ? null : HTypeUtils.classOf(tv)));
        return true;
    }

    private boolean onDotThis(HNDotThis node, HLJCompilerContext compilerContext) {
        JType tv = node.getTypeRefName().getTypeVal();
        node.setElement(new HNElementExpr(tv));
        return true;
    }

    private boolean onReturn(HNReturn node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementStatement(compilerContext.types()));
        return true;
    }

    private boolean onSwitchIf(HNSwitch.SwitchIf node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onSwitchIs(HNSwitch.SwitchIs node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr(
                JTypeUtils.forBoolean(compilerContext.types())
        ));
        HNDeclareTokenIdentifier idToken = node.getIdentifierToken();
        if (idToken != null) {
            checkAlreadyDeclaredLocalVar(idToken, node, node, compilerContext);
        }
        return true;
    }

    private boolean onSwitchCase(HNSwitch.SwitchCase node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onSwitch(HNSwitch node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onDeclareTokenTuple(HNDeclareTokenTuple node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
        return true;
    }

    private boolean onDeclareTokenList(HNDeclareTokenList node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
        return true;
    }

    private boolean onContinue(HNContinue node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementStatement(compilerContext.types()));
        return true;
    }

    private boolean onBreak(HNBreak node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementStatement(compilerContext.types()));
        return true;
    }

    private boolean onWhile(HNWhile node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onFor(HNFor node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onIs(HNIs node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr(
                JTypeUtils.forBoolean(compilerContext.types())
        ));
        HNDeclareTokenIdentifier idToken = node.getIdentifierToken();
        if (idToken != null) {
            checkAlreadyDeclaredLocalVar(idToken, node, node, compilerContext);
        }
        return true;
    }

    private boolean onWhenDoBranchNode(HNIf.WhenDoBranchNode node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onIf(HNIf node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onLambdaExpression(HNLambdaExpression node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementLambda());
        return true;
    }

    private boolean onOpCoalesce(HNOpCoalesce node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onStringInterop(HNStringInterop node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr(JTypeUtils.forString(compilerContext.types())));
        return true;
    }

    private boolean onMetaImportPackage(HNMetaImportPackage node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
        return true;
    }

    private boolean onThis(HNThis node, HLJCompilerContext compilerContext) {
        JType thisType = compilerContext.getThisType(node);
        if (thisType == null) {
            compilerContext.log().error("S---", null, "'this' cannot be referenced in this context", node.startToken());
            node.setElement(new HNElementExpr());
        } else {
            node.setElement(new HNElementExpr(thisType));
        }
        return true;
    }

    private boolean onSuper(HNSuper node, HLJCompilerContext compilerContext) {
        JType thisType = compilerContext.getThisType(node);
        if (thisType == null) {
            compilerContext.log().error("S---", null, "'super' cannot be referenced in this context", node.startToken());
            node.setElement(new HNElementExpr());
        } else {
            node.setElement(new HNElementExpr(thisType.getSuperType()));
        }
        return true;
    }

    private boolean onTuple(HNTuple node, HLJCompilerContext compilerContext) {
        node.setUserObject("LHS", isLHS(node));
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onObjectNew(HNObjectNew node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementConstructor(
                node.getObjectTypeName().getTypeVal(),
                null,node.getInits()
        ));
        return true;
    }

    private boolean onArrayNew(HNArrayNew node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr(node.getArrayTypeName().getTypeVal()));
        return false;
    }

    private boolean onOpUnaryCall(HNOpUnaryCall node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onOpBinaryCall(HNOpBinaryCall node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onTypeToken(HNTypeToken node, HLJCompilerContext compilerContext) {
        JType type = null;
        try {
            type = compilerContext.lookupType(node.getTypename());
        } catch (Exception ex) {
            compilerContext.log().error("S000", null, "invalid type :" + node.getTypename() + " : " + ex.toString(), node.startToken());
            return true;
        }
        if (type == null) {
            compilerContext.log().error("S000", null, "cannot resolve type symbol " + node.getTypename(), node.startToken());
        }
        node.setTypeVal(type);
        node.setElement(new HNElementType(type));
        return true;
    }

    private boolean onDeclareType(HNDeclareType node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
//        HNDeclareType typeDec = (HNDeclareType) node.parentNode();
        HNDeclareType typeDec = node;
//        HNDeclareType declaringType = typeDec.getDeclaringType();

        List<HNDeclareIdentifier> mainConstructorArgs = typeDec.getMainConstructorArgs();
        DefaultJType jType = (DefaultJType) compilerContext.getOrCreateType(typeDec);
        boolean indexable = !inPreprocessor && !typeDec.isInternalType();
        String source = HUtils.getSourceName(node);
        if (indexable) {
            HLIndexedClass ii = new HLIndexedClass(jType, source);
            compilerContext.indexer().indexType(ii);
        }
        if (mainConstructorArgs != null) {
            try {
                JConstructor jConstructor = jType.addConstructor(
                        compilerContext.signature(JNameSignature.of(
                                node.getNameToken().sval,
                                mainConstructorArgs.stream()
                                        .map(x -> x.getIdentifierTypeName().getTypename())
                                        .toArray(JTypeName[]::new)
                        )),
                        mainConstructorArgs.stream()
                                .map(HNDeclareIdentifier::getIdentifierName)
                                .toArray(String[]::new),
                        new HNDeclareTypeMainConstructor(node),
                        Modifier.PUBLIC, false
                );
                if (indexable) {
                    compilerContext.indexer().indexConstructor(new HLIndexedConstructor(jConstructor, source));
                }
            } catch (Exception ex) {
                compilerContext.log().error("X000", null, ex.getMessage(), typeDec.startToken());
            }
            //wont declare fields as they are declared in onDeclareIdentifier!
//            for (HNDeclareIdentifier identifier : mainConstructorArgs) {
//                try {
//                    if (indexable) {
//                        JField jField = jType.addField(identifier.getIdentifierName(),
//                                identifier.getIdentifierTypeName().getTypeVal(),
//                                identifier.getModifiers(),
//                                false
//                        );
//                        compilerContext.indexer().indexField(new HLIndexedField(jField, source));
//                    }
//                } catch (Exception ex) {
//                    compilerContext.log().error("X000", null, ex.getMessage(), identifier.startToken());
//                }
//            }
        }
        JNode b = typeDec.getBody();
        if (b != null) {
            if (b instanceof HNBlock) {
                HNBlock bl = (HNBlock) b;
                //already processed
            } else {
                compilerContext.log().error("X000", null, "expected class body", b.startToken());
            }
        } else {
            if (!Modifier.isAbstract(typeDec.getModifiers())) {
                compilerContext.log().error("X000", null, "expected class body", node.startToken());
            }
        }
        return true;
    }

    private boolean onDeclareTokenIdentifier(HNDeclareTokenIdentifier node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementLocalVar(node.getName(), node, node.getToken()));
//        HNode p = HNodeUtils.declaringStatement(node);
//        if(p instanceof HNDeclareTokenIdentifier){
//            HNode a = compilerContext.lookupEnclosingDeclarationOrMetaPackage(p);
//
//        }
        return true;
    }

    private boolean onDeclareIdentifier(HNDeclareIdentifier identifier, HLJCompilerContext compilerContext) {
        HNode idec = compilerContext.lookupEnclosingDeclarationOrMetaPackage(identifier);
        identifier.setElement(new HNElementStatement(compilerContext.types()));
        String source = HUtils.getSourceName(identifier);
        if (idec instanceof HNDeclareType) {
            identifier.setSyntacticType(HNDeclareIdentifier.SyntacticType.FIELD);
            List<JField> fields = new ArrayList<>();
            boolean indexable = isIndexableType((HNDeclareType) idec, compilerContext);
            for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(identifier.getIdentifierToken())) {
                HNTypeToken identifierTypeName = identifier.getIdentifierTypeName();
                JType typeVal = identifierTypeName == null ? null : identifierTypeName.getTypeVal();
                JField jField = null;
                try {
                    jField = ((DefaultJType) compilerContext.getOrCreateType((HNDeclareType) idec))
                            .addField(identifierToken.getName(),
                                    typeVal,
                                    identifier.getModifiers(),
                                    false
                            );
                    compilerContext.indexer().indexField(new HLIndexedField(jField, source));
                } catch (Exception ex) {
                    compilerContext.log().error("X000", null, ex.toString(), identifierToken.getToken());
                }
                if (jField != null) {
                    fields.add(jField);
                    if (typeVal == null) {
                        List<JField> noTypeFields = HLCStageUtils.getNoTypeFields(compilerContext);
                        noTypeFields.add(jField);
                    }
                    if (indexable) {
                        HLIndexedField ii = new HLIndexedField(jField, HUtils.getSourceName(identifier));
                        compilerContext.indexer().indexField(ii);
                    }
                    identifierToken.setElement(new HNElementField(jField));
                }
            }
//            identifier.setFields(fields);
        } else {
            for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(identifier.getIdentifierToken())) {
                checkAlreadyDeclaredLocalVar(identifierToken, identifier.parentNode(), idec, compilerContext);
            }
            identifier.setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL);
        }
        return true;
    }

    protected void checkAlreadyDeclaredLocalVar(HNDeclareTokenIdentifier identifierToken, JNode from, HNode idec, HLJCompilerContext compilerContext) {
        if (idec == null) {
            idec = compilerContext.lookupEnclosingDeclarationOrMetaPackage(from);
        }
        JToken location = identifierToken.getToken();
        HNElementLocalVar element = new HNElementLocalVar(identifierToken.getName(), identifierToken, location);
        identifierToken.setElement(element);
        compilerContext.markLocalDeclared(element, idec, location);
        HNElementLocalVar[] t
                = Arrays.stream(
                        compilerContext.lookupLocalVarDeclarations(identifierToken.getName(), location,
                                from, null)
                ).filter(x -> x.getDeclaration() != identifierToken)
                        .toArray(HNElementLocalVar[]::new);
        if (t.length > 0) {
            compilerContext.log().error("X000", null, "multiple local variable declaration : " + identifierToken.getName(), location);
        }
    }

    private boolean onDeclareInvokable(HNDeclareInvokable method, HLJCompilerContext compilerContext) {
        HNDeclareType tn = compilerContext.lookupEnclosingDeclareTypeImmediate(method);
//        DefaultJType jType = (DefaultJType) compilerContext.lookupEnclosingType(method);
        if (tn != null) {
            boolean indexable = isIndexableType(tn, compilerContext);
            try {
                DefaultJType jType = ((DefaultJType) compilerContext.getOrCreateType(tn));
                if (method.isConstructor()) {
                    JConstructor jConstructor = jType.addConstructor(
                            compilerContext.signature(JNameSignature.of(
                                    method.getNameToken().sval,
                                    method.getArguments().stream()
                                            .map(x -> x.getIdentifierTypeName().getTypename())
                                            .toArray(JTypeName[]::new)
                            )), method.getArguments().stream()
                            .map(HNDeclareIdentifier::getIdentifierName)
                            .toArray(String[]::new), new BodyJInvoke(method),
                            method.getModifiers(), false
                    );
                    method.setInvokable(jConstructor);
                    if (indexable) {
                        HLIndexedConstructor ii = new HLIndexedConstructor(jConstructor, HUtils.getSourceName(method));
                        compilerContext.indexer().indexConstructor(ii);
                    }
                } else {
                    HNTypeToken returnTypeName = method.getReturnTypeName();
                    JType returnType = returnTypeName == null ? null : returnTypeName.getTypeVal();
                    JMethod jMethod = jType.addMethod(
                            compilerContext.signature(JSignature.of(
                                    method.getNameToken().sval,
                                    method.getArguments().stream()
                                            .map(x -> x.getIdentifierTypeName().getTypeVal())
                                            .toArray(JType[]::new)
                            ).nameSignature()),
                            method.getArguments().stream()
                                    .map(HNDeclareIdentifier::getIdentifierName)
                                    .toArray(String[]::new),
                            returnType,
                            new BodyJInvoke(method),
                            method.getModifiers(), false
                    );
                    method.setInvokable(jMethod);
                    if (returnType == null) {
                        List<JMethod> noTypeMethods = HLCStageUtils.getNoTypeMethods(compilerContext);
                        noTypeMethods.add(jMethod);
                    }else{
                        method.setEffectiveReturnType(returnType);
                    }
                    if (indexable) {
                        HLIndexedMethod ii = new HLIndexedMethod(jMethod, HUtils.getSourceName(method));
                        compilerContext.indexer().indexMethod(ii);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.INFO, "unexpected error : " + ex.toString(), ex);
                compilerContext.log().error("X000", null, "unexpected error : " + ex.toString(), method.startToken());
            }
        } else {
            compilerContext.log().error("X000", null, "you cannot create function inside a local bloc (for the moment)", method.startToken());
        }
        method.setElement(new HNElementStatement(compilerContext.types()));
        return true;
    }

    private boolean isIndexableType(HNDeclareType tn, HLJCompilerContext compilerContext) {
        if (tn == null) {
            return false;
        }
        return !inPreprocessor
                //                && !(tn instanceof HNDeclareTypeMetaPackage)
                && !compilerContext.getOrCreateType(tn).getName().matches(".*[$][0-9]+.*");
    }

    private boolean isIndexableType(JType jType) {
        return !inPreprocessor && !jType.getName().matches(".*[$][0-9]+.*");
    }

    private boolean onBlock(HNBlock node, HLJCompilerContext compilerContext) {
        switch (node.getBlocType()) {
            case IMPORT_BLOC: {
                //processed in enclosing node
                break;
            }
            case CLASS_BODY: {
                //processed in HNDeclareType
                break;
            }
            case GLOBAL_BODY: {
                //TODO
                break;
            }
            case LOCAL_BLOC:
            case METHOD_BODY: {
                //processed in HNDeclareType
                break;
            }
            case PACKAGE_BODY: {
                //ignored
                break;
            }
            case UNKNOWN: {
                //ignored
            }
        }
        node.setElement(new HNElementStatement(compilerContext.types()));
        return true;
    }

    protected boolean onLiteralDefault(HNLiteralDefault node) {
        HNLiteralDefault n = node;
        HNTypeToken typeNameToken = n.getTypeNameToken();
        JType typeVal = typeNameToken.getTypeVal();
        n.setElement(new HNElementExpr(typeVal));
        return true;
    }

    private boolean onParsPostfix(HNParsPostfix node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onBracketsPostfix(HNBracketsPostfix node, HLJCompilerContext compilerContext) {
        List<HNode> array = node.getRight();
        if (array.size() == 0) {
            compilerContext.log().error("S044", null, "empty  brackets. missing indices.", node.startToken());
        }
        node.setUserObject("LHS", isLHS(node));
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean onPars(HNPars node, HLJCompilerContext compilerContext) {
        JNode[] items = node.getItems();
        if (items.length == 1) {
            HNode i0 = (HNode) items[0];
            node.setElement(i0.getElement());
        } else {
            node.setElement(new HNElementNonExpr());
            compilerContext.log().error("S000", null, "invalid expression", node.endToken());
        }
        return true;
    }

    private boolean onBrackets(HNBrackets node, HLJCompilerContext compilerContext) {
        if (node.fullChildInfo().equals("HNBracketsPostfix:right")) {
            node.setElement(new HNElementNonExpr());
        } else {
            //this is an array initialization
            node.setElement(new HNElementExpr());
        }
        return true;
    }
//
//    private boolean onArrayGet(HNArrayCall node, HLJCompilerContext compilerContext) {
//        JNode[] array = node.getIndexNodes();
//        if (array.length == 0) {
//            compilerContext.log().error("S044", "empty  brackets. missing indices.", node.startToken());
//        }
//        JNode instance = node.getArrayInstanceNode();
//        node.setElement(new HNElementExpr());
//        return true;
//    }

    private boolean onAssign_isValidLeft(HNode left){
        if (left instanceof HNTuple || left instanceof HNBracketsPostfix || left instanceof HNIdentifier) {
            return true;
        }
        if(left instanceof HNOpDot){
            return onAssign_isValidLeft(((HNOpDot)left).getRight());
        }
        return false;
    }
    private boolean onAssign(HNAssign node, HLJCompilerContext compilerContext) {
        HNode left = node.getLeft();
        HNode right = node.getLeft();
        node.setElement(new HNElementAssign());
        switch (left.getElement().getKind()) {
            case LOCAL_VAR:
            case FIELD: {
                //ok;
                break;
            }
            case EXPR: {
                if (!onAssign_isValidLeft(left)) {
                    compilerContext.log().error("S052", null, "invalid assignment of " + left.getClass().getSimpleName(), right.startToken());
                    return false;
                }
                break;
            }
            default: {
                compilerContext.log().error("S052", null, "invalid assignment of " + left.getClass().getSimpleName(), right.startToken());
                return false;
            }
        }
        return true;
    }

    protected boolean onIdentifier(HNIdentifier node, HLJCompilerContext compilerContext) {
        //should check if this is a LHS
        node.setUserObject("LHS", isLHS(node));
        node.setElement(new HNElementExpr());
        return true;
    }

    private boolean isLHS(HNode node) {
        return node.fullChildInfo().equals("HNAssign:left")
                || node.parentNode() instanceof HNTuple && isLHS((HNode) node.parentNode());
    }

    protected boolean onLiteral(HNLiteral node, HLJCompilerContext compilerContext) {
        HNLiteral n = node;
        if (n.getElement() == null) {
            if (n.getValue() == null) {
                JType nullType = JTypeUtils.forNull(compilerContext.types());
                n.setElement(new HNElementExpr(nullType));
            } else {
                //Literal types should be supported at early stages
                JType p = compilerContext.types().typeOf(n.getValue());
                //prefer primitive types for literals!
                JType p0 = p.toPrimitive();
                n.setElement(new HNElementExpr(p0 != null ? p0 : p));
            }
        }
        return true;
    }

    protected boolean onOpDot(HNOpDot n, HLJCompilerContext compilerContext) {
        HNode left = (HNode) n.getLeft();
        HNode right = (HNode) n.getRight();
        n.setElement(right.getElement());
        return true;
    }

    public static class HNDeclareTypeMainConstructor implements JInvoke {

        private HNDeclareType node;

        public HNDeclareTypeMainConstructor(HNDeclareType node) {
            this.node = node;
        }

        @Override
        public Object invoke(JInvokeContext context) {
            throw new JFixMeLaterException();
        }

    }

    protected boolean processCompilerStageCurrentCheck(HNode node, HLJCompilerContext compilerContext) {
        String simpleName = node.getClass().getSimpleName();
        if (node.getElement() == null) {
            compilerContext.log().error("X000", null, "node.getElement()==null for " + simpleName, node.startToken());
        }
        return true;
    }

    public boolean processCompilerStage(JCompilerContext compilerContextBase) {
        HNode node = (HNode) compilerContextBase.node();
        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
        if (node.id() == HNNodeId.H_DECLARE_META_PACKAGE && !inPreprocessor) {
            //do not go further
            return true;
        }
        processAllNextCompilerStage(compilerContextBase);
        String simpleName = node.getClass().getSimpleName();
        boolean succeed = processCompilerStageCurrent(simpleName, (HNode) node, compilerContext);
//        System.out.println("HLCStage04DefinitionResolver.processCompilerStage " + compilerContext.path().getPathString()
//                + " : " + ((HNode) node).getElement()
//                + " : " + JToken.escapeString(compilerContext.node().toString()));
        return succeed;
    }

}
