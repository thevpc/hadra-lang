package net.hl.compiler.core.invokables;

import net.hl.compiler.index.AnnInfo;
import net.hl.compiler.index.AnnValue;
import net.hl.compiler.index.HIndexer;
import net.thevpc.jeep.JAnnotationInstance;
import net.thevpc.jeep.JAnnotationInstanceField;
import net.thevpc.jeep.JTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JAnnotationInstanceFromIndex implements JAnnotationInstance {
    private AnnInfo annotation;
    private HIndexer indexer;
    private JTypes types;

    public JAnnotationInstanceFromIndex(AnnInfo annotation, HIndexer indexer, JTypes types) {
        this.annotation = annotation;
        this.indexer = indexer;
        this.types = types;
    }

    @Override
    public String getName() {
        return annotation.getName();
    }

    @Override
    public JAnnotationInstanceField[] getFields() {
        List<JAnnotationInstanceField> all = new ArrayList<>();
        for (Map.Entry<String, AnnValue> z : annotation.getValues().entrySet()) {
            all.add(new JAnnotationInstanceFieldFromIndex(z.getKey(), z.getValue()));
        }
        return all.toArray(new JAnnotationInstanceField[0]);
    }

    @Override
    public Object getObject() {
        return annotation;
    }

}
