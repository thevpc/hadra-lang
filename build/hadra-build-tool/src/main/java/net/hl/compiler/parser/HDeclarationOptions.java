package net.hl.compiler.parser;

import net.thevpc.common.textsource.log.JSourceMessage;
import net.hl.compiler.core.HTokenId;

import java.util.ArrayList;
import java.util.List;

public class HDeclarationOptions {
    boolean acceptDotName = true;
    boolean acceptFunction = true;
    boolean acceptClass = true;
    boolean acceptEqValue = true;
    boolean acceptInValue = false;
    boolean acceptModifiers = true;
    boolean acceptAnnotations = true;
    boolean acceptVar = true;
    boolean acceptVarArg = true;
    boolean acceptMultiVars = false;
    int multiVarSeparator = HTokenId.COMMA;
    NoTypeNameOption noTypeNameOption = NoTypeNameOption.ERROR;
    String defaultVarName = "value";
    boolean noMessages = false;

    List<JSourceMessage> silencedMessages = new ArrayList<>();

    public boolean isAcceptModifiers() {
        return acceptModifiers;
    }

    public HDeclarationOptions setAcceptModifiers(boolean acceptModifiers) {
        this.acceptModifiers = acceptModifiers;
        return this;
    }

    public boolean isAcceptAnnotations() {
        return acceptAnnotations;
    }

    public HDeclarationOptions setAcceptAnnotations(boolean acceptAnnotations) {
        this.acceptAnnotations = acceptAnnotations;
        return this;
    }

    public boolean isNoMessages() {
        return noMessages;
    }

    public HDeclarationOptions setNoMessages(boolean noMessages) {
        this.noMessages = noMessages;
        return this;
    }

    public boolean isAcceptVar() {
        return acceptVar;
    }

    public HDeclarationOptions setAcceptVar(boolean acceptVar) {
        this.acceptVar = acceptVar;
        return this;
    }

    public boolean isAcceptFunction() {
        return acceptFunction;
    }

    public HDeclarationOptions setAcceptFunction(boolean acceptFunction) {
        this.acceptFunction = acceptFunction;
        return this;
    }

    public boolean isAcceptClass() {
        return acceptClass;
    }

    public HDeclarationOptions setAcceptClass(boolean acceptClass) {
        this.acceptClass = acceptClass;
        return this;
    }

    public boolean isAcceptEqValue() {
        return acceptEqValue;
    }

    public HDeclarationOptions setAcceptEqValue(boolean acceptEqValue) {
        this.acceptEqValue = acceptEqValue;
        return this;
    }

    public boolean isAcceptInValue() {
        return acceptInValue;
    }

    public HDeclarationOptions setAcceptInValue(boolean acceptInValue) {
        this.acceptInValue = acceptInValue;
        return this;
    }

    public boolean isAcceptMultiVars() {
        return acceptMultiVars;
    }

    public HDeclarationOptions setAcceptMultiVars(boolean acceptMultiVars) {
        this.acceptMultiVars = acceptMultiVars;
        return this;
    }

    public String getDefaultVarName() {
        return defaultVarName;
    }

    public HDeclarationOptions setDefaultVarName(String defaultVarName) {
        this.defaultVarName = defaultVarName;
        return this;
    }

    public boolean isAcceptVarArg() {
        return acceptVarArg;
    }

    public HDeclarationOptions setAcceptVarArg(boolean acceptVarArg) {
        this.acceptVarArg = acceptVarArg;
        return this;
    }

    public NoTypeNameOption getNoTypeNameOption() {
        return noTypeNameOption;
    }

    public HDeclarationOptions setNoTypeNameOption(NoTypeNameOption noTypeNameOption) {
        this.noTypeNameOption = noTypeNameOption;
        return this;
    }

    public boolean isAcceptDotName() {
        return acceptDotName;
    }

    public HDeclarationOptions setAcceptDotName(boolean acceptDotName) {
        this.acceptDotName = acceptDotName;
        return this;
    }

    public int getMultiVarSeparator() {
        return multiVarSeparator;
    }

    public HDeclarationOptions setMultiVarSeparator(int multiVarSeparator) {
        this.multiVarSeparator = multiVarSeparator;
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
