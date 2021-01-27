package net.hl.compiler.ast;

import net.thevpc.jeep.JCompilerLog;
import net.thevpc.jeep.JToken;
import net.hl.compiler.utils.HNodeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateDefChecker {

    //check for duplicate definitions
    Map<String, List<HNDeclareIdentifier>> identifiers = new HashMap<>();
    Map<String, List<HNDeclareInvokable>> invokables = new HashMap<>();
    Map<String, List<HNDeclareType>> subTypes = new HashMap<>();

    public void addIdentifiers(List<HNDeclareIdentifier> a) {
        for (HNDeclareIdentifier x : a) {
            add(x);
        }
    }

    public void addInvokables(List<HNDeclareInvokable> a) {
        for (HNDeclareInvokable x : a) {
            add(x);
        }
    }

    public void addSubTypes(List<HNDeclareType> a) {
        for (HNDeclareType x : a) {
            add(x);
        }
    }

    public void addBody(HNode a) {
        if (a instanceof HNDeclareInvokable) {
            add((HNDeclareInvokable) a);
        } else if (a instanceof HNDeclareType) {
            add((HNDeclareType) a);
        } else if (a instanceof HNDeclareIdentifier) {
            add((HNDeclareIdentifier) a);
        } else if (a instanceof HNBlock) {
            for (HNode s : ((HNBlock) a).getStatements()) {
                if (s instanceof HNDeclareInvokable) {
                    add((HNDeclareInvokable) s);
                } else if (s instanceof HNDeclareType) {
                    add((HNDeclareType) s);
                } else if (s instanceof HNDeclareIdentifier) {
                    add((HNDeclareIdentifier) s);
                }
            }
        }
    }

    public void add(HNDeclare a) {
        if (a instanceof HNDeclareInvokable) {
            add((HNDeclareInvokable) a);
        } else if (a instanceof HNDeclareType) {
            add((HNDeclareType) a);
        } else if (a instanceof HNDeclareIdentifier) {
            add((HNDeclareIdentifier) a);
        }
    }

    public void add(HNDeclareIdentifier a) {
        for (String identifierName : HNodeUtils.flattenNames(a.getIdentifierToken())) {
            List<HNDeclareIdentifier> t = identifiers.get(identifierName);
            if (t == null) {
                t = new ArrayList<>();
                identifiers.put(identifierName, t);
            }
            t.add(a);
        }
    }

    public void add(HNDeclareInvokable a) {
        List<HNDeclareInvokable> t = invokables.get(a.getSignature().toString());
        if (t == null) {
            t = new ArrayList<>();
            invokables.put(a.getSignature().toString(), t);
        }
        t.add(a);
    }

    public void add(HNDeclareType a) {
        List<HNDeclareType> t = subTypes.get(a.getFullName());
        if (t == null) {
            t = new ArrayList<>();
            subTypes.put(a.getFullName(), t);
        }
        t.add(a);
    }

    public void checkDuplicates(JCompilerLog log) {
        for (Map.Entry<String, List<HNDeclareIdentifier>> e : identifiers.entrySet()) {
            if (e.getValue().size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("duplicate identifier definition : ").append(e.getKey());
                for (HNDeclareIdentifier i : e.getValue()) {
                    sb.append("\n\t ")
                            .append(String.format("[%4s,%3s]", i.getStartToken().startLineNumber, i.getStartToken().startColumnNumber))
                            .append(" ").append(JToken.escapeString(String.valueOf(i)));
                }
                log.error("S044", null,
                        sb.toString(), e.getValue().get(0).getStartToken()
                );
            }
        }
        for (Map.Entry<String, List<HNDeclareInvokable>> e : invokables.entrySet()) {
            if (e.getValue().size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("duplicate function/method definition : ").append(e.getValue().get(0).getName());
                for (HNDeclareInvokable i : e.getValue()) {
                    sb.append("\n\t ")
                            .append(String.format("[%4s,%3s]", i.getStartToken().startLineNumber, i.getStartToken().startColumnNumber))
                            .append(" ").append(i.getSignature());
                }
                log.error("S044", null,
                        sb.toString(), e.getValue().get(0).getStartToken()
                );
            }
        }
        for (Map.Entry<String, List<HNDeclareType>> e : subTypes.entrySet()) {
            if (e.getValue().size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("duplicate type definition : ").append(e.getValue().get(0).getName());
                for (HNDeclareType i : e.getValue()) {
                    sb.append("\n\t ")
                            .append(String.format("[%4s,%3s]", i.getStartToken().startLineNumber, i.getStartToken().startColumnNumber))
                            .append(" ").append(i.getFullName());
                }
                log.error("S044", null,
                        sb.toString(), e.getValue().get(0).getStartToken()
                );
            }
        }
    }
}
