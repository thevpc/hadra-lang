package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.DefaultJTypedValue;
import net.vpc.common.jeep.impl.functions.DefaultJInvokeContext;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.stages.runtime.HLConstantEvaluator;
import net.vpc.hadralang.stdlib.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HLUtils {
    public static final int EXPAND_RANGE_SIZE = 50;

    public static Object checkSimpleValue(Object value, JNode node, JType type, HLJCompilerContext compilerContextBase, boolean acceptSwitchCaseAlternatives, boolean[] refError) {
        if (type.boxed().name().equals(Byte.class.getName())) {
            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                long f = ((Number) value).longValue();
                if (f < Byte.MIN_VALUE || f > Byte.MAX_VALUE) {
                    refError[0]=true;
                    compilerContextBase.log().error("X450", null, "byte overflow", node.startToken());
                }
                return (byte) f;
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                if (
                        f.compareTo(BigInteger.valueOf(Byte.MIN_VALUE)) < 0
                                || f.compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X450", null, "byte overflow", node.startToken());
                }
                return f.byteValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof ByteRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X451", null, "expected constant byte value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Short.class.getName())) {
            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                long f = ((Number) value).longValue();
                if (f < Short.MIN_VALUE || f > Short.MAX_VALUE) {
                    refError[0]=true;
                    compilerContextBase.log().error("X452", null, "short overflow", node.startToken());
                }
                return (short) f;
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                if (
                        f.compareTo(BigInteger.valueOf(Short.MIN_VALUE)) < 0
                                || f.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X452", null, "short overflow", node.startToken());
                }
                return f.shortValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof ShortRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X453", null, "expected constant short value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Character.class.getName())) {
            if (value instanceof Character) {
                return (char) value;
            } else if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                long f = ((Number) value).longValue();
                if (f < Character.MIN_VALUE || f > Character.MAX_VALUE) {
                    refError[0]=true;
                    compilerContextBase.log().error("X454", null, "char overflow", node.startToken());
                }
                return (char) f;
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                if (
                        f.compareTo(BigInteger.valueOf(Character.MIN_VALUE)) < 0
                                || f.compareTo(BigInteger.valueOf(Character.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X455", null, "short overflow", node.startToken());
                }
                return (char) f.intValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof CharRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X456", null, "expected constant short value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Integer.class.getName())) {
            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                long f = ((Number) value).longValue();
                if (f < Integer.MIN_VALUE || f > Integer.MAX_VALUE) {
                    refError[0]=true;
                    compilerContextBase.log().error("X457", null, "int overflow", node.startToken());
                }
                return (int) f;
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                if (
                        f.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0
                                || f.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X457", null, "int overflow", node.startToken());
                }
                return f.shortValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof IntRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X458", null, "expected constant int value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Long.class.getName())) {
            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                return ((Number) value).longValue();
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                if (
                        f.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0
                                || f.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X459", null, "long overflow", node.startToken());
                }
                return f.shortValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof LongRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X460", null, "expected constant long value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(BigInteger.class.getName())) {
            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                return BigInteger.valueOf(((Number) value).longValue());
            } else if (value instanceof BigInteger) {
                return (BigInteger) value;
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof BigIntRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X461", null, "expected constant bigint value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Float.class.getName())) {
            if (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                double f = ((Number) value).doubleValue();
                if (f < Float.MIN_VALUE || f > Float.MAX_VALUE) {
                    refError[0]=true;
                    compilerContextBase.log().error("X462", null, "float overflow", node.startToken());
                }
                return (float) f;
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                BigDecimal d = new BigDecimal(f);
                if (
                        d.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) < 0
                                || d.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X462", null, "float overflow", node.startToken());
                }
                return d.floatValue();
            } else if (value instanceof BigDecimal) {
                BigDecimal d = ((BigDecimal) value);
                if (
                        d.compareTo(BigDecimal.valueOf(Float.MIN_VALUE)) < 0
                                || d.compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X463", null, "float overflow", node.startToken());
                }
                return d.floatValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof FloatRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X464", null, "expected constant float value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Double.class.getName())) {
            if (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                return ((Number) value).doubleValue();
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                BigDecimal d = new BigDecimal(f);
                if (
                        d.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) < 0
                                || d.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X465", null, "double overflow", node.startToken());
                }
                return d.floatValue();
            } else if (value instanceof BigDecimal) {
                BigDecimal d = ((BigDecimal) value);
                if (
                        d.compareTo(BigDecimal.valueOf(Double.MIN_VALUE)) < 0
                                || d.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0
                ) {
                    refError[0]=true;
                    compilerContextBase.log().error("X465", null, "double overflow", node.startToken());
                }
                return d.floatValue();
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof DoubleRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X466", null, "expected constant double value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(BigDecimal.class.getName())) {
            if (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else if (value instanceof BigInteger) {
                BigInteger f = (BigInteger) value;
                return new BigDecimal(f);
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value);
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof BigDecimalRange) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X466", null, "expected constant double value", node.startToken());
                return 0;
            }
        } else if (type.boxed().name().equals(Boolean.class.getName())) {
            if (value instanceof Boolean) {
                return value;
            } else {
                refError[0]=true;
                compilerContextBase.log().error("X467", null, "expected constant boolean value", node.startToken());
                return false;
            }
        } else if (type.boxed().name().equals(String.class.getName())) {
            if (value instanceof String) {
                return value;
            } else {
                if (acceptSwitchCaseAlternatives) {
                    if (value instanceof Pattern) {
                        return value;
                    }
                }
                refError[0]=true;
                compilerContextBase.log().error("X468", null, "expected constant string value", node.startToken());
                return false;
            }
        } else if (type.isAssignableFrom(compilerContextBase.context().types().typeOf(value))) {
            return value;
        } else if (type.name().equals(Pattern.class.getName())) {
            if (value instanceof Pattern) {
                return value;
            } else if (value instanceof CharSequence) {
                refError[0]=true;
                compilerContextBase.log().error("X469", null, "expected constant " + type.name() + " value", node.startToken());
                return Pattern.compile(value.toString());
            } else {
                refError[0]=true;
                compilerContextBase.log().error("X469", null, "expected constant " + type.name() + " value", node.startToken());
                return null;
            }
        } else {
            refError[0]=true;
            compilerContextBase.log().error("X469", null, "expected constant " + type.name() + " value", node.startToken());
            return null;
        }
    }

    public static Object evalCaseLiteral(JNode n, HLJCompilerContext compilerContextBase, Map<String,JTypedValue> vars, boolean[] refError) {
        try {
            if(refError==null){
                refError=new boolean[0];
            }
            JContext context = compilerContextBase.context().newContext();
            if(vars!=null){
                for (Map.Entry<String, JTypedValue> entry : vars.entrySet()) {
                    JTypedValue value = entry.getValue();
                    context.vars().declareVar(entry.getKey(),
                            value.getType(),
                            value.getValue()
                    );
                }
            }
            return HLConstantEvaluator.INSTANCE.evaluate(n, new DefaultJInvokeContext(
                    context,
                    HLConstantEvaluator.INSTANCE,
                    null, new JEvaluable[0],
                    "<<preprocessor>>"
            ));
        }catch (Exception ex){
            compilerContextBase.log().error("X470", null,"unable to evaluate constant value "+n,n.startToken());
            refError[0]=true;
            return null;
        }
    }

    public static Object simplifyCaseLiteral(JNode n, JType type, HLJCompilerContext compilerContextBase, boolean acceptSwitchCaseAlternatives, boolean[] refError) {
        JTypes types = compilerContextBase.types();
        Map<String,JTypedValue> csts=new HashMap<>();
        if (type.boxed().name().equals(Byte.class.getName())) {
            csts.put("MIN_VALUE",new DefaultJTypedValue(Byte.MIN_VALUE, JTypeUtils.forByte(types)));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Byte.MAX_VALUE, JTypeUtils.forByte(types)));
        } else if (type.boxed().name().equals(Short.class.getName())) {
            csts.put("MIN_VALUE",new DefaultJTypedValue(Short.MIN_VALUE, JTypeUtils.forShort(types)));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Short.MAX_VALUE, JTypeUtils.forShort(types)));
        } else if (type.boxed().name().equals(Integer.class.getName())) {
            csts.put("MIN_VALUE",new DefaultJTypedValue(Integer.MIN_VALUE, JTypeUtils.forInt(types)));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Integer.MAX_VALUE, JTypeUtils.forInt(types)));
        } else if (type.boxed().name().equals(Long.class.getName())) {
            csts.put("MIN_VALUE",new DefaultJTypedValue(Long.MIN_VALUE, JTypeUtils.forLong(types)));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Long.MAX_VALUE, JTypeUtils.forLong(types)));
        } else if (type.boxed().name().equals(Float.class.getName())) {
            csts.put("MIN_VALUE",new DefaultJTypedValue(Float.MIN_VALUE, JTypeUtils.forFloat(types)));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Float.MAX_VALUE, JTypeUtils.forFloat(types)));
            csts.put("POSITIVE_INFINITY",new DefaultJTypedValue(Float.POSITIVE_INFINITY, JTypeUtils.forFloat(types)));
            csts.put("NEGATIVE_INFINITY",new DefaultJTypedValue(Float.NEGATIVE_INFINITY, JTypeUtils.forFloat(types)));
            csts.put("NaN",new DefaultJTypedValue(Float.NaN, JTypeUtils.forFloat(types)));
        } else if (type.boxed().name().equals(Double.class.getName())) {
            JType aDouble = JTypeUtils.forDouble(types);
            csts.put("MIN_VALUE",new DefaultJTypedValue(Double.MIN_VALUE, aDouble));
            csts.put("MAX_VALUE",new DefaultJTypedValue(Double.MAX_VALUE, aDouble));
            csts.put("POSITIVE_INFINITY",new DefaultJTypedValue(Double.POSITIVE_INFINITY, aDouble));
            csts.put("NEGATIVE_INFINITY",new DefaultJTypedValue(Double.NEGATIVE_INFINITY, aDouble));
            csts.put("NaN",new DefaultJTypedValue(Double.NaN, aDouble));
        }
        return evalCaseLiteral(n,compilerContextBase,csts,refError);
