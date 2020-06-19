package net.vpc.hadralang.compiler.parser.ast;

public enum HNNodeId {
    H_DECLARE_META_PACKAGE,
    H_DECLARE_TYPE,
    H_DECLARE_INVOKABLE,
    H_DECLARE_IDENTIFIER,
    H_DECLARE_TOKEN_IDENTIFIER,
    H_DECLARE_TOKEN_TUPLE,
    H_DECLARE_TOKEN_LIST,
    H_CONTINUE,
    H_BLOCK,
    H_WHILE,
    H_IF,
    H_SWITCH,
    H_TRY_CATCH,
    H_CATCH,
    H_CATCH_EXPR,
    H_FOR,
    H_RETURN,
    H_TYPE_TOKEN,
//    H_FIELD,
    H_OP_BINARY,
    H_OP_UNARY,
    H_BREAK,
    H_LITERAL_DEFAULT,
    H_ARRAY_NEW,
    H_OBJECT_NEW,
    H_THIS,
    H_SUPER,
    H_META_IMPORT_PACKAGE,
    H_EXTENDS,
    H_ASSIGN,
    H_BRACES,
    H_BRACKETS,
    H_BRACKETS_POSTFIX,
    H_OP_DOT,
    H_IDENTIFIER,
    H_LITERAL,
    H_PARS,
    H_OP_COALESCE,
    H_APPLY_CAST_OPERATOR,
//    H_INVOKE_METHOD_UNCHECKED,
//    H_FIELD_UNCHECKED,
    H_RANGE,
    H_PARS_POSTFIX,
    H_LAMBDA_EXPR,
    H_TUPLE,
    H_BRACKETS_POSTFIX_LAST,
    H_CAST,
    H_IS,
    H_DOT_CLASS,
    H_DOT_THIS,
    H_LITERAL_SUPERSCRIPT,
    H_IMPORT,
    H_META_PACKAGE_ID,
    H_RAW,
    H_SWITCH_CASE,
    H_SWITCH_IF,
    H_SWITCH_IS,
    H_IF_WHEN_DO,
    H_META_PACKAGE_GROUP,
    H_META_PACKAGE_ARTIFACT,
    H_META_PACKAGE_VERSION,
    H_STRING_INTEROP,


    X_INVOKABLE_CALL,
    @Deprecated
    H_ARRAY_CALL,
}
