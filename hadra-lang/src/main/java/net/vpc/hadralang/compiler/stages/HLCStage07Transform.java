//package net.vpc.hadralang.compiler.stages;
//
//import net.vpc.common.jeep.JMethod;
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.core.nodes.AbstractJNode;
//import net.vpc.common.jeep.core.types.DefaultTypeName;
//import net.vpc.common.jeep.impl.functions.JFunctionLocal;
//import net.vpc.common.jeep.impl.functions.JNameSignature;
//import net.vpc.common.jeep.impl.functions.JSignature;
//import net.vpc.common.jeep.impl.tokens.DefaultJTokenizerReader;
//import net.vpc.common.jeep.impl.tokens.DollarVarPattern;
//import net.vpc.common.jeep.impl.tokens.JTokenizerImpl;
//import net.vpc.common.jeep.impl.types.DefaultJType;
//import net.vpc.common.jeep.util.*;
//import net.vpc.hadralang.compiler.core.*;
//import net.vpc.hadralang.compiler.core.invokables.*;
//import net.vpc.hadralang.compiler.parser.ast.*;
//import net.vpc.hadralang.compiler.utils.HTypeUtils;
//import net.vpc.hadralang.stdlib.*;
//import net.vpc.hadralang.stdlib.ext.HHelpers;
//import net.vpc.hadralang.compiler.utils.HLExtensionNames;
//import net.vpc.hadralang.compiler.utils.HPartitionHelper;
//import net.vpc.hadralang.compiler.utils.HUtils;
//
//import java.io.StringReader;
//import java.lang.reflect.Modifier;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static net.vpc.hadralang.compiler.parser.ast.HNode.*;
//
//public class HLCStage07Transform extends HLCStageType1 {
//    public static ElementTypeAndConstraint onDeclareIdentifier_detectElementTypeAndConstraint(JType valType, char assignOperator, JTypes types) {
//        InitValueConstraint valCstr = null;
//        if (valType != null) {
//            if (assignOperator == '=') {
//                return new ElementTypeAndConstraint(valType, null);
//            } else if (assignOperator == ':') {
//                if (valType.isArray()) {
//                    JTypeArray ta = (JTypeArray) valType;
//                    valType = (ta.componentType());
//                    valCstr = InitValueConstraint.ITERABLE;
//                } else if (types.forName("java.lang.CharSequence").isAssignableFrom(valType)) {
//                    valType = JTypeUtils.forChar(types);
//                    valCstr = InitValueConstraint.ITERABLE;
//                } else if (types.forName("net.vpc.hadralang.stdlib.IntRange").isAssignableFrom(valType)) {
//                    valType = JTypeUtils.forInt(types);
//                    valCstr = InitValueConstraint.ITERABLE;
//                } else if (types.forName("java.util.Iterable").isAssignableFrom(valType)) {
//                    if (types.forName("java.util.Iterable").equals(valType.rawType())) {
//                        JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
//                        if (a.length == 0) {
//                            valType = (JTypeUtils.forObject(types));
//                        } else {
//                            valType = (JType) a[0];
//                        }
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                    valCstr = InitValueConstraint.ITERABLE;
//                } else if (types.forName("java.util.Iterator").isAssignableFrom(valType)) {
//                    if (types.forName("java.util.Iterator").equals(valType.rawType())) {
//                        JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
//                        if (a.length == 0) {
//                            valType = (JTypeUtils.forObject(types));
//                        } else {
//                            valType = (JType) a[0];
//                        }
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                    valCstr = InitValueConstraint.ITERATOR;
//                } else if (types.forName("java.util.stream.BaseStream").isAssignableFrom(valType)) {
//                    if (types.forName("java.util.stream.Stream").isAssignableFrom(valType)) {
//                        if (types.forName("java.util.stream.Stream").equals(valType.rawType())) {
//                            JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
//                            if (a.length == 0) {
//                                valType = (JTypeUtils.forObject(types));
//                            } else {
//                                valType = (JType) a[0];
//                            }
//                        } else {
//                            throw new JFixMeLaterException();
//                        }
//                    } else if (types.forName("java.util.stream.IntStream").isAssignableFrom(valType)) {
//                        valType = JTypeUtils.forInt(types);
//                    } else if (types.forName("java.util.stream.LongStream").isAssignableFrom(valType)) {
//                        valType = JTypeUtils.forLong(types);
//                    } else if (types.forName("java.util.stream.DoubleStream").isAssignableFrom(valType)) {
//                        valType = JTypeUtils.forDouble(types);
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                    valCstr = InitValueConstraint.ITERATOR;
//                }
//            }
//            if (valType == null) {
//                return null;
//            }
//            return new ElementTypeAndConstraint(valType, valCstr);
//        }
//        return null;
//    }
//
//    public static HNInvokerCall createFunctionCall(JToken token, JInvokable f, JNode... nargs) {
//        JToken end=token;
//        if(nargs.length==0){
//            end=nargs[nargs.length-1].endToken();
//        }
//        HNInvokerCall jnf = new HNInvokerCall(JTokenUtils.createWordToken(f.name()), nargs,token,end);
//        jnf.setImpl(HUtils.createJInvokablePrefilled(f, nargs));
//        jnf.setType(f.returnType());
//        return jnf;
//    }
//
//    public static HNInvokerCall createFunctionCall2(JToken token, JInvokable f, JEvaluable... nargs) {
//        HNInvokerCall jnf = new HNInvokerCall(JTokenUtils.createWordToken(f.name()), new JNode[0], token,token);
//        jnf.setImpl(new JInvokablePrefilled(f, nargs));
//        jnf.setType(f.returnType());
//        return jnf;
//    }
//
//    public void visit(JNodeVisitor visitor, HLProject project) {
//        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
//            JNode root = compilationUnit.getAst();
//            root.visit(visitor);
//        }
//        project.metaPackageType().visit(visitor);
//    }
//    public void processProject(HLProject project, HLOptions options){
//        for (JCompilationUnit cu : project.getCompilationUnits()) {
//            ((AbstractJNode) cu.getAst()).parentNode(project.metaPackageType());
//        }
//        int maxPasses = 5;
//        int iteration = 1;
//        while (true) {
//            processProjectPass(iteration, HNode.STAGE_1_DECLARATIONS,project);
//            processProjectPass(iteration, HNode.STAGE_2_WIRE_TYPES,project);
//            processProjectPass(iteration, HNode.STAGE_3_WIRE_CALLS,project);
//            NoTypeErrorCountVisitor visitor = new NoTypeErrorCountVisitor(false, null);
//            visit(visitor,project);
//            if (visitor.errors == 0) {
//                break;
//            }
//            maxPasses--;
//            iteration++;
//            if (maxPasses <= 0) {
//                processProjectPass(iteration, HNode.STAGE_1_DECLARATIONS,project);
//                processProjectPass(iteration, HNode.STAGE_2_WIRE_TYPES,project);
//                processProjectPass(iteration, HNode.STAGE_3_WIRE_CALLS,project);
//                NoTypeErrorCountVisitor visitor2 = new NoTypeErrorCountVisitor(false,
//                        project.log().isSuccessful() ? project.log() : null
//                );
//                visit(visitor2,project);
//                processProjectPass(iteration, HNode.STAGE_1_DECLARATIONS,project);
//                processProjectPass(iteration, HNode.STAGE_2_WIRE_TYPES,project);
//                processProjectPass(iteration, HNode.STAGE_3_WIRE_CALLS,project);
//                break;
//            }
//        }
//        if (project.isSuccessful()) {
//            HNDeclareInvokable mainMethod = project.metaPackageType().getMainMethod();
//            if (mainMethod != null) {
//                HNDeclareInvokable runModuleMethod1 = project.metaPackageType().getRunModuleMethod();
//                if (runModuleMethod1 != null) {
//                    HNBlock body = (HNBlock) mainMethod.getBody();
//                    body.getStatements().add(0,
//                            new HNMethodCall(
//                                    (JMethod) runModuleMethod1.getInvokable(), new JNode[0], null
//                                    , body.startToken()
//                                    , body.endToken()
//                            )
//                    );
//                    mainMethod.buildInvokable();
//                }
//            }
//        }
//    }
//
//    private void processProjectPass(int iteration, int stageNumber, HLProject project) {
//        HLJCompilerContext cc = project.newCompilerContext();
//        JCompilerContext compilerContext = cc.nextNode(cc.metaPackageType())
//                .stage(stageNumber)
//                .iteration(iteration);
//        processCompilerStage(compilerContext);
//        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
//            compilerContext = cc.nextNode(compilationUnit.getAst())
//                    .stage(stageNumber);
//            JNode newNode = processCompilerStage(compilerContext);
//            compilationUnit.setAst(newNode);
//        }
//    }
//
//    public JNode processCompilerStage(JCompilerContext compilerContextBase) {
//        JNode node = compilerContextBase.node();
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        processAllNextCompilerStage(compilerContextBase);
//        String simpleName = node.getClass().getSimpleName();
//        try {
//            HNode n = (HNode) processCompilerStageCurrent((HNode) node, compilerContext);
//            System.out.println("HLCStage07Transform.processCompilerStage " + compilerContext.path().getPathString()
//                    + " : " + n.getElement()
//                    + " : " + n.getType()
//                    + " : " + JToken.escapeString(compilerContext.node().toString()));
//            return n;
//        } catch (RuntimeException ex) {
//            throw ex;
//        }
//    }
//
//    public JNode processCompilerStageCurrent(HNode anode, HLJCompilerContext compilerContext) {
//        switch (anode.id()) {
//            case H_DOT_OPERATOR: {
//                return onDotOperator(compilerContext);
//            }
//            case H_OP_COALESCE: {
//                return onOpCoalesce(compilerContext);
//            }
//            case H_ARRAY_CALL: {
//                return onArrayGet(compilerContext);
//            }
//            case H_ARRAY_NEW: {
//                return onArrayNew(compilerContext);
//            }
//            case H_ASSIGN: {
//                return onAssign(compilerContext);
//            }
//            case H_BLOCK: {
//                return onBlock(anode, compilerContext);
//            }
//            case H_BRACES: {
//                return onBraces(compilerContext);
//            }
//            case H_BRACKETS: {
//                return onBrackets(compilerContext);
//            }
//            case H_BRACKETS_POSTFIX: {
//                return onBracketsPostfix(compilerContext);
//            }
//            case H_BRACKETS_POSTFIX_LAST: {
//                return onBracketsPostfixLastIndex(compilerContext);
//            }
//            case H_BREAK:
//            case H_CONTINUE: {
//                return onBreakOrContinue(compilerContext);
//            }
//            case H_CAST: {
//                return onCast(compilerContext);
//            }
//            case H_DECLARE_IDENTIFIER: {
//                return onDeclareIdentifier(compilerContext);
//            }
//            case H_DECLARE_INVOKABLE: {
//                return onDeclareInvokable(compilerContext);
//            }
//            case H_DECLARE_TUPLE: {
//                return onDeclareTuple(compilerContext);
//            }
//            case H_DECLARE_TYPE: {
//                return onDeclareType(compilerContext);
//            }
//            case H_DOT_CLASS: {
//                return onDotClass(compilerContext);
//            }
//            case H_DOT_THIS: {
//                return onDotThis(compilerContext);
//            }
//            case H_EXTENDS: {
//                return onExtends(compilerContext);
//            }
//            case H_FIELD: {
//                return onField(compilerContext);
//            }
//            case H_FIELD_UNCHECKED: {
//                return onFieldUnchecked(compilerContext);
//            }
//            case H_FOR: {
//                return onFor(compilerContext);
//            }
//            case H_IDENTIFIER: {
//                return onIdentifier(compilerContext);
//            }
//            case H_IF: {
//                return onIf(compilerContext);
//            }
//            case H_IMPORT: {
//                return onImport(compilerContext);
//            }
//            case H_INVOKER_CALL: {
//                return onInvokerCall(compilerContext);
//            }
//            case H_IS: {
//                return onIs(compilerContext);
//            }
//            case H_LAMBDA_EXPR: {
//                return onLambdaExpr(compilerContext);
//            }
//            case H_LITERAL: {
//                return onLiteral(compilerContext);
//            }
//            case H_LITERAL_DEFAULT: {
//                return onLiteralDefault(compilerContext);
//            }
//            case H_STRING_INTEROP: {
//                return onStringInterpolation(compilerContext);
//            }
//            case H_LITERAL_SUPERSCRIPT: {
//                break;//return onLiteralSuperscript(compilerContext);
//            }
//            case H_DECLARE_META_PACKAGE: {
//                return onMetaDeclarePackage(compilerContext);
//            }
//            case H_META_IMPORT_PACKAGE: {
//                return onMetaImportPackage(compilerContext);
//            }
//            case H_INVOKE_METHOD: {
//                return onInvokeMethodCall(compilerContext);
//            }
//            case H_META_PACKAGE_ID: {
//                return onModuleId(compilerContext);
//            }
//            case H_OBJECT_NEW: {
//                return onObjectNew(compilerContext);
//            }
//            case H_OP_BINARY: {
//                return onOpBinary(compilerContext);
//            }
//            case H_OP_UNARY: {
//                return onOpUnary(compilerContext);
//            }
//            case H_PARS: {
//                return onPars(compilerContext);
//            }
//            case H_PARS_POSTFIX: {
//                return onParsPostfix(compilerContext);
//            }
//            case H_SUPER: {
//                return onSuper(compilerContext);
//            }
//            case H_SWITCH: {
//                return onSwitch(compilerContext);
//            }
//            case H_SWITCH_CASE: {
//                return onSwitchCase(compilerContext);
//            }
//            case H_SWITCH_IS: {
//                return onSwitchIs(compilerContext);
//            }
//            case H_SWITCH_IF: {
//                return onSwitchIf(compilerContext);
//            }
//            case H_THIS: {
//                return onThis(compilerContext);
//            }
//            case H_META_PACKAGE_GROUP:
//            case H_META_PACKAGE_ARTIFACT:
//            case H_META_PACKAGE_VERSION:
//                {
//                return onTokenSuite(compilerContext);
//            }
//            case H_TUPLE: {
//                return onTuple(compilerContext);
//            }
//            case H_TYPE_TOKEN: {
//                return onTypeToken(compilerContext);
//            }
//            case H_APPLY_CAST_OPERATOR: {
//                return onCastOperator(compilerContext);
//            }
//            case H_INVOKE_METHOD_UNCHECKED: {
//                return onInvokeMethodInchecked(compilerContext);
//            }
//            case H_VAR: {
//                return onVar(compilerContext);
//            }
//            case H_WHILE: {
//                return onWhile(compilerContext);
//            }
//            case H_IF_WHEN_DO: {
//                return onIfWhenDo(compilerContext);
//            }
//        }
//        //in stage 1 wont change node instance
//        throw new JShouldNeverHappenException(getClass().getSimpleName()+": Unsupported node class : " + anode.getClass().getSimpleName());
//    }
//
//    private JNode onDeclareIdentifier(HLJCompilerContext compilerContext) {
//        HNDeclareIdentifier node = (HNDeclareIdentifier) compilerContext.node();
//        processNextCompilerStage(node::getInitValue, node::setInitValue, compilerContext);
//        JType identifierType = node.getIdentifierType();
//        if (identifierType == null) {
//            HNTypeToken typeName = node.getIdentifierTypeName();
//            if (typeName == null) {
//                //expect type from value
//                JNode dv = node.getInitValue();
//                JType valType = dv == null ? null : dv.getType();
//                if (valType != null) {
//                    ElementTypeAndConstraint etc = onDeclareIdentifier_detectElementTypeAndConstraint(valType, node.getAssignOperator().image.charAt(0), compilerContext.types());
//                    if (etc == null) {
//                        if (dv == null) {
//                            //ignore. will be handled later...
//                            //compilerContext.log().error("S015", "null is not an iterable type", node.token());
//                        } else {
//                            if (node.getAssignOperator().image.charAt(0) == ':') {
//                                compilerContext.log().error("S015", "not an iterable type " + valType, dv.startToken());
//                            }
//                        }
//                    } else {
//                        node.setIdentifierTypeName(HUtils.createTypeToken(etc.valType));
//                        node.setInitValueConstraint(etc.valCstr);
//                        identifierType = etc.valType;
//                    }
//                }
//            }
////            if (identifierType == null && compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
////                if (node.getIdentifierTypeName() != null) {
////                    identifierType = compilerContext.lookupType(node.getIdentifierTypeName());
////                    if (identifierType != null) {
////                        node.setIdentifierType(identifierType);
////                        node.setIdentifierTypeName(identifierType.typeName());
////                    }
////                }
////            }
//        }
//        if (identifierType != null && node.getInitValue() == null) {
//            HNLiteral dvl = new HNLiteral(identifierType.defaultValue(), null, node.startToken());
//            dvl.setType(identifierType);
//            node.setInitValue(dvl);
//        }
//        if (node.getInitValue() != null) {
//            JNode dv = node.getInitValue();
//            if (dv.getType() == null && node.getIdentifierType() != null) {
//                if (dv instanceof HNLiteral) {
//                    HNLiteral lit = (HNLiteral) dv;
//                    if (lit.getValue() == null) {
//                        lit.setType(node.getIdentifierType());
//                    }
//                } else if (dv instanceof HNLiteralDefault) {
//                    HNLiteralDefault lit = (HNLiteralDefault) dv;
//                    if (lit.getTypeName() == null) {
//                        lit.setTypeName(node.getIdentifierTypeName());
//                        lit.setType(node.getIdentifierType());
//                    }
//                }
//            } else if (dv.getType() != null && node.getIdentifierTypeName() == null) {
//                node.setIdentifierTypeName(HUtils.createTypeToken(dv.getType()));
//                identifierType = dv.getType();
//            }
//        }
//
//        if (node.getDeclaringType() != null) {
//            node.setModifiers(HUtils.publifyModifiers(node.getModifiers()));
//            if (node.getField() == null) {
//                if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//                    if (identifierType == null) {
//                        if (node.getIdentifierTypeName() != null) {
////                            identifierType = compilerContext.lookupType(node.getIdentifierTypeName().getTypenameOrVar());
////                            node.setIdentifierType(identifierType);
//                        } else {
//                            //this is a var/val declaration.
//                            //will detect type from value
//                            JNode dv = node.getInitValue();
//                            if (dv != null) {
//                                if (dv.getType() != null) {
//                                    identifierType = dv.getType();
////                                    node.setIdentifierType(identifierType);
//                                    node.setIdentifierTypeName(HUtils.createTypeToken(identifierType));
//                                }
//                            }
//                        }
//                    }
//                    if (identifierType != null) {
//                        JRawType declaringType = (JRawType) compilerContext.getOrCreateType(node.getDeclaringType());
//                        for (String identifierName : node.getIdentifierNames()) {
//                            JField jField = declaringType.addField(identifierName,
//                                    identifierType,
//                                    node.getModifiers(),
//                                    false
//                            );
//                            compilerContext.debug("AddField", jField);
//                            node.setField(jField);
//                        }
//                    }
//                }
//            }
//        }
////        if (node.getIdentifierType() == null) {
////            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
////                compilerContext.context().log().error("S014", "unable to resolve type for declaration", node.token());
////            }
////        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return compilerContext.node();
//    }
//
//    private JNode onCast(HLJCompilerContext compilerContext) {
//        HNCast n = (HNCast) compilerContext.node();
//        processNextCompilerStage(n::getTypeNode, n::setTypeNode, compilerContext);
//        processNextCompilerStage(n::getBase, n::setBase, compilerContext);
//        if (n.getTypeNode() instanceof HNTypeToken) {
//            n.setType(((HNTypeToken) n.getTypeNode()).getTypeVal());
//        } else {
//            throw new JFixMeLaterException();
//        }
//        //check type here!!
//        return n;
//    }
//
//    public JNode onCastOperator(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNApplyCastOperator node = (HNApplyCastOperator) compilerContext.node();
//        processNextCompilerStage(node::getExpr, node::setExpr, compilerContext);
//        if (node.getType() == null) {
//            node.setType(compilerContext.lookupType(node.getCastType()));
//        }
//        return compilerContext.node();
//    }
//
//    private JNode onBreakOrContinue(HLJCompilerContext compilerContext) {
//        HNBreakOrContinue node = (HNBreakOrContinue) compilerContext.node();
//        boolean requireLabel = node.leapVal() > 0;
//        int x = node.leapVal() + 1;
//        JNode n = node.parentNode();
//        while (n != null) {
//            if (n instanceof HNWhile) {
//                x--;
//                if (x <= 0) {
//                    break;
//                }
//            }
//            if (n instanceof HNFor) {
//                if (!n.isSetUserObject("generatedFor")) {
//                    x--;
//                    if (x <= 0) {
//                        break;
//                    }
//                } else {
//                    requireLabel = true;
//                }
//            }
//            if (n instanceof HNSwitch) {
//                requireLabel = true;
//            }
//            n = n.parentNode();
//        }
//        if (n == null) {
//            compilerContext.log().error("S016", "break cannot be used outside for or while statements", node.startToken());
//        } else {
//            if (requireLabel) {
//                String l = null;
//                if (n instanceof HNFor) {
//                    l = ((HNFor) n).getLabel();
//                } else {
//                    l = ((HNWhile) n).getLabel();
//                }
//                if (l == null) {
//                    JNode n2 = n.parentNode();
//                    while (n2 != null) {
//                        if (n2 instanceof HNBlock || n2 instanceof HNDeclare) {
//                            break;
//                        }
//                        n2 = n2.parentNode();
//                    }
//                    if (n2 == null) {
//                        compilerContext.log().error("S016", "cannot find any declaring parent", n.startToken());
//                    } else {
//                        node.setLabel("LABEL" + HUtils.incUserProperty(n2, "LabelsCounter"));
//                        if (n instanceof HNFor) {
//                            ((HNFor) n).setLabel(node.getLabel());
//                        } else {
//                            ((HNWhile) n).setLabel(node.getLabel());
//                        }
//                    }
//                }
//            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    private JNode onBracketsPostfix(HLJCompilerContext compilerContext) {
////this is an array application
//
//        HNBracketsPostfix n = (HNBracketsPostfix) compilerContext.node();
//
//        processNextCompilerStage(n::getLeft, n::setLeft, compilerContext);
//        processNextCompilerStage(n,n.getRight(), compilerContext);
//        HNArrayCall cc = new HNArrayCall(
//                n.getLeft(),
//                n.getRight().toArray(new JNode[0])
//                , null, null, n.startToken(), n.endToken());
//        return processNextCompilerStage(cc, compilerContext);
//    }
//
//    private JNode onBracketsPostfixLastIndex(HLJCompilerContext compilerContext) {
////this is an array application
//        HNBracketsPostfixLastIndex n = (HNBracketsPostfixLastIndex) compilerContext.node();
//        processNextCompilerStage(n::getBase, n::setBase, compilerContext);
//        if (n.getType() == null) {
//            if (n.getBase().getType() != null) {
//                if (n.getBase().getType().isArray()) {
//                    n.setType(JTypeUtils.forInt(compilerContext.types()));
//                } else {
//                    throw new IllegalArgumentException("Please fix me");
//                }
//            }
//        }
//        return n;
//    }
//
//    private JNode onBrackets(HLJCompilerContext compilerContext) {
//        HNBrackets node = (HNBrackets) compilerContext.node();
//        JNode[] values = node.getItems();
//        processNextCompilerStage(node, values, compilerContext);
//        return node;
//    }
//
//    private JNode onBraces(HLJCompilerContext compilerContext) {
//        HNBraces node = (HNBraces) compilerContext.node();
//        processNextCompilerStage(node, node.getItems(), compilerContext);
//        return node;
//    }
//
//    protected JNode onAssign(HLJCompilerContext compilerContext) {
//        HNAssign node = (HNAssign) compilerContext.node();
//        if (node.assignType == null) {
//            if (node.left instanceof HNVar) {
//                node.assignType = HNAssign.AssignType.VAR;
//            } else if (node.left instanceof HNField) {
//                node.assignType = HNAssign.AssignType.FIELD;
//            } else if (node.left instanceof HNArrayCall) {
//                node.assignType = HNAssign.AssignType.ARRAY;
//            } else if (node.left instanceof HNTuple) {
//                node.assignType = HNAssign.AssignType.TUPLE;
//            } else if (node.left instanceof HNIdentifier) {
//                //ignore for this pass...
////                if (compilerContextBase.isStage(STAGE_3_WIRE_CALLS)) {
////                    compilerContext.log().error("S059", "unknown symbol " + ((HNIdentifier)node.getLeftNode()).getName(), node.token());
////                }
//            } else {
//                if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                    compilerContext.log().error("S059", "cannot assign to " + node.getLeft().getClass().getSimpleName(), node.startToken());
//                }
//            }
//        }
//        node.setLeft(onAssign_checkLeft(node.left, compilerContext));
//        if (node.getType() == null && node.getLeft().getType() != null) {
//            node.setType(node.getLeft().getType());
//        }
//        if (node.assignType != null) {
//            switch (node.assignType) {
//                case ARRAY: {
//                    return onAssign_processCompilerStage_JNodeHArrayGet(node, (HNArrayCall) node.getLeft(), compilerContext);
//                }
//                case TUPLE: {
//                    processNextCompilerStage(node, node.tupleSubAssignments, compilerContext);
//                    return onAssign_processCompilerStage_JNodeHTuple(node, (HNTuple) node.getLeft(), compilerContext);
//                }
//                default: {
//                    processNextCompilerStage(node::getLeft, node::setLeft, compilerContext);
//                    processNextCompilerStage(node::getRight, node::setRight, compilerContext);
//                    return onAssign_checkAssignTypes(node, compilerContext);
//                }
//            }
//        } else {
//            processNextCompilerStage(node::getLeft, node::setLeft, compilerContext);
//            processNextCompilerStage(node::getRight, node::setRight, compilerContext);
//        }
//        return compilerContext.node();
//    }
//
//    protected JNode onArrayNew(HLJCompilerContext compilerContext) {
//        JContext context = compilerContext.context();
//        HNArrayNew node = (HNArrayNew) compilerContext.node();
//        JNode[] nargs = node.getInits();
//        processNextCompilerStage(node, nargs, compilerContext);
//        processNextCompilerStage(node::getConstructor, node::setConstructor, compilerContext);
//        if (node.getArrayType() == null) {
//            JType arrayType = compilerContext.lookupType(node.getArrayTypeName().getTypename());
//            node.setArrayType(arrayType);
//        }
//        if (node.getType() == null && node.getArrayType() != null) {
//            node.setType(node.getArrayType());
//        }
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            if (node.getConstructor() != null) {
//                if (node.getInits() == null) {
//                    compilerContext.log().error("S052", "initialized array is missing initializer", node.startToken());
//                } else {
//                    if (node.getInits().length != node.getArrayType().arrayDimension()) {
//                        compilerContext.log().error("S052", "initialized array and array length must match : "
//                                + node.getInits().length + "!=" + node.getArrayType().arrayDimension(), node.startToken());
//                    }
//                    JNode setter = node.getConstructor();
//                    if (setter instanceof HNDeclareInvokable) {
//                        HNDeclareInvokable d = (HNDeclareInvokable) setter;
//                        List<HNDeclareIdentifier> arguments = d.getArguments();
//                        JType[] expected = new JType[arguments.size()];
//                        for (int i = 0; i < expected.length; i++) {
//                            expected[i] = JTypeUtils.forInt(compilerContext.types());
//                        }
//                        if (node.getInits().length != arguments.size()) {
//                            compilerContext.log().error("S052", "initialized array and array length must match : "
//                                    + node.getInits().length + "!=" + arguments.size(), node.startToken());
//                        }
//                        JNameSignature currentSig = JNameSignature.of(null, arguments.stream()
//                                .map(x -> x.getIdentifierTypeName())
//                                .map(x -> x == null ? DefaultTypeName.of("?") : x)
//                                .toArray(JTypeName[]::new)
//                        );
//                        for (HNDeclareIdentifier p : arguments) {
//                            if (p.getIdentifierType() == null) {
//                                p.setIdentifierTypeName(HUtils.createTypeToken(JTypeUtils.forInt(compilerContext.types())));
//                            } else {
//                                if (!p.getIdentifierTypeName().getTypename().fullName().equals("int")) {
//                                    compilerContext.log().error("S052", "initializer function must match signature "
//                                            + JSignature.of("", expected) + "\n\t but found : " + currentSig, p.startToken());
//                                }
//                            }
//                        }
//                        d.setReturnTypeName(node.getArrayTypeName().componentType());
//                        d.setInvokable(new JFunctionLocal(
//                                "", node.getArrayType().componentType(),
//                                expected, false,
//                                new BodyJInvoke(d)
//                        ));
//                        processNextCompilerStage(node::getConstructor, node::setConstructor, compilerContext);
//                    } else {
//                        JType jType = JNodeUtils.getType(setter);
//                        if (!node.getArrayType().rootComponentType().boxed().isAssignableFrom(jType.boxed())) {
//                            compilerContext.log().error("S052", "initializer function must match type " + node.getArrayType() + " but found " + jType, node.getConstructor().startToken());
//                        }
//                    }
//                }
//            }
//
//            return node;
//        }
//        return compilerContext.node();
//    }
//
//    protected JNode onArrayGet(HLJCompilerContext compilerContext) {
//        JContext context = compilerContext.context();
//        HNArrayCall node = (HNArrayCall) compilerContext.node();
//        processNextCompilerStage(node, node.getIndexNodes(), compilerContext);
//        JNode base = processNextCompilerStage(node::getArrayInstanceNode, node::setArrayInstanceNode, compilerContext);
//        JType baseType = base.getType();
//        if (baseType != null) {
//            if (node.getArrayType() == null) {
//                node.setArrayType(baseType);
//            }
//            if (node.getArrayType().isArray()) {
//                JNode b = base;
//                JType bt = baseType;
//                JNode[] array = node.getIndexNodes();
//                if (array.length == 0) {
//                    throw new JParseException("Missing index");
//                }
//                if (array.length > 1) {
//                    for (JNode jNode : array) {
//                        b = new HNArrayCall(b, new JNode[]{jNode}, bt, ((JTypeArray) bt).componentType(), jNode.startToken(), jNode.endToken());
//                        bt = ((JTypeArray) bt).componentType();
//                    }
//                    node = (HNArrayCall) b;
//                }
//                if (array[0].getType() != null && array[0].getType().boxed().name().equals("java.lang.Integer")) {
//                    //this is okkay
//                    node.setType(((JTypeArray) bt).componentType());
//                    return node;
//                }
//            }
//
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)
//                    && HUtils.isTypeSet(node.getIndexNodes())
//                    && !node.isSetUserObject("AssignLeftNode")
//            ) {
//                JNode[] oldNodes = node.getIndexNodes();
//                JTypeOrLambda[] oldTypes = compilerContext.jTypeOrLambdas(oldNodes);
//                JTypeOrLambda baseTypeOrLambda = compilerContext.jTypeOrLambda(base);
//                if (oldTypes != null && baseTypeOrLambda != null) {
//                    JNode[] nargs = JeepUtils.arrayAppend(JNode.class, base, oldNodes);
//                    JTypeOrLambda[] ntypes = JeepUtils.arrayAppend(JTypeOrLambda.class, baseTypeOrLambda, oldTypes);
//                    JInvokable m = compilerContext.findFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_GET_SHORT, HFunctionType.SPECIAL, ntypes, node.startToken());
//                    if (m != null) {
//                        return createFunctionCall(node.startToken(), m, nargs);
//                    }
//                }
//            }
//        }
//        return node;
//    }
//
//    protected JNode onOpCoalesce(HLJCompilerContext compilerContext) {
//        HNOpCoalesce node = (HNOpCoalesce) compilerContext.node();
//        processNextCompilerStage(node::getLeft, node::setLeft, compilerContext);
//        processNextCompilerStage(node::getRight, node::setRight, compilerContext);
//
//        if (HUtils.isTypeSet(node.getLeft()) && HUtils.isTypeSet(node.getRight())) {
//            node.setType(HUtils.firstCommonSuperType(node.getLeft(), node.getRight()));
//        }
//        return compilerContext.node();
//    }
//
//    protected JNode onDotOperator(HLJCompilerContext compilerContext) {
//        HNOpDot node = (HNOpDot) compilerContext.node();
//        String name = (String) (Object) node.getRight();
//        if (node.getLeft() == null) {
//            JNodePath path = compilerContext.path().parent();
//            for (int i = 0; i < path.size(); i++) {
//                JNode p = path.parent(i);
//                if (p instanceof HNDeclareType) {
//                    HNDeclareType cd = (HNDeclareType) p;
//                    if ("this".equals(name)) {
//                        return new HNThis(cd.getjType(), cd.startToken());
//                    } else if ("super".equals(name)) {
//                        return new HNSuper(cd.getjType(), cd.startToken());
//                    } else {
//                        JType jType = cd.getjType();
//                        JField f = jType.declaredFieldOrNull(name);
//                        if (f != null) {
//                            return new HNField(null, f, p.startToken());
//                        }
//                    }
//                } else if (p instanceof HNDeclareInvokable) {
//                    HNDeclareInvokable fd = (HNDeclareInvokable) p;
//                    //
//                } else if (p instanceof HNBlock) {
//                    HNBlock bd = (HNBlock) p;
//                    for (HNDeclareIdentifier varDeclaration : bd.getVarDeclarations()) {
//                        for (JToken identifierName : varDeclaration.getIdentifierTokens()) {
//                            if (true) {
//                                throw new JFixMeLaterException();
//                            }
//                            return processNextCompilerStage(new HNIdentifier(identifierName), compilerContext);
//                        }
//                    }
//                    //
//                }
//            }
//            if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//                JType t = compilerContext.lookupTypeOrNull(DefaultTypeName.of(name));
//                if (t != null) {
//                    return new HNTypeToken(t, node.startToken());
//                }
//            }
//            return node;
//        } else {
//            if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//                String u = node.toString();
//                JType f = compilerContext.lookupTypeOrNull(DefaultTypeName.of(u));
//                if (f != null) {
//                    return new HNTypeToken(f, node.startToken());
//                }
//                JNode p2 = processNextCompilerStage(node.getLeft(), compilerContext);
//                if (p2 != node.getLeft()) {
//                    node = new HNOpDot(p2, JTokenUtils.createOpToken("."), node.getRight(), p2.startToken(), p2.endToken());
//                }
//                name = (String) (Object) node.getRight();
//                if (node.getLeft() instanceof HNTypeToken) {
//                    HNTypeToken pp = (HNTypeToken) node.getLeft();
//                    JType ftype = JNodeUtils.getType(pp);
//                    JField ff = ftype.declaredField(name);
//                    return new HNField(node.getLeft(), ff, node.startToken());
//                } else {
//                    JType parentType = node.getLeft().getType();
//                    JField ff = parentType.declaredField(name);
//                    return new HNField(node.getLeft(), ff, node.startToken());
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    private JNode onBlock(HNode anode, HLJCompilerContext compilerContext) {
//        HNBlock node = (HNBlock) compilerContext.node();
//
//        if (compilerContext.isStage(STAGE_1_DECLARATIONS)) {
//            //check for duplicate definitions
//            DuplicateDefChecker ddc = new DuplicateDefChecker();
//            ddc.addBody(node);
//            ddc.checkDuplicates(compilerContext.log());
//        }
//        LinkedHashSet<JImportInfo> imports = new LinkedHashSet<>(node.getImports());
//        if (!node.isSetUserObject("statementsReAssigned")) {
//            node.setUserObject("statementsReAssigned");
//            List<JNode> statements = node.getStatements();
//            List<JNode> statements2 = new ArrayList<>();
//            for (int i = 0; i < statements.size(); i++) {
//                JNode statement = statements.get(i);
//                if (statement instanceof HNImport) {
//                    compilerContext = (HLJCompilerContext) compilerContext.addImport(((HNImport) statement).getJImportInfo());
//                    imports.add(((HNImport) statement).getJImportInfo());
//                } else if (statement instanceof HNDeclareType && compilerContext.path().size() > 1) {
//                    ((HNDeclareType) statement).setInternalType(true);
//                }
//                ((HNode) statement).getImports().addAll(imports);
//                statement = processNextCompilerStage(statement, compilerContext);
//                if (compilerContext.path().size() == 0) {
//                    //global
//                    if (statement instanceof HNDeclareInvokable) {
//                        HNDeclareInvokable invokable = (HNDeclareInvokable) statement;
//                        invokable.setModifiers(HUtils.STATIC | HUtils.publifyModifiers(invokable.getModifiers()));
//                        if (invokable.getSignature() == null) {
//                            invokable.setSignature(
//                                    JNameSignature.of(
//                                            invokable.getName(),
//                                            invokable.getArguments().stream()
//                                                    .map(HNDeclareIdentifier::getIdentifierTypeName).toArray(JTypeName[]::new)
//                                    )
//                            );
//                        }
//                        compilerContext.metaPackageType().addMethod(invokable, compilerContext);
//                        compilerContext.metaPackageType().sources().add(invokable.startToken().compilationUnit.getSource().name());
//                    } else if (statement instanceof HNDeclareIdentifier) {
//                        HNDeclareIdentifier varDef = (HNDeclareIdentifier) statement;
//                        varDef.setModifiers(HUtils.STATIC | HUtils.publifyModifiers(varDef.getModifiers()));
//                        JNode dv = varDef.getInitValue();
//                        if (dv == null) {
//                            dv = new HNLiteralDefault((JType) null);
//                            varDef.setInitValue(dv);
//                        }
//                        compilerContext.metaPackageType().addField(varDef, compilerContext);
//                        compilerContext.metaPackageType().sources().add(varDef.startToken().compilationUnit.getSource().name());
//                        for (JToken identifierName : varDef.getIdentifierTokens()) {
//                            HNField ff = new HNField(
//                                    null, identifierName, compilerContext
//                                    .getOrCreateType(compilerContext.metaPackageType())
//                                    .typeName(),
//                                    null,
//                                    varDef.startToken(),
//                                    varDef.endToken()
//                            );
//                            HNAssign fs = new HNAssign(
//                                    ff, JTokenUtils.createOpToken("="),
//                                    ((HNode) dv).copy()
//                                    , varDef.startToken()
//                                    , varDef.endToken()
//                            );
//                            fs.setUserObject("DefaultInstanceInitializer");
//                            if (varDef.isSetUserObject("NoInitialValue")) {
//                                fs.setUserObject("NoInitialValue");
//                            }
//                            HNBlock moduleBody = (HNBlock) compilerContext.metaPackageType().getOrCreateRunModuleMethod(compilerContext).getBody();
//                            compilerContext.metaPackageType().sources().add(varDef.startToken().compilationUnit.getSource().name());
//                            moduleBody.add(fs);
//                        }
//
//                    } else if (statement instanceof HNDeclareType) {
//                        compilerContext.metaPackageType().addTopLevelTypeNode((HNDeclareType) statement, compilerContext);
//                    } else if (statement instanceof HNDeclareMetaPackage) {
//                        //ignore...
//                    } else {
//                        HNBlock moduleBody = (HNBlock) compilerContext.metaPackageType().getOrCreateRunModuleMethod(compilerContext).getBody();
//                        moduleBody.add(statement);
//                        compilerContext.metaPackageType().sources().add(statement.startToken().compilationUnit.getSource().name());
//                    }
//                } else {
//                    //local
//                    if (statement instanceof HNDeclareInvokable) {
//                        HNDeclareInvokable invokable = (HNDeclareInvokable) statement;
//                        if (invokable.getModifiers() != 0) {
//                            invokable.setModifiers(0);
//                            compilerContext.log().error("S052", "modifiers not allowed for local functions " + HUtils.modifiersToString0(invokable.getModifiers()), invokable.startToken());
//                        }
//                        //invokable.setModifiers(HUtils.publifyModifiers(invokable.getModifiers()));
//                        if (invokable.getSignature() == null) {
//                            invokable.setSignature(
//                                    JNameSignature.of(
//                                            invokable.getName(),
//                                            invokable.getArguments().stream()
//                                                    .map(HNDeclareIdentifier::getIdentifierTypeName).toArray(JTypeName[]::new)
//                                    )
//                            );
//                        }
//                        statements2.add(statement);
//                    } else if (statement instanceof HNDeclareIdentifier) {
//                        HNDeclareIdentifier varDef = (HNDeclareIdentifier) statement;
//                        if (varDef.getModifiers() != 0) {
//                            if (
//                                    HUtils.isPublic(varDef.getModifiers())
//                                            || HUtils.isPrivate(varDef.getModifiers())
//                                            || HUtils.isProtected(varDef.getModifiers())
//                                            || HUtils.isStatic(varDef.getModifiers())
//                            ) {
//                                //varDef.setModifiers(0);
//                                compilerContext.log().error("S052", "modifiers not allowed for local vars " + HUtils.modifiersToString0(varDef.getModifiers()), varDef.startToken());
//                            }
//                        }
////                        varDef.setModifiers(HUtils.publifyModifiers(varDef.getModifiers()));
//                        JNode dv = varDef.getInitValue();
//                        if (dv == null) {
//                            dv = new HNLiteralDefault((JType) null);
//                            varDef.setInitValue(dv);
//                        }
//                        statements2.add(varDef);
//                        //only if field
//
//                        if (statement.parentNode() instanceof HNDeclareType) {
//                            varDef.setHiddenInitValue(dv);
//                            varDef.setInitValue(null);
//                            for (JToken identifierName : varDef.getIdentifierTokens()) {
//                                HNAssign fs = new HNAssign(
//                                        new HNVar(
//                                                identifierName, null, varDef.startToken()
//                                        ), JTokenUtils.createOpToken("="),
//                                        dv
//                                        , varDef.startToken()
//                                        , varDef.endToken()
//                                );
//                                fs.setUserObject("DefaultInstanceInitializer");
//                                if (varDef.isSetUserObject("NoInitialValue")) {
//                                    fs.setUserObject("NoInitialValue");
//                                }
//                                statements2.add(fs);
//                            }
//                        }
//                    } else if (statement instanceof HNDeclareType) {
//                        HNDeclareType dt = (HNDeclareType) statement;
//                        if (dt.getModifiers() != 0) {
//                            dt.setModifiers(0);
//                            compilerContext.log().error("S052", "modifiers not allowed for local types " + HUtils.modifiersToString0(dt.getModifiers()), dt.startToken());
//                        }
//                        statements2.add(statement);
//                    } else if (statement instanceof HNDeclareMetaPackage) {
//                        //ignore...
//                    } else {
//                        statements2.add(statement);
//                    }
//                }
//            }
//            node.setStatements(statements2);
//        } else {
//            List<JNode> statements = node.getStatements();
//            for (int i = 0; i < statements.size(); i++) {
//                JNode statement = statements.get(i);
//                if (statement instanceof HNImport) {
//                    compilerContext = (HLJCompilerContext) compilerContext.addImport(((HNImport) statement).getJImportInfo());
//                    imports.add(((HNImport) statement).getJImportInfo());
//                } else {
//                    statement = processNextCompilerStage(statement, compilerContext);
//                    statements.set(i, node.bind(statement, "statements", i));
//                    ((HNode) statement).getImports().addAll(imports);
//                }
//            }
//        }
//        if (node.getType() == null) {
//            JNode[] exitPoints = node.getExitPoints();
//            JType outType = null;
//            for (JNode exitPoint : exitPoints) {
//                if (exitPoint.getType() == null) {
//                    outType = null;
//                    break;
//                } else {
//                    if (outType == null) {
//                        outType = exitPoint.getType();
//                    } else {
//                        outType = outType.firstCommonSuperType(exitPoint.getType());
//                    }
//                }
//            }
//            if (outType == null) {
//                if (exitPoints.length == 0) {
//                    node.setType(JTypeUtils.forVoid(compilerContext.types()));
//                }
//            } else {
//                node.setType(outType);
//            }
//        }
//        return node;
//    }
//
//    private JNode onAssign_checkLeft(JNode o, HLJCompilerContext compilerContext) {
//        if (o instanceof HNVar) {
//            o.setUserObject("AssignLeftNode");
//            return o;
//        } else if (o instanceof HNField) {
//            o.setUserObject("AssignLeftNode");
//            return o;
//        } else if (o instanceof HNArrayCall) {
//            o.setUserObject("AssignLeftNode");
//            return o;
//        } else if (o instanceof HNTuple) {
//            JNode[] items = ((HNTuple) o).getItems();
//            for (int i = 0; i < items.length; i++) {
//                items[i] = onAssign_checkLeft(items[i], compilerContext);
//            }
//            o.setUserObject("AssignLeftNode");
//            return o;
//        } else if (o instanceof HNPars) {
//            HNPars p = new HNPars(((HNPars) o).getItems(), o.startToken(),new ArrayList<>(), o.endToken());
//            JNode[] items = ((HNPars) p).getItems();
//            for (int i = 0; i < items.length; i++) {
//                items[i] = onAssign_checkLeft(items[i], compilerContext);
//            }
//            o.setUserObject("AssignLeftNode");
//            return o;
//        } else if (o instanceof HNIdentifier) {
//            return o;
//        } else {
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                compilerContext.log().error("S059", "cannot assign to " + o.getClass().getSimpleName(), o.startToken());
//            }
//            return o;
//        }
//    }
//
//    private JTypeOrLambda[] resolveTupleTypes(JNode n, HLJCompilerContext compilerContext) {
//        if (n instanceof HNTuple) {
//            return compilerContext.jTypeOrLambdas(((HNTuple) n).getItems());
//        }
//        JType t = n.getType();
//        if (t != null) {
//            JType r = t.rawType();
//            if (r.name().startsWith("net.vpc.hadralang.stdlib.Tuple")) {
//                JType[] jTypeOrVariables = ((JParameterizedType) t).actualTypeArguments();
//                JTypeOrLambda[] a = new JTypeOrLambda[jTypeOrVariables.length];
//                for (int i = 0; i < a.length; i++) {
//                    a[i] = JTypeOrLambda.of((JType) jTypeOrVariables[i]);
//                }
//                return a;
//            } else {
//                //not a tuple
//                return null;
//            }
//        } else {
//            //cant check
//            return null;
//        }
//    }
//
//    private int resolveTupleDim(JNode n) {
//        if (n instanceof HNTuple) {
//            return ((HNTuple) n).getItems().length;
//        }
//        JType t = n.getType();
//        if (t != null) {
//            JType r = t.rawType();
//            if (r.name().equals("net.vpc.hadralang.stdlib.TupleN")) {
//                throw new IllegalArgumentException("Fix me later");
//            } else if (r.name().startsWith("net.vpc.hadralang.stdlib.Tuple")) {
//                return Integer.parseInt(r.name().substring("net.vpc.hadralang.stdlib.Tuple".length()));
//            } else {
//                //not a tuple
//                return -1;
//            }
//        } else {
//            //cant check
//            return -2;
//        }
//    }
//
//    private JNode onAssign_processCompilerStage_JNodeHTuple(HNAssign node, HNTuple left, HLJCompilerContext compilerContext) {
//        processNextCompilerStage(node::getLeft, node::setLeft, compilerContext);
//        processNextCompilerStage(node::getRight, node::setRight, compilerContext);
//        JNode right = node.getRight();
//        int ldim = resolveTupleDim(left);
//        int rdim = resolveTupleDim(right);
//        if (right.getType() != null) {
//            if (rdim == -2) {
//                //ignore for now
//            } else if (rdim == -1) {
//                //may be check if there is function to overload this ???
//                compilerContext.log().error("S044", "cannot assign non tuple to tuple", right.startToken());
//            } else if (rdim != ldim) {
//                //may be check if there is function to overload this ???
//                compilerContext.log().error("S044", "tuple dimension mismatch " + ldim + "<>" + rdim, right.startToken());
//            } else {
//                if (left.getType() != null) {
//                    //how to check types....
//                    JTypeOrLambda[] tl = resolveTupleTypes(left, compilerContext);
//                    JTypeOrLambda[] tr = resolveTupleTypes(right, compilerContext);
//                    if (tl != null && tr != null) {
//                        int max = Math.max(tl.length, tr.length);
//                        for (int i = 0; i < max; i++) {
//                            if (tl[i].isType() && tr[i].isType()) {
//                                if (tl[i].getType().isAssignableFrom(tr[i].getType())) {
//                                    //okkay
//                                } else {
//                                    //should we check for conversions ???
//                                    compilerContext.log().error("S044", "tuple dimension mismatch. unable to assign " + tr[i].getType() + " to " + tl[i].getType(),
//                                            right.startToken()
//                                    );
//                                }
//                            } else {
//                                throw new IllegalArgumentException("Fix me later");
//                            }
//                        }
//                    }
//                } else {
//                    //should we detect left from right ?
//                    JNode[] items = left.getItems();
//                    if (node.tupleSubAssignments == null) {
//                        node.tupleSubAssignments = new HNode[items.length];
//                        for (int i = 0; i < items.length; i++) {
//                            JToken op = new JToken();
//                            op.image = "=";
//                            HNAssign sa = new HNAssign(
//                                    items[i], op,
//                                    new HNField(
//                                            ((HNode) node.getRight()).copy(), JTokenUtils.createWordToken("_" + (i + 1)), null, null
//                                            , node.getRight().startToken()
//                                            , node.getRight().endToken()
//                                    )
//                                    , items[i].startToken()
//                                    , items[i].endToken()
//                            );
//                            node.bind(sa, "TupleAssignment[" + i + "]");
//                            node.tupleSubAssignments[i] = sa;
//                        }
//                    }
//                    for (int i = 0; i < node.tupleSubAssignments.length; i++) {
//                        if (items[i].getType() == null) {
//                            if (node.tupleSubAssignments[i].getType() != null) {
//                                ((HNode) items[i]).setType(node.tupleSubAssignments[i].getType());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return node;
//    }
//
//    private JNode onAssign_processCompilerStage_JNodeHArrayGet(HNAssign node, HNArrayCall left, HLJCompilerContext compilerContext) {
//        processNextCompilerStage(node::getLeft, node::setLeft, compilerContext);
//        processNextCompilerStage(node::getRight, node::setRight, compilerContext);
//        processNextCompilerStage(left, left.getIndexNodes(), compilerContext);
//        JNode base = processNextCompilerStage(left::getArrayInstanceNode, left::setArrayInstanceNode, compilerContext);
//        JType baseType = base.getType();
//        if (left.getArrayType() == null) {
//            if (base.getType() != null) {
//                left.setArrayType(base.getType());
//            }
//        }
//        JNode right = node.getRight();
//        if (baseType != null) {
//            boolean regularArrayIndexing = false;
//            if (left.getArrayType().isArray()) {
//                JNode b = base;
//                JType bt = baseType;
//                JNode[] array = left.getIndexNodes();
//                if (array.length == 0) {
//                    compilerContext.log().error("S044", "empty  brackets. missing indices.", left.startToken());
//                }
//                if (array.length > 1) {
//                    for (int i = 0; i < array.length - 1; i++) {
//                        JNode jNode = array[i];
//                        JTypeArray bta = ((JTypeArray) bt);
//                        b = new HNArrayCall(b, new JNode[]{jNode}, bt, bta.componentType(), jNode.startToken(), jNode.endToken());
//                        bt = bta.componentType();
//                    }
//                    b = new HNArrayCall(b, new JNode[]{array[array.length - 1]}, bt, ((JTypeArray) bt).componentType(),
//                            array[array.length - 1].startToken(), array[array.length - 1].endToken());
//                    node.setLeft(b);
//                    regularArrayIndexing = true;
//                } else {
//                    if (array[0].getType() != null && array[0].getType().boxed().name().equals("java.lang.Integer")) {
//                        //this is okkay
//                        node.setType(((JTypeArray) bt).componentType());
//                        regularArrayIndexing = true;
//                    }
//                }
//            }
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                if (!regularArrayIndexing) {
//                    JNode[] oldNodes = left.getIndexNodes();
//                    JTypeOrLambda valueType = compilerContext.jTypeOrLambda(right);
//                    JTypeOrLambda[] oldTypes = compilerContext.jTypeOrLambdas(oldNodes);
//                    JTypeOrLambda baseTypeOrLambda = compilerContext.jTypeOrLambda(base);
//                    if (oldTypes != null && baseTypeOrLambda != null && valueType != null) {
//                        JNode[] nargs = JeepUtils.arrayAppend(JNode.class, base, oldNodes, right);
//                        JTypeOrLambda[] ntypes = JeepUtils.arrayAppend(JTypeOrLambda.class, baseTypeOrLambda, oldTypes, valueType);
//                        JInvokable m = compilerContext.findFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_SET_SHORT, HFunctionType.SPECIAL, ntypes, left.startToken());
//                        if (m != null) {
//                            attachLambdaTypes(m, nargs, compilerContext);
//                            node.setType(m.returnType());
//                            return createFunctionCall(left.startToken(), m, nargs);
//                        }
//                    }
//                }
//            }
//            return onAssign_checkAssignTypes(node, compilerContext);
//        }
//        return node;
//    }
//
//    protected JNode onAssign_checkAssignTypes(HNAssign node, HLJCompilerContext compilerContext) {
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            JType tl = node.getLeft().getType();
//            JType tr = node.getRight().getType();
//            if (tl != null && tr != null) {
//                if (tl.isAssignableFrom(tr)) {
//                    //this is okkay
//                } else {
//                    JInvokable c = compilerContext.createConverter(JOnError.TRACE, tr, tl, node, null);
//                    if (c != null) {
//                        HNInvokerCall r2 = createFunctionCall(node.startToken(), c, node.getRight());
//                        node.setRight(r2);
//                        return node;
//                    }
//                }
//            }
//        }
//        return node;
//    }
//
//    public JNode onDeclareInvokable(HLJCompilerContext compilerContext) {
//        HNDeclareInvokable node = (HNDeclareInvokable) compilerContext.node();
//        if (node.getDeclaringType() != null) {
//            if (node.getInvokableType() == null) {
//                if ("constructor".equals(node.getName())
//                        ||
//                        (node.getDeclaringType().getName() != null && node.getDeclaringType().getName().equals(node.getName()))) {
//                    //this is a constructor
//                    node.setInvokableType(HLInvokableType.CONSTRUCTOR);
//                    if ("constructor".equals(node.getName())) {
//                        node.setNameToken(node.getDeclaringType().getNameToken());
//                    }
//                    if (node.getReturnTypeName() != null) {
//                        compilerContext.log().error("S046", "Constructor should not have a return type", node.startToken());
//                    }
//                } else {
//                    if (node.getReturnTypeName() == null) {
//                        node.setReturnTypeName(compilerContext.createSpecialTypeToken("void"));
//                    }
//                    node.setInvokableType(HLInvokableType.METHOD);
//                }
//            }
//        } else {
//            if (node.getReturnTypeName() == null) {
//                node.setReturnTypeName(compilerContext.createSpecialTypeToken("void"));
//            }
//            node.setInvokableType(HLInvokableType.METHOD);
//            if ("constructor".equals(node.getName())) {
//                compilerContext.log().error("S046", "No class to declare constructor for", node.startToken());
//            }
//        }
//
//        if (compilerContext.isStage(STAGE_1_DECLARATIONS)) {
//            //check for duplicate definitions
//            DuplicateDefChecker ddc = new DuplicateDefChecker();
//            ddc.addIdentifiers(node.getArguments());
//            ddc.addBody(node.getBody());
//            ddc.checkDuplicates(compilerContext.log());
//        }
//        processNextCompilerStage(node, node.getArguments(), compilerContext);
//        processNextCompilerStage(node::getBody, node::setBody, compilerContext);
//        List<HNDeclareIdentifier> arguments = node.getArguments();
//        if (node.getInvokable() == null && compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            List<JType> sigTypes = new ArrayList<>();
//            boolean varArg = false;
//            JNameSignature signature = null;
//            boolean typeMissing = false;
//            for (int i = 0; i < arguments.size(); i++) {
//                HNDeclareIdentifier argument = arguments.get(i);
//                HNTypeToken typeName = argument.getIdentifierTypeName();
//                if (typeName == null) {
//                    typeMissing = true;
//                    break;
//                } else {
//                    if (i == arguments.size() - 1 && typeName.getTypename().isVarArg()) {
//                        varArg = true;
//                        typeName = HUtils.createTypeToken(typeName.getTypename().replaceVarArg());
//                    }
//                    JType jtype = compilerContext.lookupType(typeName.getTypename());
////                    argument.setIdentifierType(jtype);
//                    sigTypes.add(jtype);
//                }
//            }
//            if (!typeMissing) {
//                String name = node.getName();
//                signature = JNameSignature.of(name, arguments.stream().map(x -> x.getIdentifierTypeName()).toArray(JTypeName[]::new));
//                node.setSignature(signature);
//                JType returnType = compilerContext.types().forName(node.getReturnTypeName().getTypename());
//                if (node.getDeclaringType() != null) {
//                    //wont add it twice
//                    //node.declaringType.addMethod(node,compilerContext);
//                    if (node.getInvokable() == null && node.getInvokableType() != null) {
//                        DefaultJType dt = (DefaultJType) compilerContext.getOrCreateType(node.getDeclaringType());
//                        switch (node.getInvokableType()) {
//                            case METHOD: {
//                                JMethod runModuleMethod = dt.addMethod(
//                                        JSignature.of(compilerContext.types(), node.getSignature()),
//                                        node.getArgNames(), returnType,
//                                        new BodyJInvoke(node), node.getModifiers(), false
//                                );
//                                node.setInvokable(runModuleMethod);
//                                break;
//                            }
//                            case CONSTRUCTOR:
//                            case MAIN_CONSTRUCTOR: {
//                                JConstructor runModuleMethod = dt.addConstructor(
//                                        JSignature.of(compilerContext.types(), node.getSignature()),
//                                        node.getArgNames(), new BodyJInvoke(node), node.getModifiers(), false
//                                );
//                                node.setInvokable(runModuleMethod);
//                                break;
//                            }
//                        }
//                    }
//                } else {
//                    throw new JParseException("Expect always a method");
////                node.setInvokable(
////                        new JFunctionLocal(
////                                node.getName(),
////                                returnType,
////                                sigTypes.toArray(new JType[0]),
////                                varArg,
////                                new BodyJInvoke(node)
////                        )
////                );
//                }
//            }
////            if (compilerContext.path().size() == 1) {
////                if (node.userObjects().get("generated_in_module") == null) {
////                    node.userObjects().put("generated_in_module", true);
////                    //this is a global var
////                    HNDeclareInvokable old = linker.module().getMethodDeclarations().stream().filter(x -> x.getInvokable().signature().equals(node.getInvokable().signature())).findFirst().orElse(null);
////                    if (old != null) {
////                        compilerContext.log().error("S003", "Function already declared : " + node.getInvokable().signature(), node.token());
////                    } else {
////                        linker.module().addMethod((HNDeclareInvokable) node.copy(), compilerContext);
////                    }
////                }
////            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    public JNode onDeclareTuple(HLJCompilerContext compilerContext) {
//        HNDeclareTuple node = (HNDeclareTuple) compilerContext.node();
//        processNextCompilerStage(node, node.getItems(), compilerContext);
//        processNextCompilerStage(node::getInitValue, node::setInitValue, compilerContext);
//        if (node.getInitValue() instanceof HNTuple) {
//            HNTuple vt = (HNTuple) node.getInitValue();
//            HNDeclareIdentifier[] jNodeHDeclareIdentifiers = node.getItems();
//            boolean ok = true;
//            List<JType> tupleTypes = new ArrayList<>();
//            for (int i = 0; i < jNodeHDeclareIdentifiers.length; i++) {
//                HNDeclareIdentifier identifier = jNodeHDeclareIdentifiers[i];
//                if (vt.getItems()[i].getType() != null) {
//                    ElementTypeAndConstraint ec = onDeclareIdentifier_detectElementTypeAndConstraint(
//                            vt.getItems()[i].getType(), node.getAssignOperator().image.charAt(0),
//                            compilerContext.types()
//                    );
//                    if (ec == null) {
//                        ok = false;
//                        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                            compilerContext.log().error("S015", "not an iterable type " + vt.getItems()[i].getType(), vt.getItems()[i].startToken());
//                        }
//                    } else {
////                        identifier.setIdentifierType(ec.valType);
//                        identifier.setIdentifierTypeName(HUtils.createTypeToken(ec.valType));
//                        identifier.setInitValueConstraint(ec.valCstr);
//                        tupleTypes.add(ec.valType);
//                    }
//                }
//            }
////            if(ok){
////                node.setType(HUtils.tupleType(compilerContext.types(),tupleTypes.toArray(new JType[0])));
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
////            }
//        } else {
//
//        }
////        JType identifierType = node.getIdentifierType();
////        if (identifierType == null) {
////            JTypeName typeName = node.getIdentifierTypeName();
////            if (typeName == null) {
////                //expect type from value
////                JNode dv = node.getDefaultValue();
////                identifierType = dv==null?null:dv.getType();
////                if (identifierType != null) {
////                    node.setIdentifierType(identifierType);
////                    node.setIdentifierTypeName(identifierType.typeName());
////                }
////            }
////            if (identifierType == null && compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
////                if(node.getIdentifierTypeName()!=null) {
////                    identifierType = compilerContext.lookupType(node.getIdentifierTypeName());
////                    if (identifierType != null) {
////                        node.setIdentifierType(identifierType);
////                        node.setIdentifierTypeName(identifierType.typeName());
////                    }
////                }
////            }
////        }
////        if (identifierType != null && node.getDefaultValue() == null) {
////            HNLiteral dvl = new HNLiteral(identifierType.defaultValue(),null,node.token());
////            dvl.setType(identifierType);
////            node.setDefaultValue(dvl);
////        }
////        if (node.getDefaultValue() != null) {
////            JNode dv = node.getDefaultValue();
////            if(dv.getType()==null && node.getIdentifierType()!=null){
////                if(dv instanceof HNLiteral){
////                    HNLiteral lit = (HNLiteral) dv;
////                    if(lit.getValue()==null) {
////                        lit.setType(node.getIdentifierType());
////                    }
////                }else if(dv instanceof HNLiteralDefault){
////                    HNLiteralDefault lit = (HNLiteralDefault) dv;
////                    if(lit.getTypeName()==null){
////                        lit.setTypeName(node.getIdentifierType().typeName());
////                        lit.setType(node.getIdentifierType());
////                    }
////                }
////            }
////        }
////
////        if (node.declaringTypeNode != null) {
////            node.setModifiers(HUtils.publifyModifiers(node.getModifiers()));
////            if (node.jField == null) {
////                if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
////                    if (identifierType == null) {
////                        if(node.getIdentifierTypeName()!=null) {
////                            identifierType = compilerContext.lookupType(node.getIdentifierTypeName());
////                            node.setIdentifierType(identifierType);
////                        }else{
////                            //this is a var/val declaration.
////                            //will detect type from value
////                            JNode dv = node.getDefaultValue();
////                            if(dv!=null){
////                                if(dv.getType()!=null){
////                                    identifierType=dv.getType();
////                                    node.setIdentifierType(identifierType);
////                                    node.setIdentifierTypeName(identifierType.typeName());
////                                }
////                            }
////                        }
////                    }
////                    if(identifierType!=null) {
////                        JType declaringType = node.declaringTypeNode.getOrCreateType(compilerContext);
////                        JField jField = declaringType.addField(node.getName(),
////                                identifierType,
////                                node.getModifiers(),
////                                false
////                        );
////                        compilerContext.debug("AddField",jField);
////                        node.setjField(jField);
////                    }
////                }
////            }
////        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return compilerContext.node();
//    }
//
//
//    public JNode onDeclareType(HLJCompilerContext compilerContext) {
//        //validate structure first
//        HNBlock body = null;
//        HNDeclareType typeDeclaration = (HNDeclareType) compilerContext.node();
//        typeDeclaration.setModifiers(HUtils.publifyModifiers(typeDeclaration.getModifiers()));
//        JType thisType = compilerContext.getOrCreateType(typeDeclaration);
//        if (!(typeDeclaration.getBody() instanceof HNBlock)) {
//            HNBlock body2 = new HNBlock(HNBlock.BlocType.CLASS_BODY, new JNode[]{typeDeclaration.getBody()},
//                    typeDeclaration.getBody().startToken(),
//                    typeDeclaration.getBody().endToken()
//            );
//            body = body2;
//            typeDeclaration.setBody(body);
//            typeDeclaration.setImmediateBody(true);
//        } else {
//            body = (HNBlock) typeDeclaration.getBody();
//        }
//        List<JNode> statements = body.getStatements();
//        JType newType = compilerContext.getOrCreateType(typeDeclaration);
//        if (!statements.isEmpty()) {
//            for (JNode statement : statements) {
//                //initialize hierarchy before propagating compiler stage
//                if (statement instanceof HNDeclareIdentifier) {
//                    HNDeclareIdentifier declaration = (HNDeclareIdentifier) statement;
//                    declaration.setDeclaringType(typeDeclaration);
//                } else if (statement instanceof HNDeclareInvokable) {
//                    HNDeclareInvokable declaration = (HNDeclareInvokable) statement;
//                    declaration.setDeclaringType(typeDeclaration);
//                } else if (statement instanceof HNDeclareType) {
//                    HNDeclareType declaration = (HNDeclareType) statement;
//                    declaration.setDeclaringType(typeDeclaration);
//                }
//                statement = processNextCompilerStage(statement, compilerContext);
//                if (statement instanceof HNDeclareIdentifier) {
//                    HNDeclareIdentifier declaration = (HNDeclareIdentifier) statement;
//                    declaration.setModifiers(HUtils.publifyModifiers(declaration.getModifiers()));
//                    JNode dv = declaration.getInitValue();
//                    if (dv == null) {
//                        dv = new HNLiteralDefault(declaration.getIdentifierTypeName(), declaration.startToken());
//                        dv.setStartToken(declaration.startToken());
//                        declaration.setUserObject("NoInitialValue");
//                        declaration.setInitValue(dv);
//                    }
//                    typeDeclaration.addField(declaration, compilerContext);
//                    if (HUtils.isStatic(declaration.getModifiers())) {
//                        boolean shouldDeferInitialization = false;
//                        if (shouldDeferInitialization) {
//                            declaration.setHiddenInitValue(dv);
//                            declaration.setInitValue(null);
//                            for (JToken identifierName : declaration.getIdentifierTokens()) {
//                                HNAssign fs = new HNAssign(new HNField(null, identifierName,
//                                        compilerContext.getOrCreateType(typeDeclaration).typeName(),
//                                        null,
//                                        declaration.startToken(),
//                                        declaration.endToken()
//                                ), JTokenUtils.createOpToken("="),
//                                        ((HNode) dv).copy(),
//                                        declaration.startToken(),
//                                        declaration.endToken()
//                                );
//                                typeDeclaration.addStaticInitializerNode(fs);
//                            }
////                        fs.setUserObject("DefaultInstanceInitializer");
////                        if (declaration.isSetUserObject("NoInitialValue")) {
////                            fs.setUserObject("NoInitialValue");
////                        }
//                        }
//                    } else {
//                        boolean shouldDeferInitialization = false;
//                        if (shouldDeferInitialization) {
//                            declaration.setHiddenInitValue(dv);
//                            declaration.setInitValue(null);
//                            HNAssign fs = new HNAssign(
//                                    new HNField(new HNThis(null, declaration.startToken()), declaration.getIdentifierTokens()[0],
//                                            compilerContext.getOrCreateType(typeDeclaration).typeName(), null
//                                            , declaration.startToken()
//                                            , declaration.endToken()
//                                    ), JTokenUtils.createOpToken("="),
//                                    dv, declaration.startToken(), declaration.endToken()
//                            );
//                            typeDeclaration.addInstanceInitializerNode(fs);
//                        }
////                        fs.setUserObject("DefaultInstanceInitializer");
////                        if (declaration.isSetUserObject("NoInitialValue")) {
////                            fs.setUserObject("NoInitialValue");
////                        }
//                    }
//                } else if (statement instanceof HNDeclareInvokable) {
//                    HNDeclareInvokable declaration = (HNDeclareInvokable) statement;
//                    declaration.setDeclaringType(typeDeclaration);
//                    List<HNDeclareIdentifier> arguments = declaration.getArguments();
//                    String name = declaration.getName();
//                    if (name == null) {
//                        name = "";
//                    }
//                    if (name.isEmpty() && Modifier.isStatic(declaration.getModifiers()) /*static only*/) {
//                        if (declaration.getModifiers() != Modifier.STATIC) {
//                            compilerContext.log().error("S037", "static initializer do not accept modifiers", declaration.startToken());
//                        }
//                        if (arguments.size() > 0) {
//                            compilerContext.log().error("S038", "static initializer could not have arguments", declaration.startToken());
//                        }
//                        JNode dbody = declaration.getBody();
//                        if (dbody instanceof HNBlock) {
//                            for (JNode blocStatement : ((HNBlock) dbody).getStatements()) {
//                                typeDeclaration.addStaticInitializerNode(blocStatement);
//                            }
//                        } else {
//                            declaration.setImmediateBody(true);
//                            typeDeclaration.addStaticInitializerNode(dbody);
//                        }
//                    } else if (declaration.isConstructor() || name.equals(typeDeclaration.getName()) || name.equals("constructor")) {
//                        declaration.setModifiers(HUtils.publifyModifiers(declaration.getModifiers()));
//                        if (declaration.getReturnTypeName() != null) {
//                            compilerContext.log().error("S039", "Constructor must not have a type", declaration.startToken());
//                        }
//                        typeDeclaration.addConstructor(declaration);
//                    } else if (!name.isEmpty()) {
//                        declaration.setModifiers(HUtils.publifyModifiers(declaration.getModifiers()));
//                        if (declaration.getReturnTypeName() == null) {
//                            declaration.setReturnTypeName(compilerContext.createSpecialTypeToken("void"));
//                            compilerContext.log().warn("W002", "Method without return type. Forced to void", declaration.startToken());
//                        }
//                        typeDeclaration.addMethod(declaration, compilerContext);
//                    } else {
//                        throw new JParseException("No Name method is not supported");
//                    }
//                } else if (statement instanceof HNDeclareType) {
//                    HNDeclareType declaration = (HNDeclareType) statement;
//                    declaration.setModifiers(HUtils.publifyModifiers(declaration.getModifiers()));
//                    typeDeclaration.addInnerType(declaration, compilerContext);
//                } else {
//                    typeDeclaration.addInstanceInitializerNode(statement);
//                }
//            }
//            body.setStatements(new ArrayList<>());
//        }
//        if (typeDeclaration.getMainConstructor() == null && typeDeclaration.getMainConstructorArgs() != null) {
//            HNDeclareInvokable mainConstructor = new HNDeclareInvokable(JNodeUtils.copy(typeDeclaration.getNameToken())
//                    , typeDeclaration.startToken()
//                    , typeDeclaration.endToken()
//            );
//            mainConstructor.setNameToken(typeDeclaration.getNameToken());
//            mainConstructor.setDeclaringType(typeDeclaration);
//            mainConstructor.setInvokableType(HLInvokableType.MAIN_CONSTRUCTOR);
//            //JSignature sig = HUtils.sig(node.getName(), mainConstructorArgs);
//            List<JNode> mainConstructorStatements=new ArrayList<>();
//            for (HNDeclareIdentifier fieldDeclaration : typeDeclaration.getMainConstructorArgs()) {
//                fieldDeclaration.setDeclaringType(typeDeclaration);
//                int modifiers = HUtils.publifyModifiers(fieldDeclaration.getModifiers());
//                if (HUtils.isStatic(modifiers)) {
//                    modifiers = HUtils.removeModifierStatic(modifiers);
//                    compilerContext.log().error("S004", "Arguments cannot be static", fieldDeclaration.startToken());
//                }
//                fieldDeclaration.setModifiers(modifiers);
//                fieldDeclaration.setType(compilerContext.lookupType(fieldDeclaration.getIdentifierTypeName().getTypename()));
////                fieldDeclaration.setjField(new JFieldFromSource(fieldDeclaration, compilerContext));
//                mainConstructor.getArguments().add(fieldDeclaration);
//                JNode defaultValue = fieldDeclaration.getInitValue();
//                boolean noInitial = false;
//                if (defaultValue == null) {
//                    noInitial = true;
//                    defaultValue = new HNLiteralDefault(fieldDeclaration.getType());
//                }
//                mainConstructorStatements.add(new HNAssign(new HNField(
//                        new HNThis(typeDeclaration.getjType(), fieldDeclaration.startToken()), fieldDeclaration.getIdentifierTokens()[0],
//                        newType.typeName(),
//                        null,
//                        fieldDeclaration.startToken(),
//                        fieldDeclaration.endToken()
//                ), JTokenUtils.createOpToken("="),
//                        new HNVar(fieldDeclaration.getIdentifierTokens()[0], fieldDeclaration.getIdentifierTypeName(), fieldDeclaration.startToken())
//                        , fieldDeclaration.startToken()
//                        , fieldDeclaration.endToken()
//                ));
//                typeDeclaration.addField(fieldDeclaration, compilerContext);
//
//                HNAssign fs = new HNAssign(new HNField(
//                        new HNThis(thisType, fieldDeclaration.startToken()),
//                        fieldDeclaration.getIdentifierTokens()[0], newType.typeName(),
//                        null, fieldDeclaration.startToken(), fieldDeclaration.endToken()), JTokenUtils.createOpToken("="),
//                        defaultValue
//                        , fieldDeclaration.startToken()
//                        , fieldDeclaration.endToken()
//                );
//
//                fs.setUserObject("DefaultInstanceInitializer");
//                if (noInitial) {
//                    fs.setUserObject("NoInitialValue");
//                }
//
//                typeDeclaration.addInstanceInitializerNode(fs);
//            }
//
//            mainConstructor.setBody(new HNBlock(HNBlock.BlocType.METHOD_BODY,
//                    mainConstructorStatements.toArray(new JNode[0]),
//                    typeDeclaration.startToken(),
//                    mainConstructorStatements.get(mainConstructorStatements.size()-1).endToken()
//                    ));
//            mainConstructor.setSignature(
//                    JNameSignature.of(
//                            mainConstructor.getName(),
//                            mainConstructor.getArguments().stream()
//                                    .map(HNDeclareIdentifier::getIdentifierTypeName).toArray(JTypeName[]::new)
//                    )
//            );
//            typeDeclaration.setMainConstructorArgs(null);
//            typeDeclaration.addConstructor(mainConstructor);
//            processNextCompilerStage(mainConstructor, compilerContext.packageName(thisType.name()));
//        }
//
//        if (compilerContext.isStage(STAGE_1_DECLARATIONS)) {
//            //check for duplicate definitions
//            DuplicateDefChecker ddc = new DuplicateDefChecker();
//            ddc.addIdentifiers(typeDeclaration.getFieldDeclarations());
//            ddc.addInvokables(typeDeclaration.getMethodDeclarations());
//            ddc.addInvokables(typeDeclaration.getConstructorDeclarations());
//            ddc.addSubTypes(typeDeclaration.getInnerTypeDeclarations());
//            ddc.addBody(typeDeclaration.getBody());
//            ddc.checkDuplicates(compilerContext.log());
//        }
//
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getExtends(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getMainConstructorArgs(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getConstructorDeclarations(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getInstanceInitializerNodes(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getStaticInitializerNodes(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getMethodDeclarations(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getFieldDeclarations(),compilerContext);
//        processNextCompilerStage(typeDeclaration, typeDeclaration.getInnerTypeDeclarations(),compilerContext);
//        processNextCompilerStage(typeDeclaration::getBody, typeDeclaration::setBody,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            DefaultJType dt = (DefaultJType) newType;
//            if (dt.instanceInitializer() == null) {
//                dt.instanceInitializer(new JNodeHBlocJInvoke(typeDeclaration.getInstanceInitializerNodes()));
//            }
//            if (dt.staticInitializer() == null) {
//                dt.staticInitializer(new JNodeHBlocJInvoke(typeDeclaration.getStaticInitializerNodes()));
//            }
//        }
//        if (typeDeclaration.getType() == null) {
//            typeDeclaration.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        if (typeDeclaration instanceof HNDeclareTypeMetaPackage) {
//            for (HNDeclareType topLevelTypeNode : ((HNDeclareTypeMetaPackage) typeDeclaration).getTopLevelTypeNodes()) {
////                    .appendNode(topLevelTypeNode)
//                processCompilerStage(compilerContext.nextNode(topLevelTypeNode));
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onDotClass(HLJCompilerContext compilerContext) {
//        HNDotClass node = (HNDotClass) compilerContext.node();
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            if (node.getType() == null && node.getTypeRefName().getTypeVal() != null) {
//                node.setType(((JRawType) compilerContext.types().forName(Class.class.getName())).parametrize(
//                        node.getTypeRefName().getTypeVal()
//                ));
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onDotThis(HLJCompilerContext compilerContext) {
//        HNDotThis node = (HNDotThis) compilerContext.node();
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            if (node.getType() == null) {
//                JNode n = node.parentNode();
//                boolean staticContext = false;
//                while (n != null && !(n instanceof HNDeclareType)) {
//                    if (n instanceof HNDeclareIdentifier) {
//                        HNDeclareIdentifier f = (HNDeclareIdentifier) n;
//                        staticContext |= (f.getModifiers() & Modifier.STATIC) != 0;
//                    } else if (n instanceof HNDeclareInvokable) {
//                        HNDeclareInvokable f = (HNDeclareInvokable) n;
//                        staticContext |= (f.getModifiers() & Modifier.STATIC) != 0;
//                    }
//                    n = n.parentNode();
//                }
//                if (n == null || staticContext) {
//                    compilerContext.log().error("S023", "no instance context could be resolved to use 'this'", (n == null ? node : n).startToken());
//                } else {
//                    if (node.getTypeRefName().getTypeVal() != null) {
//                        node.setType(node.getTypeRefName().getTypeVal());
//                    }
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onExtends(HLJCompilerContext compilerContext) {
//        HNExtends node = (HNExtends) compilerContext.node();
//        processNextCompilerStage(node, node.getArguments(),compilerContext);
//        return node;
//    }
//
//    public JNode onField(HLJCompilerContext compilerContext) {
//        HNField n = (HNField) compilerContext.node();
//        processNextCompilerStage(n::getInstanceNode, n::setInstanceNode,compilerContext);
//        if (n.getField() == null) {
//            if (n.getDeclaringTypeName() != null) {
//                JType jType = compilerContext.lookupType(n.getDeclaringTypeName());
//                if (jType != null) {
//                    JField f = jType.declaredFieldOrNull(n.getFieldNameToken().image);
//                    if (f != null) {
//                        n.setField(f);
//                    }
//                }
//            }
//        }
//        if (n.getInstanceNode() != null) {
//            if (n.getInstanceNode().getType() != null) {
//                n.setDeclaringTypeName(n.getInstanceNode().getType().typeName());
//                if (n.getField() == null) {
//                    JField f = n.getInstanceNode().getType().declaredFieldOrNull(n.getFieldNameToken().image);
//                    if (f != null) {
//                        n.setField(f);
//                    }
//                }
//            }
//        }
//
//        if (n.getType() == null) {
//            if (n.getField() != null) {
//                n.setType(n.getField().type());
//            }
//        }
//        return n;
//    }
//
//    public JNode onFieldUnchecked(HLJCompilerContext compilerContext) {
//        HNFieldUnchecked n = (HNFieldUnchecked) compilerContext.node();
//        processNextCompilerStage(n::getInstanceNode, n::setInstanceNode,compilerContext);
//        processNextCompilerStage(n::getFieldName, n::setFieldName,compilerContext);
//        n.setType(JTypeUtils.forObject(compilerContext.context().types()));
//        return n;
//    }
//
//    public JNode onFor(HLJCompilerContext compilerContext) {
//        HNFor node = (HNFor) compilerContext.node();
//        if (node.isIteratorType()) {
//            HNFor n = null;
//            List<JNode> initExprs = node.getInitExprs();
//            List<JNode> initExprs2 = new ArrayList<>();
//            for (int i = 0; i < initExprs.size(); i++) {
//                JNode ii = initExprs.get(i);
//                if (ii instanceof HNDeclareIdentifier) {
//                    initExprs2.add(ii);
//                    ((HNDeclareIdentifier) ii)
//                            .setInitValueConstraint(
//                                    (i != 0) ?
//                                            InitValueConstraint.ITERABLE : InitValueConstraint.ITERATOR);
//                } else if (ii instanceof HNDeclareTuple) {
//                    HNDeclareTuple dt = (HNDeclareTuple) ii;
//                    JNode tiv = dt.getInitValue();
//                    if (tiv instanceof HNTuple) {
//                        HNTuple tivt = (HNTuple) tiv;
//                        JNode[] items = tivt.getItems();
//                        for (int i1 = 0, itemsLength = items.length; i1 < itemsLength; i1++) {
//                            HNDeclareIdentifier itemt = dt.getItems()[i1];
//                            itemt.setInitValue(items[i1]);
//                            itemt
//                                    .setInitValueConstraint(
//                                            (i != 0 && i1 != 0) ?
//                                                    InitValueConstraint.ITERABLE : InitValueConstraint.ITERATOR);
//                            initExprs2.add(itemt);
//                        }
//                    } else {
//                        initExprs2.add(ii);
//                    }
//                }
//            }
//            for (int i = initExprs2.size() - 1; i >= 0; i--) {
//                JNode ii = initExprs2.get(i);
//                if (i == initExprs2.size() - 1) {
//                    if (i == 0) {
//                        //this is okkay
//                        n = node;
//                    } else {
//                        HNFor n2 = new HNFor(ii.startToken());
//                        n2.setUserObject("generatedFor");
//                        n2.setIteratorType(true);
//                        n2.addInit(ii);
//                        for (JNode inc : node.getIncs()) {
//                            n2.addInc(inc);
//                        }
//                        n2.setFilter(node.getFilter());
//                        n2.setBody(node.getBody());
//                        n = n2;
//                    }
//                } else if (i == 0) {
//                    HNFor n2 = node;
//                    n2.setBody(n);
//                    n2.setFilter(null);
//                    n2.getIncs().clear();
//                    n2.getInitExprs().clear();
//                    n2.addInit(ii);
//                    n = n2;
//                } else {
//                    HNFor n2 = new HNFor(ii.startToken());
//                    n2.setUserObject("generatedFor");
//                    n2.setIteratorType(true);
//                    n2.addInit(ii);
//                    n2.setBody(n);
//                    n = n2;
//                }
//            }
//            node = n;
//        }
//        processNextCompilerStage(node::getBody, node::setBody,compilerContext);
//        processNextCompilerStage(node::getFilter, node::setFilter,compilerContext);
//        processNextCompilerStage(node, node.getIncs(),compilerContext);
//        processNextCompilerStage(node, node.getInitExprs(),compilerContext);
//        JTypeOrLambda ft = compilerContext.jTypeOrLambda(node.getFilter());
//        if (ft != null) {
//            if (!ft.isType() || !ft.getType().boxed().name().equals("java.lang.Boolean")) {
//                compilerContext.log().error("S013", "for statement filter must be of boolean type", node.getFilter().startToken());
//            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    public JNode onIdentifier(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNIdentifier node = (HNIdentifier) compilerContext.node();
//        JToken name = node.getNameToken();
//        JNodePath path = compilerContext.path().parent();
//        if ("this".equals(name.image) || "super".equals(name.image)) {
//            for (int i = 0; i < path.size(); i++) {
//                JNode p = path.parent(i);
//                if (p instanceof HNDeclareType) {
//                    HNDeclareType cd = (HNDeclareType) p;
//                    if ("this".equals(name.image)) {
//                        return new HNThis(cd.getjType(), p.startToken());
//                    } else {
//                        return new HNSuper(cd.getjType(), p.startToken());
//                    }
//                }
//            }
//            compilerContext.log().error("S004", "No enclosing Type to use " + name.image, node.startToken());
//            return new HNThis(null, node.startToken());
//        }
//        if (node.parentNode() instanceof HNParsPostfix && ((HNParsPostfix) node.parentNode()).getLeft() == node) {
//            //is this a method;
//            //lets assume that for now and process this that way later.
//            return node;
//        }
//        HNDeclareTokenBase v = compilerContext.lookupVarDeclarationOrNull(name.image, node.startToken());
//        if (v != null) {
//            boolean fieldAnyways = v instanceof HNDeclareIdentifier && ((HNDeclareIdentifier) v).isOfTypeField();
//            JField jfield = null;
//            JNode vparent = null;
//            if (v instanceof HNDeclareIdentifier) {
//                jfield = ((HNDeclareIdentifier) v).getField();
//            } else if (v instanceof JLibField) {
//                jfield = ((JLibField) v).getField();
//            }
//            if (v instanceof JNode) {
//                vparent = ((JNode) v).parentNode();
//            }
//            boolean moduleField = false;
//            HNDeclareType parentTypeDec = null;
//            if (vparent instanceof HNBlock) {
//                HNBlock b = (HNBlock) vparent;
//                if (b.parentNode() == null || b.parentNode() instanceof HNDeclareType) {
//                    fieldAnyways = true;
//                    moduleField = b.parentNode() instanceof HNDeclareTypeMetaPackage;
//                    parentTypeDec = (HNDeclareType) b.parentNode();
//                }
//            } else if (vparent instanceof HNDeclareType) {
//                fieldAnyways = true;
//                moduleField = vparent instanceof HNDeclareTypeMetaPackage;
//                parentTypeDec = (HNDeclareType) vparent;
//            }
//            if (fieldAnyways || jfield != null) {
//                if (jfield != null) {
//                    return new HNField(null, jfield, node.startToken());
//                } else {
//                    if (moduleField) {
//                        return new HNField(null, name, compilerContext.getOrCreateType(compilerContext.metaPackageType()).typeName(), null, node.startToken(), node.endToken());
//                    } else {
//                        if (parentTypeDec == null) {
//                            throw new JFixMeLaterException();
//                        } else {
//                            return new HNField(null, name, compilerContext.getOrCreateType(parentTypeDec).typeName(), null, node.startToken(), node.endToken());
//                        }
//                    }
//                }
//            } else {
//                if (v.getIdentifierType() != null) {
//                    return new HNVar(name, HUtils.createTypeToken(v.getIdentifierType()), node.startToken());
//                }
//            }
//        } else {
//            if (name.image.equals("$")) {
//                //look for JNode firstBrackets=null;
//                JNode p = node;
//                while (p != null) {
//                    JNode p1 = p.parentNode();
//                    if (p1 instanceof HNArrayCall) {
//                        break;
//                    }
//                    p = p1;
//                }
//                if (p != null) {
//                    int goodIndex = -1;
//                    HNArrayCall arr = (HNArrayCall) p.parentNode();
//                    JNode base = null;
//                    HNArrayCall p1 = (HNArrayCall) p.parentNode();
//                    JNode[] items1 = p1.getIndexNodes();
//                    base = p1.getArrayInstanceNode();
//                    for (int i = 0; i < items1.length; i++) {
//                        if (items1[i] == p) {
//                            goodIndex = i;
//                            break;
//                        }
//                    }
//                    if (goodIndex >= 0) {
//                        JType baseType = base.getType();
//                        if (baseType != null) {
//                            if (baseType.isArray()) {
//                                //good index should be zero!!
//                                return new HNBracketsPostfixLastIndex(((HNode) base).copy(), goodIndex, arr.startToken());
//                            } else {
//                                if (compilerContext.isStage(STAGE_3_WIRE_CALLS)
////                                        && !arr.isSetUserObject("AssignLeftNode")
//                                ) {
//                                    JTypeOrLambda baseTypeOrLambda = compilerContext.jTypeOrLambda(base);
//                                    if (baseTypeOrLambda != null) {
//                                        String baseTypeNameSafe = baseType.name();
//                                        if (baseTypeNameSafe.endsWith("[]")) {
//                                            baseTypeNameSafe = "(" + baseTypeNameSafe + ")";
//                                        }
////                                        List<String> alternatives = new ArrayList<>();
//                                        JType intType = JTypeUtils.forInt(compilerContext.types());
//                                        JNode[] nargs = new JNode[]{base, new HNLiteral(goodIndex,
//                                                intType,
//                                                node.startToken()
//                                        )};
//                                        JTypeOrLambda[] ntypes = new JTypeOrLambda[]{baseTypeOrLambda, JTypeOrLambda.of(intType)};
//                                        FindMatchFailInfo failInfo = new FindMatchFailInfo("[$] operator");
//                                        JInvokable m = compilerContext.findFunctionMatch(JOnError.NULL, "upperBound", HFunctionType.NORMAL, ntypes, arr.startToken(), failInfo);
//                                        if (m != null) {
//                                            return createFunctionCall(arr.startToken(), m, nargs);
//                                        } else {
////                                            alternatives.add("instance method: " + baseTypeNameSafe + ".upperBound" + JTypeOrLambda.signatureString(new JTypeOrLambda(intType)));
////                                            alternatives.add("static   method: " + "upperBound" + JTypeOrLambda.signatureString(ntypes));
//                                        }
//                                        if (goodIndex == 0) {
//                                            //one more test!
//                                            nargs = new JNode[]{base};
//                                            ntypes = new JTypeOrLambda[]{baseTypeOrLambda};
//                                            m = compilerContext.findFunctionMatch(JOnError.NULL, "upperBound", HFunctionType.NORMAL, ntypes, arr.startToken(), failInfo);
//                                            if (m != null) {
//                                                return createFunctionCall(arr.startToken(), m, nargs);
//                                            } else {
////                                                alternatives.add("instance method: " + baseTypeNameSafe + ".upperBound()");
////                                                alternatives.add("static   method: " + "upperBound" + JTypeOrLambda.signatureString(baseTypeOrLambda));
//                                            }
//                                        }
////                                        StringBuilder errorMsg = new StringBuilder("To use " + baseTypeNameSafe + "[$] operator, you should implement either ");
////                                        for (String alternative : alternatives) {
////                                            errorMsg.append("\n").append(alternative);
////                                        }
////                                        compilerContext.log().error("S010", errorMsg.toString(), node.startToken());
//                                        failInfo.trace(compilerContext.log(), node.startToken());
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                            compilerContext.log().error("S061", "$ placeholder cannot be used in this context. unknown symbol", node.startToken());
//                        }
//                    }
//                } else {
//                    if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                        compilerContext.log().error("S061", "$ placeholder cannot be used in this context. unknown symbol", node.startToken());
//                    }
//                }
//            }
//            if (node.parentNode() instanceof HNOpBinaryCall && ((HNOpBinaryCall) node.parentNode()).getName().equals("->")) {
//                //this an implicit declaration, ignore...
//                //example is i->... where we look after i's type
//            } else if (
//                    node.parentNode() instanceof HNPars
//                            && node.parentNode().parentNode() instanceof HNOpBinaryCall
//                            && ((HNOpBinaryCall) (node.parentNode().parentNode()))
//                            .getName().equals("->")
//            ) {
//                //this an implicit declaration, as well. ignore...
//                //example is (i)->... where we look after i's type
//            } else if (node.parentNode() instanceof HNParsPostfix) {
//                //this a method call
//            } else {
//                JType t = compilerContext.lookupTypeOrNull(DefaultTypeName.of(name.image));
//                if (t != null) {
//                    return new HNTypeToken(t, node.startToken());
//                }
//            }
//        }
//        return node;
//
//    }
//
//    public JNode onIf(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNIf node = (HNIf) compilerContext.node();
//        JContext context = compilerContext.context();
//        processNextCompilerStage(node, node.getBranches(),compilerContext);
//        processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//        List<HNIf.WhenDoBranchNode> cond = node.getBranches();
//        boolean allTypesOk = true;
//        JTypeOrLambda upperBoundCond = null;
//        JTypeOrLambda upperBoundResult = null;
//        for (int i = 0; i < cond.size(); i++) {
//            JTypeOrLambda a = compilerContext.jTypeOrLambda(cond.get(i).getWhenNode());
//            if (a == null) {
//                allTypesOk = false;
//                break;
//            } else {
//                if (a.isType()) {
//                    if (upperBoundCond == null) {
//                        upperBoundCond = JTypeOrLambda.of(a.getType());
//                    } else {
//                        upperBoundCond = JTypeOrLambda.of(a.getType().firstCommonSuperType(upperBoundCond.getType()));
//                    }
//                } else {
//                    throw new IllegalArgumentException("Fix me later");
//                }
//            }
//            a = compilerContext.jTypeOrLambda(cond.get(i).getDoNode());
//            if (a == null) {
//                allTypesOk = false;
//                break;
//            } else {
//                if (a.isType()) {
//                    if (upperBoundResult == null) {
//                        upperBoundResult = JTypeOrLambda.of(a.getType());
//                    } else {
//                        upperBoundResult = JTypeOrLambda.of(a.getType().firstCommonSuperType(upperBoundResult.getType()));
//                    }
//                } else {
//                    throw new IllegalArgumentException("Fix me later");
//                }
//            }
//        }
//        if (node.getElseNode() != null) {
//            JTypeOrLambda a = compilerContext.jTypeOrLambda(node.getElseNode());
//            if (a == null) {
//                allTypesOk = false;
//            } else {
//                if (a.isType()) {
//                    if (upperBoundResult == null) {
//                        upperBoundResult = JTypeOrLambda.of(a.getType());
//                    } else {
//                        upperBoundResult = JTypeOrLambda.of(a.getType().firstCommonSuperType(upperBoundResult.getType()));
//                    }
//                } else {
//                    throw new IllegalArgumentException("Fix me later");
//                }
//            }
//        }
//        if (allTypesOk) {
//            if (upperBoundCond == null) {
//                compilerContext.log().error("S005", "null condition not allowed in if statement", node.startToken());
//            } else {
//                if (upperBoundResult.isType()) {
//                    node.setType(upperBoundResult.getType());
//                }
//                if (upperBoundCond.isType() && upperBoundCond.getType().boxed().name().equals("java.lang.Boolean")) {
//                    for (int i = 0; i < cond.size(); i++) {
//                        List<HNIf.IsVarReplacer> allReplacements = new ArrayList<>();
//                        HNIf.WhenDoBranchNode wdb = cond.get(i);
//                        if (allReplacements.size() > 0) {
//                            JNode dn = wdb.getDoNode();
//                            List<JNode> st=new ArrayList<>();
//                            JToken endToken=dn.startToken();
//                            for (HNIf.IsVarReplacer r : allReplacements) {
//                                st.add(new HNDeclareIdentifier(
//                                        new JToken[]{r.getName()},
//                                        r.getCastExpr(),
//                                        r.getType(),
//                                        JTokenUtils.createOpToken("="), r.getPlacement(),
//                                        r.getPlacement()
//                                ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//                            }
//                            HNBlock bl = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                                    st.toArray(new JNode[0]),
//                                    dn.startToken(),
//                                    endToken
//                                    );
//                        }
//
//                    }
//                    //Okkay!
//                    //do nothing special
//                } else {
//                    if (upperBoundResult == null) {
//                        upperBoundResult = JTypeOrLambda.of(JTypeUtils.forObject(context.types()));
//                    }
//                    if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                        JType branchesArrayType = context.types().forName("net.vpc.hadralang.stdlib.Branch<" + upperBoundCond.getType().name()
//                                + ","
//                                + upperBoundResult.getType().name()
//                                + ">").toArray();
//                        JType elseType = context.types().forName("java.util.function.Supplier<" + upperBoundResult.getType().name() + ">");
////                        List<String> alternatives = new ArrayList<>();
//                        FindMatchFailInfo failInfo = new FindMatchFailInfo("<if> function");
//                        JInvokable fct = compilerContext.findFunctionMatch(
//                                JOnError.TRACE, "If",
//                                HFunctionType.NORMAL, new JTypeOrLambda[]{
//                                        JTypeOrLambda.of(branchesArrayType),
//                                        JTypeOrLambda.of(elseType)
//                                }
//                                ,
//                                node.startToken(), failInfo
//                        );
//                        if (fct != null) {
//                            return createFunctionCall2(node.startToken(), fct,
//                                    new HNIf.JEvaluableFromBranchArray(branchesArrayType, cond),
//                                    new HNIf.JEvaluableFromSupplier(node.getElseNode(), elseType)
//                            );
//                        } else {
////                            alternatives.add("static   method: " + "If" + JTypeOrLambda.signatureString(
////                                    new JTypeOrLambda[]{
////                                            new JTypeOrLambda(branchesArrayType),
////                                            new JTypeOrLambda(elseType)
////                                    }
////                            ));
////                            StringBuilder errorMsg = new StringBuilder("To use if statement with non boolean condition, you should implement ");
////                            for (String alternative : alternatives) {
////                                errorMsg.append("\n").append(alternative);
////                            }
////                            compilerContext.log().error("S011", errorMsg.toString(), node.startToken());
//                        }
//                    }
//
//                }
//            }
//        }
//        return node;
//    }
//
//    public JNode onImport(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        return compilerContext.node();
//    }
//
//    public JNode onInvokerCall(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNInvokerCall node = (HNInvokerCall) compilerContext.node();
//        JNode[] nargs = node.getArgs();
//        processNextCompilerStage(node, nargs,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            if (node.impl() == null || node.getType() == null) {
//                JTypeOrLambda[] ntypes = compilerContext.jTypeOrLambdas(nargs);
//                if (ntypes != null) {
//                    JEvaluable[] eargs = JNodeUtils.getEvaluatables(nargs);
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, node.getName(), HFunctionType.NORMAL, ntypes, node.startToken());
//                    if (f != null) {
//                        node.setImpl(new JInvokablePrefilled(f, eargs));
//                        node.setType(f.returnType());
//                    }
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onIs(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNIs node = (HNIs) compilerContext.node();
//        if (node.getIdentifierNames().length != 1) {
//            throw new JShouldNeverHappenException();
//        }
//        JToken identifierName = node.getIdentifierToken();
//        processNextCompilerStage(node::getBase, node::setBase,compilerContext);
//        if (node.getIdentifierTypeName() == null) {
//            return new HNOpBinaryCall(JTokenUtils.createOpToken("=="), node.getBase(), new HNLiteral(null, null, node.startToken()), node.startToken(), node.endToken());
//        }
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
////            if(node.getIdentifierType()==null && node.getIdentifierTypeName()!=null){
////                node.identifierType=compilerContext.lookupType(node.getIdentifierTypeName().getTypename());
////            }
//            if (identifierName != null) {
//                JNode pn = node.parentNode();
//                if (HUtils.isBinaryAndNode(pn)) {
//                    HNOpBinaryCall o = (HNOpBinaryCall) pn;
//                    if (o.getLeft() == node) {
//                        JNode r = (((HNode) o.getRight())).findAndReplace(new HNIs.FindAndReplaceIsNode(node));
//                        o.setRight(r);
//                    } else if (o.getRight() == node
//                            && HUtils.isBinaryAndNode(o.parentNode())
//                            && (((HNOpBinaryCall) o.parentNode()).getLeft() == o)
//                    ) {
//                        HNOpBinaryCall op = (HNOpBinaryCall) o.parentNode();
//                        JNode r = (((HNode) op.getRight())).findAndReplace(new HNIs.FindAndReplaceIsNode(node));
//                        op.setRight(r);
//                    }
//                    if (pn.parentNode() instanceof HNIf.WhenDoBranchNode) {
//                        //this is a top level
//                        JNode dn = ((HNIf.WhenDoBranchNode) pn.parentNode()).getDoNode();
//                        JNode nn=(new HNDeclareIdentifier(
//                                new JToken[]{identifierName.copy()},
//                                new HNCast(
//                                        node.getIdentifierTypeName(),
//                                        ((HNode) node.getBase()).copy(),
//                                        null,
//                                        node.startToken(),
//                                        node.endToken()
//                                ), node.getIdentifierTypeName().getType(),
//                                JTokenUtils.createOpToken("="), node.startToken(), node.startToken()
//                        ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//
//                        HNBlock bl = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                                new JNode[]{nn},
//                                dn.startToken(),
//                                dn.startToken()
//                                );
//                        bl.add(dn);
//                        ((HNIf.WhenDoBranchNode) pn.parentNode()).setDoNode(bl);
//                    }
//                } else if (pn instanceof HNIf.WhenDoBranchNode) {
//                    HNIf.WhenDoBranchNode bb = (HNIf.WhenDoBranchNode) pn;
//                    if (bb.isTestAndSetUserObject("is_var_" + identifierName)) {
//                        JNode dn = bb.getDoNode();
//                        JNode nn=(new HNDeclareIdentifier(
//                                new JToken[]{identifierName.copy()},
//                                new HNCast(
//                                        node.getIdentifierTypeName(),
//                                        ((HNode) node.getBase()).copy(),
//                                        null,
//                                        node.startToken(),
//                                        node.endToken()
//                                ), node.getIdentifierTypeName().getType(),
//                                JTokenUtils.createOpToken("="), node.startToken(), node.startToken()
//                        ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//                        HNBlock bl = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                                new JNode[]{nn,dn},
//                                dn.startToken(),dn.endToken());
//                        ((HNIf.WhenDoBranchNode) pn).setDoNode(bl);
//                    }
//                }
//            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forBoolean(compilerContext.types()));
//        }
//        //check type here!!
//        return node;
//    }
//
//
//    public JNode onLambdaExpr(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNLambdaExpression node = (HNLambdaExpression) compilerContext.node();
//        if (compilerContext.isStage(STAGE_1_DECLARATIONS)) {
//            //check for duplicate definitions
//            DuplicateDefChecker ddc = new DuplicateDefChecker();
//            ddc.addIdentifiers(node.getArguments());
//            ddc.addBody(node.getBody());
//            ddc.checkDuplicates(compilerContext.log());
//        }
//        if (node.getBody().getType() != null) {
//            node.setReturnType(node.getBody().getType());
//        }
//        processNextCompilerStage(node, node.getArguments(),compilerContext);
//        processNextCompilerStage(node::getBody, node::setBody,compilerContext);
//        List<HNDeclareIdentifier> arguments = node.getArguments();
//        if (node.getInvokable() == null && compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            List<JType> sigTypes = new ArrayList<>();
//            boolean varArg = false;
//            JNameSignature signature = null;
//            boolean typeMissing = false;
//            for (int i = 0; i < arguments.size(); i++) {
//                HNDeclareIdentifier argument = arguments.get(i);
//                HNTypeToken typeName = argument.getIdentifierTypeName();
//                if (typeName == null) {
//                    typeMissing = true;
//                    break;
//                } else {
//                    if (i == arguments.size() - 1 && typeName.getTypename().isVarArg()) {
//                        varArg = true;
//                        typeName = HUtils.createTypeToken(typeName.getTypename().replaceVarArg());
//                    }
//                    JType jtype = compilerContext.lookupType(typeName.getTypename());
//                    argument.setIdentifierTypeName(HUtils.createTypeToken(jtype));
//                    sigTypes.add(jtype);
//                }
//            }
//            if (!typeMissing) {
//                signature = JNameSignature.of("", arguments.stream().map(x -> x.getIdentifierTypeName()).toArray(JTypeName[]::new));
//                node.setSignature(signature);
//                JType returnType = compilerContext.types().forName(node.getReturnTypeName());
//                node.setReturnType(returnType);
//            }
//        }
//        return node;
//    }
//
//    public JNode onLiteral(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNLiteral n = (HNLiteral) compilerContext.node();
//        if (n.getType() == null) {
//            if (n.getValue() == null) {
//                n.setType(JTypeUtils.forNull(compilerContext.types()));
//            } else {
//                //Literal types should be supported at early stages
//                JType p = compilerContext.types().typeOf(n.getValue());
//                //prefer primitive types for literals!
//                JType p0 = p.toPrimitive();
//                n.setType(p0 != null ? p0 : p);
//            }
//        }
//        return n;
//    }
//
//    public JNode onLiteralDefault(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            HNLiteralDefault n = (HNLiteralDefault) compilerContext.node();
//            if (n.getType() == null) {
//                JTypeName t = n.getTypeName();
//                if (t != null) {
//                    JType tt = compilerContext.lookupType(t);
//                    n.setType(tt);
//                }
//            }
//            return n;
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onStringInterpolation(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNStringInterop s = (HNStringInterop) compilerContext.node();
//        JTokenizer t = new JTokenizerImpl(new DefaultJTokenizerReader(new StringReader(s.getJavaMessageFormatString())),
//                true, true,
//                new JTokenPattern[]{
//                        new DollarVarPattern()
//                }
//        );
//        StringBuilder sb = new StringBuilder();
//        List<JNode> args = new ArrayList<>();
//        while (true) {
//            JToken token = t.next();
//            if (token.isEOF()) {
//                break;
//            }
//            switch (token.def.ttype) {
//                case JTokenType.TT_STRING: {
//                    if (token.sval.isEmpty()) {
//                        //ignore...
//                    } else {
//                        switch (token.sval) {
//                            case "__INVOKABLE_NAME__": {
//                                sb.append("%s");
//                                HNDeclareInvokable i = compilerContext.lookupEnclosingInvokable();
//                                args.add(new HNLiteral(i == null ? "" : i.getName(), null, s.startToken()));
//                            }
//                            case "__INVOKABLE_SIGNATURE__": {
//                                sb.append("%s");
//                                HNDeclareInvokable i = compilerContext.lookupEnclosingInvokable();
//                                args.add(new HNLiteral(i == null ? "" : i.getSignature(), null, s.startToken()));
//                            }
//                            case "__CLASS_NAME__": {
//                                sb.append("%s");
//                                JType i = compilerContext.lookupEnclosingType(compilerContext.node());
//                                args.add(new HNLiteral(i == null ? "" : i.name(), null, s.startToken()));
//                            }
//                            case "__CLASS_SIMPLENAME__": {
//                                sb.append("%s");
//                                JType i = compilerContext.lookupEnclosingType(compilerContext.node());
//                                args.add(new HNLiteral(i == null ? "" : i.name(), null, s.startToken()));
//                            }
//                            case "__FILE_NAME__": {
//                                sb.append("%s");
//                                String i = compilerContext.node().startToken().compilationUnit.getSource().name();
//                                args.add(new HNLiteral(i == null ? "" : i, null, s.startToken()));
//                            }
//                            default: {
//                                sb.append("%s");
//                                JNode node = compilerContext.context().parse(token.sval);
//                                if (node != null && node instanceof HNBlock && ((HNBlock) node).getStatements().size() == 1) {
//                                    node = ((HNBlock) node).getStatements().get(0);
//                                }
//                                node = processNextCompilerStage(node,compilerContext);
//                                args.add(node);
//                            }
//                        }
//                    }
//                    break;
//                }
//                case '%': {
//                    sb.append("%%");
//                    break;
//                }
//                default: {
//                    sb.append((char) token.def.ttype);
//                    break;
//                }
//            }
//        }
//        JTypes types = compilerContext.types();
//        JMethod jMethod = JTypeUtils.forString(types)
//                .declaredMethod("format(java.lang.String, java.lang.Object[])");
//        List<JNode> expandedArgs = new ArrayList<>();
//        expandedArgs.add(new HNLiteral(sb.toString(), JTypeUtils.forString(types), s.startToken()));
//        for (JNode arg : args) {
//            //((HNode) arg).setType(JeepPlatformUtils.forObject(types));
//            expandedArgs.add(arg);
//        }
//        HNMethodCall m = new HNMethodCall(jMethod, expandedArgs.toArray(new JNode[0]), null, s.startToken(), s.endToken());
//        m.setType(JTypeUtils.forString(types));
//        return m;
//    }
//
//    public JNode onMetaDeclarePackage(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNDeclareMetaPackage n = (HNDeclareMetaPackage) compilerContext.node();
//        processNextCompilerStage(n.getBody(),compilerContext);
//        if (n.getType() == null) {
//            n.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onMetaImportPackage(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNMetaImportPackage node = (HNMetaImportPackage) compilerContext.node();
//        processNextCompilerStage(node::getImportedPackageNode, node::setImportedPackageNode,compilerContext);
//        processNextCompilerStage(node, node.getExclusions(),compilerContext);
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onInvokeMethodCall(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNMethodCall node = (HNMethodCall) compilerContext.node();
//        JNode[] nargs = node.getArgs();
//        processNextCompilerStage(node, nargs,compilerContext);
//        processNextCompilerStage(node::getInstanceNode, node::setInstanceNode,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            JTypeOrLambda[] ntypes = compilerContext.jTypeOrLambdas(nargs);
//            if (ntypes != null) {
//                JEvaluable[] eargs = JNodeUtils.getEvaluatables(nargs);
//                if (node.getMethod() == null) {
//                    if (node.getInstanceNode() == null) {
//                        JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, node.getMethodNameToken().image, HFunctionType.NORMAL, ntypes, node.startToken());
//                        if (f instanceof JMethod) {
//                            JMethod jm = (JMethod) f;
//                            if (jm.isStatic()) {
//                                node.setMethod(jm);
//                            }
//                        } else if (f instanceof JFunction) {
//                            HNInvokerCall jNodeHInvokerCall = new HNInvokerCall(
//                                    node.getMethodNameToken(), node.getArgs()
//                                    , node.startToken()
//                                    , node.endToken()
//                            );
//                            jNodeHInvokerCall.setStartToken(node.startToken());
//                            jNodeHInvokerCall.setImpl(new JInvokablePrefilled(f, eargs));
//                            jNodeHInvokerCall.setType(f.returnType());
//                            return jNodeHInvokerCall;
//                        }
//                    } else if (node.getInstanceNode() instanceof HNTypeToken) {
//                        //this is an explicit static method call
//                        JType staticType = ((HNTypeToken) node.getInstanceNode()).getTypeVal();
//
//                        if (staticType != null && compilerContext.isTypes(ntypes)) {
//                            node.setMethod(compilerContext.findStaticMatch(JOnError.TRACE, staticType
//                                    , node.getMethodNameToken().image
//                                    ,HFunctionType.NORMAL
//                                    , ntypes, node.startToken(), new FindMatchFailInfo(
//                                    "static method"
//                            )));
//                        }
//                    } else {
//                        //instance methods are handled as functions with 'this' as first argument
//                        JTypeOrLambda otype = compilerContext.jTypeOrLambda(node.getInstanceNode());
//                        if (compilerContext.isTypes(ntypes)) {
//                            if (otype != null) {
//                                JTypeOrLambda[] newArgs = JeepUtils.arrayAppend(JTypeOrLambda.class, otype, ntypes);
//                                node.setMethod(compilerContext.findFunctionMatch(JOnError.TRACE, node.getMethodNameToken().image, HFunctionType.NORMAL, newArgs, node.startToken()));
//                            }
//                        } else {
//                            compilerContext.log().error("S035", "method not found " + otype.getType().name() + "." + JTypeOrLambda.signatureString(ntypes) + ". Lambda expressions not supported here yet", node.startToken());
//                        }
//                    }
//                }
//                if (node.getImpl() == null && node.getMethod() != null) {
//                    node.setImpl(new JInvokablePrefilled(node.getMethod(), eargs));
//                }
//            }
//        }
//        if (node.getType() == null) {
//            if (node.getMethod() != null) {
//                node.setType(node.getMethod().returnType());
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onModuleId(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNMetaPackageId node = (HNMetaPackageId) compilerContext.node();
//        return node;
//
//    }
//
//    public JNode onObjectNew(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNObjectNew node = (HNObjectNew) compilerContext.node();
//        JNode[] inits = node.getInits();
//        processNextCompilerStage(node, inits,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            if (node.getType() == null) {
//                JType t = compilerContext.lookupType(node.getObjectTypeName().getTypename());
//                if (t == null) {
//                    compilerContext.log().error("S032", "type not found " + node.getObjectTypeName(), node.startToken());
//                }
//                node.setType(t);
//            }
//            if (node.getType() != null) {
//
////                if (node.getInits().length == 0) {
////                    JConstructor constructor = node.getType().defaultConstructorOrNull();
////                    if(constructor==null){
////                        compilerContext.log().error("S062","default constructor not found for "+node.getType(),node.startToken());
////                    }else {
////                        node.setConstructor(new JInvokablePrefilled(constructor, inits));
////                    }
////                } else {
//                JTypeOrLambda[] consTypes = compilerContext.jTypeOrLambdas(inits);
//                JInvokable constructor = compilerContext.findConstructorMatch(JOnError.TRACE, node.getType(), consTypes, node.startToken(), new FindMatchFailInfo(null));
//                if (constructor != null) {
//                    node.setConstructor(HUtils.createJInvokablePrefilled(constructor, inits));
//                }
////                }
//            }
//            return node;
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onOpBinary(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNOpBinaryCall node = (HNOpBinaryCall) compilerContext.node();
//        String opName = node.getName();
//        if (HUtils.isComparisonOperator(opName) && node.getLeft() instanceof HNOpBinaryCall && HUtils.isComparisonOperator(((HNOpBinaryCall) node.getLeft()).getName())) {
//            //this is a problem
//            return onBinaryCall_unserializeComparisions(node);
//        }
//        if (HUtils.isAssignmentOperator(opName)) {
//            return onBinaryCall_processCompilerStageAssignment(compilerContext, node);
//        } else {
//            JNode arg1 = processNextCompilerStage(node::getLeft, node::setLeft,compilerContext);
//            JNode arg2 = processNextCompilerStage(node::getRight, node::setRight,compilerContext);
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                JTypeOrLambda[] args = compilerContext.jTypeOrLambdas(arg1, node.getRight());
//                if (args != null) {
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, opName, HFunctionType.INFIX_BINARY, args, node.startToken());
//                    if (f != null) {
//                        return createFunctionCall(node.startToken(), f, arg1, node.getRight());
//                    }
//                } else if (
//                        node.getType() == null &&
//                                HUtils.isComparisonOperator(opName)
//                                &&
//                                (HUtils.isNullLiteral(arg1))
//                                || (HUtils.isNullLiteral(arg2))
//                ) {
//                    node.setType(JTypeUtils.forBoolean(compilerContext.types()));
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    private boolean onBinaryCall_isUnserializableComparisionNode(JNode node0) {
//        if (node0 instanceof HNOpBinaryCall) {
//            HNOpBinaryCall node = (HNOpBinaryCall) node0;
//            return HUtils.isComparisonOperator(node.getName()) && node.getLeft() instanceof HNOpBinaryCall
//                    && HUtils.isComparisonOperator(((HNOpBinaryCall) node.getLeft()).getName());
//        }
//        return false;
//    }
//
//    private JNode onBinaryCall_unserializeComparisions(HNOpBinaryCall node) {
//        if (HUtils.isComparisonOperator(node.getName()) && node.getLeft() instanceof HNOpBinaryCall
//                && HUtils.isComparisonOperator(((HNOpBinaryCall) node.getLeft()).getName())) {
//            HNOpBinaryCall arg1Before = (HNOpBinaryCall) node.getLeft();
//            JNode dup = ((HNode) arg1Before.getRight()).copy();
//            JNode a = onBinaryCall_unserializeComparisions(arg1Before);
//            return new HNOpBinaryCall(JTokenUtils.createOpToken("&&"),
//                    a,
//                    new HNOpBinaryCall(node.getNameToken(),
//                            dup,
//                            node.getRight(),
//                            node.startToken(),
//                            node.endToken()),
//                    a.startToken(),
//                    a.endToken());
//        }
//        return node;
//    }
//
//    public JNode onBinaryCall_processCompilerStageAssignment(HLJCompilerContext compilerContext, HNOpBinaryCall node) {
//        JNode arg1 = node.getLeft();
//        if (arg1 instanceof HNBracketsPostfix) {
//            HNBracketsPostfix a = (HNBracketsPostfix) arg1;
//            JNode bb = processNextCompilerStage(a.getLeft(),compilerContext);
//            JNode[] items = a.getRight().toArray(new JNode[0]);
//            processNextCompilerStage(node,a.getRight(),compilerContext);
//            arg1 = new HNArrayCall(bb, items, null, null, arg1.startToken(), arg1.endToken());
//        } else {
//            arg1 = processNextCompilerStage(node::getLeft, node::setLeft,compilerContext);
//        }
//        JNode arg2 = processNextCompilerStage(node::getRight, node::setRight,compilerContext);
//        if (node.getName().equals("=")) {
//            return new HNAssign(arg1, JTokenUtils.createOpToken("="), arg2, node.startToken(), node.endToken());
//        } else {
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                JTypeOrLambda[] args = compilerContext.jTypeOrLambdas(arg1, arg2);
//                if (args != null) {
//                    String opName = node.getName();
//                    FindMatchFailInfo failInfo = new FindMatchFailInfo("'=' assignment");
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.NULL, opName, HFunctionType.INFIX_BINARY, args, node.startToken(), failInfo);
//                    if (f != null) {
//                        return createFunctionCall(node.startToken(), f, arg1, arg2);
//                    }
//
//                    opName = node.getName().substring(0, node.getName().length() - 1);
//
//                    f = compilerContext.findFunctionMatch(JOnError.TRACE, opName, HFunctionType.INFIX_BINARY, args, node.startToken(), failInfo);
//                    if (f != null) {
//                        return new HNAssign(arg1, JTokenUtils.createOpToken("="), createFunctionCall(node.startToken(), f, arg1, arg2), node.startToken(), node.endToken());
//                    }
//                }
//            }
//        }
//        return node;
//    }
//
//    public JNode onOpUnary(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNOpUnaryCall node = (HNOpUnaryCall) compilerContext.node();
//        JNode arg = processNextCompilerStage(node::getExpr, node::setExpr,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            if (node.getName().equals("++") || node.getName().equals("--")) {
//                boolean plus = node.getName().equals("++");
//                if (node.isPrefixOperator()) {
//                    HNLiteral oneLiteral = new HNLiteral(1, JTypeUtils.forInt(compilerContext.types()), arg.startToken());
//                    JTypeOrLambda[] argTypes = compilerContext.jTypeOrLambdas(arg);
//                    FindMatchFailInfo failInfo = new FindMatchFailInfo("prefix " + node.getName() + " operator");
//                    if (argTypes != null) {
//                        JInvokable f = compilerContext.findFunctionMatch(JOnError.NULL, node.getName(), HFunctionType.PREFIX_UNARY, argTypes, node.startToken(), failInfo);
//                        if (f != null) {
//                            return createFunctionCall(node.startToken(), f, arg);
//                        }
//                    }
//                    argTypes = compilerContext.jTypeOrLambdas(arg, oneLiteral);
//                    if (argTypes != null) {
//                        JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, plus ? "+" : "-", HFunctionType.PREFIX_UNARY, argTypes, node.startToken(), failInfo);
//                        if (f != null) {
//                            HNOpBinaryCall bc = new HNOpBinaryCall(JTokenUtils.createOpToken(plus ? "+" : "-"), arg, oneLiteral, arg.startToken(), arg.endToken());
//                            bc.setImpl(HUtils.createJInvokablePrefilled(f, arg, oneLiteral));
//                            JNode nn = processNextCompilerStage(new HNAssign(
//                                    arg, JTokenUtils.createOpToken("="),
//                                    bc
//                                    , arg.startToken()
//                                    , arg.endToken()
//                            ), compilerContext);
//                            HNBlock b = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                                    new JNode[]{nn,arg},
//                                    arg.startToken(),
//                                    arg.endToken()
//                            );
//                            processNextCompilerStage(b,compilerContext);
//                            return b;
//                        }
//                    }
//                } else {
//                    if (arg.getType() == null) {
//                        //not yet...
//                        return node;
//                    }
//                    FindMatchFailInfo failInfo = new FindMatchFailInfo("postfix " + node.getName() + " operator");
//                    JTypeOrLambda[] argTypes = compilerContext.jTypeOrLambdas(arg);
//                    if (argTypes != null) {
//                        JInvokable f = compilerContext.findFunctionMatch(JOnError.NULL, plus ? "++" : "--", HFunctionType.POSTFIX_UNARY, argTypes, node.startToken(), failInfo);
//                        if (f != null) {
//                            return createFunctionCall(node.startToken(), f, arg);
//                        }
//                    }
//
//                    HNLiteral oneLiteral = new HNLiteral(1, JTypeUtils.forInt(compilerContext.types()), arg.startToken());
//                    argTypes = compilerContext.jTypeOrLambdas(arg, oneLiteral);
//                    if (argTypes != null) {
//                        JType arg2Type = JNodeUtils.getType(arg);
//                        switch (arg2Type.boxed().name()) {
//                            case "java.lang.Byte":
//                            case "java.lang.Character":
//                            case "java.lang.Integer":
//                            case "java.lang.Long":
//                            case "java.lang.Float":
//                            case "java.lang.Double": {
//                                node.setType(arg2Type);
//                                return node;
//                            }
//                        }
//                        JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, plus ? "+" : "-", HFunctionType.POSTFIX_UNARY, argTypes, node.startToken(), failInfo);
//                        if (f != null) {
//                            HNDeclareIdentifier temp = new HNDeclareIdentifier(new JToken[]{
//                                    JTokenUtils.createTokenIdPointer(node.startToken(), "temp")
//                            }, arg, arg2Type, JTokenUtils.createOpToken("="), arg.startToken(), arg.startToken())
//                                    .setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL);
//                            List<JNode> st=new ArrayList<>();
//                            st.add(temp);
//                            HNVar temp2 = new HNVar(JTokenUtils.createWordToken("temp"), HUtils.createTypeToken(arg2Type), arg.startToken());
//                            HNOpBinaryCall bc = new HNOpBinaryCall(JTokenUtils.createOpToken(plus ? "+" : "-"),
//                                    temp2
//                                    , oneLiteral, arg.startToken(), arg.endToken());
//                            bc.setImpl(HUtils.createJInvokablePrefilled(f, arg, oneLiteral));
//                            HNAssign node1 = new HNAssign(arg, JTokenUtils.createOpToken("="),
//                                    bc
//                                    , arg.startToken()
//                                    , arg.endToken()
//                            );
//                            st.add(processNextCompilerStage(node1,compilerContext));
//                            HNVar temp1 = new HNVar(JTokenUtils.createWordToken("temp"), HUtils.createTypeToken(arg2Type), arg.startToken());
//                            st.add(temp1);
//                            HNBlock b = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                                    st.toArray(new JNode[0]),
//                                    arg.startToken(),
//                                    arg.endToken()
//                            );
//                            processNextCompilerStage(b,compilerContext);
//                            return b;
//                        }
//                    }
//
//                }
//            } else {
//                JTypeOrLambda[] argTypes = compilerContext.jTypeOrLambdas(arg);
//                FindMatchFailInfo failInfo = new FindMatchFailInfo((node.isPrefixOperator() ? "prefix " : "postfix ") + node.getName() + " operator");
//                if (argTypes != null) {
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, node.isPrefixOperator() ? node.getName() : ("postfix_" + node.getName()), null, argTypes, node.startToken(), failInfo);
//                    if (f != null) {
//                        return createFunctionCall(node.startToken(), f, arg);
//                    }
//                }
//
//            }
//            return node;
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onPars(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNPars node = (HNPars) compilerContext.node();
//        JNode[] values = node.getItems();
//        if (values.length != 1) {
//            processNextCompilerStage(node, values,compilerContext);
//            return new HNTuple(values, node.startToken(), new JToken[0], node.endToken());
//        } else if (values[0] instanceof HNPars && ((HNPars) values[0]).getItems().length == 1) {
//            // a tuple of one can be created by doubling the pars
//            JNode item = ((HNPars) values[0]).getItems()[0];
//            //compilerContext.processNextCompilerStage(values);
//            return new HNTuple(
//                    new JNode[]{
//                            item
//                    }, item.startToken(),new JToken[0], item.endToken());
//        } else {
//            //this is a simple par, discard it
//            processNextCompilerStage(node, values,compilerContext);
//            return values[0];
//        }
//    }
//
//    public JNode onParsPostfix(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
////this is an array application
//        HNParsPostfix node = (HNParsPostfix) compilerContext.node();
//        processNextCompilerStage(node::getLeft, node::setLeft,compilerContext);
//        processNextCompilerStage(node, node.getRight(), compilerContext);
//        if (node.getLeft() instanceof HNIdentifier) {
//            //this is mostly a method...
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                HNIdentifier id = (HNIdentifier) node.getLeft();
//                JTypeOrLambda[] ntypes = compilerContext.jTypeOrLambdas(node.getRight().toArray(new JNode[0]));
//                if (ntypes != null) {
//                    JEvaluable[] eargs = JNodeUtils.getEvaluatables(node.getRight().toArray(new JNode[0]));
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, id.getName(), HFunctionType.NORMAL, ntypes, node.startToken());
//                    if (f != null) {
//                        HNInvokerCall h = new HNInvokerCall(
//                                ((HNIdentifier) node.getLeft()).getNameToken()
//                                , node.getRight().toArray(new JNode[0])
//                                , node.startToken()
//                                , node.endToken()
//                        );
//                        h.setImpl(new JInvokablePrefilled(f, eargs));
//                        h.setType(f.returnType());
//                        return h;
//                    }
//                }
//            } else {
//                return node;
//            }
////            return new HNInvokerCall(((HNIdentifier) node.getBase()).getName(),node.getItems(),node.startToken());
//        } else if (node.getLeft() instanceof HNTypeToken) {
//            //this is mostly a constructor or generic method...
//            HNTypeToken base = (HNTypeToken) node.getLeft();
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                JType baseType = base.getTypeVal();
//                JTypeOrLambda[] tol = compilerContext.jTypeOrLambdas(node.getRight().toArray(new JNode[0]));
//                if (tol != null) {
//                    JInvokable e = compilerContext.findConstructorMatch(JOnError.TRACE, baseType, tol, base.startToken(), new FindMatchFailInfo(null));
//                    if (e != null) {
//                        return createFunctionCall(base.startToken(), e, node.getRight().toArray(new JNode[0]));
//                    }
//                }
//            }
//        } else {
//            //this is a apply function
//            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                HNode id = (HNode) node.getLeft();
//                JNode[] allArgs = JeepUtils.arrayAppend(JNode.class, id, node.getRight().toArray(new JNode[0]));
//                JTypeOrLambda[] ntypes = compilerContext.jTypeOrLambdas(allArgs);
//                if (ntypes != null) {
//                    JEvaluable[] eargs = JNodeUtils.getEvaluatables(allArgs);
//                    JInvokable f = compilerContext.findFunctionMatch(JOnError.TRACE, HLExtensionNames.FUNCTION_APPLY, HFunctionType.SPECIAL, ntypes, node.startToken());
//                    if (f != null) {
//                        HNInvokerCall h = new HNInvokerCall(((HNIdentifier) node.getLeft()).getNameToken()
//                                , node.getRight().toArray(new JNode[0])
//                                , node.startToken()
//                                , node.endToken()
//                        );
//                        h.setImpl(new JInvokablePrefilled(f, eargs));
//                        h.setType(f.returnType());
//                        return h;
//                    }
//                }
//                compilerContext.log().error("S032", "unable to resolve apply function", node.startToken());
//            } else {
//                return node;
//            }
//        }
//        return node;
//    }
//
//
//    public JNode onSuper(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            HNSuper n = (HNSuper) compilerContext.node();
//            if (n.getType() == null) {
//                throw new IllegalArgumentException("Missing Type");
//            }
//            return n;
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onSwitch(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNSwitch node = (HNSwitch) compilerContext.node();
//
//        HNode vexpr = (HNode) node.getExpr();
//        if (vexpr instanceof HNDeclareIdentifier) {
//            HNDeclareIdentifier idd = (HNDeclareIdentifier) node.getExpr();
//            if (idd.getType() == null) {
//                idd.setType(JTypeUtils.forVoid(compilerContext.types()));
//            }
//            vexpr = (HNode) idd.getInitValue();
//            vexpr = (HNode) processNextCompilerStage(vexpr,compilerContext);
//            idd.setInitValue(vexpr);
//            if (vexpr.getType() != null && idd.getIdentifierTypeName() == null) {
//                idd.setIdentifierTypeName(HUtils.createTypeToken(vexpr.getType()));
//            }
//        } else {
//            vexpr = (HNode) processNextCompilerStage(vexpr,compilerContext);
//            node.setExpr(vexpr);
//        }
//
//        if (vexpr.getType() != null) {
//            if (node.getSwitchType() == HNSwitch.SwitchType.CASE) {
//                if (vexpr.getType().boxed().name().equals("java.lang.Byte")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Byte.class, ByteRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.Short")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Short.class, ShortRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.Integer")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Integer.class, IntRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.Long")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Long.class, LongRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.BigInteger")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, BigInteger.class, BigIntRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.Float")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Float.class, FloatRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.Double")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, Double.class, DoubleRange.class);
//
//                } else if (vexpr.getType().boxed().name().equals("java.lang.BigDecimal")) {
//                    return onSwitch_processCompilerStageSwitchComparable(node, vexpr, compilerContext, BigDecimal.class, BigDecimalRange.class);
//
//                } else if (compilerContext.context().types().forName(CharSequence.class.getName()).isAssignableFrom(vexpr.getType())) {
//                    return onSwitch_processCompilerStageSwitchString(node, vexpr, compilerContext);
//                } else if (compilerContext.context().types().forName(Enum.class.getName()).isAssignableFrom(vexpr.getType())) {
//                    return onSwitch_processCompilerStageSwitchEnum(node, vexpr, compilerContext);
//                } else if (compilerContext.context().types().forName(Class.class.getName()).isAssignableFrom(vexpr.getType())) {
//                    //class switch
//                    return onSwitch_processCompilerStageSwitchClass(node, vexpr, compilerContext, false);
//                } else {
//                    return onSwitch_processCompilerStageSwitchObject(node, vexpr, compilerContext);
//                }
//            } else if (node.getSwitchType() == HNSwitch.SwitchType.IF) {
//                return onSwitch_processCompilerStageSwitchToIf(node, vexpr, compilerContext);
//            } else if (node.getSwitchType() == HNSwitch.SwitchType.IS) {
//                return onSwitch_processCompilerStageSwitchClass(node, vexpr, compilerContext, true);
//            }
//        }
//
//        processNextCompilerStage(node, node.getCases(),compilerContext);
//        processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//        onSwitch_updatetype(node);
//        return node;
//    }
//
//    private void onSwitch_updatetype(HNSwitch node) {
//        if (node.getType() == null) {
//            JType stype = null;
//            for (HNSwitch.SwitchBranch aCase : node.getCases()) {
//                JNode doNode = aCase.getDoNode();
//                if (doNode.getType() == null) {
//                    stype = null;
//                    break;
//                } else {
//                    if (stype == null) {
//                        stype = doNode.getType();
//                    } else {
//                        stype = stype.firstCommonSuperType(doNode.getType());
//                    }
//                }
//            }
//            node.setType(stype);
//        }
//    }
//
//    public JNode onSwitch_processCompilerStageSwitchComparable(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext, Class numType, Class numRangeType) {
//        //ordinal switch
//        JType labelType = compilerContext.context().types().forName(numType.getName());
//        JType rangeType = compilerContext.context().types().forName(numRangeType.getName());
//        HPartitionHelper<Comparable> visited = new HPartitionHelper<>(compilerContext.log());
//        List<HNSwitch.SwitchBranch> cases = node.getCases();
//        List<HNIf.WhenDoBranchNode> newIf = new ArrayList<>();
//        String implMode;
//        HNIdentifier varName = null;
//        if (node.getExpr() instanceof HNDeclareIdentifier) {
//            HNDeclareIdentifier e = (HNDeclareIdentifier) node.getExpr();
//            if (e.getIdentifierNames().length != 1) {
//                throw new JShouldNeverHappenException();
//            }
//            varName = new HNIdentifier(e.getIdentifierTokens()[0]);
//            varName.setType(e.getInitValue().getType());
//        }
//        if (numType.equals(Byte.class) || numType.equals(Short.class) || numType.equals(Integer.class) || numType.equals(Character.class)) {
//            implMode = "tabelswitch";
//        } else if (numType.equals(Long.class) || numType.equals(Float.class) || numType.equals(Double.class)
//                || numType.equals(BigDecimal.class)
//                || numType.equals(BigInteger.class)
//        ) {
//            implMode = "tostring";
//        } else {
//            implMode = "if";
//        }
//        for (int i2 = 0; i2 < cases.size(); i2++) {
//            HNSwitch.SwitchCase aCase = (HNSwitch.SwitchCase) cases.get(i2);
//            List<JNode> whenNodes = aCase.getWhenNodes();
//            for (int i1 = 0; i1 < whenNodes.size(); i1++) {
//                JNode whenNode = whenNodes.get(i1);
//                Object e = HLUtils.simplifyCaseLiteral(whenNode, vexpr.getType(), compilerContext, true, new boolean[1]);
//                String kind = "unknown";
//                if (numType.isInstance(e)) {
//                    kind = "simple";
//                } else if (numRangeType.isInstance(e)) {
//                    kind = "range";
//                } else {
//                    //try to find converter
//                    JType from = compilerContext.types().typeOf(e);
//                    FindMatchFailInfo failInfo = new FindMatchFailInfo(null);
//                    JInvokable cc = compilerContext.createConverter(JOnError.NULL, from, vexpr.getType(), whenNode, failInfo);
//                    if (cc != null) {
//                        HNInvokerCall r2 = createFunctionCall(node.startToken(), cc, new HNLiteral(e, from, whenNode.startToken()));
//                        Object ce = HLUtils.evalCaseLiteral(r2, compilerContext, null, null);
//                        kind = "simple";
//                        e = ce;
//                    } else {
//                        cc = compilerContext.createConverter(JOnError.NULL, from, rangeType, whenNode, failInfo);
//                        if (cc != null) {
//                            HNInvokerCall r2 = createFunctionCall(node.startToken(), cc, new HNLiteral(e, from, whenNode.startToken()));
//                            Object ce = HLUtils.evalCaseLiteral(r2, compilerContext, null, null);
//                            kind = "range";
//                            e = ce;
//                        } else {
//                            if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                                failInfo.trace(compilerContext.log(), whenNode.startToken());
//                            }
//                        }
//                    }
//                }
//                if (kind.equals("simple")) {
//                    visited.add((Comparable) e, whenNode.startToken());
//                    if ("tabelswitch".equals(implMode)) {
//                        whenNode = new HNLiteral(e, labelType, whenNode.startToken());
//                        whenNodes.set(i1, whenNode);
//                    } else if ("tostring".equals(implMode)) {
//                        whenNode = new HNLiteral(String.valueOf(e), labelType, whenNode.startToken());
//                        whenNodes.set(i1, whenNode);
//                    } else {
//                        newIf.add(new HNIf.WhenDoBranchNode(
//                                new HNOpBinaryCall(
//                                        JTokenUtils.createOpToken("=="),
//                                        ((HNode) vexpr).copy(),
//                                        new HNLiteral(e, labelType, whenNode.startToken())
//                                        , whenNode.startToken(),
//                                        whenNode.endToken()),
//                                ((HNode) aCase.getDoNode()).copy(),
//                                null
//                        ));
//                        whenNodes.remove(i1);
//                        i1--;
//                    }
//                } else if (kind.equals("range")) {
//                    ComparableRange r = (ComparableRange) e;
//                    visited.add(r, whenNode.startToken());
//                    int rangeSize = -1;
//                    if (r instanceof ByteRange) {
//                        rangeSize = ((ByteRange) r).size();
//                    } else if (r instanceof ShortRange) {
//                        rangeSize = ((ShortRange) r).size();
//                    } else if (r instanceof IntRange) {
//                        rangeSize = ((IntRange) r).size();
//                    } else if (r instanceof LongRange) {
//                        long size = ((LongRange) r).size();
//                        rangeSize = size > HLUtils.EXPAND_RANGE_SIZE ? HLUtils.EXPAND_RANGE_SIZE + 1 : (int) size;
//                    }
//                    if (rangeSize < 0 || rangeSize > HLUtils.EXPAND_RANGE_SIZE) {
//                        newIf.add(new HNIf.WhenDoBranchNode(
//                                new HNOpBinaryCall(
//                                        JTokenUtils.createOpToken("&&"),
//                                        new HNOpBinaryCall(
//                                                JTokenUtils.createOpToken(r.isStartInclusive() ? ">=" : ">"),
//                                                varName != null ? varName.copy() : ((HNode) vexpr).copy(),
//                                                new HNLiteral(r.lowerInclusive(), labelType, whenNode.startToken())
//                                                , whenNode.startToken(),
//                                                whenNode.endToken()),
//                                        new HNOpBinaryCall(
//                                                JTokenUtils.createOpToken(r.isEndInclusive() ? "<=" : "<"),
//                                                varName != null ? varName.copy() : ((HNode) vexpr).copy(),
//                                                new HNLiteral(r.upperExclusive(), labelType, whenNode.startToken())
//                                                , whenNode.startToken(),
//                                                whenNode.endToken())
//                                        , whenNode.startToken(),
//                                        whenNode.endToken()),
//                                ((HNode) aCase.getDoNode()).copy(),
//                                null
//                        ));
//                        whenNodes.remove(i1);
//                        i1--;
//                    } else {
//                        JNode removed = whenNodes.remove(i1);
//                        i1--;
//
//                        if (r instanceof ByteRange) {
//                            for (byte b : ((ByteRange) r).toByteArray()) {
//                                i1++;
//                                whenNodes.add(i1, new HNLiteral(b, labelType, removed.startToken()));
//                            }
//                        } else if (r instanceof ShortRange) {
//                            for (short b : ((ShortRange) r).toShortArray()) {
//                                i1++;
//                                whenNodes.add(i1, new HNLiteral(b, labelType, removed.startToken()));
//                            }
//                        } else if (r instanceof IntRange) {
//                            for (int b : ((IntRange) r).toIntArray()) {
//                                i1++;
//                                whenNodes.add(i1, new HNLiteral(b, labelType, removed.startToken()));
//                            }
//                        } else if (r instanceof LongRange) {
//                            for (long b : ((LongRange) r).toLongArray()) {
//                                i1++;
//                                whenNodes.add(i1, new HNLiteral(b, labelType, removed.startToken()));
//                            }
//                        } else {
//                            throw new JShouldNeverHappenException();
//                        }
//                    }
//                } else {
//                    compilerContext.log().error("S003", "expected constant " + vexpr.getType().name() + " value", whenNode.startToken());
//                }
//            }
//            if (whenNodes.size() == 0) {
//                cases.remove(i2);
//                i2--;
//            }
//        }
//        if ("tostring".equals(implMode)) {
//            JNode oe = node.getExpr();
//            if (oe instanceof HNDeclareIdentifier) {
//                HNDeclareIdentifier i = (HNDeclareIdentifier) oe;
//                i.setIdentifierTypeName(HUtils.createTypeToken(compilerContext.types().forName(String.class.getName())));
//                i.setInitValue(
//                        new HNMethodCall(
//                                JTypeUtils.forString(compilerContext.types()).declaredMethod("valueOf(java.lang.Object)"),
//                                new JNode[]{i.getInitValue()},
//                                null,
//                                i.getInitValue().startToken(),
//                                i.getInitValue().endToken()
//                        )
//                );
//            } else {
//                oe = new HNMethodCall(
//                        JTypeUtils.forString(compilerContext.types()).declaredMethod("valueOf(java.lang.Object)"),
//                        new JNode[]{oe},
//                        null,
//                        oe.startToken(),
//                        oe.endToken()
//                );
//                node.setExpr(oe);
//            }
//        }
//        if (newIf.size() > 0) {
//            HNIf i = new HNIf(node.startToken());
//            for (HNIf.WhenDoBranchNode w : newIf) {
//                i.add(w.getWhenNode(), w.getDoNode());
//            }
//            if (cases.size() > 0) {
//                if (node.getElseNode() == null) {
//                    node.setElse(i);
//                } else {
//                    i.setElse(node.getElseNode());
//                    node.setElse(i);
//                }
//                return node;
//            } else if (node.getElseNode() != null) {
//                i.setElse(node.getElseNode());
//                return i;
//            } else {
//                return i;
//            }
//        } else {
//            processNextCompilerStage(node, node.getCases(),compilerContext);
//            processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//            onSwitch_updatetype(node);
//            return node;
//        }
//    }
//
//    public JNode onSwitch_processCompilerStageSwitchToIf(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext) {
//        HNIf nif = new HNIf();
//        for (HNSwitch.SwitchBranch switchBranch : node.getCases()) {
//            if (switchBranch instanceof HNSwitch.SwitchCase) {
//                JNode c = null;
//                for (JNode whenNode : ((HNSwitch.SwitchCase) switchBranch).getWhenNodes()) {
//                    Object simple = HLUtils.simplifyCaseLiteral(whenNode, vexpr.getType(), compilerContext, true, new boolean[1]);
//                    HNOpBinaryCall c2 = new HNOpBinaryCall(
//                            JTokenUtils.createOpToken("=="),
//                            vexpr.copy(),
//                            new HNLiteral(simple, compilerContext.context().types().typeOf(simple), whenNode.startToken())
//                            , whenNode.startToken(),
//                            whenNode.endToken());
//                    if (c == null) {
//                        c = c2;
//                    } else {
//                        c = new HNOpBinaryCall(
//                                JTokenUtils.createOpToken("||"),
//                                c,
//                                c2
//                                , whenNode.startToken(),
//                                whenNode.endToken());
//                    }
//                }
//                nif.add(c, switchBranch.getDoNode());
//            } else if (switchBranch instanceof HNSwitch.SwitchIf) {
//                nif.add(((HNSwitch.SwitchIf) switchBranch).getWhenNode(), switchBranch.getDoNode());
//            } else if (switchBranch instanceof HNSwitch.SwitchIs) {
//
//                JNode c = null;
//                for (HNTypeToken whenNode : ((HNSwitch.SwitchIs) switchBranch).getWhenType()) {
//                    JNode c2 = new HNIs(
//                            whenNode,
//                            vexpr.copy(), null
//                            , switchBranch.startToken()
//                            , switchBranch.endToken()
//                    );
//                    if (c == null) {
//                        c = c2;
//                    } else {
//                        c = new HNOpBinaryCall(
//                                JTokenUtils.createOpToken("||"),
//                                c,
//                                c2
//                                , switchBranch.startToken(),
//                                switchBranch.endToken());
//                    }
//                }
//                nif.add(c, switchBranch.getDoNode());
//            }
//        }
//        nif.setElse(node.getElseNode());
//        return nif;
//    }
//
//    public JNode onSwitch_processCompilerStageSwitchEnum(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext) {
//        processNextCompilerStage(node, node.getCases(),compilerContext);
//        processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//        onSwitch_updatetype(node);
//        return node;
//    }
//
//    public JNode onSwitch_processCompilerStageSwitchObject(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext) {
//        processNextCompilerStage(node, node.getCases(),compilerContext);
//        processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//        onSwitch_updatetype(node);
//        return node;
//    }
//
//    public JNode onSwitch_processCompilerStageSwitchClass(HNSwitch switchNode, HNode vexpr, HLJCompilerContext compilerContext, boolean instance) {
//        //class switch
//        JToken initialExprVar = null;
//        JNode initialExpr = switchNode.getExpr();
//        if (switchNode.getExpr() instanceof HNDeclareIdentifier) {
//            HNDeclareIdentifier nhdi = (HNDeclareIdentifier) switchNode.getExpr();
//            if (nhdi.getIdentifierNames().length != 1) {
//                throw new JShouldNeverHappenException();
//            }
//            initialExprVar = nhdi.getIdentifierTokens()[0];
//        }
//        List<JNode> fullBlockList = new ArrayList<>();
//        HNFor forNode = new HNFor(switchNode.startToken());
//        forNode.setIteratorType(true);
//        HNDeclareType letm = compilerContext.lookupEnclosingTypeNode(compilerContext.node());
////        String lab1 = "__$LABEL" + HUtils.incUserProperty(letm, "__$LABEL");
//        String matched = "__$matched" + HUtils.incUserProperty(letm, "__$matched");
//        JToken matched_tok = JTokenUtils.createTokenIdPointer(switchNode.startToken(), matched);
//
////        fn.setLabel(lab1);
//        JType _bool = JTypeUtils.forBoolean(compilerContext.types());
//        JType _string = compilerContext.types().forName(String.class.getName());
//        fullBlockList.add(
//                new HNDeclareIdentifier(
//                        new JToken[]{matched_tok},
//                        new HNLiteral(false,
//                                _bool,
//                                switchNode.startToken()), _bool,
//                        JTokenUtils.createOpToken(":"), switchNode.startToken(), switchNode.startToken()
//                ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL)
//        );
//        String var1 =
//                "__$discr" + HUtils.incUserProperty(letm, "__$discr");
//        JToken var1_tok = JTokenUtils.createTokenIdPointer(switchNode.startToken(), var1);
//        forNode.addInit(new HNDeclareIdentifier(
//                new JToken[]{var1_tok},
//                new HNMethodCall(
//                        compilerContext.types().forName(HHelpers.class.getName()).declaredMethod(
//                                instance ? "resolveInstanceClassWithParentNames(java.lang.Object)" :
//                                        "resolveClassWithParentNames(java.lang.Class)"),
//                        new JNode[]{
//                                vexpr
//                        }, null, switchNode.startToken(), switchNode.endToken()
//                ), _string,
//                JTokenUtils.createOpToken(":"), switchNode.startToken(), switchNode.startToken()
//        ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//        if (instance) {
//            boolean ok = true;
//            for (HNSwitch.SwitchBranch b : switchNode.getCases()) {
//                HNSwitch.SwitchIs c = (HNSwitch.SwitchIs) b;
//                if (c.getIdentifierType() == null) {
//                    ok = false;
//                    break;
//                }
//            }
//            if (!ok) {
//                onSwitch_updatetype(switchNode);
//                processNextCompilerStage(switchNode, switchNode.getCases(),compilerContext);
//                processNextCompilerStage(switchNode::getElseNode, switchNode::setElse,compilerContext);
//                return switchNode;
//            }
//            switchNode.setExpr(new HNIdentifier(JTokenUtils.createWordToken(var1)).setType(_string));
//            List<HNSwitch.SwitchBranch> newCases = new ArrayList<>();
//            for (HNSwitch.SwitchBranch b : switchNode.getCases()) {
//                HNSwitch.SwitchIs c = (HNSwitch.SwitchIs) b;
//                List<HNTypeToken> whenNodes = c.getWhenTypes();
//                List<JNode> newWhenNodes = new ArrayList<>();
//                for (int i = 0; i < whenNodes.size(); i++) {
//                    HNTypeToken n = whenNodes.get(i);
//                    String tn = "";
//                    if (n == null) {
//                        tn = "null";
//                    } else {
//                        tn = compilerContext.lookupType(n.getTypename()).name();
//                    }
//                    newWhenNodes.add(new HNLiteral(tn, _string, b.startToken()));
//                }
//                HNode d = (HNode) c.getDoNode();
//                List<JNode> blList = new ArrayList<>();
//                blList.add(new HNAssign(new HNIdentifier(JTokenUtils.createWordToken(matched)).setType(_bool), JTokenUtils.createOpToken("="),
//                        new HNLiteral(true,
//                                _bool,
//                                d.startToken()
//                        ),
//                        d.startToken(),
//                        d.endToken()
//                ));
//                JToken whenVar = c.getIdentifierToken();
//                if (whenVar != null) {
//                    blList.add(new HNDeclareIdentifier(
//                            new JToken[]{whenVar},
//                            new HNCast(new HNTypeToken(c.getIdentifierType(), d.startToken()),
//                                    initialExprVar == null ? initialExpr : new HNIdentifier(
//                                            initialExprVar
//                                    ).setType(((HNDeclareIdentifier) switchNode.getExpr()).getIdentifierType()),
//                                    null,d.startToken(),
//                                    d.endToken()
//                            ),
//                            c.getIdentifierType(),
//                            JTokenUtils.createOpToken("="), d.startToken(),
//                            d.startToken()
//                    ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//                }
//                blList.add(d);
//                if (HUtils.requireExplicitExit(d)) {
//                    blList.add(new HNBreak(null, switchNode.startToken(), switchNode.startToken()));
//                }
//                HNBlock bl = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                        blList.toArray(new JNode[0]),
//                        d.startToken(),
//                        d.endToken()
//                        );
//
//                HNSwitch.SwitchCase sc = new HNSwitch.SwitchCase(newWhenNodes, JTokenUtils.createOpToken(":"), bl, c.startToken(), c.endToken());
//                newCases.add(sc);
//            }
//            switchNode.setSwitchType(HNSwitch.SwitchType.CASE);
//            switchNode.setCases(newCases);
//        } else {
//            switchNode.setExpr(new HNIdentifier(JTokenUtils.createWordToken(var1)).setType(_string));
//            for (HNSwitch.SwitchBranch b : switchNode.getCases()) {
//                HNSwitch.SwitchCase c = (HNSwitch.SwitchCase) b;
//                List<JNode> whenNodes = c.getWhenNodes();
//                for (int i = 0; i < whenNodes.size(); i++) {
//                    JNode n = whenNodes.get(i);
//                    String tn = "";
//                    if (n instanceof HNIdentifier) {
//                        tn = ((HNIdentifier) n).getName();
//                    } else if (n instanceof HNTypeToken) {
//                        tn = ((HNTypeToken) n).getTypeVal().name();
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                    whenNodes.set(i, new HNLiteral(tn, _string, n.startToken()));
//                }
//                HNode d = (HNode) c.getDoNode();
//                List<JNode> blList = new ArrayList<>();
//                blList.add(new HNAssign(new HNIdentifier(JTokenUtils.createWordToken(matched)).setType(_bool), JTokenUtils.createOpToken("="),
//                        new HNLiteral(true,
//                                _bool,
//                                d.startToken()),
//                        d.startToken(),
//                        d.endToken()
//                ));
//                blList.add(d);
//                if (HUtils.requireExplicitExit(d)) {
//                    blList.add(new HNBreak(null, switchNode.startToken(), switchNode.startToken()));
//                }
//                HNBlock bl = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                        blList.toArray(new JNode[0]),
//                        d.startToken(),
//                        d.endToken()
//                );
//                c.setDoNode(bl);
//            }
//        }
//        onSwitch_updatetype(switchNode);
//        forNode.setBody(switchNode);
//        fullBlockList.add(forNode);
//        if (switchNode.getElseNode() != null) {
//            fullBlockList.add(
//                    new HNIf(switchNode.startToken())
//                            .add(
//                                    new HNOpUnaryCall(
//                                            JTokenUtils.createWordToken("!"),
//                                            new HNIdentifier(JTokenUtils.createWordToken(matched)).setType(_bool),
//                                            true,
//                                            switchNode.startToken(),
//                                            switchNode.endToken()
//                                    ).setType(_bool),
//                                    switchNode.getElseNode()
//                            )
//            );
//            switchNode.setElse(new HNBlock());
//        }
//        HNBlock fullBlock = new HNBlock(HNBlock.BlocType.LOCAL_BLOC,
//                fullBlockList.toArray(new JNode[0]),
//                switchNode.startToken(),
//                switchNode.endToken()
//                );
//        return fullBlock;
//    }
//
//    public JNode processCompilerStageSwitchIsClass(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext) {
//        //class switch
//        HNFor fn = new HNFor(node.startToken());
//        HNDeclareType letm = compilerContext.lookupEnclosingTypeNode(compilerContext.node());
//        String lab1 = "__$LABEL" + HUtils.incUserProperty(letm, "__$LABEL");
//        String var1 = "__$v" + HUtils.incUserProperty(letm, "__$v");
//        fn.setLabel(lab1);
//        fn.addInit(new HNDeclareIdentifier(
//                new JToken[]{JTokenUtils.createTokenIdPointer(node.startToken(), var1)},
//                new HNMethodCall(
//                        compilerContext.types().forName(HHelpers.class.getName()).declaredMethod("resolveInstanceClassWithParentNames(java.lang.Object)"),
//                        new JNode[]{
//                                vexpr
//                        }, null, node.startToken(), node.endToken()
//                ), compilerContext.types().forName(String.class.getName()),
//                JTokenUtils.createOpToken(":"), node.startToken(), node.startToken()
//        ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL));
//        node.setExpr(new HNLiteral(var1, compilerContext.types().forName(String.class.getName()), node.startToken()));
//        List<HNSwitch.SwitchBranch> newBranches = new ArrayList<>();
//        for (HNSwitch.SwitchBranch b : node.getCases()) {
//            HNSwitch.SwitchIs c = (HNSwitch.SwitchIs) b;
//            List<HNTypeToken> whenNodes = c.getWhenTypes();
//            List<JNode> newWhenNodes = new ArrayList<>();
//            for (int i = 0; i < whenNodes.size(); i++) {
//                HNTypeToken tn = whenNodes.get(i);
//                newWhenNodes.add(new HNLiteral(tn, compilerContext.types().forName(String.class.getName()), c.startToken()));
//            }
//            JNode doNode = c.getDoNode();
//            if (whenNodes.size() == 1 && c.getIdentifierToken() != null) {
//
//                HNDeclareIdentifier node1 = new HNDeclareIdentifier(
//                        new JToken[]{c.getIdentifierToken()}, new HNCast(
//                        //check me
//                        new HNIdentifier(c.getWhenTypes().get(0).startToken()),
//                        ((HNode) node.getExpr()).copy(),
//                        null,
//                        c.startToken(),
//                        c.endToken()
//                ), compilerContext.types().forName(String.class.getName()),
//                        JTokenUtils.createOpToken("="), c.startToken(), c.startToken()
//                ).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL);
//                HNBlock block2 = new HNBlock(HNBlock.BlocType.LOCAL_BLOC, new JNode[]{node1,doNode},node1.startToken(),doNode.endToken());
//                doNode = block2;
//            }
//            newBranches.add(new HNSwitch.SwitchCase(
//                    newWhenNodes, JTokenUtils.createOpToken(":"), doNode, c.startToken(), c.endToken()
//            ));
//        }
//        node.setSwitchType(HNSwitch.SwitchType.CASE);
//        node.setCases(newBranches);
//        onSwitch_updatetype(node);
//        fn.setBody(node);
//        return fn;
//    }
//
//    private JNode onSwitch_processCompilerStageSwitchString(HNSwitch node, HNode vexpr, HLJCompilerContext compilerContext) {
//        //ordinal switch
//        JType labelType = JTypeUtils.forString(compilerContext.types());
//        Set<String> visitedStrings = new HashSet<>();
//        Set<String> visitedPatterns = new HashSet<>();
//        List<HNSwitch.SwitchBranch> cases = node.getCases();
//        List<HNIf.WhenDoBranchNode> newIf = new ArrayList<>();
//        for (int i2 = 0; i2 < cases.size(); i2++) {
//            HNSwitch.SwitchCase aCase = (HNSwitch.SwitchCase) cases.get(i2);
//            List<JNode> whenNodes = aCase.getWhenNodes();
//            for (int i1 = 0; i1 < whenNodes.size(); i1++) {
//                JNode whenNode = whenNodes.get(i1);
//                Object e = HLUtils.simplifyCaseLiteral(whenNode, node.getExpr().getType(), compilerContext, true, new boolean[1]);
//                if (e instanceof String) {
//                    String i = (String) e;
//                    if (visitedStrings.contains(i)) {
//                        compilerContext.log().error("S045", "duplicate case label " + i, whenNode.startToken());
//                    }
//                    visitedStrings.add(i);
//                    whenNode = new HNLiteral(i, labelType, whenNode.startToken());
//                    whenNodes.set(i1, whenNode);
//                } else if (e instanceof Pattern) {
//                    Pattern i = (Pattern) e;
//                    if (visitedPatterns.contains(i.toString())) {
//                        compilerContext.log().error("S045", "duplicate case label " + i, whenNode.startToken());
//                    }
//                    visitedPatterns.add(i.toString());
//                    whenNode = new HNLiteral(i, labelType, whenNode.startToken());
//                    whenNodes.set(i1, whenNode);
//                } else {
//                    compilerContext.log().error("S003", "expected constant " + node.getExpr().getType().name() + " value", whenNode.startToken());
//                }
//            }
//        }
//        if (visitedStrings.size() == 0 && visitedPatterns.size() == 0) {
//            compilerContext.log().error("S003", "switch statement : missing cases", node.startToken());
//            processNextCompilerStage(node, node.getCases(),compilerContext);
//            processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//            onSwitch_updatetype(node);
//            return node;
//        } else if (visitedStrings.size() > 0 && visitedPatterns.size() == 0) {
//            //no problem this is a std case!
//            processNextCompilerStage(node, node.getCases(),compilerContext);
//            processNextCompilerStage(node::getElseNode, node::setElse,compilerContext);
//            onSwitch_updatetype(node);
//            return node;
//        } else {
//            HNIf nif = new HNIf();
//            for (int i = 0; i < cases.size(); i++) {
//                HNSwitch.SwitchCase switchBranch = (HNSwitch.SwitchCase) cases.get(i);
//                JNode c = null;
//                List<JNode> wnodes = switchBranch.getWhenNodes();
//                JNode ifCond = null;
//                for (int i1 = 0; i1 < wnodes.size(); i1++) {
//                    JNode whenNode = wnodes.get(i1);
//                    Object simple = HLUtils.simplifyCaseLiteral(whenNode, vexpr.getType(), compilerContext, true, new boolean[1]);
//                    if (simple instanceof String) {
//                        //this is okkay
//                    } else {
//                        Pattern p = (Pattern) simple;
//                        HNDeclareType t = compilerContext.lookupEnclosingTypeNode(compilerContext.node());
//                        String pi = HUtils.nextNameFromUserProperty(t, "___$P");
//                        t.addField(new HNDeclareIdentifier(
//                                        new JToken[]{JTokenUtils.createTokenIdPointer(node.startToken(), pi)}, new HNMethodCall(
//                                        compilerContext.types().forName(Pattern.class.getName()).declaredMethod("compile(java.lang.String)"),
//                                        new JNode[]{new HNLiteral(p.pattern(), JTypeUtils.forString(compilerContext.types()), whenNode.startToken())},
//                                        null
//                                        , whenNode.startToken()
//                                        , whenNode.endToken()
//                                )
//                                        .setDeclaringTypeName(compilerContext.types().forName(Pattern.class.getName()).typeName())
//                                        , compilerContext.types().forName(Pattern.class.getName()), JTokenUtils.createOpToken("="), whenNode.startToken(), whenNode.startToken()
//                                ).setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL).setSyntacticType(HNDeclareIdentifier.SyntacticType.LOCAL)
//                                , compilerContext);
//                        JNode nc = new HNMethodCall(
//                                compilerContext.types().forName(Matcher.class.getName()).declaredMethod("matches()"),
//                                new JNode[0],
//                                new HNMethodCall(
//                                        compilerContext.types().forName(Pattern.class.getName()).declaredMethod("matcher(java.lang.CharSequence)"),
//                                        new JNode[]{vexpr},
//                                        new HNField(
//                                                null, JTokenUtils.createWordToken("___$P" + pi),
//                                                compilerContext.getOrCreateType(t).typeName(),
//                                                compilerContext.types().forName(Pattern.class.getName()),
//                                                whenNode.startToken(),
//                                                whenNode.endToken()
//                                        )
//                                        , whenNode.startToken()
//                                        , whenNode.endToken()
//                                )
//                                , whenNode.startToken()
//                                , whenNode.endToken()
//                        );
//                        if (ifCond == null) {
//                            ifCond = nc;
//                        } else {
//                            ifCond = new HNOpBinaryCall(JTokenUtils.createOpToken("||"), ifCond, nc, ifCond.startToken(), ifCond.endToken());
//                        }
//                        wnodes.remove(i1);
//                        i1--;
//                    }
//                    if (wnodes.isEmpty()) {
//                        cases.remove(i);
//                        i--;
//                    }
//                }
//                if (ifCond != null) {
//                    nif.add(ifCond, ((HNode) switchBranch.getDoNode()).copy());
//                }
//            }
//            if (cases.size() > 0) {
//                if (node.getElseNode() == null) {
//                    node.setElse(nif);
//                } else {
//                    nif.setElse(node.getElseNode());
//                    node.setElse(nif);
//                }
//                return node;
//            } else if (node.getElseNode() != null) {
//                nif.setElse(node.getElseNode());
//                return nif;
//            } else {
//                return nif;
//            }
//        }
//    }
//
//    public JNode onSwitchIs(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNSwitch.SwitchIs node = (HNSwitch.SwitchIs) compilerContext.node();
//        processNextCompilerStage(node::getDoNode, node::setDoNode,compilerContext);
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            if (node.getIdentifierToken() != null && node.getWhenTypes().size() == 1) {
//                node.setWhenIdentifierType(compilerContext.lookupType(node.getWhenTypes().get(0).getTypename()));
//            } else {
//                node.setWhenIdentifierType((JTypeUtils.forVoid(compilerContext.types())));
//            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    public JNode onSwitchIf(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNSwitch.SwitchIf node = (HNSwitch.SwitchIf) compilerContext.node();
//        processNextCompilerStage(node::getDoNode, node::setDoNode,compilerContext);
//        processNextCompilerStage(node::getWhenNode, node::setWhenNode,compilerContext);
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    public JNode onSwitchCase(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNSwitch.SwitchCase node = (HNSwitch.SwitchCase) compilerContext.node();
//        processNextCompilerStage(node::getDoNode, node::setDoNode,compilerContext);
//        processNextCompilerStage(node, node.getWhenNodes(),compilerContext);
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//    public JNode onThis(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNThis n = (HNThis) compilerContext.node();
//        if (n.getType() == null) {
//            JNode caller = n.parentNode();
//            if (caller instanceof HNField) {
//                JField f = ((HNField) caller).getField();
//                if (f != null) {
//                    n.setType(f.declaringType());
//                }
//            } else {
//                throw new IllegalArgumentException("Missing Type");
//            }
//        }
//        return n;
//    }
//
//    public JNode onTokenSuite(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNTokenSuite node = (HNTokenSuite) compilerContext.node();
//        return node;
//
//    }
//
//    public JNode onTuple(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNTuple node = (HNTuple) compilerContext.node();
//        JNode[] values = node.getItems();
//        if (values.length > Tuple.MAX_ELEMENTS) {
//            compilerContext.log().error("X062", "Too many elements for a tuple : "
//                    + values.length + ">" + Tuple.MAX_ELEMENTS, node.startToken());
//        }
//        processNextCompilerStage(node, values,compilerContext);
//        JTypeOrLambda[] atypes = compilerContext.jTypeOrLambdas(values);
//        if (atypes != null) {
//            JTypes types = compilerContext.types();
//            if (values.length > Tuple.MAX_ELEMENTS) {
//                node.setType(types.forName(TupleN.class.getName()));
//            } else {
//                JType c = null;
//                JType[] varTypes = new JType[atypes.length];
//                for (int i = 0; i < varTypes.length; i++) {
//                    varTypes[i] = atypes[i].getType();
//                }
//                c = HTypeUtils.tupleType(types,varTypes);
//                node.setType(c);
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onTypeToken(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNTypeToken n = (HNTypeToken) compilerContext.node();
//        if (n.getType() == null) {
//            n.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        if (n.getTypeVal() == null) {
//            n.setTypeVal(compilerContext.lookupType(n.getTypenameOrVar()));
//        }
//
//        return n;
//    }
//
//
//    public JNode onInvokeMethodInchecked(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNMethodUncheckedCall node = (HNMethodUncheckedCall) compilerContext.node();
//        JNode[] nargs = node.getArgs();
//        processNextCompilerStage(node, nargs,compilerContext);
//        processNextCompilerStage(node::getMethodName, node::setMethodName,compilerContext);
//        processNextCompilerStage(node::getInstanceNode, node::setInstanceNode,compilerContext);
//        if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//            JType t = node.getMethodName().getType();
//            if (t != null && !t.name().equals("java.lang.String")) {
//                compilerContext.log().error("S047", "expected String as method name", node.getMethodName().startToken());
//            }
//        }
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forObject(compilerContext.types()));
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onVar(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNVar n = (HNVar) compilerContext.node();
//        if (n.getType() == null) {
//            if (compilerContext.isStage(STAGE_2_WIRE_TYPES)) {
//                if (n.getVarTypeName().getTypeVal() != null) {
//                    n.setType(n.getVarTypeName().getTypeVal());
//                }
//                if (n.getType() == null) {
//                    HNDeclareTokenBase y = (HNDeclareIdentifier) compilerContext.lookupVarDeclarationOrNull(n.getName(), n.startToken());
//                    if (y != null && y.getIdentifierType() != null) {
//                        n.setType(y.getIdentifierType());
//                        n.setVarTypeName(HUtils.createTypeToken(y.getIdentifierType()));
//                    } else if (y == null) {
//                        compilerContext.log().error("S048", "symbol not found : " + n.getName(), n.startToken());
//                    }
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onWhile(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNWhile node = (HNWhile) compilerContext.node();
//        JContext context = compilerContext.context();
//        processNextCompilerStage(node::getExpr, node::setExpr,compilerContext);
//        processNextCompilerStage(node::getBlock, node::setBlock,compilerContext);
//        if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//            JNode expr = node.getExpr();
//            JTypeOrLambda exprTypeOrLambda = compilerContext.jTypeOrLambda(expr);
//            JNode block = node.getBlock();
//            JTypeOrLambda blockTypeOrLambda = compilerContext.jTypeOrLambda(block);
//            if (exprTypeOrLambda != null && blockTypeOrLambda != null) {
//                if (exprTypeOrLambda.isType() && exprTypeOrLambda.getType().boxed().name().equals("java.lang.Boolean")) {
//                    //default case
//                    node.setType(JTypeUtils.forVoid(compilerContext.types()));
//                } else {
//                    JType exprType = context.types().forName("java.util.function.Supplier<" + exprTypeOrLambda.getType().name() + ">");
//                    JType blockType = context.types().forName("java.util.function.Supplier<" + blockTypeOrLambda.getType().name() + ">");
////                    List<String> alternatives = new ArrayList<>();
//                    FindMatchFailInfo failInfo = new FindMatchFailInfo("<while> function");
//                    JInvokable fct = compilerContext.findFunctionMatch(
//                            JOnError.NULL, "While",
//                            HFunctionType.NORMAL, new JTypeOrLambda[]{
//                                    JTypeOrLambda.of(exprType),
//                                    JTypeOrLambda.of(blockType)
//                            }
//                            ,
//                            node.startToken(), failInfo
//                    );
//                    if (fct != null) {
//                        return createFunctionCall2(node.startToken(), fct,
//                                new HNWhile.JEvaluableNodeSupplier(exprType, expr),
//                                new HNWhile.JEvaluableNodeSupplier(blockType, block)
//                        );
//                    } else {
////                        alternatives.add("static   method: " + "While" + JTypeOrLambda.signatureString(
////                                new JTypeOrLambda[]{
////                                        new JTypeOrLambda(exprType),
////                                        new JTypeOrLambda(blockType)
////                                }
////                        ));
////                        StringBuilder errorMsg=new StringBuilder("To use while statement with non boolean condition, you should implement ");
////                        for (String alternative : alternatives) {
////                            errorMsg.append("\n").append(alternative);
////                        }
////                        compilerContext.log().error("S012", errorMsg.toString(), node.startToken());
//                    }
//                }
//            }
//        }
//        return compilerContext.node();
//    }
//
//    public JNode onIfWhenDo(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        HNIf.WhenDoBranchNode node = (HNIf.WhenDoBranchNode) compilerContext.node();
//        processNextCompilerStage(node::getWhenNode, node::setWhenNode,compilerContext);
//        processNextCompilerStage(node::getDoNode, node::setDoNode,compilerContext);
//        processNextCompilerStage(node, node.getIdDeclarations(),compilerContext);
//        if (node.getType() == null) {
//            node.setType(JTypeUtils.forVoid(compilerContext.types()));
//        }
//        return node;
//    }
//
//
//
////    public <T extends JNode> void processNextCompilerStage(JNode parentNode, List<T> nargs,JCompilerContext compilerContext) {
////        if (nargs != null) {
////            for (int i = 0; i < nargs.size(); i++) {
////                final int ii = i;
////                processNextCompilerStage(() -> nargs.get(ii), (x) -> nargs.set(ii, x),compilerContext);
////            }
////        }
////    }
////
////    public <T extends JNode> void processNextCompilerStage(JNode parentNode, JNode[] nargs,JCompilerContext compilerContext) {
////        if (nargs != null) {
////            for (int i = 0; i < nargs.length; i++) {
////                final int ii = i;
////                processNextCompilerStage(() -> nargs[ii], (x) -> nargs[ii] = x,compilerContext);
////            }
////        }
////    }
//
//
//
//}