//        if (n instanceof HNLiteral) {
//            return checkSimpleValue(((HNLiteral) n).getValue(), n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//        } else if (n instanceof HNIdentifier) {
//            String name = ((HNIdentifier) n).getName();
//            JNode p = compilerContextBase.lookupVarDeclaration(name);
//            if (p != null) {
//                if (p instanceof HNDeclareIdentifier
//                        && 0 != (((HNDeclareIdentifier) p).getModifiers() & Modifier.STATIC)
//                        && 0 != (((HNDeclareIdentifier) p).getModifiers() & Modifier.FINAL)
//                ) {
//                    return simplifyCaseLiteral(((HNDeclareIdentifier) p).getInitValue(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                }
//            }
//            if (type.boxed().name().equals(Byte.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Byte.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Byte.MAX_VALUE;
//                }
//            } else if (type.boxed().name().equals(Short.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Short.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Short.MAX_VALUE;
//                }
//            } else if (type.boxed().name().equals(Integer.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Integer.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Integer.MAX_VALUE;
//                }
//            } else if (type.boxed().name().equals(Long.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Long.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Long.MAX_VALUE;
//                }
//            } else if (type.boxed().name().equals(Float.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Float.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Float.MAX_VALUE;
//                }
//                if ("POSITIVE_INFINITY".equals(name)) {
//                    return Float.POSITIVE_INFINITY;
//                }
//                if ("NEGATIVE_INFINITY".equals(name)) {
//                    return Float.NEGATIVE_INFINITY;
//                }
//                if ("NaN".equals(name)) {
//                    return Float.NaN;
//                }
//            } else if (type.boxed().name().equals(Double.class.getName())) {
//                if ("MIN_VALUE".equals(name)) {
//                    return Double.MIN_VALUE;
//                }
//                if ("MAX_VALUE".equals(name)) {
//                    return Double.MAX_VALUE;
//                }
//                if ("POSITIVE_INFINITY".equals(name)) {
//                    return Double.POSITIVE_INFINITY;
//                }
//                if ("NEGATIVE_INFINITY".equals(name)) {
//                    return Double.NEGATIVE_INFINITY;
//                }
//                if ("NaN".equals(name)) {
//                    return Double.NaN;
//                }
//            }
//            refError[0] = true;
//            compilerContextBase.log().error("S003", "expected constant " + type.name() + " value", n.token());
//        } else if (n instanceof HNField) {
//            JField field = ((HNField) n).getField();
//            if (field != null) {
//                if (field.isStatic() && field.isFinal()) {
//                    return checkSimpleValue(field.get(null), n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                }
//            }
//            refError[0] = true;
//            compilerContextBase.log().error("S003", "expected constant " + type.name() + " value", n.token());
//        } else if (n instanceof HNOpBinaryCall) {
//            HNOpBinaryCall bo = ((HNOpBinaryCall) n);
//            switch (bo.getName()) {
//                case "..": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    if (!refError[0]) {
//                        Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                        if (!refError[0]) {
//                            return RangeExtensions.newRangeII(
//                                    (int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError),
//                                    (int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)
//                            );
//                        }
//                    }
//                }
//                case "<..": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    if (!refError[0]) {
//                        Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                        if (!refError[0]) {
//                            return RangeExtensions.newRangeEI(
//                                    (int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError),
//                                    (int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)
//                            );
//                        }
//                    }
//                }
//                case "..<": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    return RangeExtensions.newRangeIE(
//                            (int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError),
//                            (int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)
//                    );
//                }
//                case "<..<": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    return RangeExtensions.newRangeEE(
//                            (int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError),
//                            (int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)
//                    );
//                }
//                case "+": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives, refError);
//                    return checkSimpleValue(
//                            ((int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives, refError)) +
//                                    ((int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives, refError))
//                            , n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                }
//                case "-": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    return checkSimpleValue(
//                            ((int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)) -
//                                    ((int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError))
//                            , n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                }
//                case "*": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    return checkSimpleValue(
//                            ((int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)) *
//                                    ((int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError))
//                            , n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                }
//                case "/": {
//                    Object o_from = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    Object o_to = simplifyCaseLiteral(bo.getLeftNode(), type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                    return checkSimpleValue(
//                            ((int) checkSimpleValue(o_from, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError)) /
//                                    ((int) checkSimpleValue(o_to, n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError))
//                            , n, type, compilerContextBase, acceptSwitchCaseAlternatives,refError);
//                }
//            }
//            compilerContextBase.log().error("S003", "expected constant " + type.name() + " value", n.token());
//        } else if (n instanceof HNInvokerCall) {
//            HNInvokerCall ic = (HNInvokerCall) n;
//            JInvokablePrefilled impl = ic.impl();
//            JEvaluable[] evaluables = impl.getEvaluables();
//            boolean ok = true;
//            for (int i = 0; i < evaluables.length; i++) {
//                if (evaluables[i] instanceof JEvaluableNode) {
//                    JNode en = ((JEvaluableNode) evaluables[i]).getNode();
//
//                }
//            }
//            switch (impl.getInvokable().toString()) {
//                case "public static net.vpc.hadralang.stdlib.IntRange net.vpc.hadralang.stdlib.ext.RangeExtensions.newRangeII(int,int)": {
//
//                }
//            }
////            if (impl.getInvokable().toString().equals()) {
////
////            }
//            compilerContextBase.log().error("S003", "expected constant " + type.name() + " value", n.token());
//        }
//        return type.defaultValue();
    }
}
