package net.hl.compiler.core;

public final class HTokenId {
    private HTokenId() {
    }

    /**
     * End of File (no token to read)
     * <pre>
     * ID         : EOF
     * ID_NAME    : EOF
     * TYPE_ID    : EOF
     * TYPE_NAME  : EOF
     * STATE_ID   : <ANY>
     * STATE_NAME : N/A
     * LAYOUT     : <ANY>
     * </pre>
     */
    public static final int EOF = -1;

    /**
     * Token Id for LINE_COMMENTS
     * <pre>
     * ID         : 3
     * ID_NAME    : LINE_COMMENTS
     * TYPE_ID    : -9
     * TYPE_NAME  : TT_LINE_COMMENTS
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : //
     * </pre>
     */
    public static final int LINE_COMMENTS = 3;

    /**
     * Token Id for BLOCK_COMMENTS
     * <pre>
     * ID         : 4
     * ID_NAME    : BLOCK_COMMENTS
     * TYPE_ID    : -10
     * TYPE_NAME  : TT_BLOCK_COMMENTS
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : /**&#47;
     * </pre>
     */
    public static final int BLOCK_COMMENTS = 4;

    /**
     * Token Id for SIMPLE_QUOTES
     * <pre>
     * ID         : 31
     * ID_NAME    : SIMPLE_QUOTES
     * TYPE_ID    : -3
     * TYPE_NAME  : TT_STRING
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : '..'
     * </pre>
     */
    public static final int SIMPLE_QUOTES = 31;

    /**
     * Token Id for DOUBLE_QUOTES
     * <pre>
     * ID         : 33
     * ID_NAME    : DOUBLE_QUOTES
     * TYPE_ID    : -3
     * TYPE_NAME  : TT_STRING
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ".."
     * </pre>
     */
    public static final int DOUBLE_QUOTES = 33;

    /**
     * Token Id for TEMPORAL
     * <pre>
     * ID         : 37
     * ID_NAME    : TEMPORAL
     * TYPE_ID    : -15
     * TYPE_NAME  : TT_TEMPORAL
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : t"yyyy-MM-dd"
     * </pre>
     */
    public static final int TEMPORAL = 37;

    /**
     * Token Id for REGEX
     * <pre>
     * ID         : 39
     * ID_NAME    : REGEX
     * TYPE_ID    : -16
     * TYPE_NAME  : TT_REGEX
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : <regexp>
     * </pre>
     */
    public static final int REGEX = 39;

    /**
     * Token Id for STRING_INTERP_START
     * <pre>
     * ID         : 10035
     * ID_NAME    : STRING_INTERP_START
     * TYPE_ID    : -17
     * TYPE_NAME  : TT_STRING_INTERP
     * STATE_ID   : 1
     * STATE_NAME : STATE_DEFAULT
     * LAYOUT     : $"
     * </pre>
     */
    public static final int STRING_INTERP_START = 10035;

    /**
     * Token Id for NUMBER_INT
     * <pre>
     * ID         : 21
     * ID_NAME    : NUMBER_INT
     * TYPE_ID    : -4
     * TYPE_NAME  : TT_NUMBER
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : [0-9]+
     * </pre>
     */
    public static final int NUMBER_INT = 21;

    /**
     * Token Id for NUMBER_FLOAT
     * <pre>
     * ID         : 22
     * ID_NAME    : NUMBER_FLOAT
     * TYPE_ID    : -4
     * TYPE_NAME  : TT_NUMBER
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : [0-9]+[.][0-9]+
     * </pre>
     */
    public static final int NUMBER_FLOAT = 22;

    /**
     * Token Id for NUMBER_INFINITY
     * <pre>
     * ID         : 23
     * ID_NAME    : NUMBER_INFINITY
     * TYPE_ID    : -4
     * TYPE_NAME  : TT_NUMBER
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∞
     * </pre>
     */
    public static final int NUMBER_INFINITY = 23;

    /**
     * Token Id for KEYWORD__
     * <pre>
     * ID         : 500
     * ID_NAME    : KEYWORD__
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : _
     * </pre>
     */
    public static final int KEYWORD__ = 500;

    /**
     * Token Id for KEYWORD_ABSTRACT
     * <pre>
     * ID         : 501
     * ID_NAME    : KEYWORD_ABSTRACT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : abstract
     * </pre>
     */
    public static final int KEYWORD_ABSTRACT = 501;

    /**
     * Token Id for KEYWORD_BIGDECIMAL
     * <pre>
     * ID         : 502
     * ID_NAME    : KEYWORD_BIGDECIMAL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : bigdecimal
     * </pre>
     */
    public static final int KEYWORD_BIGDECIMAL = 502;

    /**
     * Token Id for KEYWORD_BIGINT
     * <pre>
     * ID         : 503
     * ID_NAME    : KEYWORD_BIGINT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : bigint
     * </pre>
     */
    public static final int KEYWORD_BIGINT = 503;

    /**
     * Token Id for KEYWORD_BOOL
     * <pre>
     * ID         : 504
     * ID_NAME    : KEYWORD_BOOL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : bool
     * </pre>
     */
    public static final int KEYWORD_BOOL = 504;

    /**
     * Token Id for KEYWORD_BOOLEAN
     * <pre>
     * ID         : 505
     * ID_NAME    : KEYWORD_BOOLEAN
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : boolean
     * </pre>
     */
    public static final int KEYWORD_BOOLEAN = 505;

    /**
     * Token Id for KEYWORD_BREAK
     * <pre>
     * ID         : 506
     * ID_NAME    : KEYWORD_BREAK
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : break
     * </pre>
     */
    public static final int KEYWORD_BREAK = 506;

    /**
     * Token Id for KEYWORD_BYTE
     * <pre>
     * ID         : 507
     * ID_NAME    : KEYWORD_BYTE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : byte
     * </pre>
     */
    public static final int KEYWORD_BYTE = 507;

    /**
     * Token Id for KEYWORD_CASE
     * <pre>
     * ID         : 508
     * ID_NAME    : KEYWORD_CASE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : case
     * </pre>
     */
    public static final int KEYWORD_CASE = 508;

    /**
     * Token Id for KEYWORD_CATCH
     * <pre>
     * ID         : 509
     * ID_NAME    : KEYWORD_CATCH
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : catch
     * </pre>
     */
    public static final int KEYWORD_CATCH = 509;

