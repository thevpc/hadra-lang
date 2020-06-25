/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.ide.hl4nb.editor.semantic;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeVisitor;
import net.vpc.common.jeep.JToken;
import net.hl.compiler.parser.ast.*;
import net.hl.compiler.parser.ast.HNDeclareIdentifier;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.ide.hl4nb.editor.parser.HLnbParserResult;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.openide.util.Exceptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;

/**
 * @author vpc
 */
public class HLnbSemanticAnalyzer extends SemanticAnalyzer<HLnbParserResult> {

    private static final Logger LOG = Logger.getLogger(HLnbSemanticAnalyzer.class.getName());
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights = new HashMap<>();
    private boolean cancelled = false;

    public HLnbSemanticAnalyzer() {
    }

    @Override
    public Map getHighlights() {
//        LOG.info("HLnbSemanticAnalyzer.getHighlights() = " + semanticHighlights);
        return semanticHighlights;
    }

    @Override
    public void run(HLnbParserResult result, SchedulerEvent event) {
//        LOG.info("HLnbSemanticAnalyzer.run() = " + semanticHighlights);
        try {
            cancelled = false;
            if (isCancelled()) {
                return;
            }
            semanticHighlights.clear();
            if(result.getCompilationUnit()==null){
                return;
            }
            JNode node = result.getCompilationUnit().getAst();
            try {
                node.visit(new JNodeVisitor() {
                    @Override
                    public void startVisit(JNode node) {
                        if (cancelled) {
                            throw new CancellationException();
                        }
                        Set<ColoringAttributes> coloringSet = new HashSet<>();
                        boolean processed = false;
                        switch (node.getClass().getName()) {
                            case "net.hl.compiler.parser.ast.HNDeclareType": {
                                coloringSet.add(ColoringAttributes.CLASS);
                                coloringSet.add(ColoringAttributes.DECLARATION);
                                HNDeclareType p = (HNDeclareType) node;
                                if (java.lang.reflect.Modifier.isStatic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.STATIC);
                                }
                                if (java.lang.reflect.Modifier.isPublic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PUBLIC);
                                }
                                if (java.lang.reflect.Modifier.isPrivate(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PRIVATE);
                                }
                                if (java.lang.reflect.Modifier.isProtected(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PROTECTED);
                                }
                                JToken nameToken = p.getNameToken();
                                if(nameToken!=null){
//                                    LOG.log(Level.INFO, "## "+nameToken
//                                            +"\n\t"+offsetFromToken(nameToken)
//                                            +"\n\t"+coloringSet
//                                    );
                                    semanticHighlights.put(offsetFromToken(nameToken), coloringSet);
                                }
                                processed = true;
                                break;
                            }
                            case "net.hl.compiler.parser.ast.HNDeclareInvokable": {
                                coloringSet.add(ColoringAttributes.DECLARATION);
                                HNDeclareInvokable p = (HNDeclareInvokable) node;
                                if (p.isConstructor() || "constructor".equals(p.getName())) {
                                    coloringSet.add(ColoringAttributes.CONSTRUCTOR);
                                } else {
                                    coloringSet.add(ColoringAttributes.METHOD);
                                }
                                if (java.lang.reflect.Modifier.isStatic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.STATIC);
                                }
                                if (java.lang.reflect.Modifier.isPublic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PUBLIC);
                                }
                                if (java.lang.reflect.Modifier.isPrivate(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PRIVATE);
                                }
                                if (java.lang.reflect.Modifier.isProtected(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PROTECTED);
                                }
                                JToken nameToken = p.getNameToken();
                                if(nameToken!=null){
//                                    LOG.log(Level.INFO, "## "+nameToken
//                                            +"\n\t"+offsetFromToken(nameToken)
//                                            +"\n\t"+coloringSet
//                                    );
                                    semanticHighlights.put(offsetFromToken(nameToken), coloringSet);
                                }
                                processed = true;
                                break;
                            }
                            case "net.hl.compiler.parser.ast.HNDeclareIdentifier": {
                                coloringSet.add(ColoringAttributes.DECLARATION);
                                HNDeclareIdentifier p = (HNDeclareIdentifier) node;
                                if(p.getSyntacticType()==null || p.getSyntacticType()== HNDeclareIdentifier.SyntacticType.FIELD) {
                                    coloringSet.add(ColoringAttributes.FIELD);
                                }else{
                                    coloringSet.add(ColoringAttributes.LOCAL_VARIABLE_DECLARATION);
                                }

                                if (java.lang.reflect.Modifier.isStatic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.STATIC);
                                }
                                if (java.lang.reflect.Modifier.isPublic(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PUBLIC);
                                }
                                if (java.lang.reflect.Modifier.isPrivate(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PRIVATE);
                                }
                                if (java.lang.reflect.Modifier.isProtected(p.getModifiers())) {
                                    coloringSet.add(ColoringAttributes.PROTECTED);
                                }
                                for (HNDeclareTokenIdentifier ii : HNodeUtils.flatten(p.getIdentifierToken())) {
                                    JToken identifierToken=ii.getToken();
                                    if (coloringSet.size() > 0) {
//                                        LOG.log(Level.INFO, "## "+identifierToken
//                                                +"\n\t"+offsetFromToken(identifierToken)
//                                                +"\n\t"+coloringSet
//                                        );
                                        semanticHighlights.put(offsetFromToken(identifierToken),
                                                coloringSet);
                                    }
                                }
                                processed = true;
                                break;
                            }
                        }
                        if (!processed) {
                            if (coloringSet.size() > 0) {
                                semanticHighlights.put(offsetFromToken(node.startToken()),
                                        coloringSet);
                            }
                        }
                    }

                });
            } catch (CancellationException ex) {
                //just ignore...
            }
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }

    protected OffsetRange offsetFromToken(JToken identifierToken) {
        return new OffsetRange(
                (int) identifierToken.startCharacterNumber,
                (int) identifierToken.endCharacterNumber);
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

}
