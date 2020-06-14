/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class HNBlock extends HNode {
    private List<HNode> statements = new ArrayList<HNode>();
    private BlocType blocType = BlocType.UNKNOWN;
    private Map<Class,List> cacheByType = new HashMap<>();
//    private List<HNDeclareIdentifier> varDeclarations = new ArrayList<HNDeclareIdentifier>();
//    private List<HNDeclareInvokable> functionDeclarations = new ArrayList<HNDeclareInvokable>();
//    private List<HNDeclareType> classDeclarations = new ArrayList<HNDeclareType>();

    public HNBlock() {
        super(HNNodeId.H_BLOCK);
    }

    protected HNBlock(BlocType blocType) {
        super(HNNodeId.H_BLOCK);
        this.blocType=blocType;
    }

    public HNBlock(BlocType blocType,HNode[] statements,JToken startToken,JToken endToken) {
        this();
        this.blocType=blocType;
        setStatements(new ArrayList<>(Arrays.asList(statements)));
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public boolean isEmpty(){
        return statements.isEmpty();
    }
//
//    public static HNBlock of(HNode node) {
//        if (node instanceof HNBlock) {
//            return (HNBlock) node;
//        }
//        HNBlock h = new HNBlock();
//        if (node != null) {
//            h.setStartToken(node.startToken());
//            h.add(node);
//        }
//        return h;
//    }

    public BlocType getBlocType() {
        return blocType;
    }

    public HNBlock setBlocType(BlocType blocType) {
        this.blocType = blocType;
        return this;
    }

    public static HNode[] getExitPoints(HNode[] statements) {
        List<HNode> returns = new ArrayList<>();
        HNode[] last = new HNode[0];
        for (int i = 0; i < statements.length; i++) {
            HNode statement = (HNode) statements[i];
            HNode[] exitPoints = statement.getExitPoints();
            if (i == statements.length - 1) {
                returns.addAll(Arrays.asList(exitPoints));
                return returns.toArray(new HNode[0]);
            } else {
                List<HNode> currentLast = new ArrayList<>();
                for (HNode exitPoint : exitPoints) {
                    if (exitPoint instanceof HNReturn) {
                        returns.add(exitPoint);
                    } else {
                        currentLast.add(exitPoint);
                    }
                }
                last = currentLast.toArray(new HNode[0]);
            }
        }
        returns.addAll(Arrays.asList(last));
        return returns.toArray(new HNode[0]);
    }

    public static String toString(HNode[] statements, boolean includeBraces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < statements.length; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(statements[i].toString());
            if (sb.length() > 0) {
                char c = sb.charAt(sb.length() - 1);
                if (c != '}' && c != ';') {
                    sb.append(';');
                }
            }
        }
        if (includeBraces) {
            return "{\n" + JeepUtils.indent(sb.toString()) + "\n}";
        }
        return sb.toString();
    }

//    private List<HNode> runnableBlock = new ArrayList<HNode>();

    public List<HNDeclareIdentifier> getVarDeclarations() {
        return statements.stream().filter(x -> x instanceof HNDeclareIdentifier)
                .map(x -> (HNDeclareIdentifier) x)
                .collect(Collectors.toList());
    }

    public List<HNDeclareInvokable> getFunctionDeclarations() {
        return statements.stream().filter(x -> x instanceof HNDeclareInvokable)
                .map(x -> (HNDeclareInvokable) x)
                .collect(Collectors.toList());
    }

    public List<HNDeclareType> getClassDeclarations() {
        return statements.stream().filter(x -> x instanceof HNDeclareType)
                .map(x -> (HNDeclareType) x)
                .collect(Collectors.toList());
    }

    public List<HNode> getRunnableBlock() {
        List<HNode> r = new ArrayList<>();
        for (HNode statement : statements) {
            if (statement instanceof HNDeclareType || statement instanceof HNDeclareInvokable) {
                //
            } else if (statement instanceof HNDeclareIdentifier) {
                HNDeclareIdentifier a = (HNDeclareIdentifier) statement;
                HNode i = a.getInitializerStatement();
                if (i != null) {
                    r.add(i);
                }
            } else {
                r.add(statement);
            }
        }
        return r;
    }

    public void add(HNode node) {
        statements.add(JNodeUtils.bind(this,node,"statements",statements.size()));
        cacheByType.clear();
    }

    public List<HNode> getStatements() {
        return statements;
    }

    public HNBlock setStatements(List<HNode> statements) {
        this.statements = JNodeUtils.bind(this,statements,"statements");
        return this;
    }

    @Override
    public String toString() {
        return toString(statements.toArray(new HNode[0]), true);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getStatements());
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBlock) {
            HNBlock o = (HNBlock) node;
            this.statements = JNodeUtils.copy(o.statements);
            this.blocType = o.blocType;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return (List) this.statements;
    }

    public List<HNDeclareType> findDeclaredTypes(boolean fast){
        if(fast){
            List li = cacheByType.get(HNDeclareType.class);
            if(li!=null){
                return li;
            }
            li=findDeclaredTypes();
            cacheByType.put(HNDeclareType.class,li);
            return li;
        }
        return findDeclaredTypes();
    }

    public List<HNDeclareInvokable> findDeclaredInvokables(boolean fast){
        if(fast){
            List li = cacheByType.get(HNDeclareInvokable.class);
            if(li!=null){
                return li;
            }
            li=findDeclaredTypes();
            cacheByType.put(HNDeclareInvokable.class,li);
            return li;
        }
        return findDeclaredInvokables();
    }

    public List<HNDeclareIdentifier> findDeclaredIdentifiers(boolean fast){
        if(fast){
            List li = cacheByType.get(HNDeclareIdentifier.class);
            if(li!=null){
                return li;
            }
            li=findDeclaredTypes();
            cacheByType.put(HNDeclareIdentifier.class,li);
            return li;
        }
        return findDeclaredIdentifiers();
    }

    public List<HNDeclareMetaPackage> findDeclaredMetaPackages(boolean fast){
        if(fast){
            List li = cacheByType.get(HNDeclareMetaPackage.class);
            if(li!=null){
                return li;
            }
            li=findDeclaredTypes();
            cacheByType.put(HNDeclareMetaPackage.class,li);
            return li;
        }
        return findDeclaredMetaPackages();
    }

    public List<HNDeclareType> findDeclaredTypes(){
        List<HNDeclareType> all=new ArrayList<>();
        for (HNode statement : getStatements()) {
            if(statement instanceof HNDeclareType){
                all.add((HNDeclareType) statement);
            }else if(statement instanceof HNBlock && ((HNBlock) statement).getBlocType()==BlocType.IMPORT_BLOC){
                all.addAll(((HNBlock) statement).findDeclaredTypes());
            }
        }
        return all;
    }

    public List<HNDeclareIdentifier> findDeclaredIdentifiers(){
        List<HNDeclareIdentifier> all=new ArrayList<>();
        for (HNode statement : getStatements()) {
            if(statement instanceof HNDeclareIdentifier){
                all.add((HNDeclareIdentifier) statement);
            }else if(statement instanceof HNBlock && ((HNBlock) statement).getBlocType()==BlocType.IMPORT_BLOC){
                all.addAll(((HNBlock) statement).findDeclaredIdentifiers());
            }
        }
        return all;
    }

    public List<HNDeclareInvokable> findDeclaredInvokables(){
        List<HNDeclareInvokable> all=new ArrayList<>();
        for (HNode statement : getStatements()) {
            if(statement instanceof HNDeclareInvokable){
                all.add((HNDeclareInvokable) statement);
            }else if(statement instanceof HNBlock && ((HNBlock) statement).getBlocType()==BlocType.IMPORT_BLOC){
                all.addAll(((HNBlock) statement).findDeclaredInvokables());
            }
        }
        return all;
    }

    public List<HNDeclareMetaPackage> findDeclaredModules(){
        List<HNDeclareMetaPackage> all=new ArrayList<>();
        for (HNode statement : getStatements()) {
            if(statement instanceof HNDeclareMetaPackage){
                all.add((HNDeclareMetaPackage) statement);
            }else if(statement instanceof HNBlock && ((HNBlock) statement).getBlocType()==BlocType.IMPORT_BLOC){
                all.addAll(((HNBlock) statement).findDeclaredModules());
            }
        }
        return all;
    }

    public List<HNDeclareMetaPackage> findDeclaredMetaPackages(){
        List<HNDeclareMetaPackage> all=new ArrayList<>();
        for (HNode statement : getStatements()) {
            if(statement instanceof HNDeclareMetaPackage){
                all.add((HNDeclareMetaPackage) statement);
            }else if(statement instanceof HNBlock && ((HNBlock) statement).getBlocType()==BlocType.IMPORT_BLOC){
                all.addAll(((HNBlock) statement).findDeclaredMetaPackages());
            }
        }
        return all;
    }

    @Override
    public HNode[] getExitPoints() {
        return getExitPoints(statements.toArray(new HNode[0]));
    }

    public enum BlocType{
        UNKNOWN,
        PACKAGE_BODY,
        GLOBAL_BODY,
        CLASS_BODY,
        METHOD_BODY,
        LOCAL_BLOC,
        IMPORT_BLOC,
        EXPR_GROUP,
        STATIC_INITIALIZER,
        INSTANCE_INITIALIZER,
    }

    public static class CompilationUnitBlock extends HNBlock{
        private JCompilationUnit compilationUnit;
        public CompilationUnitBlock(HNode[] statements,JToken startToken,JToken endToken) {
            super(BlocType.GLOBAL_BODY,statements,startToken,endToken);
        }

        public JCompilationUnit getCompilationUnit() {
            return compilationUnit;
        }

        public CompilationUnitBlock setCompilationUnit(JCompilationUnit compilationUnit) {
            this.compilationUnit = compilationUnit;
            return this;
        }
    }
}