    /**
     * Token Id for KEYWORD_CHAR
     * <pre>
     * ID         : 510
     * ID_NAME    : KEYWORD_CHAR
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : char
     * </pre>
     */
    public static final int KEYWORD_CHAR = 510;

    /**
     * Token Id for KEYWORD_CLASS
     * <pre>
     * ID         : 511
     * ID_NAME    : KEYWORD_CLASS
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : class
     * </pre>
     */
    public static final int KEYWORD_CLASS = 511;

    /**
     * Token Id for KEYWORD_CONST
     * <pre>
     * ID         : 512
     * ID_NAME    : KEYWORD_CONST
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : const
     * </pre>
     */
    public static final int KEYWORD_CONST = 512;

    /**
     * Token Id for KEYWORD_CONSTRUCTOR
     * <pre>
     * ID         : 513
     * ID_NAME    : KEYWORD_CONSTRUCTOR
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : constructor
     * </pre>
     */
    public static final int KEYWORD_CONSTRUCTOR = 513;

    /**
     * Token Id for KEYWORD_CONTINUE
     * <pre>
     * ID         : 514
     * ID_NAME    : KEYWORD_CONTINUE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : continue
     * </pre>
     */
    public static final int KEYWORD_CONTINUE = 514;

    /**
     * Token Id for KEYWORD_DATE
     * <pre>
     * ID         : 515
     * ID_NAME    : KEYWORD_DATE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : date
     * </pre>
     */
    public static final int KEYWORD_DATE = 515;

    /**
     * Token Id for KEYWORD_DATETIME
     * <pre>
     * ID         : 516
     * ID_NAME    : KEYWORD_DATETIME
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : datetime
     * </pre>
     */
    public static final int KEYWORD_DATETIME = 516;

    /**
     * Token Id for KEYWORD_DECIMAL
     * <pre>
     * ID         : 517
     * ID_NAME    : KEYWORD_DECIMAL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : decimal
     * </pre>
     */
    public static final int KEYWORD_DECIMAL = 517;

    /**
     * Token Id for KEYWORD_DEF
     * <pre>
     * ID         : 518
     * ID_NAME    : KEYWORD_DEF
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : def
     * </pre>
     */
    public static final int KEYWORD_DEF = 518;

    /**
     * Token Id for KEYWORD_DEFAULT
     * <pre>
     * ID         : 519
     * ID_NAME    : KEYWORD_DEFAULT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : default
     * </pre>
     */
    public static final int KEYWORD_DEFAULT = 519;

    /**
     * Token Id for KEYWORD_DO
     * <pre>
     * ID         : 520
     * ID_NAME    : KEYWORD_DO
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : do
     * </pre>
     */
    public static final int KEYWORD_DO = 520;

    /**
     * Token Id for KEYWORD_DOUBLE
     * <pre>
     * ID         : 521
     * ID_NAME    : KEYWORD_DOUBLE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : double
     * </pre>
     */
    public static final int KEYWORD_DOUBLE = 521;

    /**
     * Token Id for KEYWORD_ELSE
     * <pre>
     * ID         : 522
     * ID_NAME    : KEYWORD_ELSE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : else
     * </pre>
     */
    public static final int KEYWORD_ELSE = 522;

    /**
     * Token Id for KEYWORD_EXTENDS
     * <pre>
     * ID         : 523
     * ID_NAME    : KEYWORD_EXTENDS
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : extends
     * </pre>
     */
    public static final int KEYWORD_EXTENDS = 523;

    /**
     * Token Id for KEYWORD_FALSE
     * <pre>
     * ID         : 524
     * ID_NAME    : KEYWORD_FALSE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : false
     * </pre>
     */
    public static final int KEYWORD_FALSE = 524;

    /**
     * Token Id for KEYWORD_FINAL
     * <pre>
     * ID         : 525
     * ID_NAME    : KEYWORD_FINAL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : final
     * </pre>
     */
    public static final int KEYWORD_FINAL = 525;

    /**
     * Token Id for KEYWORD_FINALLY
     * <pre>
     * ID         : 526
     * ID_NAME    : KEYWORD_FINALLY
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : finally
     * </pre>
     */
    public static final int KEYWORD_FINALLY = 526;

    /**
     * Token Id for KEYWORD_FLOAT
     * <pre>
     * ID         : 527
     * ID_NAME    : KEYWORD_FLOAT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : float
     * </pre>
     */
    public static final int KEYWORD_FLOAT = 527;

    /**
     * Token Id for KEYWORD_FOR
     * <pre>
     * ID         : 528
     * ID_NAME    : KEYWORD_FOR
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : for
     * </pre>
     */
    public static final int KEYWORD_FOR = 528;

    /**
     * Token Id for KEYWORD_FUN
     * <pre>
     * ID         : 529
     * ID_NAME    : KEYWORD_FUN
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : fun
     * </pre>
     */
    public static final int KEYWORD_FUN = 529;

    /**
     * Token Id for KEYWORD_IF
     * <pre>
     * ID         : 530
     * ID_NAME    : KEYWORD_IF
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : if
     * </pre>
     */
    public static final int KEYWORD_IF = 530;

    /**
     * Token Id for KEYWORD_IMPLICIT
     * <pre>
     * ID         : 531
     * ID_NAME    : KEYWORD_IMPLICIT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : implicit
     * </pre>
     */
    public static final int KEYWORD_IMPLICIT = 531;

    /**
     * Token Id for KEYWORD_IMPORT
     * <pre>
     * ID         : 532
     * ID_NAME    : KEYWORD_IMPORT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : import
     * </pre>
     */
    public static final int KEYWORD_IMPORT = 532;

    /**
     * Token Id for KEYWORD_INIT
     * <pre>
     * ID         : 533
     * ID_NAME    : KEYWORD_INIT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : init
     * </pre>
     */
    public static final int KEYWORD_INIT = 533;

    /**
     * Token Id for KEYWORD_INSTANCEOF
     * <pre>
     * ID         : 534
     * ID_NAME    : KEYWORD_INSTANCEOF
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : instanceof
     * </pre>
     */
    public static final int KEYWORD_INSTANCEOF = 534;

    /**
     * Token Id for KEYWORD_INT
     * <pre>
     * ID         : 535
     * ID_NAME    : KEYWORD_INT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int
     * </pre>
     */
    public static final int KEYWORD_INT = 535;

