package net.hl.compiler.stages;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.JChildInfo;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNLambdaExpression;
import net.hl.compiler.stages.generators.java.HJavaGenUtils;
import net.vpc.common.jeep.util.JeepUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class HStageType1 extends AbstractHStage{
    public abstract JNode processCompilerStage(JCompilerContext compilerContextBase) ;

    public void processProject(HProject project, HOptions options) {
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStage(project.newCompilerContext(compilationUnit));
        }
    }


    public void processAllNextCompilerStage(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.getNode();
        List<JNode> jNodes = parentNode.getChildrenNodes();
        for (JNode jNode : jNodes) {
            if(jNode!=null) {
                JNode arg1b = processCompilerStage(compilerContextBase.nextNode(jNode));
                if (arg1b != jNode) {
                    JChildInfo s = jNode.getChildInfo();
                    if(s.isIndexed()){
                        String r=s.getName();
                        int index=Integer.parseInt(s.getIndex());
                        String setterName= JeepUtils.propertyToGetter(r,false);
                        Method m = null;
                        try {
                            m = jNode.getClass().getDeclaredMethod(setterName);
                            Object e = m.invoke(parentNode);
                            ((AbstractJNode) arg1b).parentNode(parentNode);
                            arg1b.setChildInfo(arg1b.getChildInfo());
                            if(e.getClass().isArray()){
                                Array.set(e,index,arg1b);
                            }else if(e instanceof List){
                                ((List)e).set(index,arg1b);
                            }else{
                                throw new JShouldNeverHappenException("Not a list "+e);
                            }
                        } catch (Exception e) {
                            throw new JShouldNeverHappenException(e);
                        }
                    }else{
                        String setterName=JeepUtils.propertyToSetter(s.getName());
                        Method m = null;
                        try {
                            m = jNode.getClass().getDeclaredMethod(setterName);
                            m.invoke(parentNode,arg1b);
                        } catch (Exception e) {
                            throw new JShouldNeverHappenException(e);
                        }
                    }
                }
            }
        }
    }
    public <T extends JNode> T processNextCompilerStage(JCompilerContext compilerContextBase, Supplier<T> getter, Consumer<T> setter) {
        T arg1 = getter.get();
        if (arg1 == null) {
            return null;
        }
        T arg1b = (T) processCompilerStage(compilerContextBase.nextNode(arg1));
        if (arg1b != arg1) {
            setter.accept(arg1b);
        }
        return arg1b;
    }

    public <T extends JNode> void processNextCompilerStage(JCompilerContext compilerContextBase,List<T> nargs) {
        AbstractJNode parentNode=(AbstractJNode) compilerContextBase.getNode();
        if (nargs != null) {
            for (int i = 0; i < nargs.size(); i++) {
                final int ii = i;
                T argi = nargs.get(ii);
                if (argi != null) {
                    T argib = (T) processCompilerStage(compilerContextBase.nextNode(argi));
                    if(argib!=argi){
                        //copy bind info!
                        ((AbstractJNode) argib).parentNode(parentNode);
                        argib.setChildInfo(argi.getChildInfo());
                        nargs.set(i, argib);
                    }
                }
            }
        }
    }
    public <T extends JNode> void processNextCompilerStage(JCompilerContext compilerContextBase,JNode[] nargs) {
        AbstractJNode parentNode=(AbstractJNode) compilerContextBase.getNode();
        if (nargs != null) {
            for (int i = 0; i < nargs.length; i++) {
                T argi = (T) nargs[i];
                if (argi != null) {
                    T argib = (T) processCompilerStage(compilerContextBase.nextNode(argi));
                    if(argib!=argi){
                        //copy bind info!
                        ((AbstractJNode) argib).parentNode(parentNode);
                        argib.setChildInfo(argi.getChildInfo());
                        nargs[i]=argib;
                    }
                }
            }
        }
    }

    public void attachLambdaTypes(JInvokable f, JNode[] nargs, JCompilerContext context){
        for (int i = 0; i < nargs.length; i++) {
            JNode narg = nargs[i];
            if(/*narg.getType()==null && */narg instanceof HNLambdaExpression){
                //try to match lambda expression
                HNLambdaExpression lx=(HNLambdaExpression) narg;
                JType y = f.getSignature().argType(i);
//                lx.setType(y);
                JMethod[] dm = Arrays.stream(y.getDeclaredMethods()).filter(x->x.isAbstract() && !x.isStatic())
                        .toArray(JMethod[]::new);
                if(dm.length==1){
                    JSignature signature = dm[0].getSignature();
                    for (int j = 0; j < signature.argsCount(); j++) {
                        lx.getArguments().get(j).setIdentifierTypeNode(HJavaGenUtils.type(signature.argType(j)));
                    }
                    lx.setReturnType(dm[0].getReturnType());
                    lx.setReturnTypeName(dm[0].getReturnType().typeName());
                    lx.setSignature(dm[0].getSignature().nameSignature());
                    lx.setTargetMethod(dm[0]);
                }
                //apply type updates...
                processNextCompilerStage(narg,context);
            }
        }
    }

    public JNode processNextCompilerStage(JNode node, JCompilerContext compilerContext) {
        return processCompilerStage(compilerContext.nextNode(node));
    }

    public <T extends JNode> T processNextCompilerStage(Supplier<T> getter, Consumer<T> setter, JCompilerContext compilerContext) {
        T arg1 = getter.get();
        if (arg1 == null) {
            return null;
        }
        T arg1b = (T) processCompilerStage(compilerContext.nextNode(arg1));
        if (arg1b != arg1) {
            setter.accept(arg1b);
        }
        return arg1b;
    }

    public <T extends JNode> void processNextCompilerStage(JNode parentNode, List<T> nargs, JCompilerContext compilerContext) {
        if (nargs != null) {
            for (int i = 0; i < nargs.size(); i++) {
                final int ii = i;
                processNextCompilerStage(() -> nargs.get(ii), (x) -> {
                    if (x != null) {
                        ((AbstractJNode) x).parentNode(parentNode);
                    }
                    nargs.set(ii, x);
                }, compilerContext);
            }
        }
    }

    public <T extends JNode> void processNextCompilerStage(JNode parentNode, JNode[] nargs, JCompilerContext compilerContext) {
        if (nargs != null) {
            for (int i = 0; i < nargs.length; i++) {
                final int ii = i;
                processNextCompilerStage(() -> nargs[ii], (x) -> {
                    if (x != null) {
                        ((AbstractJNode) x).parentNode(parentNode);
                    }
                    nargs[ii] = x;
                }, compilerContext);
            }
        }
    }
}
