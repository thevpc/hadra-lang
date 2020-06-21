package net.vpc.hadralang.compiler.utils;

import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JTokenUtils;

public class HTokenUtils {
    public static JToken createToken(String image) {
        switch (image) {
            case "null":
            case "void":
            case "int":
            case "long":
            case "float":
            case "byte":
            case "short":
            case "char":
            case "boolean":
            case "double": {
                return JTokenUtils.createKeywordToken(image);
            }
            case "=":
            case ":":
            case "^":
            case "*":
            case "+":
            case ".": {
                return JTokenUtils.createOpToken(image);
            }
        }
        return JTokenUtils.createWordToken(image);
    }
}