    /**
     * Token Id for KEYWORD_INT128
     * <pre>
     * ID         : 536
     * ID_NAME    : KEYWORD_INT128
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int128
     * </pre>
     */
    public static final int KEYWORD_INT128 = 536;

    /**
     * Token Id for KEYWORD_INT16
     * <pre>
     * ID         : 537
     * ID_NAME    : KEYWORD_INT16
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int16
     * </pre>
     */
    public static final int KEYWORD_INT16 = 537;

    /**
     * Token Id for KEYWORD_INT32
     * <pre>
     * ID         : 538
     * ID_NAME    : KEYWORD_INT32
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int32
     * </pre>
     */
    public static final int KEYWORD_INT32 = 538;

    /**
     * Token Id for KEYWORD_INT64
     * <pre>
     * ID         : 539
     * ID_NAME    : KEYWORD_INT64
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int64
     * </pre>
     */
    public static final int KEYWORD_INT64 = 539;

    /**
     * Token Id for KEYWORD_INT8
     * <pre>
     * ID         : 540
     * ID_NAME    : KEYWORD_INT8
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : int8
     * </pre>
     */
    public static final int KEYWORD_INT8 = 540;

    /**
     * Token Id for KEYWORD_INTERFACE
     * <pre>
     * ID         : 541
     * ID_NAME    : KEYWORD_INTERFACE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : interface
     * </pre>
     */
    public static final int KEYWORD_INTERFACE = 541;

    /**
     * Token Id for KEYWORD_IS
     * <pre>
     * ID         : 542
     * ID_NAME    : KEYWORD_IS
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : is
     * </pre>
     */
    public static final int KEYWORD_IS = 542;

    /**
     * Token Id for KEYWORD_IT
     * <pre>
     * ID         : 543
     * ID_NAME    : KEYWORD_IT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : it
     * </pre>
     */
    public static final int KEYWORD_IT = 543;

    /**
     * Token Id for KEYWORD_LONG
     * <pre>
     * ID         : 544
     * ID_NAME    : KEYWORD_LONG
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : long
     * </pre>
     */
    public static final int KEYWORD_LONG = 544;

    /**
     * Token Id for KEYWORD_NATIVE
     * <pre>
     * ID         : 545
     * ID_NAME    : KEYWORD_NATIVE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : native
     * </pre>
     */
    public static final int KEYWORD_NATIVE = 545;

    /**
     * Token Id for KEYWORD_NULL
     * <pre>
     * ID         : 546
     * ID_NAME    : KEYWORD_NULL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : null
     * </pre>
     */
    public static final int KEYWORD_NULL = 546;

    /**
     * Token Id for KEYWORD_OBJECT
     * <pre>
     * ID         : 547
     * ID_NAME    : KEYWORD_OBJECT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : object
     * </pre>
     */
    public static final int KEYWORD_OBJECT = 547;

    /**
     * Token Id for KEYWORD_OPERATOR
     * <pre>
     * ID         : 548
     * ID_NAME    : KEYWORD_OPERATOR
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : operator
     * </pre>
     */
    public static final int KEYWORD_OPERATOR = 548;

    /**
     * Token Id for KEYWORD_PACKAGE
     * <pre>
     * ID         : 549
     * ID_NAME    : KEYWORD_PACKAGE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : package
     * </pre>
     */
    public static final int KEYWORD_PACKAGE = 549;

    /**
     * Token Id for KEYWORD_PRIVATE
     * <pre>
     * ID         : 550
     * ID_NAME    : KEYWORD_PRIVATE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : private
     * </pre>
     */
    public static final int KEYWORD_PRIVATE = 550;

    /**
     * Token Id for KEYWORD_PROTECTED
     * <pre>
     * ID         : 551
     * ID_NAME    : KEYWORD_PROTECTED
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : protected
     * </pre>
     */
    public static final int KEYWORD_PROTECTED = 551;

    /**
     * Token Id for KEYWORD_PUBLIC
     * <pre>
     * ID         : 552
     * ID_NAME    : KEYWORD_PUBLIC
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : public
     * </pre>
     */
    public static final int KEYWORD_PUBLIC = 552;

    /**
     * Token Id for KEYWORD_READONLY
     * <pre>
     * ID         : 553
     * ID_NAME    : KEYWORD_READONLY
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : readonly
     * </pre>
     */
    public static final int KEYWORD_READONLY = 553;

    /**
     * Token Id for KEYWORD_RECORD
     * <pre>
     * ID         : 554
     * ID_NAME    : KEYWORD_RECORD
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : record
     * </pre>
     */
    public static final int KEYWORD_RECORD = 554;

    /**
     * Token Id for KEYWORD_REF
     * <pre>
     * ID         : 555
     * ID_NAME    : KEYWORD_REF
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ref
     * </pre>
     */
    public static final int KEYWORD_REF = 555;

    /**
     * Token Id for KEYWORD_RETURN
     * <pre>
     * ID         : 556
     * ID_NAME    : KEYWORD_RETURN
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : return
     * </pre>
     */
    public static final int KEYWORD_RETURN = 556;

    /**
     * Token Id for KEYWORD_SHORT
     * <pre>
     * ID         : 557
     * ID_NAME    : KEYWORD_SHORT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : short
     * </pre>
     */
    public static final int KEYWORD_SHORT = 557;

    /**
     * Token Id for KEYWORD_STATIC
     * <pre>
     * ID         : 558
     * ID_NAME    : KEYWORD_STATIC
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : static
     * </pre>
     */
    public static final int KEYWORD_STATIC = 558;

    /**
     * Token Id for KEYWORD_STRICTFP
     * <pre>
     * ID         : 559
     * ID_NAME    : KEYWORD_STRICTFP
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : strictfp
     * </pre>
     */
    public static final int KEYWORD_STRICTFP = 559;

    /**
     * Token Id for KEYWORD_STRING
     * <pre>
     * ID         : 560
     * ID_NAME    : KEYWORD_STRING
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : string
     * </pre>
     */
    public static final int KEYWORD_STRING = 560;

    /**
     * Token Id for KEYWORD_STRUCT
     * <pre>
     * ID         : 561
     * ID_NAME    : KEYWORD_STRUCT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : struct
     * </pre>
     */
    public static final int KEYWORD_STRUCT = 561;

