package net.hl.compiler.core.elements;

import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;

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

    public JTypePattern getTypePattern() {
        return null;
    }
    @Override
    public String toString() {
        return "ModuleName{" +
                name+
                '}';
    }
}
