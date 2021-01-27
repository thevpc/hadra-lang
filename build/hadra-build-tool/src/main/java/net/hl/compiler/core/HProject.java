package net.hl.compiler.core;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.eval.JEvaluableValue;
import net.thevpc.jeep.impl.functions.DefaultJInvokeContext;
import net.hl.compiler.index.HIndexedProject;
import net.hl.compiler.ast.*;
import net.hl.compiler.core.invokables.HLJCompilerContext;
import net.hl.compiler.core.invokables.JNodeHBlocJInvoke;
import net.hl.compiler.utils.HNodeUtils;

import java.util.*;
import net.hl.compiler.index.HIndexer;

public class HProject implements HProjectContext{
    HNDeclareType metaPackageType = new HNDeclareType();
    private JContext context;
    private HIndexer indexer;
    private HIndexedProject indexedProject;
    private HNDeclareMetaPackage resolvedMetaPackage;
    private String rootId;
    private Map<String,Object> userProperties=new HashMap<>();

    public HProject(JContext context,HIndexer indexer) {
        this.context = context;
        this.indexer = indexer;
        metaPackageType.addAnnotationNoDuplicates(HNodeUtils.createAnnotationModifierCall("public"));
    }

    public HNDeclareMetaPackage getResolvedMetaPackage() {
        return resolvedMetaPackage;
    }

    public HProject setResolvedMetaPackage(HNDeclareMetaPackage resolvedMetaPackage) {
        this.resolvedMetaPackage = resolvedMetaPackage;
        return this;
    }

    @Override
    public String rootId() {
        return rootId;
    }

    public HProject setRootId(String rootId) {
        this.rootId = rootId;
        return this;
    }

    public HIndexer indexer() {
        return indexer;
    }

    public HProject setIndexer(HIndexer indexer) {
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
        if(metaPackageType.getStartToken()==null){
            metaPackageType.setStartToken(n.getStartToken());
        }
        metaPackageType.setBody(new HNBlock(HNBlock.BlocType.GLOBAL_BODY, new HNode[0],metaPackageType.getStartToken(),metaPackageType.getEndToken()));
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
                            "main",null,null
                    ));

            //then executing main with arguments
            return main.getInvokable().invoke(new DefaultJInvokeContext(
                    context,
                    this.context.evaluators().newEvaluator(),
                    null,
                    new JEvaluable[]{new JEvaluableValue(args,context.types().forName(String.class.getName()).toArray())},
                    "main",null,null
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
                            "main",null,null
                    ));
        }
    }

    public HNDeclareType getMetaPackageType() {
        return metaPackageType;
    }

    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public HIndexedProject getIndexedProject() {
        return indexedProject;
    }

    public HProject setIndexedProject(HIndexedProject indexedProject) {
        this.indexedProject = indexedProject;
        return this;
    }
}