    /**
     * Token Id for KEYWORD_SUPER
     * <pre>
     * ID         : 562
     * ID_NAME    : KEYWORD_SUPER
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : super
     * </pre>
     */
    public static final int KEYWORD_SUPER = 562;

    /**
     * Token Id for KEYWORD_SWITCH
     * <pre>
     * ID         : 563
     * ID_NAME    : KEYWORD_SWITCH
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : switch
     * </pre>
     */
    public static final int KEYWORD_SWITCH = 563;

    /**
     * Token Id for KEYWORD_SYNCHRONIZED
     * <pre>
     * ID         : 564
     * ID_NAME    : KEYWORD_SYNCHRONIZED
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : synchronized
     * </pre>
     */
    public static final int KEYWORD_SYNCHRONIZED = 564;

    /**
     * Token Id for KEYWORD_THIS
     * <pre>
     * ID         : 565
     * ID_NAME    : KEYWORD_THIS
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : this
     * </pre>
     */
    public static final int KEYWORD_THIS = 565;

    /**
     * Token Id for KEYWORD_TIME
     * <pre>
     * ID         : 566
     * ID_NAME    : KEYWORD_TIME
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : time
     * </pre>
     */
    public static final int KEYWORD_TIME = 566;

    /**
     * Token Id for KEYWORD_TRANSIENT
     * <pre>
     * ID         : 567
     * ID_NAME    : KEYWORD_TRANSIENT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : transient
     * </pre>
     */
    public static final int KEYWORD_TRANSIENT = 567;

    /**
     * Token Id for KEYWORD_TRUE
     * <pre>
     * ID         : 568
     * ID_NAME    : KEYWORD_TRUE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : true
     * </pre>
     */
    public static final int KEYWORD_TRUE = 568;

    /**
     * Token Id for KEYWORD_TRY
     * <pre>
     * ID         : 569
     * ID_NAME    : KEYWORD_TRY
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : try
     * </pre>
     */
    public static final int KEYWORD_TRY = 569;

    /**
     * Token Id for KEYWORD_UINT
     * <pre>
     * ID         : 570
     * ID_NAME    : KEYWORD_UINT
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint
     * </pre>
     */
    public static final int KEYWORD_UINT = 570;

    /**
     * Token Id for KEYWORD_UINT128
     * <pre>
     * ID         : 571
     * ID_NAME    : KEYWORD_UINT128
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint128
     * </pre>
     */
    public static final int KEYWORD_UINT128 = 571;

    /**
     * Token Id for KEYWORD_UINT16
     * <pre>
     * ID         : 572
     * ID_NAME    : KEYWORD_UINT16
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint16
     * </pre>
     */
    public static final int KEYWORD_UINT16 = 572;

    /**
     * Token Id for KEYWORD_UINT32
     * <pre>
     * ID         : 573
     * ID_NAME    : KEYWORD_UINT32
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint32
     * </pre>
     */
    public static final int KEYWORD_UINT32 = 573;

    /**
     * Token Id for KEYWORD_UINT64
     * <pre>
     * ID         : 574
     * ID_NAME    : KEYWORD_UINT64
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint64
     * </pre>
     */
    public static final int KEYWORD_UINT64 = 574;

    /**
     * Token Id for KEYWORD_UINT8
     * <pre>
     * ID         : 575
     * ID_NAME    : KEYWORD_UINT8
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : uint8
     * </pre>
     */
    public static final int KEYWORD_UINT8 = 575;

    /**
     * Token Id for KEYWORD_ULONG
     * <pre>
     * ID         : 576
     * ID_NAME    : KEYWORD_ULONG
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ulong
     * </pre>
     */
    public static final int KEYWORD_ULONG = 576;

    /**
     * Token Id for KEYWORD_UNSAFE
     * <pre>
     * ID         : 577
     * ID_NAME    : KEYWORD_UNSAFE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : unsafe
     * </pre>
     */
    public static final int KEYWORD_UNSAFE = 577;

    /**
     * Token Id for KEYWORD_VAL
     * <pre>
     * ID         : 578
     * ID_NAME    : KEYWORD_VAL
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : val
     * </pre>
     */
    public static final int KEYWORD_VAL = 578;

    /**
     * Token Id for KEYWORD_VAR
     * <pre>
     * ID         : 579
     * ID_NAME    : KEYWORD_VAR
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : var
     * </pre>
     */
    public static final int KEYWORD_VAR = 579;

    /**
     * Token Id for KEYWORD_VOID
     * <pre>
     * ID         : 580
     * ID_NAME    : KEYWORD_VOID
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : void
     * </pre>
     */
    public static final int KEYWORD_VOID = 580;

    /**
     * Token Id for KEYWORD_VOLATILE
     * <pre>
     * ID         : 581
     * ID_NAME    : KEYWORD_VOLATILE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : volatile
     * </pre>
     */
    public static final int KEYWORD_VOLATILE = 581;

    /**
     * Token Id for KEYWORD_WHILE
     * <pre>
     * ID         : 582
     * ID_NAME    : KEYWORD_WHILE
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : while
     * </pre>
     */
    public static final int KEYWORD_WHILE = 582;

    /**
     * Token Id for KEYWORD_YIELD
     * <pre>
     * ID         : 583
     * ID_NAME    : KEYWORD_YIELD
     * TYPE_ID    : -12
     * TYPE_NAME  : TT_KEYWORD
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : yield
     * </pre>
     */
    public static final int KEYWORD_YIELD = 583;

    /**
     * Token Id for COMMA
     * <pre>
     * ID         : 90
     * ID_NAME    : COMMA
     * TYPE_ID    : -13
     * TYPE_NAME  : TT_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : ,
     * </pre>
     */
    public static final int COMMA = 90;

    /**
     * Token Id for MINUS_GT
     * <pre>
     * ID         : 91
     * ID_NAME    : MINUS_GT
     * TYPE_ID    : -13
     * TYPE_NAME  : TT_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : ->
     * </pre>
     */
    public static final int MINUS_GT = 91;

    /**
     * Token Id for COLON
     * <pre>
     * ID         : 92
     * ID_NAME    : COLON
     * TYPE_ID    : -13
     * TYPE_NAME  : TT_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : :
     * </pre>
     */
    public static final int COLON = 92;

    /**
     * Token Id for SEMICOLON
     * <pre>
     * ID         : 93
     * ID_NAME    : SEMICOLON
     * TYPE_ID    : -13
     * TYPE_NAME  : TT_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : ;
     * </pre>
     */
    public static final int SEMICOLON = 93;

