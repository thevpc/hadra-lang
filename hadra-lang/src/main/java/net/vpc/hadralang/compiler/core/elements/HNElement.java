package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JTypeOrLambda;

public abstract class HNElement {
    private HNElementKind kind;
    private JToken location;
    private JSource source;
    private JInvokable convertInvokable;

    public JToken getLocation() {
        return location;
    }

    public HNElement setLocation(JToken location) {
        this.location = location;
        return this;
    }

    public JSource getSource() {
        return source;
    }

    public HNElement setSource(JSource source) {
        this.source = source;
        return this;
    }

    public HNElement(HNElementKind kind) {
        this.kind = kind;
    }

    public HNElementKind getKind() {
        return kind;
    }
    public abstract JTypeOrLambda getTypeOrLambda() ;

    public JType getType(){
        JTypeOrLambda typeOrLambda = getTypeOrLambda();
        return typeOrLambda==null?null:typeOrLambda.getType();
    }

    @Override
    public String toString() {
        return "HNElement{" +
                "kind=" + kind +
                '}';
    }

    public String toDescString() {
        if(getLocation()!=null && getSource()!=null){
            return JCompilerMessage.toRangeString(getLocation(),getSource(),true);
        }
        return toString();
    }

    public void setConverterInvokable(JInvokable convertInvokable) {
        this.convertInvokable=convertInvokable;
    }

    public JInvokable getConvertInvokable() {
        return convertInvokable;
    }
}
