/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.editor.hl4nb.editor.lexer;

import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author vpc
 */
public class HLnbToken implements TokenId {

    private int ordinal;
    private String cat;
    private String name;

    public HLnbToken(int ordinal, String cat, String name) {
        this.ordinal = ordinal;
        this.cat = cat;
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String primaryCategory() {
        return cat;
    }

    @Override
    public String toString() {
        return "HLnbToken{" + "ordinal=" + ordinal + ", cat=" + cat + ", name=" + name + '}';
    }
    

}