    /**
     * Token Id for EXCLAMATION
     * <pre>
     * ID         : 100
     * ID_NAME    : EXCLAMATION
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : !
     * </pre>
     */
    public static final int EXCLAMATION = 100;

    /**
     * Token Id for EXCLAMATION_EQ
     * <pre>
     * ID         : 101
     * ID_NAME    : EXCLAMATION_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : !=
     * </pre>
     */
    public static final int EXCLAMATION_EQ = 101;

    /**
     * Token Id for EXCLAMATION_EQ2
     * <pre>
     * ID         : 102
     * ID_NAME    : EXCLAMATION_EQ2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : !==
     * </pre>
     */
    public static final int EXCLAMATION_EQ2 = 102;

    /**
     * Token Id for PERCENT
     * <pre>
     * ID         : 103
     * ID_NAME    : PERCENT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : %
     * </pre>
     */
    public static final int PERCENT = 103;

    /**
     * Token Id for PERCENT_EQ
     * <pre>
     * ID         : 104
     * ID_NAME    : PERCENT_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : %=
     * </pre>
     */
    public static final int PERCENT_EQ = 104;

    /**
     * Token Id for AMPERSAND
     * <pre>
     * ID         : 105
     * ID_NAME    : AMPERSAND
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : &
     * </pre>
     */
    public static final int AMPERSAND = 105;

    /**
     * Token Id for AMPERSAND2
     * <pre>
     * ID         : 106
     * ID_NAME    : AMPERSAND2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : &&
     * </pre>
     */
    public static final int AMPERSAND2 = 106;

    /**
     * Token Id for AMPERSAND_EQ
     * <pre>
     * ID         : 107
     * ID_NAME    : AMPERSAND_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : &=
     * </pre>
     */
    public static final int AMPERSAND_EQ = 107;

    /**
     * Token Id for ASTERISK
     * <pre>
     * ID         : 108
     * ID_NAME    : ASTERISK
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : *
     * </pre>
     */
    public static final int ASTERISK = 108;

    /**
     * Token Id for ASTERISK2
     * <pre>
     * ID         : 109
     * ID_NAME    : ASTERISK2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : **
     * </pre>
     */
    public static final int ASTERISK2 = 109;

    /**
     * Token Id for ASTERISK3
     * <pre>
     * ID         : 110
     * ID_NAME    : ASTERISK3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ***
     * </pre>
     */
    public static final int ASTERISK3 = 110;

    /**
     * Token Id for ASTERISK_EQ
     * <pre>
     * ID         : 111
     * ID_NAME    : ASTERISK_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : *=
     * </pre>
     */
    public static final int ASTERISK_EQ = 111;

    /**
     * Token Id for PLUS
     * <pre>
     * ID         : 112
     * ID_NAME    : PLUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : +
     * </pre>
     */
    public static final int PLUS = 112;

    /**
     * Token Id for PLUS2
     * <pre>
     * ID         : 113
     * ID_NAME    : PLUS2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ++
     * </pre>
     */
    public static final int PLUS2 = 113;

    /**
     * Token Id for PLUS_EQ
     * <pre>
     * ID         : 114
     * ID_NAME    : PLUS_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : +=
     * </pre>
     */
    public static final int PLUS_EQ = 114;

    /**
     * Token Id for MINUS
     * <pre>
     * ID         : 115
     * ID_NAME    : MINUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : -
     * </pre>
     */
    public static final int MINUS = 115;

    /**
     * Token Id for MINUS2
     * <pre>
     * ID         : 116
     * ID_NAME    : MINUS2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : --
     * </pre>
     */
    public static final int MINUS2 = 116;

    /**
     * Token Id for MINUS_EQ
     * <pre>
     * ID         : 117
     * ID_NAME    : MINUS_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : -=
     * </pre>
     */
    public static final int MINUS_EQ = 117;

    /**
     * Token Id for DOT
     * <pre>
     * ID         : 118
     * ID_NAME    : DOT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : .
     * </pre>
     */
    public static final int DOT = 118;

    /**
     * Token Id for DOT2
     * <pre>
     * ID         : 119
     * ID_NAME    : DOT2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ..
     * </pre>
     */
    public static final int DOT2 = 119;

    /**
     * Token Id for DOT3
     * <pre>
     * ID         : 120
     * ID_NAME    : DOT3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ...
     * </pre>
     */
    public static final int DOT3 = 120;

    /**
     * Token Id for DOT2_LT
     * <pre>
     * ID         : 121
     * ID_NAME    : DOT2_LT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ..<
     * </pre>
     */
    public static final int DOT2_LT = 121;

    /**
     * Token Id for DOT_QUESTION
     * <pre>
     * ID         : 122
     * ID_NAME    : DOT_QUESTION
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : .?
     * </pre>
     */
    public static final int DOT_QUESTION = 122;

    /**
     * Token Id for SOLIDUS
     * <pre>
     * ID         : 123
     * ID_NAME    : SOLIDUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : /
     * </pre>
     */
    public static final int SOLIDUS = 123;

    /**
     * Token Id for COLON_ASTERISK
     * <pre>
     * ID         : 124
     * ID_NAME    : COLON_ASTERISK
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :*
     * </pre>
     */
    public static final int COLON_ASTERISK = 124;

    /**
     * Token Id for COLON_ASTERISK2
     * <pre>
     * ID         : 125
     * ID_NAME    : COLON_ASTERISK2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :**
     * </pre>
     */
    public static final int COLON_ASTERISK2 = 125;

    /**
     * Token Id for COLON_ASTERISK3
     * <pre>
     * ID         : 126
     * ID_NAME    : COLON_ASTERISK3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :***
     * </pre>
     */
    public static final int COLON_ASTERISK3 = 126;

    /**
     * Token Id for COLON_PLUS
     * <pre>
     * ID         : 127
     * ID_NAME    : COLON_PLUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :+
     * </pre>
     */
    public static final int COLON_PLUS = 127;

    /**
     * Token Id for COLON_PLUS2
     * <pre>
     * ID         : 128
     * ID_NAME    : COLON_PLUS2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :++
     * </pre>
     */
    public static final int COLON_PLUS2 = 128;

