package net.hl.compiler.utils;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.eval.JEvaluableNode;
import net.vpc.common.jeep.impl.functions.*;
import net.vpc.common.textsource.JTextSource;
import net.hl.compiler.parser.ast.*;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class HUtils {

    public static final int READONLY = 0x00400000;
    public static final int PACKAGE = 0x00800000;
    public static final int CONST = 0x01000000;
    public static final int STATIC = Modifier.STATIC;
    public static final int PUBLIC = Modifier.PUBLIC;

    public static boolean isPackageProtected(int mod) {
        return (mod & PACKAGE) != 0;
    }

    public static boolean isReadOnly(int mod) {
        return (mod & READONLY) != 0;
    }

    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    public static boolean isPublic(int modifiers) {
        return Modifier.isPublic(modifiers);
    }

    public static boolean isPrivate(int modifiers) {
        return Modifier.isPrivate(modifiers);
    }

    public static boolean isProtected(int modifiers) {
        return Modifier.isProtected(modifiers);
    }

    public static final String modifiersToString(int modifiers) {
        List<String> sb = new ArrayList<>();
        if (Modifier.isPublic(modifiers)) {
            sb.add("public");
        } else if (Modifier.isProtected(modifiers)) {
            sb.add("protected");
        } else if (Modifier.isPrivate(modifiers)) {
            sb.add("private");
        } else if ((modifiers & PACKAGE) != 0) {
            sb.add("package");
        }
//        else{
//            sb.add("public");
//        }
        if (Modifier.isStatic(modifiers)) {
            sb.add("static");
        }
        if (Modifier.isAbstract(modifiers)) {
            sb.add("abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            sb.add("final");
        }
        if (Modifier.isTransient(modifiers)) {
            sb.add("transient");
        }
        if (Modifier.isVolatile(modifiers)) {
            sb.add("volatile");
        }
        if (Modifier.isNative(modifiers)) {
            sb.add("native");
        }
        if (Modifier.isStrict(modifiers)) {
            sb.add("strict");
        }
        if ((modifiers & READONLY) != 0) {
            sb.add("readonly");
        }
        return String.join(" ", sb);
    }

    public static final String modifiersToString0(int modifiers) {
        List<String> sb = new ArrayList<>();
        if (Modifier.isPublic(modifiers)) {
            sb.add("public");
        }
        if (Modifier.isProtected(modifiers)) {
            sb.add("protected");
        }
        if (Modifier.isPrivate(modifiers)) {
            sb.add("private");
        }
        if ((modifiers & PACKAGE) != 0) {
            sb.add("package");
        }
//        else{
//            sb.add("public");
//        }
        if (Modifier.isStatic(modifiers)) {
            sb.add("static");
        }
        if (Modifier.isAbstract(modifiers)) {
            sb.add("abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            sb.add("final");
        }
        if (Modifier.isTransient(modifiers)) {
            sb.add("transient");
        }
        if (Modifier.isVolatile(modifiers)) {
            sb.add("volatile");
        }
        if (Modifier.isNative(modifiers)) {
            sb.add("native");
        }
        if (Modifier.isStrict(modifiers)) {
            sb.add("strict");
        }
        if ((modifiers & READONLY) != 0) {
            sb.add("readonly");
        }
        return String.join(" ", sb);
    }

    public static int publifyModifiers(int modifiers) {
        if (!Modifier.isPrivate(modifiers)
                && !Modifier.isProtected(modifiers)
                && !HUtils.isPackageProtected(modifiers)) {
            //default is public!
            modifiers |= Modifier.PUBLIC;
        }
        return modifiers;
    }

    public static int statifyModifiers(int modifiers) {
        modifiers |= Modifier.STATIC;
        return modifiers;
    }

    public static String[] splitNameAndPackage(JToken[] tokens) {
        if (tokens.length == 1) {
            return new String[]{null, tokens[0].image};
        }
        StringBuilder ns = new StringBuilder();
        for (int i = 0; i < tokens.length - 2; i++) {
            ns.append(tokens[i].image);
        }
        return new String[]{ns.toString(), tokens[tokens.length - 1].image};
    }

    public static String[] splitNameAndPackage(String name) {
        String packageName = null;
        int packageIndex = name.lastIndexOf('.');
        if (packageIndex >= 0) {
            packageName = name.substring(0, packageIndex);
            name = name.substring(packageIndex + 1);
        }
        return new String[]{packageName, name};
    }

    public static JSignature sig(String name, List<HNDeclareIdentifier> args) {
        List<JType> argTypes = new ArrayList<>();
        boolean varArg = false;
        for (int i = 0; i < args.size(); i++) {
            HNDeclareIdentifier arg = args.get(i);
            if (i == args.size() - 1 && arg.getIdentifierTypeNode().getTypename().isVarArg()) {
                varArg = true;
            }
            argTypes.add(HNodeUtils.getType(arg));
        }
        return JSignature.of(name, argTypes.toArray(new JType[0]), varArg);
    }

    public static int removeModifierStatic(int modifiers) {
        modifiers = modifiers & ~Modifier.STATIC;
        return modifiers;
    }

    public static JMethod resolveToMethod(JInvokable invokable) {
        if (invokable instanceof JMethod) {
            return (JMethod) invokable;
        }
        if (invokable instanceof JFunction) {
            JFunction f = (JFunction) invokable;
            if (f instanceof JFunctionFromStaticMethod) {
                return ((JFunctionFromStaticMethod) f).getMethod();
            }
            if (f instanceof JFunctionFromJMethod) {
                return ((JFunctionFromJMethod) f).getMethod();
            }
            if (f instanceof JMethodInvocationFunction) {
                return ((JMethodInvocationFunction) f).getMethod();
            }
            if (f instanceof JFunctionFromInvoke) {
                return resolveToMethod(((JFunctionFromInvoke) f).getBase());
            }
        }
        throw new JParseException("Cannot resolve as Method..." + invokable);
    }

    public static boolean isIdNode(String name, JNode n) {
        return n instanceof HNIdentifier && ((HNIdentifier) n).getName().equals(name);
    }

    public static boolean isBinaryAndNode(JNode n) {
        if (n instanceof HNOpBinaryCall && ((HNOpBinaryCall) n).getName().equals("&&")) {
            return true;
        }
        return false;
    }

    public static boolean isBinaryNode(String name, JNode n) {
        return n instanceof HNOpBinaryCall && ((HNOpBinaryCall) n).getName().equals(name);
    }

    public static boolean isNullLiteral(JNode arg1) {
        return arg1 instanceof HNLiteral && ((HNLiteral) arg1).getValue() == null;
    }

    //    public static JNode createUnknownBlocNode(){
//        HNBlock hnBlock = new HNBlock(HNBlock.BlocType.LOCAL_BLOC, JTokenUtils.createSeparatorToken("{"));
//        hnBlock.setEndToken(JTokenUtils.createSeparatorToken("}"));
//        return hnBlock;
//    }
    public static JToken appendTokens(JToken left, JToken right) {
        left.image += right.sval;
        left.sval = right.image;
        left.endCharacterNumber = right.endCharacterNumber;
        left.endColumnNumber = right.endColumnNumber;
        left.endLineNumber = right.endLineNumber;
        return left;
    }

    public static String[] getImports(JNode node) {
        LinkedHashSet<String> imports = new LinkedHashSet<>();
        while (node != null) {
            if (node instanceof HNBlock) {
                HNBlock node1 = HNBlock.get(node);
                if (node1.getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                    for (JNode statement : node1.getStatements()) {
                        if (statement instanceof HNImport) {
                            imports.add(((HNImport) statement).getValue());
                        }
                    }
                }
            }
            node = node.parentNode();
        }
        return imports.toArray(new String[0]);
    }

    public static void setSource(JNode node, JTextSource src) {
        node.setUserObject(JTextSource.class.getName(),src);
    }
    public static JTextSource getSource(JNode node) {
        while (node != null) {
            Object ss = node.userObjects().get(JTextSource.class.getName());
            if(ss instanceof JTextSource){
                return (JTextSource)ss;
            }
            if (node instanceof HNBlock.CompilationUnitBlock) {
                JCompilationUnit cu=((HNBlock.CompilationUnitBlock) node).getCompilationUnit();
                if(cu!=null){
                    JTextSource s = cu.getSource();
                    if(s!=null){
                        return s;
                    }
                }
            }
            node = node.parentNode();
        }
        return null;
    }

    public static String getSourceName(JNode node) {
        JTextSource cu = getSource(node);
        if (cu != null) {
            return cu.name();
        }
        return null;
    }

    public static JCompilationUnit getCompilationUnit(JNode node) {
        while (node != null) {
            if (node instanceof HNBlock.CompilationUnitBlock) {
                return ((HNBlock.CompilationUnitBlock) node).getCompilationUnit();
            }
            node = node.parentNode();
        }
        return null;
    }

    public static boolean isComparisonOperator(String opName) {
        switch (opName) {
            case "==":
            case "===":
            case "!==":
            case "!===":
            case "<":
            case ">":
            case "<=":
            case ">=": {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignmentOperator(String opName) {
        return opName.equals("=")
                || (!opName.endsWith("==") && !opName.endsWith("===")
                && !opName.endsWith("!=")
                && !opName.endsWith("<=") && !opName.endsWith(">=")
                && opName.length() > 1 && opName.endsWith("="));
    }

    //    @Override
    public static String getStaticConstructorName(JType baseType) {
        JType jType = baseType.getRawType();
        if (jType.isArray()) {
            JTypeArray ta = (JTypeArray) jType;
            jType = ta.rootComponentType();
        }
        StringBuilder staticConstructorName = new StringBuilder("new");
        if (jType.isPrimitive()) {
            //here to distinguish from primitive and boxed types
            //to get : newPrimitiveLongArray and newLongArray for instance
            char[] s = jType.simpleName().toCharArray();
            s[0] = Character.toUpperCase(s[0]);
            staticConstructorName.append("Primitive");
            staticConstructorName.append(s);
        } else {
            staticConstructorName.append(jType.simpleName());
        }
        if (baseType.isArray()) {
            staticConstructorName.append("Array");
            JTypeArray ta = (JTypeArray) baseType;
            if (ta.arrayDimension() > 1) {
                staticConstructorName.append(ta.arrayDimension());
            }
        }
        return staticConstructorName.toString();
    }

    public static JInvokablePrefilled createJInvokablePrefilled(JInvokable invokable, JNode... nodes) {
        getTypes(nodes);//just to check types!!
        return new JInvokablePrefilled(
                invokable,
                getEvaluables(nodes)
        );
    }

    public static JEvaluable[] getEvaluables(JNode[] args) {
        JType[] ntypes = getTypes(args);
        JEvaluable[] ev = new JEvaluable[args.length];
        for (int i = 0; i < ev.length; i++) {
            ev[i] = new JEvaluableNode(args[i], ntypes[i]);
        }
        return ev;
    }

    public static JType[] getTypes(JNode[] args) {
        if (args == null) {
            return null;
        }
        JType[] argTypes = new JType[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = HNodeUtils.getType(args[i]);
        }
        return argTypes;
    }

    public static Temporal parseTemporal(String sval){
        if(sval==null){
            sval="";
        }
        Temporal parsed = null;
        String mostLikelyFormat=null;
        int svalLength = sval.length();
        if(sval.indexOf('/')>0){
            sval=sval.replace('/','-');
        }
        boolean dte=false;
        boolean tm=false;
        if(sval.indexOf('-')>=0){
            dte=true;
        }
        if(sval.indexOf(':')>=0){
            tm=true;
        }
        if(dte && tm){
            mostLikelyFormat = "2019-03-27T10:15:30";
            try {
                parsed = (LocalDateTime.parse(sval));
            } catch (Exception ex) {
                //ignore
            }
            try {
                parsed = (LocalDateTime.parse(sval.replace(' ','T')));
            } catch (Exception ex) {
                //ignore
            }
        }else if(dte){
            mostLikelyFormat = "2019-03-27";
            if ("2019-03-27".length() == svalLength) {
                try {
                    parsed = (LocalDate.parse(sval));
                } catch (Exception ex) {
                    //
                }
            }
        }else if(tm){
            mostLikelyFormat = "12:03:27";
            try {
                parsed = (LocalTime.parse(sval));
            } catch (Exception ex) {
                //
            }
        }else{
            mostLikelyFormat = "2019-03-27T10:15:30";
        }
        if (parsed == null) {
            throw new JParseException("local date/time must be in the ISO format ex: "+mostLikelyFormat);
        }
        return parsed;
    }

    public static HNode skipFirstPar(HNode n){
        if(n instanceof HNPars){
            HNode[] i = ((HNPars) n).getItems();
            if(i.length==1){
                return i[0];
            }
        }
        return n;
    }
}
