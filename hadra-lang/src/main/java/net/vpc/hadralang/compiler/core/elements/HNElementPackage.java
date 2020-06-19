package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementPackage extends HNElement implements Cloneable{
    String name;

    public HNElementPackage(String name) {
        super(HNElementKind.PACKAGE);
        this.name = name;
    }
    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }
    public JType getType(){
        return null;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Package{" +
                name+
                '}';
    }
}