    /**
     * Token Id for COLON_MINUS
     * <pre>
     * ID         : 129
     * ID_NAME    : COLON_MINUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :-
     * </pre>
     */
    public static final int COLON_MINUS = 129;

    /**
     * Token Id for COLON_MINUS2
     * <pre>
     * ID         : 130
     * ID_NAME    : COLON_MINUS2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :--
     * </pre>
     */
    public static final int COLON_MINUS2 = 130;

    /**
     * Token Id for COLON2
     * <pre>
     * ID         : 131
     * ID_NAME    : COLON2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ::
     * </pre>
     */
    public static final int COLON2 = 131;

    /**
     * Token Id for COLON_EQ
     * <pre>
     * ID         : 132
     * ID_NAME    : COLON_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :=
     * </pre>
     */
    public static final int COLON_EQ = 132;

    /**
     * Token Id for COLON_TILDE
     * <pre>
     * ID         : 133
     * ID_NAME    : COLON_TILDE
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : :~
     * </pre>
     */
    public static final int COLON_TILDE = 133;

    /**
     * Token Id for LT
     * <pre>
     * ID         : 134
     * ID_NAME    : LT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <
     * </pre>
     */
    public static final int LT = 134;

    /**
     * Token Id for LT_MINUS
     * <pre>
     * ID         : 135
     * ID_NAME    : LT_MINUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <-
     * </pre>
     */
    public static final int LT_MINUS = 135;

    /**
     * Token Id for LT_DOT2
     * <pre>
     * ID         : 136
     * ID_NAME    : LT_DOT2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <..
     * </pre>
     */
    public static final int LT_DOT2 = 136;

    /**
     * Token Id for LT2
     * <pre>
     * ID         : 137
     * ID_NAME    : LT2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <<
     * </pre>
     */
    public static final int LT2 = 137;

    /**
     * Token Id for LT3
     * <pre>
     * ID         : 138
     * ID_NAME    : LT3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <<<
     * </pre>
     */
    public static final int LT3 = 138;

    /**
     * Token Id for LT_EQ
     * <pre>
     * ID         : 139
     * ID_NAME    : LT_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <=
     * </pre>
     */
    public static final int LT_EQ = 139;

    /**
     * Token Id for LT_GT
     * <pre>
     * ID         : 140
     * ID_NAME    : LT_GT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : <>
     * </pre>
     */
    public static final int LT_GT = 140;

    /**
     * Token Id for EQ
     * <pre>
     * ID         : 141
     * ID_NAME    : EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : =
     * </pre>
     */
    public static final int EQ = 141;

    /**
     * Token Id for EQ_LT
     * <pre>
     * ID         : 142
     * ID_NAME    : EQ_LT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : =<
     * </pre>
     */
    public static final int EQ_LT = 142;

    /**
     * Token Id for EQ2
     * <pre>
     * ID         : 143
     * ID_NAME    : EQ2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ==
     * </pre>
     */
    public static final int EQ2 = 143;

    /**
     * Token Id for EQ3
     * <pre>
     * ID         : 144
     * ID_NAME    : EQ3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ===
     * </pre>
     */
    public static final int EQ3 = 144;

    /**
     * Token Id for EQ_GT
     * <pre>
     * ID         : 145
     * ID_NAME    : EQ_GT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : =>
     * </pre>
     */
    public static final int EQ_GT = 145;

    /**
     * Token Id for GT
     * <pre>
     * ID         : 146
     * ID_NAME    : GT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : >
     * </pre>
     */
    public static final int GT = 146;

    /**
     * Token Id for GT_EQ
     * <pre>
     * ID         : 147
     * ID_NAME    : GT_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : >=
     * </pre>
     */
    public static final int GT_EQ = 147;

    /**
     * Token Id for GT2
     * <pre>
     * ID         : 148
     * ID_NAME    : GT2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : >>
     * </pre>
     */
    public static final int GT2 = 148;

    /**
     * Token Id for GT3
     * <pre>
     * ID         : 149
     * ID_NAME    : GT3
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : >>>
     * </pre>
     */
    public static final int GT3 = 149;

    /**
     * Token Id for QUESTION
     * <pre>
     * ID         : 150
     * ID_NAME    : QUESTION
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ?
     * </pre>
     */
    public static final int QUESTION = 150;

    /**
     * Token Id for QUESTION2
     * <pre>
     * ID         : 151
     * ID_NAME    : QUESTION2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ??
     * </pre>
     */
    public static final int QUESTION2 = 151;

    /**
     * Token Id for QUESTION2_EQ
     * <pre>
     * ID         : 152
     * ID_NAME    : QUESTION2_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ??=
     * </pre>
     */
    public static final int QUESTION2_EQ = 152;

    /**
     * Token Id for CIRCUMFLEX
     * <pre>
     * ID         : 153
     * ID_NAME    : CIRCUMFLEX
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ^
     * </pre>
     */
    public static final int CIRCUMFLEX = 153;

    /**
     * Token Id for CIRCUMFLEX_EQ
     * <pre>
     * ID         : 154
     * ID_NAME    : CIRCUMFLEX_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ^=
     * </pre>
     */
    public static final int CIRCUMFLEX_EQ = 154;

    /**
     * Token Id for CIRCUMFLEX2
     * <pre>
     * ID         : 155
     * ID_NAME    : CIRCUMFLEX2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ^^
     * </pre>
     */
    public static final int CIRCUMFLEX2 = 155;

    /**
     * Token Id for IS
     * <pre>
     * ID         : 156
     * ID_NAME    : IS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : is
     * </pre>
     */
    public static final int IS = 156;

    /**
     * Token Id for PIPE
     * <pre>
     * ID         : 157
     * ID_NAME    : PIPE
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : |
     * </pre>
     */
    public static final int PIPE = 157;

    /**
     * Token Id for PIPE_EQ
     * <pre>
     * ID         : 158
     * ID_NAME    : PIPE_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : |=
     * </pre>
     */
    public static final int PIPE_EQ = 158;

    /**
     * Token Id for PIPE2
     * <pre>
     * ID         : 159
     * ID_NAME    : PIPE2
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ||
     * </pre>
     */
    public static final int PIPE2 = 159;

    /**
     * Token Id for TILDE
     * <pre>
     * ID         : 160
     * ID_NAME    : TILDE
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ~
     * </pre>
     */
    public static final int TILDE = 160;

