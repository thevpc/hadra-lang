package net.vpc.hadralang.compiler.stages.runtime;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.DefaultJTypedValue;
import net.vpc.common.jeep.core.eval.JEvaluableValue;
import net.vpc.common.jeep.JTypeArray;
import net.vpc.common.jeep.impl.types.host.HostJArray;
import net.vpc.common.jeep.impl.types.host.HostJRawType;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.hadralang.compiler.core.HLCompilerEnv;
import net.vpc.hadralang.compiler.core.HLDependency;
import net.vpc.hadralang.compiler.core.elements.HNElement;
import net.vpc.hadralang.compiler.core.elements.HNElementField;
import net.vpc.hadralang.compiler.core.elements.HNElementKind;
import net.vpc.hadralang.compiler.core.elements.HNElementLocalVar;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.core.invokables.BodyJInvoke;
import net.vpc.hadralang.compiler.core.invokables.JNodeHBlocJInvoke;
import net.vpc.hadralang.compiler.parser.ast.extra.HXInvokableCall;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HTypeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;
import net.vpc.hadralang.stdlib.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.*;

public class HLEvaluator implements JEvaluator {
    public static final HLEvaluator INSTANCE = new HLEvaluator();

    @Override
    public Object evaluate(JNode node, JInvokeContext context) {
        switch (((HNode)node).id()) {
            case H_BLOCK: {
                HNBlock a = (HNBlock) node;
                return new JNodeHBlocJInvoke(a).invoke(context);
            }
            case H_DECLARE_INVOKABLE: {
                HNDeclareInvokable a = (HNDeclareInvokable) node;
                return null;
            }
            case H_DECLARE_IDENTIFIER: {
                HNDeclareIdentifier a = (HNDeclareIdentifier) node;
                //already declared, now just initialize!
                Object varValue = evaluate(a.getInitValue(), context);
                for (HNDeclareTokenIdentifier name : HNodeUtils.flatten(a.getIdentifierToken())) {
                    context.getContext().vars().setValue(name.getName(),varValue, context);
                }
                return varValue;
            }
            case H_DECLARE_TYPE: {
                HNDeclareType a = (HNDeclareType) node;
                return null;
            }
            case H_IF: {
                HNIf a = (HNIf) node;
                List<HNIf.WhenDoBranchNode> cond = a.getBranches();
                for (HNIf.WhenDoBranchNode c : cond) {
                    JInvokablePrefilled fct = c.getImpl();
                    if (fct != null) {
                        return fct.invoke(context);
                    } else {
                        Boolean ok = (Boolean) evaluate(c.getWhenNode(), context);
                        if (ok != null && ok) {
                            return evaluate(c.getDoNode(), context);
                        }
                    }
                }
                if (a.getElseNode() != null) {
                    return evaluate(a.getElseNode(), context);
                }
                return a.getElement().getTypePattern().getType().getDefaultValue();
            }
            case H_WHILE: {
                HNWhile a = (HNWhile) node;
                JNode expr = a.getExpr();
                JType r = HNodeUtils.getType(expr);
                Object whileResult = null;
                while (true) {
                    Boolean ok = (Boolean) evaluate(expr, context);
                    if (ok != null && ok) {
                        try {
                            whileResult = evaluate(a.getBlock(), context);
                        } catch (InternalBreak b) {
                            if (b.getLabel() == null || b.getLabel().equals(a.getLabel())) {
                                break;
                            } else {
                                throw b;
                            }
                        } catch (InternalContinue b) {
                            if (b.getLabel() == null || b.getLabel().equals(a.getLabel())) {
                                continue;
                            } else {
                                throw b;
                            }
                        }
                    } else {
                        break;
                    }
                }
                return whileResult;
            }
            case H_TUPLE: {
                HNTuple a = (HNTuple) node;
                JNode[] aitems = a.getItems();
                Object[] items = new Object[aitems.length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = evaluate(aitems[i], context);
                }
                switch (items.length) {
                    case 0:
                        return Tuple0.INSTANCE;
                    case 1:
                        return new Tuple1(items[0]);
                    case 2:
                        return new Tuple2(items[0], items[1]);
                    case 3:
                        return new Tuple3(items[0], items[1], items[2]);
//                    case 4:return new Tuple4(items[0],items[1],items[2],items[3]);
//                    case 5:return new Tuple5(items[0],items[1],items[2],items[3],items[4]);
//                    case 5:return new Tuple6(items[0],items[1],items[2],items[3],items[4],items[5]);
//                    case 6:return new Tuple7(items[0],items[1],items[2],items[3],items[4],items[5],items[7]);
                }
                if (items.length < Tuple.MAX_ELEMENTS) {
                    Class<?> c = null;
                    try {
                        c = Class.forName(HTypeUtils.tupleTypeForCount(items.length,context.getContext().types()).getName());
                        return c.getConstructors()[0].newInstance(items);
                    } catch (Exception e) {
                        throw new JShouldNeverHappenException();
                    }
                } else {
                    return new TupleN(items);
                }
            }
            case H_FOR: {
                HNFor a = (HNFor) node;
                List<HNode> initExprs = a.getInitExprs();
                JInvokeContext ncc = context.builder().setContext(context.getContext().newContext()).build();
                if (a.isIteratorType()) {
                    HNode initExpr = a.getInitExprs().get(0);
                    if (initExpr instanceof HNDeclareIdentifier) {
                        HNDeclareIdentifier i = (HNDeclareIdentifier) initExpr;
                        HNDeclareToken identifierToken = i.getIdentifierToken();
                        if(identifierToken instanceof HNDeclareTokenIdentifier) {
                            Object initValue = evaluate(i.getInitValue(), ncc);
                            Iterable iter = iter(initValue, i.getInitValueConstraint());
                            if (iter == null) {
                                throw new IllegalArgumentException("Unable to iterate over " + initValue +
                                        (initValue == null ? "" : (" of type " + (initValue.getClass().getSimpleName())))
                                );
                            }
                            Loop loop = new Loop(a.getLabel(), iter, i.getIdentifierName(), i.getIdentifierType(), a.getBody(), a.getFilter(), a.getIncs().toArray(new HNode[0]));
                            return loop.run(ncc);
                        }else{
                            HNDeclareTokenTuple ti=(HNDeclareTokenTuple) identifierToken;
                            Object obj = evaluate(i.getInitValue(), ncc);
                            HNDeclareTokenIdentifier[] identifiers = HNodeUtils.flatten(ti); //FIX ME LATER
                            if (obj instanceof Tuple) {
                                Tuple tupleInit = (Tuple) obj;
                                Loop loop = null;
                                for (int ii = identifiers.length - 1; ii >= 0; ii--) {
                                    if (ii != 0 && ii == identifiers.length - 1) {
                                        Iterable iter = iter(tupleInit.valueAt(ii), i.getInitValueConstraint());
                                        loop = new Loop(null, iter, i.getIdentifierName(), identifiers[ii].getIdentifierType(),
                                                a.getBody(),
                                                a.getFilter(),
                                                a.getIncs().toArray(new HNode[0])
                                        );
                                    } else if (ii == 0) {
                                        //the only thing special here is that the iterable is actually an iterator
                                        Iterable iter = iter(tupleInit.valueAt(ii),i.getInitValueConstraint());
                                        loop = new Loop(((HNFor) node).getLabel(), iter, i.getIdentifierName(), identifiers[ii].getIdentifierType(),
                                                loop,
                                                null,
                                                new HNode[0]
                                        );
                                    } else {
                                        Iterable iter = iter(tupleInit.valueAt(ii), i.getInitValueConstraint());
                                        loop = new Loop(null, iter, i.getIdentifierName(), identifiers[ii].getIdentifierType(),
                                                loop,
                                                null,
                                                new HNode[0]
                                        );
                                    }
                                }
                                return loop.run(ncc);
                            } else {
                                HNDeclareIdentifier i2 = (HNDeclareIdentifier) ncc;
                                Object initValue = evaluate(i2.getInitValue(), ncc);
                                Iterable iterOfTuples = iter(initValue, i2.getInitValueConstraint());
                                Loop loop = new Loop(a.getLabel(), iterOfTuples, i2.getIdentifierName(), i2.getIdentifierType(), a.getBody(), a.getFilter(), a.getIncs().toArray(new HNode[0])) {
                                    @Override
                                    public void runInit(JInvokeContext context) {
                                        for (int i = 0; i < identifiers.length; i++) {
                                            HNDeclareTokenIdentifier id = identifiers[i];
                                            context.getContext().vars().declareVar(id.getName(), id.getIdentifierType(), null);
                                        }
                                    }

                                    @Override
                                    public void runItem(JInvokeContext context, Object item) {
                                        Tuple t = (Tuple) item;
                                        for (int i = 0; i < identifiers.length; i++) {
                                            HNDeclareTokenIdentifier id = identifiers[i];
                                            context.getContext().vars().setValue(id.getName(), t.valueAt(i), context);
                                        }
                                    }
                                };
                                return loop.run(context);
                            }
                        }
                    }
                } else {
                    for (HNode initExpr : initExprs) {
                        if (initExpr instanceof HNDeclareIdentifier) {
                            HNDeclareIdentifier i = (HNDeclareIdentifier) initExpr;
                            Object initValue = evaluate(i.getInitValue(), ncc);
                            if(i.getIdentifierToken() instanceof HNDeclareTokenIdentifier) {
                                String name = ((HNDeclareTokenIdentifier) i.getIdentifierToken()).getName();
                                ncc.getContext().vars().declareVar(name, i.getIdentifierType(), initValue);
                            }else{
                                HNDeclareTokenTuple ti = (HNDeclareTokenTuple) i.getIdentifierToken();
                                Tuple tupleInit = (Tuple) evaluate(i.getInitValue(), ncc);
                                for (int ii = 0; ii < ti.getItems().length; ii++) {
                                    //TODO must process HNDeclareTokenTuple
                                    HNDeclareTokenIdentifier id = (HNDeclareTokenIdentifier) ti.getItems()[ii];
                                    ncc.getContext().vars().declareVar(id.getName(), id.getIdentifierType(), tupleInit.valueAt(ii));
                                }
                            }
                        }
                    }
                    Object ret = null;
                    while (true) {
                        if (a.getFilter() != null) {
                            Boolean b = (Boolean) evaluate(a.getFilter(), ncc);
                            if (!b) {
                                break;
                            }
                        }
                        try {
                            ret = evaluate(a.getBody(), ncc);
                        } catch (InternalBreak b) {
                            break;
                        } catch (InternalContinue b) {
                            continue;
                        }
                        for (HNode inc : a.getIncs()) {
                            evaluate(inc, ncc);
                        }
                    }
                    return ret;
                }
                break;
            }
            case X_INVOKABLE_CALL: {
                HXInvokableCall a = (HXInvokableCall) node;
                JContext newCtx = context.getContext().newContext();
                return a.getInvokable().invoke(
                        context.builder().setContext(newCtx)
                                .setInstance(null)
                                .build()
                );
            }

//            case H_FIELD: {
//                HNField a = (HNField) node;
//                JField f = a.getField();
//                if (f.isStatic()) {
//                    return f.get(null);
//                } else {
//                    HNode instanceNode = a.getInstanceNode();
//                    Object instance = instanceNode == null ? context.instance().getValue() : evaluate(instanceNode, context);
//                    if (a.isNullableInstance()) {
//                        if (instance == null) {
//                            return f.type().defaultValue();
//                        } else {
//                            return f.get(instance);
//                        }
//                    } else {
//                        return f.get(instance);
//                    }
//                }
//            }
            case H_OP_COALESCE: {
                HNOpCoalesce a = (HNOpCoalesce) node;
                HNode b = a.getLeft();
                HNode e = a.getRight();
                Object v = evaluate(b, context);
                if (v != null) {
                    return v;
                }
                return evaluate(e, context);
            }
            case H_PARS: {
                HNPars a = (HNPars) node;
                HNode[] n = a.getItems();
                if (n.length == 1) {
                    return evaluate(n[0], context);
                }
                JType[] types = HUtils.getTypes(n);
                Object[] values = new Object[n.length];
                for (int i = 0; i < n.length; i++) {
                    values[i] = evaluate(n[i], context);
                }
                return new JUplet("", values, types);
            }
            case H_OP_BINARY: {
                HNOpBinaryCall c = (HNOpBinaryCall) node;
                JInvokablePrefilled impl = c.getImpl();
                return impl.invoke(context);
            }
            case H_OP_UNARY: {
                HNOpUnaryCall c = (HNOpUnaryCall) node;
                JInvokablePrefilled impl = c.impl();
                if (impl == null) {
                    int inc = 1;
                    if (c.getName().equals("++")) {
                        inc = 1;
                    } else if (c.getName().equals("--")) {
                        inc = -1;
                    } else {
                        throw new IllegalArgumentException("Unsupported");
                    }
                    if (c.isPostfixOperator()) {
                        //standard
                        HNode arg = c.getExpr();
                        switch (arg.id()) {
                            case H_IDENTIFIER: {
                                HNIdentifier a = (HNIdentifier) arg;
                                HNElement element = arg.getElement();
                                if(element.getKind()== HNElementKind.LOCAL_VAR) {
                                    return evalVarInc(a, inc,context)._1;
                                }else if(element.getKind()== HNElementKind.FIELD){
                                    return evalFieldInc(a,inc, context)._1;
                                }else{
                                    throw new JEvalException("Unsupported");
                                }
                            }
                            case H_BRACKETS_POSTFIX: {
                                return evalArrayInc((HNBracketsPostfix) arg, inc, context)._1;
                            }
                            default: {
                                throw new JEvalException("Unsupported");
                            }
                        }
                    } else {
                        //postfix
                        //standard
                        HNode arg = c.getExpr();
                        switch (arg.id()) {
                            case H_IDENTIFIER: {
                                HNIdentifier a = (HNIdentifier) arg;
                                HNElement element = arg.getElement();
                                if(element.getKind()== HNElementKind.LOCAL_VAR) {
                                    return evalVarInc(a, inc,context)._2;
                                }else if(element.getKind()== HNElementKind.FIELD){
                                    return evalFieldInc(a,inc, context)._2;
                                }else{
                                    throw new JEvalException("Unsupported");
                                }
                            }
                            case H_BRACKETS_POSTFIX: {
                                return evalArrayInc((HNBracketsPostfix) arg, inc, context)._2;
                            }
                            default: {
                                throw new JEvalException("Unsupported");
                            }
                        }
                    }
                } else {
                    return impl.invoke(context);
                }
            }
            case H_IDENTIFIER: {
                HNIdentifier a = (HNIdentifier) node;
                switch (a.getElement().getKind()){
                    case LOCAL_VAR:{
                        HNElementLocalVar ee = (HNElementLocalVar) a.getElement();
                        return context.getContext().vars().getValue(ee.getName(), context);
                    }
                    case FIELD:{
                        HNElementField ee = (HNElementField) a.getElement();
                        JField f = ee.getField();
                        if(f.isStatic()){
                            return f.get(null);
                        }else{
                            HNode p = a.parentNode();
                            if(p.id()==HNNodeId.H_OP_DOT){
                                HNOpDot d=(HNOpDot) p;
                                return f.get(evaluate(d.getLeft(),context));
                            }
                            return f.get(context.getInstance().getValue());
                        }
                    }
                    default:{
                        throw new JFixMeLaterException();
                    }
                }
            }
            case H_ASSIGN: {
                HNAssign n = (HNAssign) node;
                Object o = evaluate(((HNAssign) node).getRight(), context);
                switch (n.getAssignType()) {
//                    case FIELD: {
//                        HNField a = (HNField) n.getLeft();
//                        JField jfield = a.getField();
//                        if (jfield.isStatic()) {
//                            jfield.set(null, o);
//                        } else {
//                            Object _THIS = evaluate(a.getInstanceNode(), context);
//                            jfield.set(_THIS, o);
//                        }
//                        return o;
//                    }
                    case VAR: {
                        HNIdentifier a = (HNIdentifier) n.getLeft();
                        context.getContext().vars().setValue(a.getName(), o, context);
                        return o;
                    }
                    case ARRAY: {
                        HNArrayCall a = (HNArrayCall) n.getLeft();
                        Object v = evaluate(a.getArrayInstanceNode(), context);
                        if (v == null) {
                            throw new NullPointerException();
                        }
                        int index = ((Number) evaluate(a.getIndexNodes()[0], context)).intValue();
                        JTypeArray t = (JTypeArray) context.getContext().types().typeOf(v);
                        JArray aa = t.asArray(v);
                        aa.set(index, o);
                        return o;
                    }
                }
                break;
            }
            case H_ARRAY_NEW: {
                HNArrayNew a = (HNArrayNew) node;
                JTypeArray arrayType = (JTypeArray) a.getArrayType();
                HNode[] inits = a.getInits();
                int[] allLen = new int[arrayType.arrayDimension()];
                for (int i = 0; i < allLen.length; i++) {
                    if (i < inits.length) {
                        allLen[i] = ((Number) evaluate(inits[i], context)).intValue();
                    }
                }
                Object newArray = arrayType.newArray(allLen);
                if (a.getConstructor() != null) {
                    HNode setter = a.getConstructor();
                    if (setter instanceof HNDeclareInvokable) {
                        HNDeclareInvokable fd = (HNDeclareInvokable) setter;
                        JArray jarr0 = arrayType.asArray(newArray);
                        JFunction functionHandler = (JFunction) fd.getInvokable();
                        JType intType = JTypeUtils.forInt(context.getContext().types());
                        switch (arrayType.arrayDimension()) {
                            case 1: {
                                for (int i = 0; i < allLen[0]; i++) {
                                    Object v = functionHandler.invoke(
                                            context.builder()
                                                    .setName("<anonymous-function>")
                                                    .setArguments(new JEvaluable[]{
                                                            new JEvaluableValue(i, intType)
                                                    })
                                                    .build()
                                    );
                                    jarr0.set(i, v);
                                }
                                break;
                            }
                            case 2: {
                                JTypeArray tt = arrayType;
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        Object v = functionHandler.invoke(
                                                context.builder()
                                                        .setName("anonymous-function")
                                                        .setArguments(new JEvaluable[]{
                                                                new JEvaluableValue(i, intType),
                                                                new JEvaluableValue(j, intType)
                                                        })
                                                        .build()
                                        );
                                        jarr1.set(j, v);
                                    }
                                }
                                break;
                            }
                            case 3: {
                                JTypeArray tt = arrayType;
                                JTypeArray tt1 = (JTypeArray) arrayType.componentType();
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        JArray jarr2 = tt1.asArray(jarr1.get(j));
                                        for (int k = 0; k < allLen[2]; k++) {
                                            Object v = functionHandler.invoke(
                                                    context.builder()
                                                            .setName("anonymous-function")
                                                            .setArguments(new JEvaluable[]{
                                                                    new JEvaluableValue(i, intType),
                                                                    new JEvaluableValue(j, intType),
                                                                    new JEvaluableValue(k, intType),
                                                            })
                                                            .build()
                                            );
                                            jarr2.set(j, v);
                                        }
                                    }
                                }
                                break;
                            }
                            case 4: {
                                JTypeArray tt = arrayType;
                                JTypeArray tt1 = (JTypeArray) arrayType.componentType();
                                JTypeArray tt2 = (JTypeArray) tt1.componentType();
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        JArray jarr2 = tt1.asArray(jarr1.get(j));
                                        for (int k = 0; k < allLen[2]; k++) {
                                            JArray jarr3 = tt2.asArray(jarr2.get(j));
                                            for (int l = 0; l < allLen[3]; l++) {
                                                Object v = functionHandler.invoke(
                                                        context.builder()
                                                                .setName("anonymous-function")
                                                                .setArguments(new JEvaluable[]{
                                                                        new JEvaluableValue(i, intType),
                                                                        new JEvaluableValue(j, intType),
                                                                        new JEvaluableValue(k, intType),
                                                                        new JEvaluableValue(k, intType)
                                                                })
                                                                .build()
                                                );
                                                jarr3.set(j, v);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Not Supported yet arrays of dimension " + arrayType.arrayDimension());
                            }
                        }
                    } else {
                        Object v = evaluate(setter, context);
                        JArray jarr0 = arrayType.asArray(newArray);
                        switch (arrayType.arrayDimension()) {
                            case 1: {
                                for (int i = 0; i < allLen[0]; i++) {
                                    jarr0.set(i, v);
                                }
                                break;
                            }
                            case 2: {
                                JTypeArray tt = arrayType;
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        jarr1.set(j, v);
                                    }
                                }
                                break;
                            }
                            case 3: {
                                JTypeArray tt = arrayType;
                                JTypeArray tt1 = (JTypeArray) arrayType.componentType();
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        JArray jarr2 = tt1.asArray(jarr1.get(j));
                                        for (int k = 0; k < allLen[2]; k++) {
                                            jarr2.set(j, v);
                                        }
                                    }
                                }
                                break;
                            }
                            case 4: {
                                JTypeArray tt = arrayType;
                                JTypeArray tt1 = (JTypeArray) arrayType.componentType();
                                JTypeArray tt2 = (JTypeArray) tt1.componentType();
                                for (int i = 0; i < allLen[0]; i++) {
                                    JArray jarr1 = tt.asArray(jarr0.get(i));
                                    for (int j = 0; j < allLen[1]; j++) {
                                        JArray jarr2 = tt1.asArray(jarr1.get(j));
                                        for (int k = 0; k < allLen[2]; k++) {
                                            JArray jarr3 = tt2.asArray(jarr2.get(j));
                                            for (int l = 0; l < allLen[3]; l++) {
                                                jarr3.set(j, v);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Not Supported yet arrays of dimension " + arrayType.arrayDimension());
                            }
                        }
                    }
                }
                return newArray;
            }
            case H_ARRAY_CALL: {
                HNArrayCall n = (HNArrayCall) node;
                Object v = evaluate(n.getArrayInstanceNode(), context);
                if (v == null) {
                    throw new NullPointerException();
                }
                int index = ((Number) evaluate(n.getIndexNodes()[0], context)).intValue();
                JTypeArray t = (JTypeArray) context.getContext().types().typeOf(v);
                JArray a = t.asArray(v);
                return a.get(index);
            }

