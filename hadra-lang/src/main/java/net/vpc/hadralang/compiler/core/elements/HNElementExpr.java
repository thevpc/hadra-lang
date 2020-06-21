package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.parser.ast.HNode;

public class HNElementExpr extends HNElement implements Cloneable{
    public JTypeOrLambda type;

    public HNElementExpr() {
        super(HNElementKind.EXPR);
    }

    public HNElementExpr(JType type) {
        super(HNElementKind.EXPR);
        setType(type);
    }
    public HNElementExpr(JTypeOrLambda type) {
        super(HNElementKind.EXPR);
        setType(type);
    }

    public static HNElementExpr get(HNode n) {
        return get(n.getElement());
    }

    public static HNElementExpr get(HNElement element) {
        return (HNElementExpr) element;
    }

    public JType getType() {
        return type==null?null:type.getType();
    }

    public HNElementExpr setType(JType type) {
        this.type = type==null?null:JTypeOrLambda.of(type);
        return this;
    }

    public HNElementExpr setType(JTypeOrLambda type) {
        this.type = type;
        return this;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        return type;
    }

    @Override
    public String toString() {
        return "Expr{" +
                (type == null ? "null" : type.toString()) +
                '}';
    }
}