    /**
     * Token Id for TILDE_EQ
     * <pre>
     * ID         : 161
     * ID_NAME    : TILDE_EQ
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ~=
     * </pre>
     */
    public static final int TILDE_EQ = 161;

    /**
     * Token Id for PLUS_MINUS_SIGN
     * <pre>
     * ID         : 162
     * ID_NAME    : PLUS_MINUS_SIGN
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ±
     * </pre>
     */
    public static final int PLUS_MINUS_SIGN = 162;

    /**
     * Token Id for MULTIPLICATION_SIGN
     * <pre>
     * ID         : 163
     * ID_NAME    : MULTIPLICATION_SIGN
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ×
     * </pre>
     */
    public static final int MULTIPLICATION_SIGN = 163;

    /**
     * Token Id for DIVISION_SIGN
     * <pre>
     * ID         : 164
     * ID_NAME    : DIVISION_SIGN
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ÷
     * </pre>
     */
    public static final int DIVISION_SIGN = 164;

    /**
     * Token Id for FOR_ALL
     * <pre>
     * ID         : 165
     * ID_NAME    : FOR_ALL
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∀
     * </pre>
     */
    public static final int FOR_ALL = 165;

    /**
     * Token Id for PARTIAL_DIFFERENTIAL
     * <pre>
     * ID         : 166
     * ID_NAME    : PARTIAL_DIFFERENTIAL
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∂
     * </pre>
     */
    public static final int PARTIAL_DIFFERENTIAL = 166;

    /**
     * Token Id for THERE_EXISTS
     * <pre>
     * ID         : 167
     * ID_NAME    : THERE_EXISTS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∃
     * </pre>
     */
    public static final int THERE_EXISTS = 167;

    /**
     * Token Id for THERE_DOES_NOT_EXIST
     * <pre>
     * ID         : 168
     * ID_NAME    : THERE_DOES_NOT_EXIST
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∄
     * </pre>
     */
    public static final int THERE_DOES_NOT_EXIST = 168;

    /**
     * Token Id for INCREMENT
     * <pre>
     * ID         : 169
     * ID_NAME    : INCREMENT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∆
     * </pre>
     */
    public static final int INCREMENT = 169;

    /**
     * Token Id for NABLA
     * <pre>
     * ID         : 170
     * ID_NAME    : NABLA
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∇
     * </pre>
     */
    public static final int NABLA = 170;

    /**
     * Token Id for ELEMENT_OF
     * <pre>
     * ID         : 171
     * ID_NAME    : ELEMENT_OF
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∈
     * </pre>
     */
    public static final int ELEMENT_OF = 171;

    /**
     * Token Id for NOT_AN_ELEMENT_OF
     * <pre>
     * ID         : 172
     * ID_NAME    : NOT_AN_ELEMENT_OF
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∉
     * </pre>
     */
    public static final int NOT_AN_ELEMENT_OF = 172;

    /**
     * Token Id for SMALL_ELEMENT_OF
     * <pre>
     * ID         : 173
     * ID_NAME    : SMALL_ELEMENT_OF
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∊
     * </pre>
     */
    public static final int SMALL_ELEMENT_OF = 173;

    /**
     * Token Id for CONTAINS_AS_MEMBER
     * <pre>
     * ID         : 174
     * ID_NAME    : CONTAINS_AS_MEMBER
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∋
     * </pre>
     */
    public static final int CONTAINS_AS_MEMBER = 174;

    /**
     * Token Id for DOES_NOT_CONTAIN_AS_MEMBER
     * <pre>
     * ID         : 175
     * ID_NAME    : DOES_NOT_CONTAIN_AS_MEMBER
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∌
     * </pre>
     */
    public static final int DOES_NOT_CONTAIN_AS_MEMBER = 175;

    /**
     * Token Id for SMALL_CONTAINS_AS_MEMBER
     * <pre>
     * ID         : 176
     * ID_NAME    : SMALL_CONTAINS_AS_MEMBER
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∍
     * </pre>
     */
    public static final int SMALL_CONTAINS_AS_MEMBER = 176;

    /**
     * Token Id for END_OF_PROOF
     * <pre>
     * ID         : 177
     * ID_NAME    : END_OF_PROOF
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∎
     * </pre>
     */
    public static final int END_OF_PROOF = 177;

    /**
     * Token Id for N_ARY_PRODUCT
     * <pre>
     * ID         : 178
     * ID_NAME    : N_ARY_PRODUCT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∏
     * </pre>
     */
    public static final int N_ARY_PRODUCT = 178;

    /**
     * Token Id for N_ARY_COPRODUCT
     * <pre>
     * ID         : 179
     * ID_NAME    : N_ARY_COPRODUCT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∐
     * </pre>
     */
    public static final int N_ARY_COPRODUCT = 179;

    /**
     * Token Id for N_ARY_SUMMATION
     * <pre>
     * ID         : 180
     * ID_NAME    : N_ARY_SUMMATION
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∑
     * </pre>
     */
    public static final int N_ARY_SUMMATION = 180;

    /**
     * Token Id for MINUS_OR_PLUS_SIGN
     * <pre>
     * ID         : 181
     * ID_NAME    : MINUS_OR_PLUS_SIGN
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∓
     * </pre>
     */
    public static final int MINUS_OR_PLUS_SIGN = 181;

    /**
     * Token Id for DOT_PLUS
     * <pre>
     * ID         : 182
     * ID_NAME    : DOT_PLUS
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∔
     * </pre>
     */
    public static final int DOT_PLUS = 182;

    /**
     * Token Id for RING_OPERATOR
     * <pre>
     * ID         : 183
     * ID_NAME    : RING_OPERATOR
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∘
     * </pre>
     */
    public static final int RING_OPERATOR = 183;

    /**
     * Token Id for SQUARE_ROOT
     * <pre>
     * ID         : 184
     * ID_NAME    : SQUARE_ROOT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : √
     * </pre>
     */
    public static final int SQUARE_ROOT = 184;

    /**
     * Token Id for CUBE_ROOT
     * <pre>
     * ID         : 185
     * ID_NAME    : CUBE_ROOT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∛
     * </pre>
     */
    public static final int CUBE_ROOT = 185;

    /**
     * Token Id for FOURTH_ROOT
     * <pre>
     * ID         : 186
     * ID_NAME    : FOURTH_ROOT
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∜
     * </pre>
     */
    public static final int FOURTH_ROOT = 186;

