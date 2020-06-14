package net.vpc.hadralang.compiler.utils;

public class HLExtensionNames {

    /**
     * a+b
     */
    public static final String PLUS_SHORT ="+";
    public static final String PLUS_LONG ="plus";

    /**
     * =name[arguments...]
     */
    public static final String BRACKET_GET_SHORT ="[]";
    public static final String BRACKET_GET_LONG ="getAt";
    /**
     * name[arguments...]=
     */
    public static final String BRACKET_SET_SHORT ="[]=";
    public static final String BRACKET_SET_LONG ="setAt";

    public static final String NEW_RANGE_EE_SHORT ="<..<";
    public static final String NEW_RANGE_EE_LONG ="newRangeEE";
    public static final String NEW_RANGE_EI_SHORT ="<..";
    public static final String NEW_RANGE_EI_LONG ="newRangeEI";
    public static final String NEW_RANGE_II_SHORT ="..";
    public static final String NEW_RANGE_II_LONG ="newRangeII";

    public static final String NEW_RANGE_IE_SHORT ="..<";
    public static final String NEW_RANGE_IE_LONG ="newRangeIE";
    public static final String FUNCTION_APPLY = "apply";
}
