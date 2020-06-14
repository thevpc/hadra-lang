/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.editor.hl4nb.editor.lexer;

import net.vpc.common.jeep.JTokenizerReader;
import org.netbeans.spi.lexer.LexerInput;

/**
 *
 * @author vpc
 */
public class HLnbLexerInputJTokenizerReader implements JTokenizerReader {

    private final LexerInput input;

    public HLnbLexerInputJTokenizerReader(LexerInput input) {
        this.input = input;
    }

    @Override
    public int read() {
        return input.read();
    }

    @Override
    public void unread(char c) {
        input.backup(1);
    }

    @Override
    public void unread(char[] c) {
        input.backup(c.length);
    }
}
