package net.hl.compiler.index;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class ArrAnnotationVisitor extends AnnotationVisitor {
    Consumer<AnnValue> annotations;
    public ArrAnnotationVisitor(Consumer<AnnValue> annotations) {
        super(Opcodes.ASM9);
        this.annotations=annotations;
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            annotations.accept(new AnnValue(new TypeValue(((Type) value).getClassName())));
        } else {
            annotations.accept(new AnnValue(value));
        }
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        annotations.accept(
                new AnnValue(
                        new EnumVal(
                                JavaClassNames.parseFullNameFromEncoded(descriptor).fullName,
                                value
                        )
                )
        );
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return new MyAnnotationVisitor(
                JavaClassNames.parseFullNameFromEncoded(descriptor).fullName, true, x -> {
            annotations.accept(new AnnValue(x));
        });
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        List<AnnValue> all = new ArrayList<>();
        return
                new ArrAnnotationVisitor(
                        x -> {
                            all.add(x);
                        }
                );
    }

    @Override
    public void visitEnd() {
    }
}
