/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.utils;

import net.hl.lang.IntToIntFunction;
import net.thevpc.nuts.NConstants;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.text.NTextStyle;

/**
 *
 * @author vpc
 */
public class StringUtils {

    public static String center(String msg, int length, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        boolean left = false;
        while (sb.length() < length) {
            if (left) {
                sb.insert(0, c);
            } else {
                sb.append(c);
            }
            left = !left;
        }
        return sb.toString();
    }

    public static String left(String msg, int length, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        while (sb.length() < length) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String right(String msg, int length) {
        return right(msg, length, ' ');
    }

    public static String left(String msg, int length) {
        return left(msg, length, ' ');
    }

    public static String right(String msg, int length, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        while (sb.length() < length) {
            sb.insert(0, c);
        }
        return sb.toString();
    }

    public static String center2(String msg, int length, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        NTexts f = NTexts.of();
        int len0 = f.of(sb.toString()).length();
        int variant = 2;
        IntToIntFunction color
                = variant == 1 ? (i -> 30 + (i % 40) / 2)
                : variant == 2 ? (i -> 236 + (i) / 2)
                        : (i -> 20 + (i / 2));
        int cc = 0;
        while (len0 < length) {
            int z = color.applyAsInt(cc);
            String cc2 = f.ofStyled(String.valueOf(c), NTextStyle.foregroundColor(z)).toString() + NConstants.Ntf.SILENT;
            if (cc % 2 == 0) {
                sb.insert(0, cc2);
            } else {
                sb.append(cc2).append(NConstants.Ntf.SILENT);
            }
            len0++;
            cc++;
        }
        return sb.toString();
    }
}
