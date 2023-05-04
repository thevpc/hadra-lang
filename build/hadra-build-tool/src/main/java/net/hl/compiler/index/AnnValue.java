package net.hl.compiler.index;

import java.util.List;
import java.util.Map;

public class AnnValue {
    private Type type;
    private Object value;

    public AnnValue(EnumVal value) {
        this(value, Type.ENUM);
    }

    public AnnValue(AnnInfo value) {
        this(value, Type.ANNOTATION);
    }

    public AnnValue(Object value) {
        this(value, null);
    }

    public AnnValue(TypeValue value) {
        this(value, Type.TYPE);
    }

    public AnnValue(List<AnnValue> value) {
        this(value, Type.ARRAY);
    }

    public AnnValue(Object value, Type type) {
        this.value = value;
        if (type == null) {
            if (value instanceof List) {
                this.type = Type.ARRAY;
            } else if (value instanceof EnumVal) {
                this.type = Type.ENUM;
            } else if (value instanceof AnnInfo) {
                this.type = Type.ANNOTATION;
            } else if (value instanceof AnnValue) {
                this.type = Type.ANNOTATION;
            } else if (value instanceof TypeValue) {
                this.type = Type.TYPE;
            } else if (value instanceof String) {
                this.type = Type.STRING;
            } else if ((value instanceof Boolean)
                            || (value instanceof Byte)
                            || (value instanceof Integer)
                            || (value instanceof Short)
                            || (value instanceof Long)
                            || (value instanceof Float)
                            || (value instanceof Double)
            ) {
                this.type = Type.PRIMITIVE;
            } else {
                throw new IllegalArgumentException("unsupported");
            }
        } else {
            this.type = type;
        }
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public enum Type {
        PRIMITIVE,
        STRING,
        ARRAY,
        ANNOTATION,
        ENUM,
        TYPE,
    }

}
