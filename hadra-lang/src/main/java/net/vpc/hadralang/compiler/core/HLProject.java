package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.eval.JEvaluableValue;
import net.vpc.common.jeep.impl.functions.DefaultJInvokeContext;
import net.vpc.hadralang.compiler.index.HLIndexedProject;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.index.HLIndexer;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.core.invokables.JNodeHBlocJInvoke;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.*;

public class HLProject implements HLProjectContext{
    HNDeclareType metaPackageType = new HNDeclareType();
    private JContext context;
    private HLIndexer indexer;
    private HLIndexedProject indexedProject;
    private HNDeclareMetaPackage resolvedMetaPackage;
    private String rootId;
    private Map<String,Object> userProperties=new HashMap<>();

    public HLProject(JContext context,HLIndexer indexer) {
        this.context = context;
        this.indexer = indexer;
    }

    public HNDeclareMetaPackage getResolvedMetaPackage() {
        return resolvedMetaPackage;
    }

    public HLProject setResolvedMetaPackage(HNDeclareMetaPackage resolvedMetaPackage) {
        this.resolvedMetaPackage = resolvedMetaPackage;
        return this;
    }

    public String rootId() {
        return rootId;
    }

    public HLProject setRootId(String rootId) {
        this.rootId = rootId;
        return this;
    }

    public HLIndexer indexer() {
        return indexer;
    }

    public HLProject setIndexer(HLIndexer indexer) {
        this.indexer = indexer;
        return this;
    }

    private List<JCompilationUnit> compilationUnits = new ArrayList<>();


    public HLJCompilerContext newCompilerContext(){
        return new HLJCompilerContext(this);
    }

    public HLJCompilerContext newCompilerContext(JCompilationUnit compilationUnit){
        return new HLJCompilerContext(this,compilationUnit);
    }

    public void addCompilationUnit(JCompilationUnit c) {
        compilationUnits.add(c);
        JNode n = c.getAst();
        if(metaPackageType.startToken()==null){
            metaPackageType.setStartToken(n.startToken());
        }
        metaPackageType.setBody(new HNBlock(HNBlock.BlocType.GLOBAL_BODY, new HNode[0],metaPackageType.startToken(),metaPackageType.endToken()));
        metaPackageType.setModifiers(HUtils.PUBLIC);
    }



    public boolean isSuccessful() {
        return log().isSuccessful();
    }

    public int errorCount() {
        return log().errorCount();
    }

    public int warningCount() {
        return log().warningCount();
    }

    public JCompilerLog log() {
        return context.log();
    }

    public JContext languageContext() {
        return context;
    }

    public JCompilationUnit getCompilationUnit(int index) {
        return compilationUnits.get(index);
    }

    public JCompilationUnit[] getCompilationUnits() {
        return compilationUnits.toArray(new JCompilationUnit[0]);
    }


    public void printCompilationUnits() {
        for (JCompilationUnit compilationUnit : compilationUnits) {
            System.out.println("-------------------------------------------");
            System.out.println(compilationUnit.toString());
            System.out.println("-------------------------------------------");
            System.out.println(compilationUnit.getAst().toString());
        }
    }

    public Object run(String[] args) {
//        printCompilationUnits();
        Object result = null;
        JContext context = this.context.newContext();
        HNDeclareInvokable main = HNodeUtils.getMainMethod(metaPackageType);
        //no entry point is found so executing all static code in the module!!
        HNBlock bloc = (HNBlock) metaPackageType.getBody();
        if (main != null) {
            //an entry point is found
            //so executing initializer without arguments
            new JNodeHBlocJInvoke(bloc)
                    .invoke(new DefaultJInvokeContext(
                            context,
                            this.context.evaluators().newEvaluator(),
                            null,
                            new JEvaluable[]{
                                    new JEvaluableValue(new String[0],context.types().forName(String.class.getName()).toArray())
                            },
                            "main",null
                    ));

            //then executing main with arguments
            return main.getInvokable().invoke(new DefaultJInvokeContext(
                    context,
                    this.context.evaluators().newEvaluator(),
                    null,
                    new JEvaluable[]{new JEvaluableValue(args,context.types().forName(String.class.getName()).toArray())},
                    "main",null
            ));
        } else {
            //no entry point is found
            //so executing initializer with arguments
            return new JNodeHBlocJInvoke(bloc)
                    .invoke(new DefaultJInvokeContext(
                            context,
                            this.context.evaluators().newEvaluator(),
                            null,
                            new JEvaluable[]{new JEvaluableValue(args,context.types().forName(String.class.getName()).toArray())},
                            "main",null
                    ));
        }
    }

    public HNDeclareType getMetaPackageType() {
        return metaPackageType;
    }

    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public HLIndexedProject getIndexedProject() {
        return indexedProject;
    }

    public HLProject setIndexedProject(HLIndexedProject indexedProject) {
        this.indexedProject = indexedProject;
        return this;
    }
}
