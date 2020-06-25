package net.hl.compiler.parser;

import net.vpc.common.jeep.JTokenEvaluator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class HNumberEvaluator implements JTokenEvaluator {
    public static final JTokenEvaluator H_NUMBER=new HNumberEvaluator();
    @Override
    public Object eval(int id, String image, String cleanImage, String typeName) {
        if (image.indexOf('.') >= 0 || image.indexOf('e') >= 0 || image.indexOf('E') >= 0) {
            switch (cleanImage.charAt(cleanImage.length() - 1)) {
                case 'f':
                case 'F': {
                    return Float.parseFloat(cleanImage.substring(0, cleanImage.length() - 1));
                }
                case 'd':{
                    return new BigDecimal(cleanImage.substring(0, cleanImage.length() - 1), MathContext.DECIMAL64);
                }
                case 'D': {
                    return new BigDecimal(cleanImage.substring(0, cleanImage.length() - 1), MathContext.UNLIMITED);
                }
            }
            return Double.parseDouble(cleanImage);
        } else {
            int radix;
            if (cleanImage.startsWith("0b")) {
                radix = 2;
            } else if (cleanImage.startsWith("0x")) {
                radix = 16;
            } else if (cleanImage.startsWith("0")) {
                radix = 8;
            } else {
                radix = 10;
            }
            switch (cleanImage.charAt(cleanImage.length() - 1)) {
                case 'b': {
                    return Byte.parseByte(cleanImage.substring(0, cleanImage.length() - 1), radix);
                }
                case 's':
                case 'S': {
                    return Short.parseShort(cleanImage.substring(0, cleanImage.length() - 1), radix);
                }
                case 'l':
                case 'L': {
                    return Long.parseLong(cleanImage.substring(0, cleanImage.length() - 1), radix);
                }
                case 'B': {
                    return new BigInteger(cleanImage.substring(0, cleanImage.length() - 1), radix);
                }
            }
            return Integer.parseInt(cleanImage, radix);
        }
    }
}
