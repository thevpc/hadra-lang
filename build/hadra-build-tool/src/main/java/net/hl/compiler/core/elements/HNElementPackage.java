package net.hl.compiler.core.elements;

import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;

public class HNElementPackage extends HNElement implements Cloneable{
    String name;

    public HNElementPackage(String name) {
        super(HNElementKind.PACKAGE);
        this.name = name;
    }
    public JTypePattern getTypePattern() {
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
