package net.hl.compiler.stages;

import net.hl.compiler.ast.*;
import net.hl.compiler.core.elements.*;
import net.hl.compiler.core.invokables.BodyJInvoke;
import net.hl.compiler.core.invokables.HLJCompilerContext;
import net.hl.compiler.core.invokables.JTypeFromHIndex;
import net.hl.compiler.core.types.JPrimitiveModifierAnnotationInstance;
import net.hl.compiler.index.HIndexedClass;
import net.hl.compiler.index.HIndexedConstructor;
import net.hl.compiler.index.HIndexedField;
import net.hl.compiler.index.HIndexedMethod;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HSharedUtils;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.DefaultTypeName;
import net.thevpc.jeep.impl.functions.JNameSignature;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.DefaultJRawMethod;
import net.thevpc.jeep.impl.types.DefaultJType;
import net.thevpc.jeep.util.JTypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;

public class HStage04DefinitionResolver extends HStageType2 {

    public static final Logger LOG = Logger.getLogger(HStage04DefinitionResolver.class.getName());
    private boolean inPreprocessor = false;

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.RESOLVED_AST};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return options.containsAnyTask(HTask.RESOLVED_AST, HTask.COMPILE, HTask.RUN);
    }

    public HStage04DefinitionResolver(boolean inPreprocessor) {
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
            case H_CATCH: {
                node.setElement(new HNElementExpr());
                return true;
            }
            case H_TRY_CATCH: {
                node.setElement(new HNElementExpr());
                return true;
            }
            case H_CAST: {
                node.setElement(new HNElementExpr());
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
        node.setElement(new HNElementExpr(tv == null ? null : JTypeUtils.classOf(tv)));
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
            compilerContext.getLog().jerror("S---", null, node.getStartToken(), "'this' cannot be referenced in this context");
            node.setElement(new HNElementExpr());
        } else {
            node.setElement(new HNElementExpr(thisType));
        }
        return true;
    }

    private boolean onSuper(HNSuper node, HLJCompilerContext compilerContext) {
        JType thisType = compilerContext.getThisType(node);
        if (thisType == null) {
            compilerContext.getLog().jerror("S---", null, node.getStartToken(), "'super' cannot be referenced in this context");
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
                null, node.getInits()
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
            compilerContext.getLog().jerror("S000", null, node.getStartToken(), "invalid type :" + node.getTypename() + " : " + ex.toString());
            return true;
        }
        if (type == null) {
            if (node.getChildInfo().getName().endsWith("exceptionTypes")
                    && !node.getTypename().name().endsWith("Exception")) {
                //this is an exception in the catch clause
                JTypeName typename = node.getTypename();
                JTypeName typename2 = new DefaultTypeName(typename.name() + "Exception", typename.vars(), typename.arrayDimension(), typename.isVarArg());
                try {
                    type = compilerContext.lookupType(typename2);
                } catch (Exception ex) {
                    compilerContext.getLog().jerror("S000", null, node.getStartToken(), "invalid type :" + node.getTypename() + " : " + ex.toString());
                    return true;
                }
            }
        } else if (node.getChildInfo().getName().endsWith("exceptionTypes")
                && !node.getTypename().name().endsWith("Exception")
                && !JTypeUtils.forThrowable(compilerContext.types()).isAssignableFrom(type)) {

            //this is an exception in the catch clause
            JTypeName typename = node.getTypename();
            JTypeName typename2 = new DefaultTypeName(typename.name() + "Exception", typename.vars(), typename.arrayDimension(), typename.isVarArg());
            try {
                JType type1 = compilerContext.lookupType(typename2);
                if (type1 != null) {
                    type = type1;
                }
            } catch (Exception ex) {
                //
            }
        }
        if (type == null) {
            compilerContext.getLog().jerror("S000", null, node.getStartToken(), "cannot resolve type symbol " + node.getTypename());
        }
        node.setTypeVal(type);
        node.setElement(new HNElementType(type, compilerContext.types()));
        return true;
    }

    private boolean onDeclareType(HNDeclareType node, HLJCompilerContext compilerContext) {
        node.setElement(new HNElementNonExpr());
//        HNDeclareType typeDec = (HNDeclareType) node.parentNode();
        HNDeclareType typeDec = node;
//        HNDeclareType declaringType = typeDec.getDeclaringType();

        List<HNDeclareIdentifier> mainConstructorArgs = typeDec.getMainConstructorArgs();
        JType jType = compilerContext.getOrCreateType(typeDec);
        boolean indexable = !inPreprocessor && !typeDec.isInternalType();
        String source = HSharedUtils.getSourceName(node);
        if (indexable) {
            HIndexedClass ii = new HIndexedClass(jType, source);
            compilerContext.indexer().indexType(ii);
        }
        if (mainConstructorArgs != null) {
            try {
                JConstructor jConstructor = ((DefaultJType)jType).addConstructor(
                        compilerContext.signature(JNameSignature.of(
                                node.getNameToken().sval,
                                mainConstructorArgs.stream()
                                        .map(x -> x.getIdentifierTypeNode().getTypename())
                                        .toArray(JTypeName[]::new)
                        )),
                        mainConstructorArgs.stream()
                                .map(HNDeclareIdentifier::getIdentifierName)
                                .toArray(String[]::new),
                        new HNDeclareTypeMainConstructor(node),
                        new JModifier[0], new JAnnotationInstance[]{
                            JPrimitiveModifierAnnotationInstance.SPECIAL_DEFAULT_CONSTRUCTOR
                        }, false
                );
                if (indexable) {
                    compilerContext.indexer().indexConstructor(new HIndexedConstructor(jConstructor, source));
                }
            } catch (Exception ex) {
                compilerContext.getLog().jerror("X000", null, typeDec.getStartToken(), ex.getMessage());
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
                compilerContext.getLog().jerror("X000", null, b.getStartToken(), "expected class body");
            }
        } else {
            if (!HNAnnotationList.isAbstract(typeDec.getAnnotations())) {
                compilerContext.getLog().jerror("X000", null, node.getStartToken(), "expected class body");
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
        String source = HSharedUtils.getSourceName(identifier);
        if (idec instanceof HNDeclareType) {
            identifier.setSyntacticType(HNDeclareIdentifier.SyntacticType.FIELD);
            List<JField> fields = new ArrayList<>();
            boolean indexable = isIndexableType((HNDeclareType) idec, compilerContext);
            for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(identifier.getIdentifierToken())) {
                HNTypeToken identifierTypeName = identifier.getIdentifierTypeNode();
                JType typeVal = identifierTypeName == null ? null : identifierTypeName.getTypeVal();
                JField jField = null;
                try {
                    jField = ((JTypeFromHIndex) compilerContext.getOrCreateType((HNDeclareType) idec))
                            .addField(identifierToken.getName(),
                                    typeVal,
                                    new JModifier[0],
                                    HNodeUtils.toAnnotations(identifierToken.getAnnotations()),
                                    false
                            );
                    compilerContext.indexer().indexField(new HIndexedField(jField, source));
                } catch (Exception ex) {
                    compilerContext.getLog().jerror("X000", null, identifierToken.getToken(), ex.toString());
                }
                if (jField != null) {
                    fields.add(jField);
                    if (typeVal == null) {
                        List<JField> noTypeFields = HStageUtils.getNoTypeFields(compilerContext);
                        noTypeFields.add(jField);
                    }
                    if (indexable) {
                        HIndexedField ii = new HIndexedField(jField, HSharedUtils.getSourceName(identifier));
                        compilerContext.indexer().indexField(ii);
                    }
                    identifierToken.setElement(new HNElementField(jField));
                }
            }
//            identifier.setFields(fields);
        } else {
            for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(identifier.getIdentifierToken())) {
                checkAlreadyDeclaredLocalVar(identifierToken, identifier.getParentNode(), idec, compilerContext);
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
            compilerContext.getLog().jerror("X000", null, location, "multiple local variable declaration : " + identifierToken.getName());
        }
    }

    private boolean onDeclareInvokable(HNDeclareInvokable method, HLJCompilerContext compilerContext) {
        HNDeclareType tn = compilerContext.lookupEnclosingDeclareTypeImmediate(method);
//        DefaultJType jType = (DefaultJType) compilerContext.lookupEnclosingType(method);
        if (tn != null) {
            boolean indexable = isIndexableType(tn, compilerContext);
            try {
                JMutableRawType jType = compilerContext.getOrCreateType(tn);
                if (method.isConstructor()) {
                    JConstructor jConstructor = ((DefaultJType)jType).addConstructor(
                            compilerContext.signature(JNameSignature.of(
                                    method.getNameToken().sval,
                                    method.getArguments().stream()
                                            .map(x -> x.getIdentifierTypeNode().getTypename())
                                            .toArray(JTypeName[]::new)
                            )), method.getArguments().stream()
                            .map(HNDeclareIdentifier::getIdentifierName)
                            .toArray(String[]::new), new BodyJInvoke(method),
                            new JModifier[0],
                            HNodeUtils.toAnnotations(method.getAnnotations()),
                            false
                    );
                    method.setInvokable(jConstructor);
                    if (indexable) {
                        HIndexedConstructor ii = new HIndexedConstructor(jConstructor, HSharedUtils.getSourceName(method));
                        compilerContext.indexer().indexConstructor(ii);
                    }
                } else {
                    HNTypeToken returnTypeName = method.getReturnTypeName();
                    JType returnType = returnTypeName == null ? null : returnTypeName.getTypeVal();
                    JMethod jMethod =  ((DefaultJType)jType).addMethod(
                            compilerContext.signature(JSignature.of(
                                    method.getNameToken().sval,
                                    method.getArguments().stream()
                                            .map(x -> x.getIdentifierTypeNode().getTypeVal())
                                            .toArray(JType[]::new)
                            ).nameSignature()),
                            method.getArguments().stream()
                                    .map(HNDeclareIdentifier::getIdentifierName)
                                    .toArray(String[]::new),
                            returnType,
                            new BodyJInvoke(method),
                            new JModifier[0],
                            HNodeUtils.toAnnotations(method.getAnnotations()),
                            false
                    );
                    if(jMethod instanceof DefaultJRawMethod) {
                        ((DefaultJRawMethod)jMethod).setSourceName(HSharedUtils.getSourceName(method));
                    }
                    method.setInvokable(jMethod);
                    if (returnType == null) {
                        List<JMethod> noTypeMethods = HStageUtils.getNoTypeMethods(compilerContext);
                        noTypeMethods.add(jMethod);
                    } else {
                        method.setEffectiveReturnType(returnType);
                    }
                    if (indexable) {
                        HIndexedMethod ii = new HIndexedMethod(jMethod, HSharedUtils.getSourceName(method));
                        compilerContext.indexer().indexMethod(ii);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.FINE, "unexpected error : " + ex.toString(), ex);
                compilerContext.getLog().jerror("X000", null, method.getStartToken(), "unexpected error : " + ex.toString());
                ex.printStackTrace();
            }
        } else {
            compilerContext.getLog().jerror("X000", null, method.getStartToken(), "you cannot create function inside a local bloc (for the moment)");
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
            compilerContext.getLog().jerror("S044", null, node.getStartToken(), "empty  brackets. missing indices.");
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
            compilerContext.getLog().jerror("S000", null, node.getEndToken(), "invalid expression");
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

    private boolean onAssign_isValidLeft(HNode left) {
        if (left instanceof HNTuple || left instanceof HNBracketsPostfix || left instanceof HNIdentifier) {
            return true;
        }
        if (left instanceof HNOpDot) {
            return onAssign_isValidLeft(((HNOpDot) left).getRight());
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
                    compilerContext.getLog().jerror("S052", null, right.getStartToken(), "invalid assignment of " + left.getClass().getSimpleName());
                    return false;
                }
                break;
            }
            default: {
                compilerContext.getLog().jerror("S052", null, right.getStartToken(), "invalid assignment of " + left.getClass().getSimpleName());
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
                || node.getParentNode() instanceof HNTuple && isLHS((HNode) node.getParentNode());
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

    public boolean processCompilerStage(JCompilerContext compilerContextBase) {
        HNode node = (HNode) compilerContextBase.getNode();
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

    protected boolean processCompilerStageCurrentCheck(HNode node, HLJCompilerContext compilerContext) {
        String simpleName = node.getClass().getSimpleName();
        if (node.getElement() == null) {
            compilerContext.getLog().jerror("X000", null, node.getStartToken(), "node.getElement()==null for " + simpleName);
        }
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

}
