package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementMetaPackageVersion extends HNElement {
    String name;

    public HNElementMetaPackageVersion(String name) {
        super(HNElementKind.MODULE_VERSION);
        this.name = name;
    }
    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }
    public String getName() {
        return name;
    }
    public JType getType(){
        return null;
    }
    @Override
    public String toString() {
        return "HNElementMetaPackageVersion{" +
                name +
                '}';
    }
}
