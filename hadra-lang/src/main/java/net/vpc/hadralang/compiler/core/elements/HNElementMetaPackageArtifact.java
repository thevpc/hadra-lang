package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementMetaPackageArtifact extends HNElement implements Cloneable{
    String name;

    public HNElementMetaPackageArtifact(String name) {
        super(HNElementKind.MODULE_NAME);
        this.name = name;
    }

    public JType getType(){
        return null;
    }
    public String getName() {
        return name;
    }

    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }
    @Override
    public String toString() {
        return "ModuleName{" +
                name+
                '}';
    }
}