    /**
     * Token Id for THEREFORE
     * <pre>
     * ID         : 187
     * ID_NAME    : THEREFORE
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∴
     * </pre>
     */
    public static final int THEREFORE = 187;

    /**
     * Token Id for BECAUSE
     * <pre>
     * ID         : 188
     * ID_NAME    : BECAUSE
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∵
     * </pre>
     */
    public static final int BECAUSE = 188;

    /**
     * Token Id for PROPORTION
     * <pre>
     * ID         : 189
     * ID_NAME    : PROPORTION
     * TYPE_ID    : -11
     * TYPE_NAME  : TT_OPERATOR
     * STATE_ID   : [1, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_CODE]
     * LAYOUT     : ∷
     * </pre>
     */
    public static final int PROPORTION = 189;

    /**
     * Token Id for LEFT_PARENTHESIS
     * <pre>
     * ID         : 80
     * ID_NAME    : LEFT_PARENTHESIS
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : (
     * </pre>
     */
    public static final int LEFT_PARENTHESIS = 80;

    /**
     * Token Id for RIGHT_PARENTHESIS
     * <pre>
     * ID         : 81
     * ID_NAME    : RIGHT_PARENTHESIS
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : )
     * </pre>
     */
    public static final int RIGHT_PARENTHESIS = 81;

    /**
     * Token Id for LEFT_SQUARE_BRACKET
     * <pre>
     * ID         : 82
     * ID_NAME    : LEFT_SQUARE_BRACKET
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : [
     * </pre>
     */
    public static final int LEFT_SQUARE_BRACKET = 82;

    /**
     * Token Id for RIGHT_SQUARE_BRACKET
     * <pre>
     * ID         : 83
     * ID_NAME    : RIGHT_SQUARE_BRACKET
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : ]
     * </pre>
     */
    public static final int RIGHT_SQUARE_BRACKET = 83;

    /**
     * Token Id for LEFT_CURLY_BRACKET
     * <pre>
     * ID         : 84
     * ID_NAME    : LEFT_CURLY_BRACKET
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : {
     * </pre>
     */
    public static final int LEFT_CURLY_BRACKET = 84;

    /**
     * Token Id for RIGHT_CURLY_BRACKET
     * <pre>
     * ID         : 88
     * ID_NAME    : RIGHT_CURLY_BRACKET
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 1
     * STATE_NAME : STATE_DEFAULT
     * LAYOUT     : }
     * </pre>
     */
    public static final int RIGHT_CURLY_BRACKET = 88;

    /**
     * Token Id for IDENTIFIER
     * <pre>
     * ID         : 1
     * ID_NAME    : IDENTIFIER
     * TYPE_ID    : -2
     * TYPE_NAME  : TT_IDENTIFIER
     * STATE_ID   : [4, 3]
     * STATE_NAME : [STATE_STRING_INTERP_CODE, STATE_STRING_INTERP_VAR]
     * LAYOUT     : [[a-z]+, $".${|}."]
     * </pre>
     */
    public static final int IDENTIFIER = 1;

    /**
     * Token Id for SUPERSCRIPT
     * <pre>
     * ID         : 10008
     * ID_NAME    : SUPERSCRIPT
     * TYPE_ID    : -18
     * TYPE_NAME  : TT_SUPERSCRIPT
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : ¹²³
     * </pre>
     */
    public static final int SUPERSCRIPT = 10008;

    /**
     * Token Id for WHITESPACE
     * <pre>
     * ID         : 2
     * ID_NAME    : WHITESPACE
     * TYPE_ID    : -98
     * TYPE_NAME  : TT_WHITESPACE
     * STATE_ID   : [1, 2, 3, 4]
     * STATE_NAME : [STATE_DEFAULT, STATE_STRING_INTERP_TEXT, STATE_STRING_INTERP_VAR, STATE_STRING_INTERP_CODE]
     * LAYOUT     : [ \n\t]
     * </pre>
     */
    public static final int WHITESPACE = 2;

    /**
     * Token Id for STRING_INTERP_TEXT
     * <pre>
     * ID         : 10036
     * ID_NAME    : STRING_INTERP_TEXT
     * TYPE_ID    : -17
     * TYPE_NAME  : TT_STRING_INTERP
     * STATE_ID   : 2
     * STATE_NAME : STATE_STRING_INTERP_TEXT
     * LAYOUT     : $".|."
     * </pre>
     */
    public static final int STRING_INTERP_TEXT = 10036;

    /**
     * Token Id for STRING_INTERP_END
     * <pre>
     * ID         : 10037
     * ID_NAME    : STRING_INTERP_END
     * TYPE_ID    : -17
     * TYPE_NAME  : TT_STRING_INTERP
     * STATE_ID   : 2
     * STATE_NAME : STATE_STRING_INTERP_TEXT
     * LAYOUT     : $"..|"
     * </pre>
     */
    public static final int STRING_INTERP_END = 10037;

    /**
     * Token Id for STRING_INTERP_DOLLAR_START
     * <pre>
     * ID         : 10039
     * ID_NAME    : STRING_INTERP_DOLLAR_START
     * TYPE_ID    : -17
     * TYPE_NAME  : TT_STRING_INTERP
     * STATE_ID   : 2
     * STATE_NAME : STATE_STRING_INTERP_TEXT
     * LAYOUT     : $".$|."
     * </pre>
     */
    public static final int STRING_INTERP_DOLLAR_START = 10039;

    /**
     * Token Id for REVERSE_SOLIDUS_RIGHT_CURLY_BRACKET
     * <pre>
     * ID         : 89
     * ID_NAME    : REVERSE_SOLIDUS_RIGHT_CURLY_BRACKET
     * TYPE_ID    : -14
     * TYPE_NAME  : TT_GROUP_SEPARATOR
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : \}
     * </pre>
     */
    public static final int REVERSE_SOLIDUS_RIGHT_CURLY_BRACKET = 89;

    /**
     * Token Id for STRING_INTERP_DOLLAR_END
     * <pre>
     * ID         : 10040
     * ID_NAME    : STRING_INTERP_DOLLAR_END
     * TYPE_ID    : -17
     * TYPE_NAME  : TT_STRING_INTERP
     * STATE_ID   : 4
     * STATE_NAME : STATE_STRING_INTERP_CODE
     * LAYOUT     : }
     * </pre>
     */
    public static final int STRING_INTERP_DOLLAR_END = 10040;
}
