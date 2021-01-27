package net.hl.compiler.core.elements;

import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;

public class HNElementMetaPackageVersion extends HNElement implements Cloneable{
    String name;

    public HNElementMetaPackageVersion(String name) {
        super(HNElementKind.MODULE_VERSION);
        this.name = name;
    }
    public JTypePattern getTypePattern() {
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
