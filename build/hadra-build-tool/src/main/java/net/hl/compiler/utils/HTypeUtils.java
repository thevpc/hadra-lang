package net.hl.compiler.utils;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JTypeUtils;
import net.hl.compiler.parser.ast.ElementTypeAndConstraint;
import net.hl.compiler.parser.ast.InitValueConstraint;
import net.hl.lang.Tuple;
import net.hl.lang.TupleN;

import java.util.Arrays;

public class HTypeUtils {

    public static boolean isTupleType(JTypePattern t) {
        return t != null && t.isType() && isTupleType(t.getType());
    }

    public static boolean isTupleType(JType t) {
        return tupleTypeBase(t.types()).isAssignableFrom(t);
    }

    public static JType[] tupleArgTypes(JType t) {
        if (!tupleTypeBase(t.types()).isAssignableFrom(t)) {
            throw new IllegalArgumentException("Expected Tuple Type : " + t.getName());
        }
        JType n = t;
        while (n != null) {
            JType r = n.getRawType();
            if (r.getName().startsWith("net.hl.lang.Tuple")) {
                String s = r.getName().substring("net.hl.lang.Tuple".length());
                if (s.equals("N")) {
                    return new JType[0];
                }
                int i = -1;
                try {
                    i = Integer.parseInt(s);
                } catch (Exception ex) {
                    //ignore
                }
                if (i < 0) {
                    throw new IllegalArgumentException("Expected a valid Tuple Type : " + t.getName());
                }
                if (i <= Tuple.MAX_ELEMENTS) {
                    JType[] e = new JType[i];
                    if (t.isRawType()) {
                        Arrays.fill(e, JTypeUtils.forObject(t.types()));
                        return e;
                    }
                    JParameterizedType p = (JParameterizedType) t;
                    return p.actualTypeArguments();
                }
            }
            n = n.getSuperType();
        }
        throw new IllegalArgumentException("Expected a valid Tuple Type : " + t.getName());
    }

    public static JType tupleTypeBase(JTypes t) {
        return t.forName("net.hl.lang.Tuple");
    }

    public static JType tupleType(JTypes t, JType... others) {
        if (others.length > Tuple.MAX_ELEMENTS) {
            return t.forName(TupleN.class.getName());
        } else {
            JRawType raw = (JRawType) t.forName("net.hl.lang.Tuple" + others.length);
            if (others.length > 0) {
                return raw.parametrize(others);
            }
            return raw;
        }
    }

    public static JRawType tupleTypeForCount(int count, JTypes types) {
        if (count > Tuple.MAX_ELEMENTS) {
            return (JRawType) types.forName(TupleN.class.getName());
        } else {
            return (JRawType) types.forName("net.hl.lang.Tuple" + count);
        }
    }

    public static JType[] extractLambdaArgTypes(JType type) {
        JMethod[] jMethods = type.getDeclaredMethods();
        if (jMethods.length > 1) {
            jMethods = Arrays.stream(type.getDeclaredMethods()).filter(x -> !x.isDefault()
                    && x.isPublic()
                    && !JTypeUtils.isSynthetic(x.getModifiers())
            ).toArray(JMethod[]::new);
        }
        if (jMethods.length == 1) {
            return jMethods[0].getArgTypes();
        } else {
            return null;
        }
    }

    public static ElementTypeAndConstraint resolveIterableComponentType(JType valType, JTypes types) {
        if (valType.isArray()) {
            JTypeArray ta = (JTypeArray) valType;
            return new ElementTypeAndConstraint(
                    (ta.componentType()),
                    InitValueConstraint.ITERABLE
            );
        } else if (types.forName("java.lang.CharSequence").isAssignableFrom(valType)) {
            return new ElementTypeAndConstraint(
                    JTypeUtils.forChar(types),
                    InitValueConstraint.ITERABLE
            );
        } else if (types.forName("net.hl.lang.IntRange").isAssignableFrom(valType)) {
            return new ElementTypeAndConstraint(
                    JTypeUtils.forInt(types),
                    InitValueConstraint.ITERABLE
            );
        } else if (types.forName("java.util.Iterable").isAssignableFrom(valType)) {
            if (types.forName("java.util.Iterable").equals(valType.getRawType())) {
                JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
                if (a.length == 0) {
                    valType = (JTypeUtils.forObject(types));
                } else {
                    valType = (JType) a[0];
                }
            } else {
                throw new JFixMeLaterException();
            }
            return new ElementTypeAndConstraint(
                    valType,
                    InitValueConstraint.ITERABLE
            );
        } else if (types.forName("java.util.Iterator").isAssignableFrom(valType)) {
            if (types.forName("java.util.Iterator").equals(valType.getRawType())) {
                JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
                if (a.length == 0) {
                    valType = (JTypeUtils.forObject(types));
                } else {
                    valType = (JType) a[0];
                }
            } else {
                throw new JFixMeLaterException();
            }
            return new ElementTypeAndConstraint(
                    valType,
                    InitValueConstraint.ITERATOR
            );
        } else if (types.forName("java.util.stream.BaseStream").isAssignableFrom(valType)) {
            if (types.forName("java.util.stream.Stream").isAssignableFrom(valType)) {
                if (types.forName("java.util.stream.Stream").equals(valType.getRawType())) {
                    JType[] a = (valType instanceof JParameterizedType) ? ((JParameterizedType) valType).actualTypeArguments() : new JType[0];
                    if (a.length == 0) {
                        valType = (JTypeUtils.forObject(types));
                    } else {
                        valType = (JType) a[0];
                    }
                } else {
                    throw new JFixMeLaterException();
                }
            } else if (types.forName("java.util.stream.IntStream").isAssignableFrom(valType)) {
                valType = JTypeUtils.forInt(types);
            } else if (types.forName("java.util.stream.LongStream").isAssignableFrom(valType)) {
                valType = JTypeUtils.forLong(types);
            } else if (types.forName("java.util.stream.DoubleStream").isAssignableFrom(valType)) {
                valType = JTypeUtils.forDouble(types);
            } else {
                throw new JFixMeLaterException();
            }
            return new ElementTypeAndConstraint(
                    valType,
                    InitValueConstraint.ITERATOR
            );
        }
        return null;
    }
}
