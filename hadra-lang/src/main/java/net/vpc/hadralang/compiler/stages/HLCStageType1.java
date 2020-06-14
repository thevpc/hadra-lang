package net.vpc.hadralang.compiler.stages;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JStringUtils;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.parser.ast.HNLambdaExpression;
import net.vpc.hadralang.compiler.utils.HNodeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class HLCStageType1 implements HLCStage{
    public abstract JNode processCompilerStage(JCompilerContext compilerContextBase) ;

    public void processProject(HLProject project, HLCOptions options) {
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStage(project.newCompilerContext(compilationUnit));
        }
    }


    public void processAllNextCompilerStage(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.node();
        List<JNode> jNodes = parentNode.childrenNodes();
        for (JNode jNode : jNodes) {
            if(jNode!=null) {
                JNode arg1b = processCompilerStage(compilerContextBase.nextNode(jNode));
                if (arg1b != jNode) {
                    String s = (String) jNode.childInfo();
                    int brackets = s.indexOf('[');
                    if(brackets>0){
                        String r=s.substring(0, brackets);
                        int index=Integer.parseInt(r.substring(brackets+1,r.length()-1));
                        String setterName="get"+ JStringUtils.capitalize(s);
                        Method m = null;
                        try {
                            m = jNode.getClass().getDeclaredMethod(setterName);
                            Object e = m.invoke(parentNode);
                            ((AbstractJNode) arg1b).parentNode(parentNode);
                            arg1b.childInfo(arg1b.childInfo());
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
                        String setterName="set"+JStringUtils.capitalize(s);
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
        AbstractJNode parentNode=(AbstractJNode) compilerContextBase.node();
        if (nargs != null) {
            for (int i = 0; i < nargs.size(); i++) {
                final int ii = i;
                T argi = nargs.get(ii);
                if (argi != null) {
                    T argib = (T) processCompilerStage(compilerContextBase.nextNode(argi));
                    if(argib!=argi){
                        //copy bind info!
                        ((AbstractJNode) argib).parentNode(parentNode);
                        argib.childInfo(argi.childInfo());
                        nargs.set(i, argib);
                    }
                }
            }
        }
    }
    public <T extends JNode> void processNextCompilerStage(JCompilerContext compilerContextBase,JNode[] nargs) {
        AbstractJNode parentNode=(AbstractJNode) compilerContextBase.node();
        if (nargs != null) {
            for (int i = 0; i < nargs.length; i++) {
                T argi = (T) nargs[i];
                if (argi != null) {
                    T argib = (T) processCompilerStage(compilerContextBase.nextNode(argi));
                    if(argib!=argi){
                        //copy bind info!
                        ((AbstractJNode) argib).parentNode(parentNode);
                        argib.childInfo(argi.childInfo());
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
                JType y = f.signature().argType(i);
//                lx.setType(y);
                JMethod[] dm = Arrays.stream(y.declaredMethods()).filter(x->x.isAbstract() && !x.isStatic())
                        .toArray(JMethod[]::new);
                if(dm.length==1){
                    JSignature signature = dm[0].signature();
                    for (int j = 0; j < signature.argsCount(); j++) {
                        lx.getArguments().get(j).setIdentifierTypeName(HNodeUtils.createTypeToken(signature.argType(j)));
                    }
                    lx.setReturnType(dm[0].returnType());
                    lx.setReturnTypeName(dm[0].returnType().typeName());
                    lx.setSignature(dm[0].signature().nameSignature());
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