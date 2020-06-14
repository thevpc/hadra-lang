package net.vpc.hadralang.compiler.parser;

import net.vpc.common.jeep.JCompilerMessage;

import java.util.ArrayList;
import java.util.List;

public class HLDeclarationOptions {
    boolean acceptFunction = true;
    boolean acceptClass = true;
    boolean requiredSemiColumnForVar = false;
    boolean acceptEqValue = true;
    boolean acceptInValue = false;
    boolean acceptModifiers = true;
    boolean acceptVar = true;
    boolean acceptVarArg = true;
    boolean acceptMultiVars = false;
    boolean noMessages = false;
    List<JCompilerMessage> silencedMessages = new ArrayList<>();

    public boolean isAcceptModifiers() {
        return acceptModifiers;
    }

    public HLDeclarationOptions setAcceptModifiers(boolean acceptModifiers) {
        this.acceptModifiers = acceptModifiers;
        return this;
    }

    public boolean isNoMessages() {
        return noMessages;
    }

    public HLDeclarationOptions setNoMessages(boolean noMessages) {
        this.noMessages = noMessages;
        return this;
    }

    public boolean isAcceptVar() {
        return acceptVar;
    }

    public HLDeclarationOptions setAcceptVar(boolean acceptVar) {
        this.acceptVar = acceptVar;
        return this;
    }

    public boolean isAcceptFunction() {
        return acceptFunction;
    }

    public HLDeclarationOptions setAcceptFunction(boolean acceptFunction) {
        this.acceptFunction = acceptFunction;
        return this;
    }

    public boolean isAcceptClass() {
        return acceptClass;
    }

    public HLDeclarationOptions setAcceptClass(boolean acceptClass) {
        this.acceptClass = acceptClass;
        return this;
    }

    public boolean isRequiredSemiColumnForVar() {
        return requiredSemiColumnForVar;
    }

    public HLDeclarationOptions setRequiredSemiColumnForVar(boolean requiredSemiColumnForVar) {
        this.requiredSemiColumnForVar = requiredSemiColumnForVar;
        return this;
    }

    public boolean isAcceptEqValue() {
        return acceptEqValue;
    }

    public HLDeclarationOptions setAcceptEqValue(boolean acceptEqValue) {
        this.acceptEqValue = acceptEqValue;
        return this;
    }

    public boolean isAcceptInValue() {
        return acceptInValue;
    }

    public HLDeclarationOptions setAcceptInValue(boolean acceptInValue) {
        this.acceptInValue = acceptInValue;
        return this;
    }

    public boolean isAcceptMultiVars() {
        return acceptMultiVars;
    }

    public HLDeclarationOptions setAcceptMultiVars(boolean acceptMultiVars) {
        this.acceptMultiVars = acceptMultiVars;
        return this;
    }

    public boolean isAcceptVarArg() {
        return acceptVarArg;
    }

    public HLDeclarationOptions setAcceptVarArg(boolean acceptVarArg) {
        this.acceptVarArg = acceptVarArg;
        return this;
    }
}
