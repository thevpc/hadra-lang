package net.vpc.hadralang.compiler.parser;

import net.vpc.common.textsource.log.JSourceMessage;

import java.util.ArrayList;
import java.util.List;

public class HLDeclarationOptions {
    boolean acceptFunction = true;
    boolean acceptClass = true;
    boolean acceptEqValue = true;
    boolean acceptInValue = false;
    boolean acceptModifiers = true;
    boolean acceptVar = true;
    boolean acceptVarArg = true;
    boolean acceptMultiVars = false;
    NoTypeNameOption noTypeNameOption = NoTypeNameOption.ERROR;
    boolean noMessages = false;

    List<JSourceMessage> silencedMessages = new ArrayList<>();

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

    public NoTypeNameOption getNoTypeNameOption() {
        return noTypeNameOption;
    }

    public HLDeclarationOptions setNoTypeNameOption(NoTypeNameOption noTypeNameOption) {
        this.noTypeNameOption = noTypeNameOption;
        return this;
    }

    public enum NoTypeNameOption{
        /**
         * when declaration omits type or name,
         * the parsed item is considered as Name
         * and the type is considered null as if itr was
         * declared with 'var' prefix
         */
        NAME,
        /**
         * when declaration omits type or name,
         * the parsed item is considered as Type
         * and the name is considered 'value'
         */
        TYPE,
        /**
         * when declaration omits type or name,
         * consider reporting an error
         * and the name is considered 'value'
         */
        ERROR
    }
}
