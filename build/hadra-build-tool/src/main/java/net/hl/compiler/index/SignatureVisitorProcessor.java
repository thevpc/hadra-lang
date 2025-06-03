package net.hl.compiler.index;

import net.thevpc.jeep.JTypeNameOrVariable;
import net.thevpc.jeep.core.types.DefaultTypeName;
import net.thevpc.jeep.core.types.JTypeNameParser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class SignatureVisitorProcessor extends SignatureVisitor {
    private Consumer<JTypeNameOrVariable> c;
    private static final boolean TRACE = false;

    private static final int NO_TYPE = 0;

    private static final int FIELD_TYPE = 1;

    private static final int RETURN_TYPE = 2;

    private static final int PARAMETER_TYPE = 3;
    public SignatureVisitorProcessor(Consumer<JTypeNameOrVariable> c) {
        super(Opcodes.ASM9);
        this.c=c;
        init();
    }


    // The type of the current Type
    private int typeType;

    // The current Type identified.
    private JTypeNameOrVariable type;

    // The number of dimensions on an array for the current Type.
    private int arrayDimensions = 0;

    // Completed Field Type is stored here
    private JTypeNameOrVariable fieldType;

    // Completed Return Type is stored here
    private JTypeNameOrVariable returnType;

    // Completed Parameter Types are stored here
    private List<JTypeNameOrVariable> parameterTypes = new ArrayList<>(0);


    protected void println(String s) {
        //p.println(s);
    }

    protected void printlnIndent(String s) {
        //p.printlnIndent(s);
    }

    public void init() {
        typeType = FIELD_TYPE;
        type = null;
        arrayDimensions = 0;
        parameterTypes.clear();
    }

    public JTypeNameOrVariable getFieldType() {
        popType();
        if (fieldType == null) {
            throw new RuntimeException();
        }
        return fieldType;
    }

    public JTypeNameOrVariable getMethodReturnType() {
        popType();
        if (returnType == null) {
            throw new RuntimeException();
        }
        return returnType;
    }

    public Class<?>[] getMethodParameterTypes() {
        popType();
        if (parameterTypes == null) {
            throw new RuntimeException();
        }
        return parameterTypes.toArray(new Class<?>[0]);
    }

    private void pushType(int type) {
        this.typeType = type;
    }

    private void popType() {
        switch (typeType) {
            case NO_TYPE:
                break;
            case FIELD_TYPE:
                fieldType = getType();
                break;
            case RETURN_TYPE:
                returnType = getType();
                break;
            case PARAMETER_TYPE:
                parameterTypes.add(getType());
                break;
            default:
                throw new RuntimeException("Unknown type type: " + typeType);
        }

        typeType = NO_TYPE;
        type = null;
        arrayDimensions = 0;
    }

    private JTypeNameOrVariable getType() {
        JTypeNameOrVariable type = null;
        if (this.type != null) {
            type = this.type;
            for (int i = 0; i < arrayDimensions; i++) {
                // Is there another way to get Array Classes?
                type=type.toArray();
            }
        }
        return type;
    }

    @Override
    public SignatureVisitor visitArrayType() {
        if (TRACE) {
            println("visitArrayType:");
        }
        arrayDimensions++;
        return this;
    }

    @Override
    public void visitBaseType(char descriptor) {
        if (TRACE) {
            println("visitBaseType:");
            printlnIndent("descriptor: " + descriptor);
        }
        switch (descriptor) {
            case 'B':
                type = new DefaultTypeName("byte");
                break;
            case 'C':
                type = new DefaultTypeName("char");
                break;
            case 'D':
                type = new DefaultTypeName("double");
                break;
            case 'F':
                type = new DefaultTypeName("float");
                break;
            case 'I':
                type = new DefaultTypeName("int");
                break;
            case 'J':
                type = new DefaultTypeName("long");
                break;
            case 'S':
                type = new DefaultTypeName("short");
                break;
            case 'Z':
                type = new DefaultTypeName("boolean");
                break;
            case 'V':
                type = new DefaultTypeName("void");
                break;
            default:
                throw new RuntimeException("Unknown baseType descriptor: " + descriptor);
        }
    }

    @Override
    public SignatureVisitor visitClassBound() {
        if (TRACE) {
            println("visitClassBound:");
        }
        return this;
    }

    @Override
    public void visitClassType(String name) {
        if (TRACE) {
            println("visitClassType:");
            printlnIndent("name: " + name);
        }
        name = JavaClassNames.parseFullNameFromPath(name).fullName;
        this.type = JTypeNameParser.parseType(name);
    }

    @Override
    public void visitEnd() {
        if (TRACE) {
            println("visitEnd:");
        }
        popType();
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        if (TRACE) {
            println("visitExceptionType:");
        }
        return this;
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        if (TRACE) {
            println("visitFormalTypeParameter:");
            printlnIndent("name: " + name);
        }
    }

    @Override
    public void visitInnerClassType(String name) {
        if (TRACE) {
            println("visitInnerClassType:");
            printlnIndent("name: " + name);
        }
    }

    @Override
    public SignatureVisitor visitInterface() {
        if (TRACE) {
            println("visitInterface:");
        }
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        if (TRACE) {
            println("visitInterfaceBound:");
        }
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        if (TRACE) {
            println("visitParameterType:");
        }
        popType();
        pushType(PARAMETER_TYPE);
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        if (TRACE) {
            println("visitReturnType:");
        }
        popType();
        pushType(RETURN_TYPE);
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        if (TRACE) {
            println("visitSuperclass:");
        }
        return this;
    }

    @Override
    public void visitTypeArgument() {
        if (TRACE) {
            println("visitTypeArgument:");
        }
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (TRACE) {
            println("visitTypeArgument:");
            printlnIndent("wildcard: " + wildcard);
        }
        return this;
    }

    @Override
    public void visitTypeVariable(String name) {
        if (TRACE) {
            println("visitTypeVariable:");
            printlnIndent("name: " + name);
        }
    }
}
