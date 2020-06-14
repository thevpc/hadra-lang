package net.vpc.hadralang.compiler.stages.generators.java;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.eval.JEvaluableNode;
import net.vpc.common.jeep.core.eval.JEvaluableValue;
import net.vpc.common.jeep.impl.CastJConverter;
import net.vpc.common.jeep.impl.eval.JEvaluableConverter;
import net.vpc.common.jeep.impl.functions.JFunctionConverted;
import net.vpc.common.jeep.impl.functions.JMethodInvocationFunction;
import net.vpc.common.jeep.JParameterizedType;
import net.vpc.common.jeep.JTypeArray;
import net.vpc.common.jeep.util.JStringUtils;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.invokables.*;
import net.vpc.hadralang.compiler.stages.HLCStage;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.stdlib.IntRange;
import net.vpc.hadralang.stdlib.ext.HHelpers;
import net.vpc.hadralang.stdlib.ext.HJavaDefaultOperators;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.hadralang.compiler.core.elements.HNElement;
import net.vpc.hadralang.compiler.core.elements.HNElementAssign;
import net.vpc.hadralang.compiler.core.elements.HNElementConstructor;
import net.vpc.hadralang.compiler.core.elements.HNElementMethod;

public class HLCStage09JavaGenerator implements HLCStage {

    int counter = 0;
    private boolean staticStdImportExtensions = true;
    private boolean staticStdDefaults = true;
    private static final Logger LOG = Logger.getLogger(HLCStage09JavaGenerator.class.getName());

    @Override
    public void processProject(HLProject project, HLCOptions options) {
        generate(project, options.generateJavaFolder(), options.isClean());
    }

    public void generate(HLProject program, File folder, boolean clean) {
        if (clean) {
            if (folder.isDirectory()) {
//                if(new File(".java-generated").exists()){
//                   folder.get
//                }
            }
        }
        JavaNodes jn = JavaNodes.of(program);
        GenGlobalContext globalContext = new GenGlobalContext(program);
        Set<String> sources = new LinkedHashSet<>();
        for (JSource sourceNode : jn.getMetaPackageSources()) {
            sources.add(sourceNode.name());
        }
        generateClassFile((HNDeclareType) jn.getMetaPackage(), folder, sources.toArray(new String[0]), globalContext, program, true);
        for (HNDeclareType classDeclaration : jn.getTopLevelTypes()) {
            generateClassFile(classDeclaration, folder, new String[]{HUtils.getSourceName(classDeclaration)}, globalContext, program, false);
        }
    }

    protected void processCompilationUnit(HNBlock compilationUnitNode, File folder, String[] sources, GenGlobalContext globalContext, HLProject project, boolean metaPackageType) {
        for (JNode childrenNode : compilationUnitNode.childrenNodes()) {
            if (childrenNode instanceof HNDeclareType) {
                generateClassFile((HNDeclareType) childrenNode, folder,
                        new String[]{HUtils.getSourceName(childrenNode)}, globalContext, project, false);
            } else {
                throw new JShouldNeverHappenException();
            }
        }
    }

