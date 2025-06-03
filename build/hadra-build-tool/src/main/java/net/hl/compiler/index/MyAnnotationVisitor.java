package net.hl.compiler.index;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class MyAnnotationVisitor extends AnnotationVisitor {
    Map<String, AnnValue> values = new LinkedHashMap<>();
    Consumer<AnnInfo> annotations;
    String name;
    boolean visible;

    public MyAnnotationVisitor(String name, boolean visible, Consumer<AnnInfo> annotations) {
        super(Opcodes.ASM9);
        this.name = name;
        this.visible = visible;
        this.annotations = annotations;
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            values.put(name, new AnnValue(new TypeValue(((Type) value).getClassName())));
        } else {
            values.put(name, new AnnValue(value));
        }
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        values.put(
                name,
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
            values.put(name, new AnnValue(x));
        });
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        List<AnnValue> all = new ArrayList<>();
        values.put(name, new AnnValue(all));
        return
                new ArrAnnotationVisitor(
                        x -> {
                            all.add(x);
                        }
                );
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        AnnInfo a = new AnnInfo(name, values);
        annotations.accept(a);
    }
}
