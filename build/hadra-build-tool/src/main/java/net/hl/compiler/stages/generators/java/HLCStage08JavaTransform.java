package net.hl.compiler.stages.generators.java;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.CastJConverter;
import net.vpc.common.jeep.impl.functions.ConvertedJMethod2;
import net.vpc.common.jeep.impl.functions.JArgumentConverterByIndex;
import net.vpc.common.jeep.impl.functions.JInstanceArgumentResolverFromArgumentByIndex;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.impl.types.DefaultJModifierList;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.common.textsource.JTextSource;
import net.hl.compiler.core.HLOptions;
import net.hl.compiler.core.HLProject;
import net.hl.compiler.core.elements.*;
import net.hl.compiler.core.invokables.*;
import net.hl.compiler.ast.*;
import net.hl.compiler.stages.HLCStage;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;
import net.hl.compiler.utils.HUtils;
import net.hl.lang.BooleanRef;
import net.hl.lang.ext.HJavaDefaultOperators;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HLCStage08JavaTransform implements HLCStage {

    public static final Logger LOG = Logger.getLogger(HLCStage08JavaTransform.class.getName());
    private boolean showFinalErrors = false;
    private boolean inPreprocessor = false;
    private boolean inMetaPackage = false;
    private Stack<HNode> contextStack = new Stack<>();
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

    private static int refIndexOf(JNode node, List<JNode> all, int fallbackIndex) {
        for (int i = 0; i < all.size(); i++) {
            if (node == all.get(i)) {
                return i;
            }
        }
        return fallbackIndex;
//        throw new JShouldNeverHappenException();
    }

    public static HNElementInvokable getElementInvokable(JNode node) {
        if (node instanceof HNode) {
            HNElement element = ((HNode) node).getElement();
            if (element != null) {
                switch (element.getKind()) {
                    case CONSTRUCTOR: {
                        return (HNElementInvokable) element;
                    }
                    case METHOD: {
                        JInvokable invokable = ((HNElementMethod) element).getInvokable();
                        if (invokable instanceof JMethod) {
                            JMethod jm = (JMethod) invokable;
                            //jm.declaringType()==null for convert functions...
                            if (
                                    jm.getDeclaringType() != null &&
                                            jm.getDeclaringType().getName().equals("net.hl.lang.ext.HJavaDefaultOperators")) {
                                //this is a standard operator
                                return null;
                            }
                        }
                        return (HNElementInvokable) element;
                    }
                }
            }
        }
        return null;
    }

    private static HNode Convert_ToString(JConverter converter, HNode anyResultString, HLJCompilerContext2 compilerContext) {
        if (converter instanceof CastJConverter) {
            CastJConverter c = (CastJConverter) converter;
            if (c.targetType().getType().equals(c.originalType().getType().boxed())) {
                return anyResultString;
            }
            return HGenUtils.Cast(c.targetType().getType(), anyResultString);
        }
        if (converter instanceof JTypeUtils.NumberToByteJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(
                    NumberType.getDeclaredMethod("byteValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        if (converter instanceof JTypeUtils.NumberToIntJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(NumberType.getDeclaredMethod("intValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        if (converter instanceof JTypeUtils.NumberToShortJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(NumberType.getDeclaredMethod("shortValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        if (converter instanceof JTypeUtils.NumberToLongJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(NumberType.getDeclaredMethod("longValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        if (converter instanceof JTypeUtils.NumberToFloatJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(NumberType.getDeclaredMethod("floatValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        if (converter instanceof JTypeUtils.NumberToDoubleJConverter) {
            JType NumberType = compilerContext.types().forName(Number.class.getName());
            return HGenUtils.call(NumberType.getDeclaredMethod("doubleValue()"), HGenUtils.Cast(NumberType, anyResultString), new HNode[0]);
        }
        throw new IllegalArgumentException("Unsupported");
    }

    public HNode prepareInvokableCall(HNode base, HNode[] args, JInvokable invokable, HLJCompilerContext2 compilerContext) {
        if (invokable instanceof ConvertedJMethod2) {
            ConvertedJMethod2 c = (ConvertedJMethod2) invokable;
            JArgumentConverter[] argConverters = c.getArgConverters();
            JInvokable other = c.getOther();
            int newArgCount = other.getSignature().argsCount();
            HNode[] args2 = new HNode[newArgCount];
            for (int i = 0; i < newArgCount; i++) {
                if (argConverters != null && argConverters[i] != null) {
                    if (argConverters[i] instanceof JArgumentConverterByIndex) {
                        int newIndex = ((JArgumentConverterByIndex) argConverters[i]).getNewIndex();
                        args2[i] = args[newIndex];
                    } else {
                        throw new JShouldNeverHappenException();
                    }
                } else {
                    args2[i] = args[i];
                }
            }
            JInstanceArgumentResolver ir = c.getInstanceArgumentResolver();
            if (ir == null) {
                throw new JShouldNeverHappenException();
            } else if (ir instanceof JInstanceArgumentResolverFromArgumentByIndex) {
                JInstanceArgumentResolverFromArgumentByIndex rr = (JInstanceArgumentResolverFromArgumentByIndex) ir;
                HNode nn = args[rr.getInstanceArgumentIndex()];
                HNode s = prepareInvokableCall(nn, args2, other, compilerContext/*.append(node)*/);
                if (c.getResultConverter() != null) {
                    s = Convert_ToString(c.getResultConverter(), s, compilerContext);
                }
                return s;
            } else {
                throw new JShouldNeverHappenException();
            }
        } else if (invokable instanceof JMethod) {
            JMethod m = (JMethod) invokable;
            JType t = m.getDeclaringType();
            if (m.isStatic() && t.getName().equals(HJavaDefaultOperators.class.getName())) {
                switch (m.getName()) {
                    case "neg": {
                        if (args.length == 1) {
                            return HGenUtils.neg(args[0]);
                        }
                        break;
                    }
                    case "plus": {
                        if (args.length == 2) {
                            return HGenUtils.binop("+", args);
                        }
                        break;
                    }
                    case "minus": {
                        if (args.length == 2) {
                            return HGenUtils.binop("-", args);
                        }
                        break;
                    }
                    case "mul": {
                        if (args.length == 2) {
                            return HGenUtils.binop("*", args);
                        }
                        break;
                    }
                    case "div": {
                        if (args.length == 2) {
                            return HGenUtils.binop("/", args);
                        }
                        break;
                    }
                    case "rem": {
                        if (args.length == 2) {
                            return HGenUtils.binop("%", args);
                        }
                        break;
                    }
                    case "and": {
                        if (args.length == 2) {
                            return HGenUtils.binop("&&", args);
                        }
                        break;
                    }
                    case "or": {
                        if (args.length == 2) {
                            return HGenUtils.binop("||", args);
                        }
                        break;
                    }
                }
                throw new JParseException("Unsupported");

            } else {
                HNode newBase = null;
                if (m.isStatic()) {
                    newBase = HNodeUtils.createTypeToken(t);
                } else {
                    if (base != null) {
                        newBase = base;
                    }
                }
                return HGenUtils.call(m, newBase, args);
            }
        } else if (invokable instanceof JConstructor) {
            JConstructor m = (JConstructor) invokable;
            JType t = m.getDeclaringType();
            return HGenUtils.New(t, args);
        } else if (invokable instanceof PrimitiveEqualsInvokable
                || (invokable instanceof StrictEqualsInvokable)) {
            return HGenUtils.binop("==", args);
        } else if (invokable instanceof PrimitiveNotEqualsInvokable
                || (invokable instanceof StrictNotEqualsInvokable)) {
            return HGenUtils.binop("!=", args);
        } else if ((invokable instanceof SafeEqualsInvokable)) {
            return HGenUtils.callStatic(
                    compilerContext.types().forName(Objects.class.getName()).getDeclaredMethod(
                            "deepEquals(java.lang.Object,java.lang.Object)"
                    ),
                    compilerContext.types().forName(Objects.class.getName()), args);
        } else if (invokable instanceof SafeNotEqualsInvokable) {
            return HGenUtils.not(HGenUtils.callStatic(compilerContext.types().forName(Objects.class.getName()).getDeclaredMethod(
                    "deepEquals(java.lang.Object,java.lang.Object)"
            ), compilerContext.types().forName(Objects.class.getName()), args));
        } else if (invokable instanceof PrimitiveCompareInvokable) {
            return HGenUtils.binop(((PrimitiveCompareInvokable) invokable).getOp(), args);
        } else if (invokable instanceof CompareToGreaterEqualsThanInvokable) {
            return HGenUtils.binop(">=", args);
        } else if (invokable instanceof CompareToGreaterThanInvokable) {
            return HGenUtils.binop(">", args);
        } else if (invokable instanceof CompareToLessEqualsThanInvokable) {
            return HGenUtils.binop("<=", args);
        } else if (invokable instanceof CompareToLessThanInvokable) {
            return HGenUtils.binop("<", args);
        } else {
            throw new IllegalArgumentException("Unsupported Invokable " + invokable.getClass().getSimpleName());
        }
    }

    public HNode getInvokableCallNode(JNode newNode, JNode oldNode) {
        HNElementInvokable ei = getElementInvokable(newNode);
        HNode ebase = null;
        HNode[] eArgs = null;
        if (ei != null) {
            //sometimes the element is copied from child, so process this
            eArgs = ei.getArgNodes();
            List<JNode> oldNodes = oldNode.getChildrenNodes();
            List<JNode> newNodes = newNode.getChildrenNodes();
            boolean processedFromChild = false;
            for (int i = 0; i < oldNodes.size(); i++) {
                if (((HNode) newNodes.get(i)).getElement() != null && ((HNode) oldNode).getElement() == ((HNode) oldNodes.get(i)).getElement()) {
                    ei = (HNElementInvokable) ((HNode) newNodes.get(i)).getElement();
                    processedFromChild = true;
                }
            }
            if (!processedFromChild) {
                HNode[] oldArgNode = ei.getArgNodes();
                HNode[] newArgNode = new HNode[oldArgNode.length];
                for (int i = 0; i < oldArgNode.length; i++) {
                    int index = refIndexOf(oldArgNode[i], oldNodes, -1);
                    if (index < 0) {
                        newArgNode[i] = oldArgNode[i];
                    } else {
                        if (i < newNodes.size()) {
                            newArgNode[i] = (HNode) newNodes.get(index);
                        } else {
                            newArgNode[i] = oldArgNode[i];
                        }
                    }
                }
                ei = (HNElementInvokable) ei.copy();
                ei.setArgNodes(newArgNode);
                eArgs = newArgNode;
            }
            if (ei instanceof HNElementMethod) {
                HNElementMethod.Arg0Kind k = ((HNElementMethod) ei).getArg0Kind();
                if (k == HNElementMethod.Arg0Kind.BASE) {
                    ebase = ei.getArgNodes()[0];
                }
            }
            return prepareInvokableCall(
                    ebase,
                    eArgs,
                    ei.getInvokable(),
                    compilerContext0
            );
        }
        return null;
    }

    @Override
    public void processProject(HLProject project, HLOptions options) {
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
            HNode node = compilerContextBase.getNode();
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
            case H_LITERAL: {
                return onLiteral((HNLiteral) node, compilerContext);
            }
            case H_STRING_INTEROP: {
                return onStringInterop((HNStringInterop) node, compilerContext);
            }
            case H_EXTENDS: {
                return onExtends((HNExtends) node, compilerContext);
            }
            case H_CAST: {
                return onCast((HNCast) node, compilerContext);
            }
            case H_TRY_CATCH: {
                return onTryCatch((HNTryCatch) node, compilerContext);
            }
            case H_CATCH: {
                return onCatch((HNTryCatch.CatchBranch) node, compilerContext);
            }
            case H_ANNOTATION: {
                return onAnnotation((HNAnnotationCall) node, compilerContext);
            }
            case H_THIS:
            case H_LITERAL_DEFAULT:
            case H_TYPE_TOKEN:
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

    private HNode onAnnotation(HNAnnotationCall node, HLJCompilerContext2 compilerContext) {
        return node;
    }

    private HNode onCatch(HNTryCatch.CatchBranch node, HLJCompilerContext2 compilerContext) {
        HNTryCatch.CatchBranch c = (HNTryCatch.CatchBranch) copy0(node);
        if (c.getExceptionTypes().length == 0) {
            c.setExceptionTypes(new HNTypeToken[]{
                    HNodeUtils.createTypeToken(JTypeUtils.forException(compilerContext.base.types()))
            });
        }
        if (c.getIdentifier() == null) {
            c.setIdentifier(new HNDeclareTokenIdentifier(
                    HTokenUtils.createToken("exception")
            ));
        }
        return c;
    }

    private HNode onTryCatch(HNTryCatch node, HLJCompilerContext2 compilerContext) {
        HNTryCatch n = (HNTryCatch) copy0(node);
        if(n.getFinallyBranch()==null && n.getResource()==null && n.getCatches().length==0){
            n.addCatch(HGenUtils.Catch(
                    JTypeUtils.forException(compilerContext.types()),
                    null,
                    HGenUtils.localBlock()));
        }
        if(n.getBody()==null){
            n.setBody(HGenUtils.localBlock());
        }else if (!(n.getBody() instanceof HNBlock)){
            if(!n.isExpressionMode()){
                n.setBody(HGenUtils.localBlock(n.getBody()));
            }
        }
        for (HNTryCatch.CatchBranch aCatch : n.getCatches()) {
            if(aCatch.getExceptionTypes().length==0){
                aCatch.setExceptionTypes(new HNTypeToken[]{
                        HGenUtils.type(JTypeUtils.forException(compilerContext.types()))
                });
            }
            if(aCatch.getIdentifier()==null){
                aCatch.setIdentifier(HGenUtils.tokenIdentifier("$e"));
            }
        }
        if (n.isExpressionMode()) {
            if(!JTypeUtils.isVoid(n.getElement().getType())) {
                n.setBody(HGenUtils.EnsureReturn(n.getBody()));
                HNTryCatch.CatchBranch[] catches = n.getCatches();
                for (HNTryCatch.CatchBranch aCatch : catches) {
                    aCatch.setDoNode(HGenUtils.EnsureReturn(aCatch.getDoNode()));
                }
            }
        }
        return n;
    }

    private HNode onCast(HNCast node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onExtends(HNExtends node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onStringInterop(HNStringInterop node, HLJCompilerContext2 compilerContext) {
        HNStringInterop newNode = (HNStringInterop) node.copy(copyFactory);
        JType MessageFormatType = compilerContext.base.types().forName("java.text.MessageFormat");
        JType StringType = JTypeUtils.forString(compilerContext.base.types());

        JMethod jMethod = MessageFormatType.getDeclaredMethod("format(java.lang.String,java.lang.Object[])");
        List<HNode> allArgs = new ArrayList<>();
        allArgs.add(new HNLiteral(newNode.getJavaMessageFormatString(), StringType, null).setElement(new HNElementExpr(StringType)));
        allArgs.addAll(Arrays.asList(newNode.getExpressions()));
        return HGenUtils.callStatic(jMethod, MessageFormatType, allArgs.toArray(new HNode[0]));
    }

    public HNDeclareType lookupEnclosingDeclareTypeNew(HLJCompilerContext2 compilerContext) {
        for (int i = contextStack.size() - 1; i >= 0; i--) {
            if (contextStack.get(i) instanceof HNDeclareType) {
                return (HNDeclareType) contextStack.get(i);
            }
        }
        JavaNodes jn = JavaNodes.of(compilerContext.project());
        return jn.getMetaPackage();
    }

    private HNode onLiteral(HNLiteral node, HLJCompilerContext2 compilerContext) {
        if (node.getValue() instanceof Pattern) {
            Pattern p0 = (Pattern) node.getValue();
            HNDeclareType newType = lookupEnclosingDeclareTypeNew(compilerContext);
            HNBlock body = HNBlock.get(newType.getBody());
            if (body == null) {
                body = new HNBlock(HNBlock.BlocType.CLASS_BODY, new HNode[0], null, null);
                newType.setBody(body);
            }
            JType patternType = compilerContext.base.types().forName(Pattern.class.getName());
            for (HNDeclareIdentifier old : body.findDeclaredIdentifiers()) {
                if (old.isSetUserObject("CompilerGeneratedPattern")) {
                    Pattern p = (Pattern) ((HNLiteral) old.getInitValue()).getValue();
                    if (p.equals(p0)) {
                        HNDeclareTokenIdentifier identifierToken = (HNDeclareTokenIdentifier) old.getIdentifierToken();
                        JToken token = identifierToken.getToken();
                        return new HNIdentifier(
                                token
                        ).setElement(identifierToken.getElement());
                    }
                }
            }
            String nextVar = compilerContext.base.nextVarName(null, newType);
            JToken nextVarToken = HTokenUtils.createToken(nextVar);
            HNDeclareTokenIdentifier hnDeclareTokenIdentifier = new HNDeclareTokenIdentifier(nextVarToken);
            HNDeclareIdentifier newDecl = (HNDeclareIdentifier) new HNDeclareIdentifier(
                    (HNDeclareToken)
                            hnDeclareTokenIdentifier.setElement(new HNElementField(
                                    nextVarToken.image,
                                    compilerContext.base.lookupType(newType.getFullName()),
                                    hnDeclareTokenIdentifier,
                                    null
                            ).setEffectiveType(patternType)),
                    copy0(node), HNodeUtils.createTypeToken(patternType),
                    HTokenUtils.createToken("="), null, null
            ).addAnnotations(
                    HNodeUtils.createAnnotationModifierCall("private"),
                    HNodeUtils.createAnnotationModifierCall("static")
            );
            newDecl.setUserObject("CompilerGeneratedPattern");
            body.add(newDecl);

            return new HNIdentifier(nextVarToken).setElement(hnDeclareTokenIdentifier.getElement());
        }

        return copy0(node);
    }

    private HNode onBlock(HNBlock node, HLJCompilerContext2 compilerContext) {
        switch (node.getBlocType()) {
            case GLOBAL_BODY: {
                for (HNode statement : node.getStatements()) {
                    onBlockGlobal(statement, compilerContext);
                }
                return null;
            }
            case CLASS_BODY: {
                HNBlock newBlock = new HNBlock(node.getBlocType(), new HNode[0], null, null);
                BooleanRef dissociateInstance = new BooleanRef(false);
                BooleanRef dissociateStatic = new BooleanRef(false);
                for (HNode statement : node.getStatements()) {
                    onBlockClass(statement, compilerContext, newBlock, dissociateStatic, dissociateInstance);
                }
                return newBlock;
            }

            case LOCAL_BLOC:
            case METHOD_BODY:
            case INSTANCE_INITIALIZER:
            case STATIC_INITIALIZER: {
                HNBlock newBlock = new HNBlock(node.getBlocType(), new HNode[0], null, null);
                for (HNode statement : node.getStatements()) {
                    onBlockLocal(statement, compilerContext, newBlock);
                }
                return newBlock;
            }
            default: {
                throw new JShouldNeverHappenException();
            }
        }
    }

    private void onBlockGlobal(HNode statement, HLJCompilerContext2 compilerContext) {
        JavaNodes jn = JavaNodes.of(compilerContext.project());
        switch (statement.id()) {
            case H_IMPORT: {
                //ignore
                break;
            }
            case H_DECLARE_TYPE: {
                HNDeclareType r = (HNDeclareType) processNode(statement, compilerContext);
                JTextSource s0 = HUtils.getSource(statement);
                HUtils.setSource(r, s0);
                jn.getTopLevelTypes().add(r);
                break;
            }

            case H_DECLARE_INVOKABLE: {
                JTextSource s0 = HUtils.getSource(statement);
                if (s0 != null) {
                    jn.getMetaPackageSources().add(s0);
                }
                HNDeclareInvokable r = (HNDeclareInvokable) processNode(statement, compilerContext);
                r.addAnnotationsNoDuplicates(
                        HNodeUtils.createAnnotationModifierCall("static")
                );
                HNBlock metaBody = (HNBlock) jn.getMetaPackage().getBody();
                if (metaBody == null) {
                    metaBody = new HNBlock(HNBlock.BlocType.CLASS_BODY, new HNode[0], null, null);
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
                JTextSource s0 = HUtils.getSource(statement);
                if (s0 != null) {
                    jn.getMetaPackageSources().add(s0);
                }
                Supplier<HNBlock> dissociateBlock = () -> getOrCreateRunModuleMethodBody(compilerContext);
                HNDeclareIdentifier hs0 = (HNDeclareIdentifier) statement;
                HNode left = (HNode) copyFactory.copy(hs0.getIdentifierToken());
                HNode right = hs0.getInitValue() == null ? null : (HNode) copyFactory.copy(hs0.getInitValue());
                _fillBlockAssign(left, right,
                        (HNBlock) jn.getMetaPackage().getBody(), dissociateBlock,
                        isAssignRequireTempVar(left, right), true, true, compilerContext);
                //ignore
                break;
            }
            default: {
                if (statement instanceof HNBlock && HNBlock.get(statement).getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                    for (HNode hNode : HNBlock.get(statement).getStatements()) {
                        onBlockGlobal(hNode, compilerContext);
                    }
                } else {
                    JTextSource s0 = HUtils.getSource(statement);
                    if (s0 != null) {
                        jn.getMetaPackageSources().add(s0);
                    }
                    HNBlock body = getOrCreateRunModuleMethodBody(compilerContext);
                    HNode n2 = processNode(statement, compilerContext.withNewParent(body));
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
    }

    private void onBlockClass(HNode statement, HLJCompilerContext2 compilerContext, HNBlock newBlock,
                              BooleanRef dissociateStatic, BooleanRef dissociateInstance) {
        switch (statement.id()) {
            case H_IMPORT: {
                break;
            }
            case H_DECLARE_TYPE: {
                newBlock.add(processNode(statement, compilerContext));
                break;
            }
            case H_DECLARE_INVOKABLE: {
                newBlock.add(processNode(statement, compilerContext));
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
                HNDeclareIdentifier hs0 = (HNDeclareIdentifier) (HNode) statement;
                boolean dissociate = hs0.isStatic() ? dissociateStatic.get() : dissociateInstance.get();
                Supplier<HNBlock> dissociateBlock = null;
                HNBlock newBlock0 = newBlock;
                if (dissociate) {
                    dissociateBlock = () -> getOrCreateSpecialInitializer(newBlock, compilerContext, hs0.isStatic());
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
                HNAssign hs0 = (HNAssign) (HNode) statement;
                if (hs0.isStatic()) {
                    dissociateStatic.set(true);
                } else {
                    dissociateInstance.set(true);
                }
                HNode left = (HNode) copyFactory.copy(hs0.getLeft());
                HNode right = hs0.getRight() == null ? null : (HNode) copyFactory.copy(hs0.getRight());
                _fillBlockAssign(left, right,
                        getOrCreateSpecialInitializer(newBlock, compilerContext, hs0.isStatic())
                        , null, isAssignRequireTempVar(left, right), true, false, compilerContext);
                break;
            }
            default: {
                if (statement instanceof HNBlock && HNBlock.get(statement).getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                    for (HNode hNode : HNBlock.get(statement).getStatements()) {
                        onBlockClass(hNode, compilerContext, newBlock, dissociateStatic, dissociateInstance);
                    }
                } else {
                    dissociateInstance.set(true);
                    HNBlock body = getOrCreateSpecialInitializer(newBlock, compilerContext, false);
                    body.add(processNode((HNode) statement, compilerContext));
                }
            }
        }
    }

    private void onBlockLocal(HNode statement, HLJCompilerContext2 compilerContext, HNBlock newBlock) {
        switch (statement.id()) {
            case H_IMPORT: {
                break;//ignore
            }
            case H_DECLARE_TYPE: {
                newBlock.add(processNode(statement, compilerContext));
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
                HNDeclareIdentifier hs0 = (HNDeclareIdentifier) statement;
                HNode left = (HNode) copyFactory.copy(hs0.getIdentifierToken());
                HNode right = hs0.getInitValue() == null ? null : (HNode) copyFactory.copy(hs0.getInitValue());
                right = initializeAssignRight(compilerContext, left, right, false);
                _fillBlockAssign(left, right,
                        newBlock, null, isAssignRequireTempVar(left, right), false, false, compilerContext);
                //ignore
                break;
            }
            default: {
                if (statement instanceof HNBlock && HNBlock.get(statement).getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                    for (HNode hNode : HNBlock.get(statement).getStatements()) {
                        onBlockLocal(hNode, compilerContext, newBlock);
                    }
                } else {
                    HNode n2 = processNode(statement, compilerContext);
                    if (n2.id() == HNNodeId.H_BLOCK && ((HNBlock) n2).getBlocType() == HNBlock.BlocType.EXPR_GROUP) {
                        for (HNode s2 : ((HNBlock) n2).getStatements()) {
                            newBlock.add(s2);
                        }
                    } else {
                        newBlock.add(n2);
                    }
                }
                break;
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
                    nhs1.addAnnotationsNoDuplicates(
                            HNodeUtils.createAnnotationModifierCall("static")
                    );
                }
                if (publify) {
                    nhs1.setAnnotations(HUtils.publifyModifiers(nhs1.getAnnotations()));
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
                        (HNTypeToken) copyFactory.copy(nhs0.getIdentifierTypeNode()),
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
        HNode r = copy0(node);
        if (r instanceof HNSwitch) {
            HNSwitch sw = (HNSwitch) r;
            HNode e = sw.getExpr();
            sw.setExpr(e = HUtils.skipFirstPar(e));
            if (e instanceof HNDeclareIdentifier) {
                HNBlock b = createExprGroup();
                b.add(e);
                HNDeclareTokenIdentifier identifierToken = (HNDeclareTokenIdentifier) ((HNDeclareIdentifier) e).getIdentifierToken();
                sw.setExpr(new HNIdentifier(identifierToken.getToken()));
            }
        }
        return r;
    }

    private HNBlock createExprGroup() {
        return new HNBlock(HNBlock.BlocType.EXPR_GROUP, new HNode[0], null, null);
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
        try {
            HNDeclareType newType = new HNDeclareType();
            contextStack.push(newType);
            newType.copyFrom(node, copyFactory);
            prepareJavaModifiers(newType);
            List<HNExtends> anExtends = newType.getExtends();
            List<HNExtends> interfaces = new ArrayList<>();
            for (Iterator<HNExtends> iterator = anExtends.iterator(); iterator.hasNext(); ) {
                HNExtends anExtend = iterator.next();
                JType jt = HNElementType.get(anExtend).getValue();
                if (jt.isInterface()) {
                    interfaces.add(anExtend);
                    iterator.remove();
                }
            }
            //put interfaces at the end of the extends clause
            anExtends.addAll(interfaces);
            newType.setPackageName(newType.getFullPackage());
            newType.setMetaPackageName(null);
            newType.setAnnotations(HNAnnotationList.publify(newType.getAnnotations()));
            return newType;
        } finally {
            contextStack.pop();
        }
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

    private HNode copy0(JNode oldNode) {
        HNElementInvokable ei = getElementInvokable(oldNode);
        if (ei != null) {
            HNode[] oldArgNodes = ei.getArgNodes();
            HNode[] newArgNodes = new HNode[oldArgNodes.length];
            for (int i = 0; i < newArgNodes.length; i++) {
                newArgNodes[i] = processNode(oldArgNodes[i], compilerContext0);
            }
            HNode ebase = null;
            if (ei instanceof HNElementMethod) {
                HNElementMethod m = (HNElementMethod) ei;
                HNElementMethod.Arg0Kind arg0Kind = m.getArg0Kind();
                if (arg0Kind == HNElementMethod.Arg0Kind.BASE) {
                    ebase = newArgNodes[0];
                }
            }
            return prepareInvokableCall(
                    ebase,
                    newArgNodes,
                    ei.getInvokable(),
                    compilerContext0
            );
        }
        HNode newNode = (HNode) oldNode.copy(copyFactory);
//        HNode ei = getInvokableCallNode(newNode, oldNode);
//        if (ei != null) {
//            return ei;
//        }
        return newNode;
    }

    private HNode onBracketsPostfix(HNBracketsPostfix node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onObjectNew(HNObjectNew node, HLJCompilerContext2 compilerContext) {
        return copy0(node);
    }

    private HNode onAssign(HNAssign node, HLJCompilerContext2 compilerContext) {
        HNBlock b = createExprGroup();
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
        HNBlock b = createExprGroup();
        HNode left = (HNode) copyFactory.copy(node.getIdentifierToken());
        HNode right = (HNode) (node.getInitValue() == null ? null : copyFactory.copy(node.getInitValue()));
        right = initializeAssignRight(compilerContext, left, right, true);
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

    private boolean isWithinLocalBlock(HNode left, HLJCompilerContext2 compilerContext) {
        HNode dd = compilerContext.base.lookupEnclosingDeclaration(left);
        if (dd instanceof HNBlock) {
            HNBlock bb = HNBlock.get(dd);
            if (bb.getBlocType() == HNBlock.BlocType.LOCAL_BLOC) {
                return true;
            }
        }
        return false;
    }

    private HNode initializeAssignRight(HLJCompilerContext2 compilerContext, HNode left, HNode right, boolean check) {
        if (right == null) {
            if (!check || isWithinLocalBlock(left, compilerContext)) {
                JType ll = null;
                if (left instanceof HNDeclareTokenList) {
                    ll = (((HNDeclareTokenList) left).getIdentifierType());
                } else if (left instanceof HNDeclareTokenIdentifier) {
                    ll = (((HNDeclareTokenIdentifier) left).getIdentifierType());
                } else {
                    ll = compilerContext.base.getTypePattern(left).getType();
                }
                if (ll.isPrimitive()) {
                    switch (ll.getName()) {
                        case "boolean": {
                            right = new HNLiteral(false, JTypeUtils.forBoolean(compilerContext.base.types()), null);
                            break;
                        }
                        case "byte": {
                            right = new HNLiteral((byte) 0, JTypeUtils.forByte(compilerContext.base.types()), null);
                            break;
                        }
                        case "short": {
                            right = new HNLiteral((short) 0, JTypeUtils.forShort(compilerContext.base.types()), null);
                            break;
                        }
                        case "int": {
                            right = new HNLiteral((int) 0, JTypeUtils.forInt(compilerContext.base.types()), null);
                            break;
                        }
                        case "long": {
                            right = new HNLiteral((long) 0, JTypeUtils.forLong(compilerContext.base.types()), null);
                            break;
                        }
                        case "char": {
                            right = new HNLiteral((char) 0, JTypeUtils.forChar(compilerContext.base.types()), null);
                            break;
                        }
                        case "float": {
                            right = new HNLiteral((float) 0, JTypeUtils.forFloat(compilerContext.base.types()), null);
                            break;
                        }
                        case "double": {
                            right = new HNLiteral((double) 0, JTypeUtils.forDouble(compilerContext.base.types()), null);
                            break;
                        }
                    }
                } else {
                    right = new HNLiteral(null, JTypeUtils.forNull(compilerContext.base.types()), null);
                }


            }
        }
        return right;
    }

    private HNode onParsPostfix(HNParsPostfix node, HLJCompilerContext2 compilerContext) {
        HNode hNode = copy0(node);
        if (!(hNode instanceof HNParsPostfix)) {
            return hNode;
        }
//        HNParsPostfix m = (HNParsPostfix) hNode;
//        if (m.getElement().getKind() == HNElementKind.METHOD) {
//            JInvokable i = ((HNElementMethod) m.getElement()).getInvokable();
//            if (i instanceof JMethod) {
//                JMethod jm = (JMethod) i;
//                if (jm.isStatic()) {
//                    if (node.parentNode() != null) {
//                        switch (node.parentNode().id()) {
//                            case H_OP_DOT: {
//                                return m;
//                            }
//                        }
//                    }
//                    return new HNOpDot(
//                            new HNTypeToken(jm.declaringType(), null),
//                            HTokenUtils.createToken("."),
//                            m,
//                            null, null
//                    ).setElement(m.getElement());
//                }
//            }
//        }
//        return m;
        throw new IllegalArgumentException("Unupported");
    }

    private HNode onArrayNew(HNArrayNew node, HLJCompilerContext2 compilerContext) {
        return copy0(node);

    }

    private void prepareJavaModifiers(HNode node){
        DefaultJModifierList modifiers=new DefaultJModifierList();
        for (HNAnnotationCall annotation : node.getAnnotations()) {
            String modifierAnnotation = HNodeUtils.getModifierAnnotation(annotation);
            if(modifierAnnotation!=null){
                JPrimitiveModifier r = DefaultJModifierList.getDefault(modifierAnnotation);
                if(r!=null){
                    modifiers.add(r);
                    node.removeAnnotations(annotation);
                }
            }
        }
        if(!modifiers.contains(DefaultJModifierList.PUBLIC) && !modifiers.contains(DefaultJModifierList.PROTECTED) && !modifiers.contains(DefaultJModifierList.PRIVATE)){
            modifiers.add(DefaultJModifierList.PUBLIC);
        }
        node.setUserObject("JModifierList",modifiers);
    }

    private HNode onDeclareInvokable(HNDeclareInvokable node, HLJCompilerContext2 compilerContext) {
        HNDeclareInvokable copy = (HNDeclareInvokable) copy0(node);
        prepareJavaModifiers(copy);
        HNode body = copy.getBody();
        if (copy.isImmediateBody() && !JTypeUtils.isVoid(copy.getReturnType())) {
            copy.setBody(
                    new HNReturn(body, null, null)
            );
        }
        if(copy.isConstr()){
            copy.setNameToken(
                    HTokenUtils.createToken(compilerContext.base.getOrCreateType(node.getDeclaringType()).simpleName())
            );
        }
        body = copy.getBody();
        if (body != null) {
            if (!(body instanceof HNBlock)) {
                body = HGenUtils.localBlock(body);
            }
            HNBlock b = (HNBlock) body;
            if (b.getStatements().size() > 0) {
                boolean requireCatch = true;
                //TODO check if try is required!
                if (requireCatch) {
                    body = HGenUtils.localBlock(new HNTryCatch(null)
                            .setBody(body)
                            .addCatch(
                                    HGenUtils.Catch(compilerContext.types().forName(RuntimeException.class.getName()),
                                            HGenUtils.tokenIdentifier("$e"),
                                            HGenUtils.localBlock(HGenUtils.Throw(HGenUtils.identifier("$e")))
                                    )
                            )
                            .addCatch(
                                    HGenUtils.Catch(compilerContext.types().forName(Exception.class.getName()),
                                            HGenUtils.tokenIdentifier("$e"),
                                            HGenUtils.localBlock(HGenUtils.Throw(
                                                    HGenUtils.New(
                                                            compilerContext.types().forName(RuntimeException.class.getName()),
                                                            new HNode[]{
                                                                    HGenUtils.identifier("$e")
                                                            }
                                                    )
                                            ))
                                    )
                            ));
                }
            }
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
        if (node.getElement() instanceof HNElementType) {
            return HGenUtils.type(((HNElementType) node.getElement()).getValue());
        }
        HNode n = copy0(node);
        return n;
    }

    protected HNode onOpDot(HNOpDot node, HLJCompilerContext2 compilerContext) {
//        if (node.getRight().getElement() == node.getElement()) {
//            return processNode(node.getRight(), compilerContext);
//        }
        return copy0(node);
    }

    ///////////////////////////////////////////////:
    public HNDeclareInvokable getRunModuleMethod(HLJCompilerContext2 compilerContext) {
        HNDeclareType meta = JavaNodes.of(compilerContext.project()).getMetaPackage();
        HNBlock b = (HNBlock) meta.getBody();
        if (b == null) {
            b = new HNBlock(HNBlock.BlocType.CLASS_BODY, new HNode[0], null, null);
            meta.setBody(b);
        }
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
            ii.setElement(new HNElementMethod(null).setMethodName("runModule"));
            ii.setReturnTypeName(compilerContext.base.createSpecialTypeToken("void"));
            ii.setDeclaringType(meta);
            ii.setBody(new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[0], ii.getStartToken(), ii.getEndToken()));
//            ii.buildInvokable();
            ii.addAnnotationsNoDuplicates(
                    HNodeUtils.createAnnotationModifierCall("public"),
                    HNodeUtils.createAnnotationModifierCall("static")
            );
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

    public HNBlock getSpecialInitializer(HNBlock classTypeBody, HLJCompilerContext2 compilerContext, boolean isStatic) {
        for (HNode statement : classTypeBody.getStatements()) {
            if (statement instanceof HNBlock) {
                HNBlock specInit = (HNBlock) statement;
                if (isStatic) {
                    if (specInit.getBlocType() == HNBlock.BlocType.STATIC_INITIALIZER && specInit.isSetUserObject("SPECIAL_INITIALIZER")) {
                        return specInit;
                    }
                } else {
                    if (specInit.getBlocType() == HNBlock.BlocType.INSTANCE_INITIALIZER && specInit.isSetUserObject("SPECIAL_INITIALIZER")) {
                        return specInit;
                    }
                }
            }
        }
        return null;
    }

    public HNBlock getOrCreateSpecialInitializer(HNBlock classTypeBody, HLJCompilerContext2 compilerContext, boolean isStatic) {
        HNBlock m = getSpecialInitializer(classTypeBody, compilerContext, isStatic);
        if (m == null) {
            m = new HNBlock(
                    isStatic ? HNBlock.BlocType.STATIC_INITIALIZER : HNBlock.BlocType.INSTANCE_INITIALIZER,
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
        JToken dot = HTokenUtils.createToken(".");
        JToken assignOp = HTokenUtils.createToken("=");
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
            JToken newVarToken = HTokenUtils.createToken(newVarName);
            JTypePattern rightTypePattern = right.getElement().getTypePattern();
            if (rightTypePattern.isLambda()) {
                rightTypePattern = left.getElement().getTypePattern();
            }
            if (!fieldTemp || dissociatedBlock == null || right == null) {
                HNDeclareIdentifier decl = new HNDeclareIdentifier(
                        (HNDeclareToken) new HNDeclareTokenIdentifier(newVarToken)
                                .setElement(new HNElementLocalVar(newVarName).setEffectiveType(rightTypePattern.getType()))
                        , right,
                        new HNTypeToken(rightTypePattern.getType(), null), assignOp, null, null);
                if (fieldTemp) {
                    if(isField) {
                        decl.addAnnotationsNoDuplicates(
                                HNodeUtils.createAnnotationModifierCall("private")
                        );
                    }
                    if (isStatic) {
                        decl.addAnnotationsNoDuplicates(
                                HNodeUtils.createAnnotationModifierCall("static")
                        );
                    }
                }
                valDeclContext.add(decl);
                right = new HNIdentifier(newVarToken)
                        .setElement(
                                isField ? new HNElementField(newVarName).setEffectiveType(rightTypePattern.getType())
                                        : new HNElementLocalVar(newVarName).setEffectiveType(rightTypePattern.getType())
                        );
            } else {
                HNDeclareIdentifier decl = new HNDeclareIdentifier(
                        (HNDeclareToken) new HNDeclareTokenIdentifier(newVarToken)
                                .setElement(new HNElementLocalVar(newVarName).setEffectiveType(rightTypePattern.getType()))
                        , null,
                        new HNTypeToken(rightTypePattern.getType(), null), assignOp, null, null);
                if (fieldTemp) {
                    if(isField) {
                        decl.addAnnotationsNoDuplicates(
                                HNodeUtils.createAnnotationModifierCall("private")
                        );
                    }
                    if (isStatic) {
                        decl.addAnnotationsNoDuplicates(
                                HNodeUtils.createAnnotationModifierCall("static")
                        );
                    }
                }
                HNBlock body = valDeclContext;//dissociatedBlock.get();

                body.add(decl);

                HNIdentifier newRight = (HNIdentifier) new HNIdentifier(newVarToken)
                        .setElement(
                                isField ? new HNElementField(newVarName).setEffectiveType(rightTypePattern.getType())
                                        : new HNElementLocalVar(newVarName).setEffectiveType(rightTypePattern.getType())
                        );

                HNAssign hass = new HNAssign((HNode) copyFactory.copy(newRight), assignOp, right, null, null);
                hass.setElement(new HNElementAssign(rightTypePattern.getType()));
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
                HNIdentifier tupleMember = new HNIdentifier(HTokenUtils.createToken(tupleMemberName));
                tupleMember.setElement(new HNElementField(tupleMemberName));
                _fillBlockAssign(n, new HNOpDot(right, dot, tupleMember, null, null), block, dissociatedBlock, false, isField, isStatic, compilerContext);
            }
        } else if (left instanceof HNIdentifier) {
            HNIdentifier leftId = (HNIdentifier) left;
            JType identifierType = leftId.getElement().getType();
            HNAssign hass = new HNAssign(left, HTokenUtils.createToken("="), right, null, null);

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
                if(isField) {
                    nhs1.setAnnotations(HUtils.publifyModifiers(nhs1.getAnnotations()));
                }
                if (isStatic) {
                    nhs1.setAnnotations(HUtils.statifyModifiers(nhs1.getAnnotations()));
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
                if(isField) {
                    nhs1.setAnnotations(HUtils.publifyModifiers(nhs1.getAnnotations()));
                }
                if (isStatic) {
                    nhs1.setAnnotations(HUtils.statifyModifiers(nhs1.getAnnotations()));
                }
                block.add(nhs1);
            }
        } else if (left instanceof HNDeclareTokenTuple) {
            HNDeclareTokenTuple leftTuple = (HNDeclareTokenTuple) left;
            HNode[] items = leftTuple.getItems();
            for (int i = 1; i <= items.length; i++) {
                HNode n = (HNode) items[i - 1];
                String tupleMemberName = "_" + i;
                HNIdentifier tupleMember = new HNIdentifier(HTokenUtils.createToken(tupleMemberName));
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
                if(isField) {
                    nhs1.setAnnotations(HUtils.publifyModifiers(nhs1.getAnnotations()));
                }
                if (isStatic) {
                    nhs1.setAnnotations(HUtils.statifyModifiers(nhs1.getAnnotations()));
                }
                block.add(nhs1);
                if (right != null) {
                    HNode first = null;
                    for (HNDeclareTokenIdentifier object : leftId.getItems()) {
                        HNode hnIdentifier = new HNIdentifier(object.getToken())
                                .setElement(
                                        isField ? new HNElementField(object.getName()).setEffectiveType(identifierType)
                                                : new HNElementLocalVar(object.getName()).setEffectiveType(identifierType)
                                );
                        if (first == null || right instanceof HNLiteral) {
                            first = hnIdentifier;
                            _fillBlockAssign(hnIdentifier, right, dissociatedBlock.get(), null, false, isField, isStatic, compilerContext);
                        } else {
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
                if(isField) {
                    nhs1.setAnnotations(HUtils.publifyModifiers(nhs1.getAnnotations()));
                }
                if (isStatic) {
                    nhs1.setAnnotations(HUtils.statifyModifiers(nhs1.getAnnotations()));
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

        public JTypes types() {
            return base.types();
        }

        public HLJCompilerContext2 withNewParent(HNBlock newParent) {
            return new HLJCompilerContext2(base, newParent);
        }

    }


}