    protected void generateClassFile(HNDeclareType classDeclaration, File folder, String[] sources, GenGlobalContext globalContext, HLProject program, boolean metaPackageType) {
        String ns = classDeclaration.getFullPackage();
        String name = classDeclaration.getName();
        StringBuilder relativePath = new StringBuilder();
        if (!JStringUtils.isBlank(ns)) {
            if (relativePath.length() > 0 && relativePath.charAt(relativePath.length() - 1) != File.separatorChar) {
                relativePath.append(File.separatorChar);
            }
            relativePath.append(ns.replace('.', File.separatorChar));
        }
        if (relativePath.length() > 0 && relativePath.charAt(relativePath.length() - 1) != File.separatorChar) {
            relativePath.append(File.separatorChar);
        }
        relativePath.append(name);
        relativePath.append(".java");
        File file = new File(folder, relativePath.toString());

        try {
            HLGenCompilationUnitContext cuctx = new HLGenCompilationUnitContext(globalContext, program.languageContext().types());
            cuctx.setCurrentType(classDeclaration.getjType());
            cuctx.setModuleClass(metaPackageType);
            StringPrec s = onDeclareType(classDeclaration, cuctx, new JNodePath());
            StringBuilder fileString = new StringBuilder();
            fileString.append(JGHelper.commentsCartridge(Arrays.asList(sources)));
            if (ns != null && ns.length() > 0) {
                fileString.append("package ").append(ns).append(";").append("\n");
            }
            for (String value : cuctx.imports()) {
                fileString.append("import ").append(value).append(";\n");
            }
            for (String value : cuctx.staticImports()) {
                fileString.append("import static ").append(value).append(";\n");
            }
            fileString.append(s);
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
            LOG.log(Level.INFO, "Generate {0}", file.getCanonicalPath());
            Files.write(file.toPath(), fileString.toString().getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public StringPrec nodeToString(HNode node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (node == null) {
            return new StringPrec("null");
        }
        switch (((HNode) node).id()) {
            case H_DECLARE_TYPE:
                return onDeclareType((HNDeclareType) node, cuctx, path);
            case H_BLOCK:
                return onBlock((HNBlock) node, cuctx, false, false, path);
            case H_DECLARE_IDENTIFIER:
                return onDeclareIdentifier((HNDeclareIdentifier) node, cuctx, path);
            case H_DECLARE_INVOKABLE:
                return onDeclareInvokable((HNDeclareInvokable) node, cuctx, path);
            case H_LITERAL:
                return JNodeLiteral_ToString((HNLiteral) node, cuctx, path);
            case H_LITERAL_DEFAULT:
                return JNodeHDefaultLiteral_ToString((HNLiteralDefault) node, cuctx, path);
            case H_ASSIGN:
                return onAssign((HNAssign) node, cuctx, path);
            case H_THIS:
                return JNodeHThis_ToString((HNThis) node, cuctx, path);
            case H_SUPER:
                return JNodeHSuper_ToString((HNSuper) node, cuctx, path);
            case H_OBJECT_NEW:
                return onObjectNew((HNObjectNew) node, cuctx, path);
            case H_ARRAY_NEW:
                return onArrayNew((HNArrayNew) node, cuctx, path);
            case H_LAMBDA_EXPR:
                return onLambdaExpression((HNLambdaExpression) node, cuctx, path);
            case H_TUPLE:
                return onTuple((HNTuple) node, cuctx, path);
            case H_IF:
                return JNodeHIf_ToString((HNIf) node, cuctx, path);
            case H_WHILE:
                return JNodeHWhileNode_ToString((HNWhile) node, cuctx, path);
            case H_FOR:
                return JNodeHForNode_ToString((HNFor) node, cuctx, path);
            case H_BREAK:
                return JNodeHBreak_ToString((HNBreak) node, cuctx, path);
            case H_CONTINUE:
                return JNodeHContinueJNodeHBreak_ToString((HNContinue) node, cuctx, path);
            case H_DOT_THIS:
                return JNodeHOpThis_ToString((HNDotThis) node, cuctx, path);
            case H_SWITCH:
                return JNodeHSwitch_ToString((HNSwitch) node, cuctx, path);
            case H_RETURN:
                return onReturn((HNReturn) node, cuctx, path);
            case H_IDENTIFIER:
                return JNodeHIdentifier_ToString((HNIdentifier) node, cuctx, path);
            case H_CAST:
                return JNodeHCast_ToString((HNCast) node, cuctx, path);
            case H_TYPE_TOKEN:
                return JNodeHTypeToken_ToString((HNTypeToken) node, cuctx, path);
            case H_IS:
                return JNodeHIs_ToString((HNIs) node, cuctx, path);
            case H_OP_DOT:
                return onOpDot((HNOpDot) node, cuctx, path);
            case H_PARS_POSTFIX:
                return onParsPostfix((HNParsPostfix) node, cuctx, path);
            case H_PARS:
                return onPars((HNPars) node, cuctx, path);
            case H_BRACKETS_POSTFIX:
                return onBraketsPostfix((HNBracketsPostfix) node, cuctx, path);
//            case "HNField":
//                return JNodeHField_ToString((HNField) node, cuctx, path);
//            case "JNodeRaw":
//                return JNodeRaw_ToString((JNodeRaw) node, cuctx, path);
//            case "JNodeHOpClass":
//                return JNodeHOpClass_ToString((HNDotClass) node, cuctx, path);
            case H_OP_UNARY:
                return onOpUnaryCall((HNOpUnaryCall) node, cuctx, path);
            case H_OP_BINARY:
                return onOpBinaryCall((HNOpBinaryCall) node, cuctx, path);
//            case "HNOpCoalesce":
//                return JNodeHApplyWhenExistsOperator_ToString((HNOpCoalesce) node, cuctx, path);
//            case "HNArrayCall":
//                return JNodeHArrayGet_ToString((HNArrayCall) node, cuctx, path);
//            case "HNInvokerCall":
//                return JNodeFunctionCall_ToString((HNInvokerCall) node, cuctx, path);
//            case "HNMethodCall":
//                return JNodeHMethodCall_ToString((HNMethodCall) node, cuctx, path);
//            case "HNVar":
//                return JNodeHVar_ToString((HNVar) node, cuctx, path);
//            case "JNodeHAssignStaticField":
//                return JNodeHAssignStaticField_ToString((JNodeHAssignStaticField) node, cuctx);
        }
        throw new IllegalArgumentException("Unsupported Java String for " + node.getClass().getSimpleName());
    }

    private StringPrec JNodeHIs_ToString(HNIs node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(nodeToString(node.getBase(), cuctx, path)
                //always box type in instanceof and de-generify it
                + " instanceof " + cuctx.nameWithImports(node.getIdentifierType().boxed().rawType()));
    }

    private StringPrec JNodeHTypeToken_ToString(HNTypeToken node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(
                cuctx.nameWithImports(node.getTypeVal())
        );
    }

    private StringPrec JNodeHCast_ToString(HNCast node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(
                "(("
                + nodeToString(node.getTypeNode(), cuctx, path)
                + ")"
                + "(" + nodeToString(node.getBase(), cuctx, path) + "))"
        );
    }

    private StringPrec JNodeHIdentifier_ToString(HNIdentifier node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(node.getName());
    }

    private StringPrec onReturn(HNReturn node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(
                "return "
                + (node.getExpr() == null ? "" : nodeToString(node.getExpr(), cuctx, path))
                +";"
        );
    }

    private StringPrec JNodeHSwitch_ToString(HNSwitch node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNode ee = node.getExpr();
        StringBuilder sb = new StringBuilder();
        if (ee instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier nid = (HNDeclareIdentifier) ee;
            String identifierName = ((HNDeclareTokenIdentifier) nid.getIdentifierToken()).getName();
            sb.append(nid.getIdentifierTypeName());
            sb.append(" ");
            sb.append(identifierName);
            sb.append("=");
            sb.append(nodeToString(nid.getInitValue(), cuctx, path));
            sb.append(";");
            sb.append("\nswitch(").append(identifierName).append("){");
        } else {
            sb.append("\nswitch(").append(nodeToString(ee, cuctx, path)).append("){");
        }
        for (HNSwitch.SwitchBranch aCase : node.getCases()) {
            StringBuilder sb2 = new StringBuilder();
            if (aCase instanceof HNSwitch.SwitchCase) {
                for (HNode whenNode : ((HNSwitch.SwitchCase) aCase).getWhenNodes()) {
                    sb2.append("\ncase ").append(nodeToString(whenNode, cuctx, path)).append(": ");
                }
                sb2.append(nodeToString(aCase.getDoNode(), cuctx, path));
                HNSwitch.SwitchCase cc = (HNSwitch.SwitchCase) aCase;
                HNode doNode = cc.getDoNode();
                if (HNodeUtils.requireExplicitBreak(doNode)) {
                    sb2.append("\nbreak;");
                }
            } else {
                throw new JShouldNeverHappenException();
            }
            sb.append(indent(sb2.toString()));
        }
        if (node.getElseNode() != null) {
            sb.append("\n").append(indent(
                    "default: " + nodeToString(node.getElseNode(), cuctx, path)
            ));
        }
        sb.append("\n}");
        if (ee instanceof HNDeclareIdentifier) {
            return new StringPrec("{\n"
                    + indent(sb.toString())
                    + "\n}"
            );
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec JNodeHOpClass_ToString(HNDotClass node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(node.getTypeRefName().getTypenameOrVar().name() + ".class");
    }

    private StringPrec JNodeHOpThis_ToString(HNDotThis node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(node.getTypeRefName().getTypenameOrVar().name() + ".this");
    }

    private StringPrec JNodeHBreak_ToString(HNBreak node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (node.getLabel() == null) {
            return new StringPrec("break");
        }
        return new StringPrec("break " + node.getLabel());
    }

    private StringPrec JNodeHContinueJNodeHBreak_ToString(HNContinue node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (node.getLabel() == null) {
            return new StringPrec("break");
        }
        return new StringPrec("break " + node.getLabel());
    }

    private StringPrec JNodeHForNode_ToString(HNFor node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        boolean iter = node.isIteratorType();
        boolean tuple = false;
        for (HNode initExpr : node.getInitExprs()) {
            if (initExpr instanceof HNDeclareIdentifier) {
                tuple = ((HNDeclareIdentifier) initExpr).getIdentifierToken() instanceof HNDeclareTokenTuple;
                break;
            }
        }
        if ((node.getInitExprs().size() == 1 || !node.isIteratorType()) && !tuple) {
            StringBuilder sb = new StringBuilder();
            if (node.getLabel() != null) {
                sb.append(node.getLabel() + ": ");
            }
            sb.append("for(");
            List<HNode> initExprs = node.getInitExprs();
            for (int i = 0; i < initExprs.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                HNDeclareIdentifier initExpr = (HNDeclareIdentifier) initExprs.get(i);
                sb.append(cuctx.nameWithImports(initExpr.getIdentifierType()));
                sb.append(" ");
                String identifierName = ((HNDeclareTokenIdentifier) initExpr.getIdentifierToken()).getName();
                sb.append(identifierName);
                sb.append(initExpr.getAssignOperator());
                HNode initValue = (HNode) initExpr.getInitValue();
                StringPrec obj = nodeToString(initValue, cuctx, path.append(node));
                sb.append(obj);
                if (cuctx.types().forName(IntRange.class.getName()).isAssignableFrom(initValue.getElement().getType())) {
                    //i can do better here to transform the foreach to a simple for
                    //OPTIMIZE ME LATER
                    sb.append(".toIntArray()");
                }
            }
            if (!iter) {
                sb.append(";");
                if (node.getFilter() != null) {
                    sb.append(nodeToString(node.getFilter(), cuctx, path.append(node)));
                }
                sb.append(";");
                List<HNode> incs = node.getIncs();
                for (int i = 0; i < incs.size(); i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    HNode inc = incs.get(i);
                    sb.append(nodeToString(inc, cuctx, path.append(node)));
                }
            }
            sb.append(")");
            if (iter && (node.getFilter() != null || node.getIncs().size() > 0)) {
                sb.append("{");
                if (node.getFilter() != null) {
                    sb.append("\n").append(indent("if(" + nodeToString(node.getFilter(), cuctx, path.append(node)).toString() + "){\n"));
                    sb.append("\n").append(indent(indent(nodeToString(node.getBody(), cuctx, path.append(node)).toString())));
                    sb.append("\n").append(indent("}"));
                } else {
                    sb.append("\n").append(indent(nodeToString(node.getBody(), cuctx, path.append(node)).toString()));
                }
                for (HNode inc : node.getIncs()) {
                    sb.append("\n").append(indent(nodeToString(inc, cuctx, path.append(node)).toString()));
                }
                sb.append("\n}");
            } else {
                HNode b = node.getBody();
                if (b == null) {
                    sb.append("{}");
                } else if (b instanceof HNBlock) {
                    sb.append(nodeToString(b, cuctx, path.append(node)));
                } else {
                    sb.append("{\n");
                    sb.append(indent(nodeToString(b, cuctx, path.append(node)).toString()));
                    sb.append("\n}");
                }
            }
            return new StringPrec(sb.toString());
        }
        if (iter) {
            //
            //  for((i,j):fct(),h:1..6,i%2==0){
            //      println(i,j);
            //  }
            //  ==>
            //  Tuple<int[],int[]> r=fct();
            //  int[] ha=[1..6];
            //  for(i:r._1){
            //      println(i,j);
            //  }
            //
            //

            StringBuilder sb = new StringBuilder();
            sb.append("//complex form of for statement");
            List<ForEachDec> list = new ArrayList<>();
            for (HNode initExpr : node.getInitExprs()) {
                HNDeclareIdentifier t = (HNDeclareIdentifier) initExpr;
                HNDeclareToken identifierToken = t.getIdentifierToken();
                HNode initValue = (HNode) t.getInitValue();
                if (identifierToken instanceof HNDeclareTokenTuple) {
                    HNDeclareTokenTuple tupleToken = (HNDeclareTokenTuple) identifierToken;
                    String v = nextVar();
                    JType tupleType = initValue.getElement().getType();
                    sb.append("\n").append(cuctx.nameWithImports(tupleType))
                            .append(" ").append(v)
                            .append("=")
                            .append(nodeToString(initValue, cuctx, path));
                    HNDeclareTokenTupleItem[] identifiers = tupleToken.getItems();
                    for (int i = 0; i < identifiers.length; i++) {
                        HNDeclareTokenIdentifier identifier = (HNDeclareTokenIdentifier) identifiers[i];
                        String identifierName = identifier.getName();

                        JType iterableType = ((JParameterizedType) tupleType).actualTypeArguments()[i];
                        JType elementType = identifier.getIdentifierType();
                        list.add(new ForEachDec(
                                identifierName,
                                cuctx.nameWithImports(elementType),
                                v + ".valueAt(" + i + ")"
                        ));
                    }
                } else if (identifierToken instanceof HNDeclareTokenIdentifier) {
                    String identifierName = ((HNDeclareTokenIdentifier) identifierToken).getName();
                    list.add(new ForEachDec(
                            identifierName,
                            cuctx.nameWithImports(t.getIdentifierType()),
                            nodeToString(initValue, cuctx, path.append(node)).toString()
                    ));
                } else {
                    throw new JShouldNeverHappenException();
                }
            }
            String s = "";
            for (int i = list.size() - 1; i >= 0; i--) {
                ForEachDec forEachDec = list.get(i);
                if (i == list.size() - 1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("for(" + forEachDec.type + " " + forEachDec.name + ":" + forEachDec.value + ")");
                    if (node.getFilter() != null || node.getIncs().size() > 0) {
                        sb2.append("{");
                        if (node.getFilter() != null) {
                            sb2.append("\n").append(indent("if(" + nodeToString(node.getFilter(), cuctx, path.append(node)).toString() + "){\n"));
                            sb2.append("\n").append(indent(indent(nodeToString(node.getBody(), cuctx, path.append(node)).toString())));
                            sb2.append("\n").append(indent("}"));
                        } else {
                            sb2.append("\n").append(indent(nodeToString(node.getBody(), cuctx, path.append(node)).toString()));
                        }
                        for (HNode inc : node.getIncs()) {
                            sb2.append("\n").append(indent(nodeToString(inc, cuctx, path.append(node)).toString()));
                        }
                        sb2.append("\n}");
                    } else {
                        sb2.append(nodeToString(node.getBody(), cuctx, path.append(node)).toString());
                    }
                    s = sb2.toString();
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("for(" + forEachDec.type + " " + forEachDec.name + ":" + forEachDec.value + ")");
                    sb2.append("{\n");
                    sb2.append(indent(s));
                    sb2.append("\n}");
                    s = sb2.toString();
                }
            }
            sb.append(s);
            return new StringPrec(s);
        } else {
            //
            //  for((i,j)=fct(),h=1;i%2==0;i++){
            //      println(i,j);
            //  }
            //  ==>
            //  Tuple<int[],int[]> r=fct();
            //  int[] ha=[1..6];
            //  for(i:r._1){
            //      println(i,j);
            //  }
            //
            //

            StringBuilder sb = new StringBuilder();
            sb.append("//complex form of for statement");
            List<ForEachDec> list = new ArrayList<>();
            for (HNode initExpr : node.getInitExprs()) {
                HNDeclareIdentifier t = (HNDeclareIdentifier) initExpr;
                HNode initValue = (HNode) t.getInitValue();
                if (t.getIdentifierToken() instanceof HNDeclareTokenTuple) {
                    HNDeclareTokenTuple tuToken = (HNDeclareTokenTuple) t.getIdentifierToken();
                    String v = nextVar();
                    JType tupleType = initValue.getElement().getType();
                    sb.append("\n").append(cuctx.nameWithImports(tupleType))
                            .append(" ").append(v)
                            .append("=")
                            .append(nodeToString(initValue, cuctx, path));
                    HNDeclareTokenIdentifier[] identifiers = HNodeUtils.flatten(tuToken);
                    for (int i = 0; i < identifiers.length; i++) {
                        HNDeclareTokenIdentifier identifier = identifiers[i];
                        JType elementType = identifier.getIdentifierType();
                        String identifierName = identifier.getName();
                        list.add(new ForEachDec(
                                identifierName,
                                cuctx.nameWithImports(elementType),
                                v + ".valueAt(" + i + ")"
                        ));
                    }
                } else {
                    HNDeclareTokenIdentifier tuToken = (HNDeclareTokenIdentifier) t.getIdentifierToken();
                    String identifierName = tuToken.getName();
                    list.add(new ForEachDec(
                            identifierName,
                            cuctx.nameWithImports(t.getIdentifierType()),
                            nodeToString(initValue, cuctx, path.append(node)).toString()
                    ));
                }
            }
            sb.append("for(");
            for (int i = 0; i < list.size(); i++) {
                ForEachDec forEachDec = list.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(forEachDec.type + " " + forEachDec.name + "=" + forEachDec.value);
            }
            sb.append(";");
            if (node.getFilter() != null) {
                sb.append(nodeToString(node.getFilter(), cuctx, path.append(node)));
            }
            sb.append(";");
            List<HNode> incs = node.getIncs();
            for (int i = 0; i < incs.size(); i++) {
                HNode inc = incs.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(nodeToString(inc, cuctx, path.append(node)).toString());
            }
            sb.append(")");
            HNode b = node.getBody();
            if (b == null) {
                sb.append("{}");
            } else if (b instanceof HNBlock) {
                sb.append(nodeToString(b, cuctx, path.append(node)));
            } else {
                sb.append("{\n");
                sb.append(indent(nodeToString(b, cuctx, path.append(node)).toString()));
                sb.append("\n}");
            }
            return new StringPrec(sb.toString());
        }
    }

    private StringPrec JNodeHWhileNode_ToString(HNWhile node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        if (node.getLabel() != null) {
            sb.append(node.getLabel() + ": ");
        }
        sb.append("while");
        sb.append("(");
        sb.append(nodeToString(node.getExpr(), cuctx, path.append(node)));
        sb.append(")");
        sb.append(nodeToString(node.getBlock(), cuctx, path));
        return new StringPrec(sb.toString());
    }

    private StringPrec JNodeHIf_ToString(HNIf node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.getBranches().size(); i++) {
            if (i == 0) {
                sb.append("if");
            } else {
                sb.append(" else if");
            }
            sb.append("(");
            sb.append(nodeToString(node.getBranches().get(i).getWhenNode(), cuctx, path.append(node)));
            sb.append(")");
            sb.append(nodeToString(node.getBranches().get(i).getDoNode(), cuctx, path.append(node)));
        }
        if (node.getElseNode() != null) {
            sb.append(" else ");
            sb.append(nodeToString(node.getElseNode(), cuctx, path.append(node)));
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec JNodeHArrayGet_ToString(HNArrayCall node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        sb.append(nodeToString(node.getArrayInstanceNode(), cuctx, path.append(node)));
        for (HNode index : node.getIndexNodes()) {
            sb.append("[");
            sb.append(nodeToString(index, cuctx, path.append(node)));
            sb.append("]");
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec JNodeRaw_ToString(JNodeRaw node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(node.javaCode);
    }

    private StringPrec onTuple(HNTuple node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        sb.append("new " + cuctx.nameWithImports(node.getElement().getType()))
                /*.append("<>")*/.append("(");
        for (int i = 0; i < node.getItems().length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(nodeToString(node.getItems()[i], cuctx, path));
        }
        sb.append(")");
        return new StringPrec(sb.toString());
    }

    private StringPrec onLambdaExpression(HNLambdaExpression node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder("(");
        List<HNDeclareIdentifier> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            HNDeclareIdentifier a = arguments.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(cuctx.nameWithImports(a.getIdentifierType()));
            sb.append(" ");
            String identifierName = HNodeUtils.flattenNames(a.getIdentifierToken())[0];
            sb.append(identifierName);
        }
        sb.append(")");
        sb.append("->");
        if (node.isImmediateBody()) {
            sb.append(nodeToString(node.getBody(), cuctx, path.append(node)));
        } else {
            if (node.getReturnTypeName().name().equals("void")) {
                sb.append(nodeToString(node.getBody(), cuctx, path.append(node)));
            } else {
                HNBlock body = (HNBlock) node.getBody();
                sb.append(blockWithReturnString(
                        body.getStatements(), cuctx, path.append(node)
                ));
            }
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec onArrayNew(HNArrayNew node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNode setter = node.getConstructor();
        if (setter == null) {
            StringBuilder sb = new StringBuilder("new ");
            sb.append(cuctx.nameWithImports(node.getArrayType().rootComponentType()));
            for (int i = 0; i < node.getInits().length; i++) {
                sb.append("[");
                sb.append(nodeToString(node.getInits()[i], cuctx, path));
                sb.append("]");
            }
            return new StringPrec(sb.toString());
        } else {
            if (setter instanceof HNDeclareInvokable) {
                String t = cuctx.nameWithImports(cuctx.types().forName(HHelpers.class.getName()));
                StringBuilder sb = new StringBuilder(t).append(".").append("newArray") //                        .append(node.getInits().length)
                        ;
                sb.append("(");
                for (int i = 0; i < node.getInits().length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(nodeToString(node.getInits()[i], cuctx, path));
                }
                sb.append(",");
                setter.setUserObject("lambdaExpression");
                sb.append(nodeToString(setter, cuctx, path));
                sb.append(")");
                return new StringPrec(sb.toString());
            } else {
                String t = cuctx.nameWithImports(cuctx.types().forName(HHelpers.class.getName()));
                StringBuilder sb = new StringBuilder(t).append(".").append("newArray") //                        .append(node.getInits().length)
                        ;
                sb.append("(");
                for (int i = 0; i < node.getInits().length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(nodeToString(node.getInits()[i], cuctx, path));
                }
                sb.append(",");
                sb.append(nodeToString(setter, cuctx, path));
                sb.append(")");
                return new StringPrec(sb.toString());
            }
        }
    }

    private StringPrec JNodeHApplyWhenExistsOperator_ToString(HNOpCoalesce node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        String t = cuctx.nameWithImports(cuctx.types().forName(HHelpers.class.getName()));
        return new StringPrec(t + ".nonNull("
                + nodeToString(node.getLeft(), cuctx, path.append(node)) + ","
                + "()->" + nodeToString(node.getRight(), cuctx, path) + ")");
    }

    private StringPrec onObjectNew(HNObjectNew node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (HNodeUtils.isElementInvokable(node)) {
            return onElementInvokable(node, cuctx, path);
        }
        throw new JFixMeLaterException();
//        return JInvokable_ToString(node, element, cuctx, path);
    }

    private StringPrec onElementInvokable(HNode node, HLGenCompilationUnitContext cuctx, JNodePath path) {
//        if (isElementInvokable(node)) {
        return onElementInvokable(node, ((HNode) node).getElement(), cuctx, path);
//        }
//        throw new JFixMeLaterException();
    }

    private StringPrec onElementInvokable(HNode node, HNElement element, HLGenCompilationUnitContext cuctx, JNodePath path) {
        switch (element.getKind()) {
            case CONSTRUCTOR: {
                HNElementConstructor c = (HNElementConstructor) element;
                return JInvokable_ToString(node, c.getInvokable(),
                        HUtils.getEvaluables(c.getArgNodes()),
                        cuctx, path);
            }
            case METHOD: {
                HNElementMethod c = (HNElementMethod) element;
                return JInvokable_ToString(node, c.getInvokable(),
                        HUtils.getEvaluables(c.getArgNodes()),
                        cuctx, path);
            }
        }
        throw new JFixMeLaterException();
//        return JInvokable_ToString(node, element, cuctx, path);
    }

    private StringPrec onOpUnaryCall(HNOpUnaryCall node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNElement element = node.getElement();
        if (element != null) {
            switch (element.getKind()) {
                case METHOD: {
                    throw new JFixMeLaterException();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (node.isPrefixOperator()) {
            sb.append(node.getName());
        }
        sb.append("(");
        sb.append(nodeToString(node.getExpr(), cuctx, path.append(node)));
        sb.append(")");
        if (!node.isPrefixOperator()) {
            sb.append(node.getName());
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec JNodeHDefaultLiteral_ToString(HNLiteralDefault node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        JType t = node.getElement().getType();
        if (t.isPrimitive()) {
            if (t.name().equals("boolean")) {
                return new StringPrec("false");
            }
            if (t.name().equals("char")) {
                return new StringPrec("'\0'");
            }
            if (t.name().equals("byte")) {
                return new StringPrec("(byte)0");
            }
            if (t.name().equals("short")) {
                return new StringPrec("(short)0");
            }
            if (t.name().equals("int")) {
                return new StringPrec("0");
            }
            if (t.name().equals("long")) {
                return new StringPrec("0L");
            }
            if (t.name().equals("float")) {
                return new StringPrec("0.0f");
            }
            if (t.name().equals("double")) {
                return new StringPrec("0.0");
            }
            throw new JParseException("Unsupported type " + t);
        } else {
            return new StringPrec("null");
        }
    }

//    private StringPrec JNodeHField_ToString(HNField node, HLGenCompilationUnitContext cuctx, JNodePath path) {
//        HNode i = node.getInstanceNode();
//        JField f = node.getField();
//        if (f.isStatic()) {
//            return new StringPrec(cuctx.nameWithImports(f.declaringType()) + "." + f.name());
//        }
//        if (node.isNullableInstance()) {
//            String t = cuctx.nameWithImports(cuctx.types().forName(HHelpers.class.getName()));
////            int ii = nextNumber();
////            return t+".applyOrDefault("
////                    +nodeToString(i,cuctx,path.append(node))+","
////                    +"("+
////                    cuctx.nameWithImports(i.getType())
////                    +" x"+ii+")->x"+ii+"."+f.name()+")";
//            String varName = "_$";
//
//            return new StringPrec(t + ".applyOrDefault("
//                    + nodeToString(i, cuctx, path.append(node)) + ","
//                    + "(" + varName + ")->" + varName + "." + f.name() + ")");
//
//        } else {
//            return new StringPrec((i == null ? "this" : nodeToString(i, cuctx, path.append(node))) + "." + f.name());
//        }
//    }
    private StringPrec onAssign(HNAssign node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNElementAssign e = (HNElementAssign) node.getElement();
        StringBuilder sb = new StringBuilder();
        if (e.getDeclareTempVarName() != null) {
            sb.append(cuctx.nameWithImports(((HNode) node.getRight()).getElement().getType()));
            sb.append(" ");
            sb.append(e.getDeclareTempVarName());
            sb.append(" = ");
            sb.append(nodeToString(node.getRight(), cuctx, path.append(node)));
            sb.append(";\n");
            sb.append(nodeToString(node.getLeft(), cuctx, path.append(node)));
            sb.append(" = ");
            sb.append(e.getDeclareTempVarName());
            return new StringPrec(sb.toString());
        }
        if (HNodeUtils.isElementInvokable(node.getLeft())) {
            return nodeToString(node.getLeft(), cuctx, path.append(node));
        }
        return new StringPrec(nodeToString(node.getLeft(), cuctx, path.append(node)) + " = " + nodeToString(node.getRight(), cuctx, path.append(node)));
//        if (node.getLeft() instanceof HNIdentifier) {
//            HNIdentifier vg = (HNIdentifier) node.getLeft();
//            return new StringPrec(vg.getName() + "=" + nodeToString(node.getRight(), cuctx, path.append(node)));
//        }else if (node.getLeft() instanceof HNBracketsPostfix) {
//        } else if (node.getLeft() instanceof HNTuple) {
//            StringBuilder sb = new StringBuilder();
//            int n = nextNumber();
//            sb.append(cuctx.nameWithImports(((HNTuple) node.getLeft()).getElement().getType()));
//            HNode[] items = ((HNTuple) node.getLeft()).getItems();
////            for (int i = 0; i < items.length; i++) {
////                if(i==0){
////                    sb.append("<");
////                }else{
////                    sb.append(",");
////                }
////                sb.append(cuctx.nameWithImports(items[i].getType().boxed()));
////                if(i==items.length-1){
////                    sb.append(">");
////                }
////            }
//            sb.append(" $" + n);
//            sb.append("=");
//            sb.append(nodeToString(node.getRight(), cuctx, path.append(node)));
//            sb.append(";");
//            HNode[] a = node.getTupleSubAssignments();
//            for (int i = 0; i < items.length; i++) {
//                sb.append("\n");
//                HNode item1 = (HNode) items[i];
//                HNode item = item1;
//                if (a == null || a[i] == null || a[i] instanceof HNAssign) {
//                    sb.append(Assign_ToString(
//                            new HNAssign(
//                                    item.copy(), JTokenUtils.createOpToken("="),
//                                    new JNodeRaw("(" + cuctx.nameWithImports(item1.getElement().getType()) + ")$" + n + ".valueAt(" + (i) + ")"),
//                                    item.startToken(),
//                                    item.endToken()
//                            ), cuctx, path
//                    ));
//                } else if (a[i] instanceof HNInvokerCall) {
//                    HNInvokerCall hc = (HNInvokerCall) a[i].copy();
//                    HNode[] args = hc.getArgs();
//                    args[args.length - 1] = new JNodeRaw("(" + cuctx.nameWithImports(item1.getElement().getType()) + ")$" + n + ".valueAt(" + (i) + ")");
//                    sb.append(nodeToString(
//                            hc, cuctx, path
//                    ));
//                } else {
//                    throw new JParseException("Unexpected");
//                }
//                sb.append(";");
//            }
//            return new StringPrec(sb.toString());
//        } else {
//            throw new JParseException("Unexpected");
//        }
    }

    private StringPrec JNodeHThis_ToString(HNThis node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec("this");
    }

    private StringPrec JNodeHSuper_ToString(HNSuper node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec("super");
    }

    private StringPrec JNodeHVar_ToString(HNVar node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return new StringPrec(node.getName());
    }

    private StringPrec JInvokablePrefilled_ToString(HNode node, JInvokablePrefilled impl, HLGenCompilationUnitContext cuctx, JNodePath path) {
        JInvokable ii = impl.getInvokable();
        return JInvokable_ToString(node, ii, impl.getEvaluables(), cuctx, path.append(node));
    }

    private StringPrec JNodeFunctionCall_ToString(HNInvokerCall node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        JInvokablePrefilled impl = node.impl();
        JInvokable ii = impl.getInvokable();
        return JInvokable_ToString(node, ii, impl.getEvaluables(), cuctx, path.append(node));
    }

    private StringPrec JNodeHMethodCall_ToString(HNMethodCall node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        JInvokablePrefilled impl = node.impl();
        JInvokable ii = impl.getInvokable();
        return JInvokable_ToString(node.getInstanceNode() == null ? node : node.getInstanceNode(), ii, impl.getEvaluables(), cuctx, path.append(node));
    }

    private StringPrec Convert_ToString(JConverter converter, StringPrec anyResultString, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (converter instanceof CastJConverter) {
            CastJConverter c = (CastJConverter) converter;
            if (c.targetType().getType().equals(c.originalType().getType().boxed())) {
                return anyResultString;
            }
            return new StringPrec("((" + cuctx.nameWithImports(c.targetType().getType()) + ")" + anyResultString + ")");
        }
        if (converter instanceof JTypeUtils.NumberToByteJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").byteValue()");
        }
        if (converter instanceof JTypeUtils.NumberToIntJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").intValue()");
        }
        if (converter instanceof JTypeUtils.NumberToShortJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").shortValue()");
        }
        if (converter instanceof JTypeUtils.NumberToLongJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").longValue()");
        }
        if (converter instanceof JTypeUtils.NumberToFloatJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").floatValue()");
        }
        if (converter instanceof JTypeUtils.NumberToDoubleJConverter) {
            return new StringPrec("((Number)" + anyResultString + ").doubleValue()");
        }
        throw new IllegalArgumentException("Unsupported");
    }

    private StringPrec JLiteralValue_ToString(Object v, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (v == null) {
            return new StringPrec("null");
        }
        if (v instanceof String) {
            return new StringPrec("\"" + JToken.escapeString((String) v) + "\"");
        }
        if (v instanceof Character) {
            return new StringPrec("\'" + JToken.escapeString("" + (Character) v) + "\'");
        }
        if (v instanceof Number) {
            return new StringPrec(String.valueOf(v));
        }
        if (v instanceof Boolean) {
            return new StringPrec(String.valueOf(v));
        }
        throw new IllegalArgumentException("Unsupported Literal of type " + v.getClass().getName());
//        return String.valueOf(v);
    }

    private StringPrec JEvaluatable_ToString(JEvaluable e, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNode node = (HNode) path.parent(0);
        if (e instanceof JEvaluableNode) {
            return nodeToString((HNode) ((JEvaluableNode) e).getNode(), cuctx, path.append(node));
        }
        if (e instanceof JEvaluableConverter) {
            JEvaluableConverter c = (JEvaluableConverter) e;
            JConverter conv = c.getArgConverter();
            JEvaluable val = c.getValue();
            return Convert_ToString(conv, JEvaluatable_ToString(val, cuctx, path.append(node)), cuctx, path.append(node));
        }
        if (e instanceof JEvaluableValue) {
            JEvaluableValue c = (JEvaluableValue) e;
            Object o = ((JEvaluableValue) e).getObject();
            return JLiteralValue_ToString(o, cuctx, path.append(node));
        }
        throw new IllegalArgumentException("Unsupported");
    }

    private StringPrec binop(String op, StringPrec a, StringPrec b) {
        int prec = getJavaBinaryOperatorPrecedence(op);
        StringBuilder sb = new StringBuilder();
        if (prec < 0 || a.prec < prec) {
            sb.append("(");
            sb.append(a);
            sb.append(")");
        } else {
            sb.append(a);
        }
        if (op.length() > 1) {
            sb.append(" ");
        }
        sb.append(op);
        if (op.length() > 1) {
            sb.append(" ");
        }
        if (prec < 0 || b.prec < prec
                || b.str.startsWith("-")
                || b.str.startsWith("+")) {
            sb.append("(");
            sb.append(b);
            sb.append(")");
        } else {
            sb.append(b);
        }
        return new StringPrec(sb.toString(), prec);
    }

    private StringPrec unop(String op, int prec, StringPrec a) {
        StringBuilder sb = new StringBuilder();
        sb.append(op);
        if (a.prec < prec || a.str.startsWith("-")
                || a.str.startsWith("+")
                || a.str.startsWith("!")
                || a.str.startsWith("~")) {
            sb.append("(");
            sb.append(a);
            sb.append(")");
        } else {
            sb.append(a);
        }
        return new StringPrec(sb.toString(), prec);
    }

    private StringPrec JInvokable_ToString(HNode node, JInvokable invokable, JEvaluable[] args, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (invokable instanceof JFunctionConverted) {
            JFunctionConverted c = (JFunctionConverted) invokable;
            JConverter[] argConverters = c.getArgConverters();
            JEvaluable[] args2 = new JEvaluable[args.length];
            for (int i = 0; i < args.length; i++) {
                if (argConverters != null && argConverters[i] != null) {
                    args2[i] = new JEvaluableConverter(argConverters[i], args[i]);
                } else {
                    args2[i] = args[i];
                }
            }
            StringPrec s = JInvokable_ToString(node, c.getOther(), args2, cuctx, path.append(node));
            if (c.getResultConverter() != null) {
                s = Convert_ToString(c.getResultConverter(), s, cuctx, path.append(node));
            }
            return s;
        } else if (invokable instanceof JMethodInvocationFunction) {
            return JInvokable_ToString(node, ((JMethodInvocationFunction) invokable).getMethod(), args, cuctx, path);
        } else if (invokable instanceof JMethod) {
            JMethod m = (JMethod) invokable;
            StringBuilder sb = new StringBuilder();
            JType t = m.declaringType();
            if (m.isStatic() && t.name().equals(HJavaDefaultOperators.class.getName())) {
                switch (m.name()) {
                    case "neg": {
                        if (args.length == 1) {
                            return unop("-", 14, JEvaluatable_ToString(args[0], cuctx, path.append(node)));
                        }
                        break;
                    }
                    case "plus": {
                        if (args.length == 2) {
                            return binop("+",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "minus": {
                        if (args.length == 2) {
                            return binop("-",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "mul": {
                        if (args.length == 2) {
                            node.userObjects().put("operator", 12);
                            return binop("*",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "div": {
                        if (args.length == 2) {
                            node.userObjects().put("operator", 12);
                            return binop("/",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "rem": {
                        if (args.length == 2) {
                            return binop("%",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "and": {
                        if (args.length == 2) {
                            return binop("&&",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                    case "or": {
                        if (args.length == 2) {
                            return binop("||",
                                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
                            );
                        }
                        break;
                    }
                }
                throw new JParseException("Unsupported");

            } else {
                if (m.isStatic()) {
                    boolean doPrint = true;
                    if (m.declaringType().name().equals("net.vpc.hadralang.stdlib.HDefaults")) {
                        if (staticStdDefaults) {
                            cuctx.nameWithImports(t, true);
                            doPrint = false;
                        }
                    } else if (m.declaringType().name().startsWith("net.vpc.hadralang.stdlib.ext.")) {
                        if (staticStdImportExtensions) {
                            cuctx.nameWithImports(t, true);
                            doPrint = false;
                        }
                    }
                    if (doPrint) {
                        sb.append(cuctx.nameWithImports(t));
                        sb.append(".");
                    }
                } else {
                    sb.append(nodeToString(node, cuctx, path.append(node)));
                    sb.append(".");
                }
                sb.append(m.name());
                sb.append("(");
                for (int i = 0; i < args.length; i++) {
                    JEvaluable jEvaluable = args[i];
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(JEvaluatable_ToString(jEvaluable, cuctx, path.append(node)));
                }
                sb.append(")");
                return new StringPrec(sb.toString());
            }
        } else if (invokable instanceof JConstructor) {
            JConstructor m = (JConstructor) invokable;
            StringBuilder sb = new StringBuilder();
            JType t = m.declaringType();
            sb.append("new ");
            sb.append(cuctx.nameWithImports(t));
            sb.append("(");
            for (int i = 0; i < args.length; i++) {
                JEvaluable jEvaluable = args[i];
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(JEvaluatable_ToString(jEvaluable, cuctx, path.append(node)));
            }
            sb.append(")");
            return new StringPrec(sb.toString());
        } else if (invokable instanceof PrimitiveEqualsInvokable
                || (invokable instanceof StrictEqualsInvokable)) {
            return binop("==",
                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
            );
        } else if (invokable instanceof PrimitiveNotEqualsInvokable
                || (invokable instanceof StrictNotEqualsInvokable)) {
            return binop("!=",
                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
            );
        } else if ((invokable instanceof SafeEqualsInvokable)) {
            String obs = cuctx.nameWithImports(cuctx.types().forName(Objects.class.getName()));
            return new StringPrec(obs + ".deepEquals(" + JEvaluatable_ToString(args[0], cuctx, path.append(node)) + ","
                    + "" + JEvaluatable_ToString(args[1], cuctx, path.append(node)) + ")");
        } else if (invokable instanceof SafeNotEqualsInvokable) {
            String obs = cuctx.nameWithImports(cuctx.types().forName(Objects.class.getName()));
            return new StringPrec("!" + obs + ".deepEquals(" + JEvaluatable_ToString(args[0], cuctx, path.append(node)) + ","
                    + "" + JEvaluatable_ToString(args[1], cuctx, path.append(node)) + ")", 14);
        } else if (invokable instanceof PrimitiveCompareInvokable) {
            return binop(((PrimitiveCompareInvokable) invokable).getOp(),
                    JEvaluatable_ToString(args[0], cuctx, path.append(node)),
                    JEvaluatable_ToString(args[1], cuctx, path.append(node))
            );
        } else if (invokable instanceof CompareToGreaterEqualsThanInvokable) {
            return new StringPrec(JEvaluatable_ToString(args[0], cuctx, path) + ">=" + JEvaluatable_ToString(args[1], cuctx, path), 9);
        } else if (invokable instanceof CompareToGreaterThanInvokable) {
            return new StringPrec(JEvaluatable_ToString(args[0], cuctx, path) + ">" + JEvaluatable_ToString(args[1], cuctx, path), 9);
        } else if (invokable instanceof CompareToLessEqualsThanInvokable) {
            return new StringPrec(JEvaluatable_ToString(args[0], cuctx, path) + "<=" + JEvaluatable_ToString(args[1], cuctx, path), 9);
        } else if (invokable instanceof CompareToLessThanInvokable) {
            return new StringPrec(JEvaluatable_ToString(args[0], cuctx, path) + "<" + JEvaluatable_ToString(args[1], cuctx, path), 9);
        } else {
            throw new IllegalArgumentException("Unsupported Invokable " + invokable.getClass().getSimpleName());
        }
    }

    private StringPrec JNodeLiteral_ToString(HNLiteral node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        Object v = node.getValue();
        HNode ae = node.getAssociatedExpression();
        if (ae == null) {
            return JLiteralValue_ToString(v, cuctx, path.append(node));
        } else {
            return nodeToString(ae, cuctx, path);
        }
    }

    private StringPrec onDeclareInvokable(HNDeclareInvokable node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        int m = node.getModifiers();
        sb.append(beforeMethod());
        sb.append(JGHelper.modifiersToString(m));
        sb.append(" ");
        if (node.getName() == null && Modifier.isStatic(m) && node.getArguments().isEmpty()) {
            //this is a static bloc!
            HNBlock jb = null;
            if (node.isImmediateBody()) {
                if (node.getBody() != null && !(node.getBody() instanceof HNBlock)) {
                    jb = new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[]{node.getBody()}, node.getBody().startToken(), node.getBody().endToken());
                } else if (node.getBody() == null) {
                    JToken aFalse = JTokenUtils.createKeywordToken("false");
                    jb = new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[]{new HNLiteral(false, aFalse)}, aFalse, aFalse);
                }
            } else {
                jb = (HNBlock) node.getBody();
            }
            sb.append(onBlock(jb, cuctx, false, node.isSetUserObject("javagen.injectRunModule"), path.append(node)));
        } else {
            //if constructor
            //  sb.append(node.getConstructor().declaringType().simpleName());
            sb.append(cuctx.nameWithImports(node.getReturnType()));
            sb.append(" ");
            sb.append(node.getName());
            sb.append("(");
            List<HNDeclareIdentifier> arguments = node.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                HNDeclareIdentifier argument = arguments.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                if (argument.getIdentifierTypeName().getTypename().isVarArg()) {
                    JTypeArray identifierType = (JTypeArray) argument.getIdentifierType();
                    sb.append(cuctx.nameWithImports(identifierType.componentType()));
                    sb.append("...");
                } else {
                    sb.append(cuctx.nameWithImports(argument.getIdentifierType()));
                }
                sb.append(" ");
                String identifierName = HNodeUtils.flattenNames(argument.getIdentifierToken())[0];
                sb.append(identifierName);
            }
            sb.append(")");
            if (Modifier.isAbstract(m)) {
                sb.append(";\n");
            } else {
                HNBlock jb = null;
                if (node.isImmediateBody()) {
                    if (node.getBody() != null && !(node.getBody() instanceof HNBlock)) {
                        jb = new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[]{node.getBody()}, node.getBody().startToken(), node.getBody().endToken());
                    } else if (node.getBody() == null) {
                        JToken aFalse = JTokenUtils.createKeywordToken("false");
                        jb = new HNBlock(HNBlock.BlocType.METHOD_BODY, new HNode[]{new HNLiteral(false, aFalse)}, aFalse, aFalse);
                    }
                } else {
                    jb = (HNBlock) node.getBody();
                }
                boolean withReturn
                        = !node.isConstructor()
                        && node.getInvokableType() != HLInvokableType.CONSTRUCTOR
                        && node.getInvokableType() != HLInvokableType.MAIN_CONSTRUCTOR
                        && !node.getReturnType().name().equals("void");
                sb.append(onBlock(jb, cuctx, withReturn, node.isSetUserObject("javagen.injectRunModule"), path.append(node)));
            }
        }
        return new StringPrec(sb.toString());
    }

    private StringPrec onDeclareIdentifier(HNDeclareIdentifier node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        int m = node.getModifiers();
        if (node.isSetUserObject("javagen.static")) {
            m |= Modifier.STATIC;
        }
        StringBuilder sb = new StringBuilder();
        if (m != 0) {
            sb.append(JGHelper.modifiersToString(m));
            sb.append(" ");
        }
        sb.append(cuctx.nameWithImports(node.getIdentifierType()));
        sb.append(" ");
        String[] identifierNames = HNodeUtils.flattenNames(node.getIdentifierToken());
        for (int i = 0; i < identifierNames.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String identifierName = identifierNames[i];
            sb.append(identifierName);
        }
        HNode dv = node.getInitValue();
        if (dv != null /*&& !cuctx.isModuleClass()*/) {
            sb.append("=");
            sb.append(nodeToString(dv, cuctx, path.append(node)));
        }
        sb.append(";");
        return new StringPrec(sb.toString());
    }

    public StringPrec onBlock(HNBlock node, HLGenCompilationUnitContext cuctx, boolean withReturn, boolean injectRunModule, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        if(node.getBlocType()== HNBlock.BlocType.STATIC_INITIALIZER){
            sb.append("static ");
        }
        sb.append("{");
        if (injectRunModule) {
            sb.append("\n");
            sb.append(indent("runModule();"));
        }
        for (HNode statement : node.getStatements()) {
            sb.append("\n");
            sb.append(indent(nodeToString(statement, cuctx, path.append(node)).toString()));
        }
//        if (withReturn) {
//            sb.append("\n");
//            sb.append(indent(blockWithReturnString(node.getRunnableBlock(), cuctx, path.append(node)).toString()));
//        } else {
//            sb.append("\n");
//            String s1 = nodeArrayToString(node.getRunnableBlock(), ";\n", cuctx, path.append(node)).toString();
//            String s2 = indent(s1);
//            sb.append(s2);
//        }
        sb.append("\n}");
        return new StringPrec(sb.toString());
    }

    public StringPrec onDeclareType(HNDeclareType node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        sb.append(beforeClass());
        if (node.getModifiers() != 0) {
            sb.append(HUtils.modifiersToString(node.getModifiers())).append(" ");
        }
        sb.append("class ");
        sb.append(node.getName());
        sb.append("{");

        HNode b = node.getBody();
        if (b == null) {

        } else if (b instanceof HNBlock) {
            for (HNode object : ((HNBlock) b).getStatements()) {
                sb.append("\n").append(indent(nodeToString(object, cuctx, path.append(node))));
            }
        } else {
            sb.append("\n").append(indent(nodeToString(b, cuctx, path.append(node))));
        }
        sb.append("\n}");

        return new StringPrec(sb.toString());
    }

    private StringPrec onOpDot(HNOpDot node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (!node.isNullableInstance()
                && !node.isUncheckedMember()) {
            return new StringPrec(
                    nodeToString(node.getLeft(), cuctx, path.append(node))
                    + "."
                    + nodeToString(node.getRight(), cuctx, path.append(node))
            );
        } else {
            throw new JFixMeLaterException();
        }
    }

    private StringPrec onParsPostfix(HNParsPostfix node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getLeft());
        sb.append("(");
        List<HNode> right = node.getRight();
        for (int i = 0; i < right.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(right.get(i));
        }
        sb.append(")");
        return new StringPrec(sb.toString());
    }

    private StringPrec onPars(HNPars node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        HNode[] right = node.getItems();
        for (int i = 0; i < right.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(right[i]);
        }
        sb.append(")");
        return new StringPrec(sb.toString());
    }

    private StringPrec onBraketsPostfix(HNBracketsPostfix node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (HNodeUtils.isElementInvokable(node)) {
            return onElementInvokable(node, cuctx, path);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(node.getLeft());
        sb.append("[");
        List<HNode> right = node.getRight();
        for (int i = 0; i < right.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(right.get(i));
        }
        sb.append("]");
        return new StringPrec(sb.toString());
    }

    private StringPrec onOpBinaryCall(HNOpBinaryCall node, HLGenCompilationUnitContext cuctx, JNodePath path) {
        if (HNodeUtils.isElementInvokable(node)) {
            return onElementInvokable(node, cuctx, path);
        }
        HNElement element = node.getElement();
        if (element != null) {
            switch (element.getKind()) {
                case METHOD: {
                    HNElementMethod m = (HNElementMethod) element;
                    JInvokable ik = m.getInvokable();
                    if (ik instanceof JMethod) {
                        JMethod jm = (JMethod) ik;
                        if (jm.declaringType().name().equals("net.vpc.hadralang.stdlib.ext.HJavaDefaultOperators")) {
                            //this is a standard operator
                            return binop(node.getName(), nodeToString(node.getLeft(), cuctx, path.append(node)), nodeToString(node.getRight(), cuctx, path.append(node)));
                        }
                    }
                    throw new JFixMeLaterException();
                }

            }
        }
        return binop(node.getName(), nodeToString(node.getLeft(), cuctx, path.append(node)), nodeToString(node.getRight(), cuctx, path.append(node)));
    }

    private static class ForEachDec {

        String name;
        String type;
        String value;

        public ForEachDec(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    private static class JNodeRaw extends HNode {

        private String javaCode;

        public JNodeRaw(String javaCode) {
            super(HNNodeId.H_RAW);
            this.javaCode = javaCode;
        }

        @Override
        protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        }

        @Override
        public List<JNode> childrenNodes() {
            return Collections.emptyList();
        }
    }

    private static class StringPrec {

        private String str;
        private int prec;

        public StringPrec(String str, int prec) {
            this.str = str;
            this.prec = prec;
        }

        public StringPrec(String str) {
            this.str = str;
            this.prec = Integer.MAX_VALUE;
        }

        @Override
        public String toString() {
            return String.valueOf(str);
        }

        public int length() {
            return str.length();
        }

        public boolean endsWith(String s) {
            return str.endsWith(s);
        }
    }

    private boolean isReturn(HNode[] n) {
        for (int i = 0; i < n.length; i++) {
            if (isReturn(n[i])) {
                return true;
            }
        }
        return false;
    }

    public StringPrec blockWithReturnString(List<HNode> nodesList, HLGenCompilationUnitContext cuctx, JNodePath path) {
        HNode[] nodes = nodesList.toArray(new HNode[0]);
        if (isReturn(nodes)) {
            return nodeArrayToString(nodes, ";\n", cuctx, path);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            String s = nodeToString(nodes[i], cuctx, path).toString();
            if (i == nodes.length - 1) {
                s = "return " + s;
            }
            sb.append(s);
            if (!s.endsWith("}") && !s.endsWith(";")) {
                sb.append(";\n");
            }
        }
        return new StringPrec(sb.toString(), 0);
    }

    public StringPrec nodeArrayToString(List<HNode> nodes, String sep, HLGenCompilationUnitContext cuctx, JNodePath path) {
        return nodeArrayToString(nodes.toArray(new HNode[0]), sep, cuctx, path);
    }

    public StringPrec nodeArrayToString(HNode[] nodes, String sep, HLGenCompilationUnitContext cuctx, JNodePath path) {
        StringBuilder sb = new StringBuilder();
        if (sep.equals(";\n")) {
            int counter = 0;
            for (HNode node : nodes) {
                StringPrec s = nodeToString(node, cuctx, path);
                if (s != null && s.length() > 0) {
                    if (counter > 0) {
                        sb.append("\n");
                    }
                    sb.append(s);
                    if (!s.endsWith("}") && !s.endsWith(";")) {
                        sb.append(";");
                    }
                    counter++;
                }
            }
        } else {
            for (int i = 0; i < nodes.length; i++) {
                StringPrec s = nodeToString(nodes[i], cuctx, path);
                if (s != null) {
                    if (!sep.startsWith(";")) {
                        if (i > 0) {
                            sb.append(sep);
                        }
                    }
                    sb.append(s);
                    if (sep.startsWith(";")) {
                        if (s.length() > 0 && !s.endsWith("}") && !s.endsWith(";")) {
                            sb.append(sep);
                        }
                    }
                }
            }
        }
        return new StringPrec(sb.toString(), 0);
    }

    public String nextVar() {
        return "$" + nextNumber();
    }

    public int nextNumber() {
        return ++counter;
    }

    private boolean isReturn(HNode n) {
        return n.isExitContext();
    }

    public int getJavaBinaryOperatorPrecedence(String name) {
        return JOperatorPrecedences.getDefaultPrecedence(JOperator.infix(name));
    }

    public static String indent(StringPrec str) {
        return indent(str.toString());
    }

    public static String beforeClass() {
        return "\n";
    }

    public static String beforeMethod() {
        return "\n";
    }

    public static String indent(String str) {
        return JeepUtils.indent("    ", str);
    }
}