            case H_LITERAL: {
                HNLiteral a = (HNLiteral) node;
                return a.getValue();
            }
            case H_OBJECT_NEW: {
                HNObjectNew a = (HNObjectNew) node;
                return a.getConstructor().invoke(context);
            }
            case H_THIS: {
                Object instance = context.getInstance().getValue();
                if (instance == null) {
                    throw new JEvalException("No This in context");
                }
                return instance;
            }
            case H_META_IMPORT_PACKAGE: {
                HNMetaImportPackage a = (HNMetaImportPackage) node;
                Object instance = context.getInstance().getValue();
                if (!(instance instanceof HLCompilerEnv)) {
                    throw new JEvalException("No Compiler in context");
                }
                HLCompilerEnv ce = (HLCompilerEnv) instance;
                String p = (String) evaluate(a.getImportedPackageNode(), context);
                List<String> exclusions = new ArrayList<>();
                for (HNode exclusion : a.getExclusions()) {
                    exclusions.add((String) evaluate(exclusion, context));
                }
                ce.addDependency(new HLDependency(
                        p, a.getScope(),
                        a.isOptional(),
                        exclusions.toArray(new String[0])
                ));
                return instance;
            }
            case H_LAMBDA_EXPR: {
                HNLambdaExpression lnode = (HNLambdaExpression) node;
                if (lnode.getProxy() == null) {
                    JType t = lnode.getElement().getType();
                    if (t instanceof HostJRawType) {
                        Class<?> aClass = null;
                        try {
                            aClass = Class.forName(t.getName());
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                        JInvokeContext newContext = context.builder().build();
                        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                                new Class[]{
                                        aClass
                                }, new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                        if (method.getName().equals(lnode.getTargetMethod().name())) {
                                            JEvaluable[] a = new JEvaluable[args.length];
                                            for (int i = 0; i < a.length; i++) {
                                                a[i] = new JEvaluableValue(args[i], lnode.getArguments().get(i).getIdentifierType());
                                            }
                                            JInvokeContext b = newContext.builder().setArguments(a)
                                                    .setInstance(new DefaultJTypedValue(proxy, t))
                                                    .build();
                                            return new BodyJInvoke(lnode).invoke(b);
                                        } else if (method.getName().equals("toString")) {
                                            return "ProxyForLambda(" + lnode.getElement().getType().getName() + ")";
                                        } else if (method.getName().equals("equals")) {
                                            return false;
                                        } else if (method.getName().equals("hashCode")) {
                                            return 0;
                                        } else {
                                            return null;
                                        }
                                    }
                                }
                        );
                        lnode.setProxy(proxy);
                    } else {
                        throw new JFixMeLaterException();
                    }
                }
                return lnode.getProxy();
            }
            case H_BREAK: {
                HNBreak label = (HNBreak) node;
                throw new InternalBreak(label.getLabel());
            }
            case H_CONTINUE: {
                HNContinue label = (HNContinue) node;
                throw new InternalContinue(label.getLabel());
            }
            case H_RETURN: {
                HNReturn n = (HNReturn) node;
                Object e = evaluate(n.getExpr(), context);
                throw new InternalReturn(e);
            }
            case H_IS: {
                HNIs n = (HNIs) node;
                Object e = evaluate(n.getBase(), context);
                JType t = n.getIdentifierType();
                if (t.isInstance(e)) {
                    return e;
                }
                throw new ClassCastException("Cannot cast " + (e == null ? "null" : context.getContext().types().typeOf(e)) + " to " + t.getName());
            }
            case H_STRING_INTEROP: {
                HNStringInterop n = (HNStringInterop) node;
                Object[] pp=new Object[n.getExpressions().length];
                for (int i = 0; i < pp.length; i++) {
                    pp[i]=evaluate(n.getExpressions()[i],context);
                }
                return MessageFormat.format(n.getJavaMessageFormatString(),pp);
            }
        }
        throw new JEvalException("Unable to evaluate " + ((HNode)node).id() + "@" + node.getClass().getSimpleName() + " : " + node);
    }

    private Tuple2<Object,Object> evalVarInc(HNIdentifier arg, int inc,JInvokeContext context) {
        String argTypeName = HNodeUtils.getType(arg).boxed().getName();
        JVars vars = context.getContext().vars();
        Object oldValue0 = vars.getValue(arg.getName(), context);
        Tuple2<Object, Object> ret = evalValueInc(argTypeName, oldValue0, inc);
        vars.setValue(arg.getName(), ret._2, context);
        return ret;
    }
    private Tuple2<Object,Object> evalFieldInc(HNIdentifier arg, int inc,JInvokeContext context) {
        String argTypeName = HNodeUtils.getType(arg).boxed().getName();
        HNElementField efield=(HNElementField) arg.getElement();
        JField field = efield.getField();
        Object base=null;
        if(field.isStatic()) {
        }else{
            HNode n=arg.parentNode();
            if(n instanceof HNOpDot){
                base=((HNOpDot) n).getLeft();
            }
        }
        Object oldValue0 = field.get(base);
        Tuple2<Object, Object> ret = evalValueInc(argTypeName, oldValue0, inc);
        field.set(base,ret._2);
        return ret;
    }

    private Tuple2<Object,Object> evalArrayInc(HNBracketsPostfix arg, int inc, JInvokeContext context) {
        HNBracketsPostfix a = arg;
        int index = (int) evaluate(a.getRight().get(0), context);
        JTypeArray arrayType = (JTypeArray) a.getLeft().getElement().getType();
        String argTypeName = arrayType.componentType().getName();
        JArray array = arrayType.asArray(evaluate(a.getLeft(), context));
        Object oldValue0 = array.get(index);
        Tuple2<Object, Object> ret = evalValueInc(argTypeName, oldValue0, inc);
        array.set(index, ret._2);
        return ret;
    }

    private Tuple2<Object,Object> evalValueInc(String argTypeName, Object oldValue0, int inc) {
        switch (argTypeName) {
            case "java.lang.Byte": {
                Byte oldValue = ((Byte) oldValue0);
                Byte newValue = (byte) (oldValue + (byte)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Character": {
                Character oldValue = ((Character) oldValue0);
                Character newValue = (char) (oldValue + (char)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Short": {
                Short oldValue = ((Short) oldValue0);
                Short newValue = (short) (oldValue + (short)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Integer": {
                Integer oldValue = ((Integer) oldValue0);
                Integer newValue = (int) (oldValue + (int)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Long": {
                Long oldValue = ((Long) oldValue0);
                Long newValue = (long) (oldValue + (long)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Float": {
                Float oldValue = ((Float) oldValue0);
                Float newValue = (float) (oldValue + (float)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            case "java.lang.Double": {
                Double oldValue = ((Double) oldValue0);
                Double newValue = (double) (oldValue + (double)inc);
                return new Tuple2<>(oldValue,newValue);
            }
            default: {
                throw new JEvalException("Not supported type");
            }
        }

    }


    private Iterator resolveIteratorOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof Iterator) {
            return (Iterator) a;
        }
        if (a instanceof Iterable) {
            return ((Iterable) a).iterator();
        }
        Stream q = resolveStreamOrNull(a);
        if (q != null) {
            return q.iterator();
        }
        return null;
    }

    private Iterable resolveIterableOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof Iterable) {
            return (Iterable) a;
        }
        if (a instanceof boolean[]) {
            return () -> {
                boolean[] x = (boolean[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof byte[]) {
            return () -> {
                byte[] x = (byte[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof short[]) {
            return () -> {
                short[] x = (short[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof char[]) {
            return () -> {
                char[] x = (char[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof CharSequence) {
            return () -> ((CharSequence) a).chars().mapToObj(i -> (char) i).iterator();
        } else if (a instanceof int[]) {
            return () -> Arrays.stream((int[]) a).boxed().iterator();
        } else if (a instanceof long[]) {
            return () -> Arrays.stream((long[]) a).boxed().iterator();
        } else if (a instanceof float[]) {
            return () -> {
                float[] x = (float[]) a;
                return IntStream.range(0, x.length)
                        .mapToObj(i -> x[i]).iterator();
            };
        } else if (a instanceof double[]) {
            return () -> Arrays.stream((double[]) a).boxed().iterator();
        } else if (a.getClass().isArray()) {
            return () -> Arrays.stream((Object[]) a).iterator();
        } else if (a instanceof IntRange) {
            return () -> ((IntRange) a).stream().iterator();
        }
        return null;
    }

    private Stream resolveStreamOrNull(Object a) {
        if (a == null) {
            return null;
        }
        if (a instanceof boolean[]) {
            boolean[] x = (boolean[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof byte[]) {
            byte[] x = (byte[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof short[]) {
            short[] x = (short[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof char[]) {
            char[] x = (char[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof CharSequence) {
            return ((CharSequence) a).chars().mapToObj(i -> (char) i);
        } else if (a instanceof int[]) {
            return Arrays.stream((int[]) a).boxed();
        } else if (a instanceof long[]) {
            return Arrays.stream((long[]) a).boxed();
        } else if (a instanceof float[]) {
            float[] x = (float[]) a;
            return IntStream.range(0, x.length)
                    .mapToObj(i -> x[i]);
        } else if (a instanceof double[]) {
            return Arrays.stream((double[]) a).boxed();
        } else if (a.getClass().isArray()) {
            return Arrays.stream((Object[]) a);
        } else if (a instanceof Iterable) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize((((Iterable) a).iterator()), Spliterator.ORDERED),
                    false);
        } else if (a instanceof Iterator) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize((Iterator) a, Spliterator.ORDERED),
                    false);
        } else if (a instanceof Stream) {
            return (Stream) a;
        } else if (a instanceof IntStream) {
            return ((IntStream) a).boxed();
        } else if (a instanceof LongStream) {
            return ((LongStream) a).boxed();
        } else if (a instanceof DoubleStream) {
            return ((DoubleStream) a).boxed();
        } else if (a instanceof IntRange) {
            return ((IntRange) a).stream().boxed();
        }
        return null;
    }

    private JArray evaluateArray(HNode expr, JInvokeContext jContext) {
        Object a = evaluate(expr, jContext);
        JTypes types = jContext.getContext().types();
        if (a == null) {
            return null;
        }
        if (a.getClass().isArray()) {
            return new HostJArray(a, types.forName(a.getClass().getName()));
        }
        if (a instanceof Iterable) {
            JType componentType = null;
            List<Object> all = new ArrayList<>();
            for (Object o : ((Iterable) a)) {
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
                all.add(o);
            }
            return new HostJArray(new Object[0], componentType);
        }
        if (a instanceof Iterator) {
            List<Object> all = new ArrayList<>();
            Iterator i = (Iterator) a;
            JType componentType = null;
            while (i.hasNext()) {
                Object o = i.next();
                all.add(i);
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
            }
            return new HostJArray(new Object[0], componentType);
        }
        if (a instanceof Stream) {
            Object[] b = ((Stream) a).toArray();
            JType componentType = null;
            for (Object o : b) {
                JType p = types.typeOf(o);
                if (p != null) {
                    if (componentType == null) {
                        componentType = p;
                    } else {
                        componentType = componentType.firstCommonSuperType(p);
                    }
                }
            }
            return new HostJArray(b, componentType);
        }
        throw new JEvalException("Expected an iterable type but found " + a.getClass().getName());
    }

    private Iterable iter(Object val, InitValueConstraint c) {
        Iterable i = resolveIterableOrNull(val);
        if (i != null) {
            return i;
        }
        if (val != null) {
            throw new IllegalArgumentException("Cannot resolve iterator from " + val.getClass().getSimpleName());
        }
        return null;
    }

    private boolean evaluateBoolean(HNode node, JInvokeContext context) {
        Object v = evaluate(node, context);
        if (v instanceof Boolean) {
            return ((Boolean) v);
        }
        return false;
    }

    public interface InvokeRunnable {
        void invoke(JInvokeContext context);
    }

    private static class Loop {
        private String label;
        private Iterable iter;
        private String varName;
        private JType type;
        private HNode bodyNode;
        private HNode filter;
        private HNode[] incs;
        private Loop bodyLoop;

        public Loop(String label, Iterable iter, String varName, JType type, HNode bodyNode, HNode filter, HNode[] incs) {
            this.label = label;
            this.iter = iter;
            this.varName = varName;
            this.type = type;
            this.bodyNode = bodyNode;
            this.filter = filter;
            this.incs = incs;
        }

        public Loop(String label, Iterable iter, String varName, JType type, Loop bodyLoop, HNode filter, HNode[] incs) {
            this.label = label;
            this.iter = iter;
            this.varName = varName;
            this.type = type;
            this.bodyLoop = bodyLoop;
            this.filter = filter;
            this.incs = incs;
        }

        public Object run(JInvokeContext context) {
            JInvokeContext cc = context.builder().setContext(context.getContext().newContext()).build();
            Iterator iter = this.iter.iterator();
            cc.getContext().vars().declareVar(varName, type, null);
            runInit(cc);
            Object ret = null;
            while (iter.hasNext()) {
                Object item = iter.next();
                cc.getContext().vars().setValue(varName, item, context);
                runItem(context, item);
                if (filter != null) {
                    Boolean b = (Boolean) cc.evaluate(filter);
                    if (!b.booleanValue()) {
                        continue;
                    }
                }
                try {
                    if (bodyNode != null) {
                        ret = cc.evaluate(bodyNode);
                    } else if (bodyLoop != null) {
                        ret = bodyLoop.run(cc);
                    }
                } catch (InternalBreak b) {
                    if (b.getLabel() == null || b.getLabel().equals(label)) {
                        break;
                    } else {
                        throw b;
                    }
                } catch (InternalContinue b) {
                    if (b.getLabel() == null || b.getLabel().equals(label)) {
                        //continue;
                    } else {
                        throw b;
                    }
                }
                for (HNode inc : incs) {
                    ret = cc.evaluate(inc);
                }
            }
            return ret;
        }

        public void runInit(JInvokeContext context) {

        }

        public void runItem(JInvokeContext context, Object item) {

        }
    }

    public static class InternalBreak extends RuntimeException {
        private String label;

        public InternalBreak(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class InternalContinue extends RuntimeException {
        private String label;

        public InternalContinue(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class InternalReturn extends RuntimeException {
        private Object value;

        public InternalReturn(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}
