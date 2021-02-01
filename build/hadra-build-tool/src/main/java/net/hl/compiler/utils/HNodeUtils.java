package net.hl.compiler.utils;

import net.hl.compiler.core.types.JPrimitiveModifierAnnotationInstance;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.JTypeNameParser;
import net.thevpc.jeep.util.JNodeUtils;
import net.thevpc.jeep.util.JTokenUtils;
import net.hl.compiler.core.elements.HNElement;
import net.hl.compiler.core.elements.HNElementKind;
import net.hl.compiler.core.elements.HNElementMethod;
import net.hl.compiler.ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class HNodeUtils {

    public static HNode declaringStatement(HNDeclareToken item) {
        return declaringStatement0(item, item.getParentNode());
    }

    private static HNode declaringStatement0(HNDeclareToken item, JNode lookInto) {
        if (lookInto == null) {
            return null;
        }
        if (lookInto instanceof HNDeclareIdentifier) {
            return (HNode) lookInto;
        }
        if (lookInto instanceof HNIs) {
            return (HNode) lookInto;
        }
        return declaringStatement0(item, lookInto.getParentNode());
    }

    public static String[] flattenNames(HNDeclareToken item) {
        return Arrays.stream(flatten(item)).map(x -> x.getName()).toArray(String[]::new);
    }

    public static HNDeclareTokenIdentifier[] flatten(HNDeclareToken item) {
        if (item == null) {
            return new HNDeclareTokenIdentifier[0];
        }
        if (item instanceof HNDeclareTokenIdentifier) {
            return new HNDeclareTokenIdentifier[]{(HNDeclareTokenIdentifier) item};
        }
        if (item instanceof HNDeclareTokenList) {
            List<HNDeclareTokenIdentifier> list = new ArrayList<>();
            for (HNDeclareTokenTupleItem hnDeclareTokenTupleItem : ((HNDeclareTokenList) item).getItems()) {
                list.addAll(Arrays.asList(flatten(hnDeclareTokenTupleItem)));
            }
            return list.toArray(new HNDeclareTokenIdentifier[0]);
        }
        if (item instanceof HNDeclareTokenTuple) {
            List<HNDeclareTokenIdentifier> list = new ArrayList<>();
            for (HNDeclareTokenTupleItem hnDeclareTokenTupleItem : ((HNDeclareTokenTuple) item).getItems()) {
                list.addAll(Arrays.asList(flatten(hnDeclareTokenTupleItem)));
            }
            return list.toArray(new HNDeclareTokenIdentifier[0]);
        }
        return new HNDeclareTokenIdentifier[0];
    }

    public static HNDeclareTokenTupleItem toDeclareTupleItem(HNTuple tuple, JCompilerLog log) {
        List<HNDeclareTokenTupleItem> children = new ArrayList<>();
        for (JNode item : tuple.getItems()) {
            if (item instanceof HNDeclareTokenTupleItem) {
                children.add((HNDeclareTokenTupleItem) item);
            } else if (item instanceof HNIdentifier) {
                children.add(toDeclareTokenIdentifier((HNIdentifier) item));
            } else if (item instanceof HNTuple) {
                children.add(toDeclareTupleItem((HNTuple) item, log));
            } else {
                log.jerror("X000", null, item.getStartToken(), "expected valid tuple item declaration");
            }
        }
        //should i add other tokens?
        return new HNDeclareTokenTuple(
                children.toArray(new HNDeclareTokenTupleItem[0]),
                tuple.getSeparators(), tuple.getStartToken(),
                tuple.getEndToken()
        );
    }

    public static HNDeclareTokenIdentifier toDeclareTokenIdentifier(HNIdentifier id) {
        return new HNDeclareTokenIdentifier(id.getNameToken());
    }

    public static boolean isElementInvokable(JNode node) {
        if (node instanceof HNode) {
            HNElement element = ((HNode) node).getElement();
            if (element != null) {
                switch (element.getKind()) {
                    case CONSTRUCTOR: {
                        return true;
                    }
                    case METHOD: {
                        JMethod jm = (JMethod) ((HNElementMethod) element).getInvokable();
                        //jm.declaringType()==null for convert functions...
                        if (
                                jm.getDeclaringType()!=null &&
                                jm.getDeclaringType().getName().equals("net.hl.lang.ext.HJavaDefaultOperators")) {
                            //this is a standard operator
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static HNTypeToken createTypeToken(JToken token) {
        return new HNTypeToken(
                token,
                JTypeNameParser.parseType(token.image), null, null, null, token, token
        );
    }

    public static HNTypeToken createTypeToken(String type) {
        return new HNTypeToken(
                JTokenUtils.createKeywordToken(type),
                JTypeNameParser.parseType(type), null, null, null, null, null
        );
    }

    public static HNDeclareInvokable getMainMethod(HNBlock block) {
        return block.findDeclaredInvokables().stream().filter(x -> {
            HNElement e = x.getElement();
            if (e != null && e.getKind() == HNElementKind.METHOD) {
                HNElementMethod me = (HNElementMethod) e;
                JInvokable invokable = me.getInvokable();
                if (invokable != null && invokable.getSignature().toString().equals("main(java.lang.String[])")) {
                    return true;
                }
            }
            return x.getSignature().toString().equals("main(java.lang.String[])");
        })
                .findFirst().orElse(null);
    }

    public static HNDeclareInvokable getMainMethod(HNDeclareType type) {
        HNode b = type.getBody();
        if (b instanceof HNBlock) {
            return getMainMethod(HNBlock.get(b));
        }
        return null;
    }

    public static String nextNameFromUserProperty(JNode n2, String propertyName) {
        return propertyName + incUserProperty(n2, propertyName);
    }

    public static int incUserProperty(JNode n2, String propertyName) {
        Integer labelsCounter = (Integer) n2.getUserObjects().get(propertyName);
        if (labelsCounter == null) {
            labelsCounter = 1;
        } else {
            labelsCounter++;
        }
        n2.getUserObjects().put(propertyName, labelsCounter);
        return labelsCounter;
    }

    public static HNDeclareType lookupEnclosingType(JNode node) {
        JNode h = node == null ? null : node.getParentNode();
        while (h != null) {
            if (h instanceof HNDeclareType) {
                return (HNDeclareType) h;
            }
            h = h.getParentNode();
        }
        return null;
    }

    public static boolean requireExplicitExit(JNode n) {
        if (!(n instanceof HNode)) {
            return true;
        }
        JNode[] e = ((HNode) n).getExitPoints();
        if (e.length == 0) {
            return true;
        }
        for (JNode jNode : e) {
            if (jNode instanceof HNReturn) {
                //okkay
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean requireExplicitBreak(JNode n) {
        if (!(n instanceof HNode)) {
            return true;
        }
        JNode[] e = ((HNode) n).getExitPoints();
        if (e.length == 0) {
            return true;
        }
        for (JNode jNode : e) {
            if (jNode instanceof HNReturn) {
                //okkay
            } else if (jNode instanceof HNBreak) {
                //okkay
            } else if (jNode instanceof HNContinue) {
                //okkay
            } else {
                return true;
            }
        }
        return false;
    }

    public static JNode[] findVarUsage(String name, JNode n) {
        return JNodeUtils.findNodes(n, node -> (node instanceof HNIdentifier && ((HNIdentifier) node).getName().equals(name)));
    }

    //    public JToken createToken(JToken fromToken,String newImageAndSval){
    //        JToken t = fromToken.copy();
    //        t.image=newImageAndSval;
    //        t.sval=newImageAndSval;
    //        t.en=newImageAndSval;
    //    }
    public static HNTypeToken createTypeToken(JTypeName typename) {
        if (typename == null) {
            return null;
        }
        JToken wordToken = HTokenUtils.createToken(typename.toString());
        return new HNTypeToken(
                wordToken,
                typename,
                new HNTypeToken[0],
                new HNTypeToken[0],
                new HNTypeToken[0],
                wordToken,
                wordToken
        );
    }

    public static HNTypeToken createTypeToken(JType typename) {
        if (typename == null) {
            return null;
        }
        JToken wordToken = HTokenUtils.createToken(typename.toString());
        HNTypeToken jNodeHTypeToken = new HNTypeToken(
                wordToken,
                typename.typeName(),
                new HNTypeToken[0],
                new HNTypeToken[0],
                new HNTypeToken[0],
                wordToken,
                wordToken
        );
        jNodeHTypeToken.setTypeVal(typename);
        return jNodeHTypeToken;
    }

    public static JNode skipImportBlock(JNode v) {
        while (v instanceof HNBlock && HNBlock.get(v).getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
            v = v.getParentNode();
        }
        return v;
    }

    public static JNode findImmediateParent(JNode v) {
        JNode n = v.getParentNode();
        if (v instanceof HNBlock) {
            if ((HNBlock.get(v)).getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                return findImmediateParent(v);
            }
        }
        return n;
    }

    public static JNode prunePars(HNode n) {
        if (n.id() == HNNodeId.H_PARS) {
            JNode[] p = ((HNPars) n).getItems();
            if (p.length == 1) {
                return p[0];
            }
        }
        return n;
    }

    public static JType getType(JNode arg) {
        if (arg == null) {
            return null;
        }
        HNElement elem = ((HNode) arg).getElement();
        if (elem == null) {
            return null;
        }
        JType t = elem.getType();
        if (t == null) {
            if (arg instanceof HNLiteral
                    && ((HNLiteral) arg).getValue() == null) {
                //this is ok
            } else {
                throw new JParseException("Type Not Found for Node " + arg.getClass().getSimpleName() + " as " + arg);
            }
        }
        return t;
    }

    public static boolean isTypeSet(JNode arg) {
        if (arg == null) {
            return false;
        }
        HNElement elem = ((HNode) arg).getElement();
        if (elem == null) {
            return false;
        }
        JType t = elem.getType();
        if (t == null) {
            if (arg instanceof HNLiteral
                    && ((HNLiteral) arg).getValue() == null) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isTypeSet(JNode... arg) {
        for (JNode jNode : arg) {
            if (!isTypeSet(jNode)) {
                return false;
            }
        }
        return true;
    }

    public static HNDeclareTokenBase lookupDeclareTokenBase(String n, HNDeclareToken t) {
        if (t == null) {
            return null;
        }
        if (t instanceof HNDeclareTokenIdentifier) {
            if (((HNDeclareTokenIdentifier) t).getToken().sval.equals(n)) {
                return (HNDeclareTokenIdentifier) t;
            }
        }
        if (t instanceof HNDeclareTokenList) {
            for (HNDeclareTokenTupleItem item : ((HNDeclareTokenList) t).getItems()) {
                HNDeclareTokenBase u = lookupDeclareTokenBase(n, item);
                if (u != null) {
                    return u;
                }
            }
        }
        if (t instanceof HNDeclareTokenTuple) {
            for (HNDeclareTokenTupleItem item : ((HNDeclareTokenTuple) t).getItems()) {
                HNDeclareTokenBase u = lookupDeclareTokenBase(n, item);
                if (u != null) {
                    return u;
                }
            }
        }
        return null;
    }
    public static HNode assignToDeclare(HNode expr,boolean skipPar){
        if(skipPar && expr instanceof HNPars){
            HNPars p = (HNPars) expr;
            HNode[] i = p.getItems();
            if(i.length==1){
                HNode n2=assignToDeclare(i[0],false);
                if(n2!=i[0]){
                    p.setItems(new HNode[]{n2});
                    return p;
                }
            }
            return expr;
        }
        if (expr instanceof HNAssign && ((HNAssign) expr).getLeft() instanceof HNIdentifier) {
            return new HNDeclareIdentifier(
                    new HNDeclareTokenIdentifier(((HNIdentifier) ((HNAssign) expr).getLeft()).getNameToken()),
                    ((HNAssign) expr).getRight(), null, ((HNAssign) expr).getOp(), expr.getStartToken(),
                    expr.getEndToken()
            );
        }
        return expr;
    }

    public static HNAnnotationCall createAnnotationModifierCall(String name){
        JToken token = HTokenUtils.createToken(name);
        JTokenBoundsBuilder jTokenBoundsBuilder = new JTokenBoundsBuilder();
        jTokenBoundsBuilder.visit(token);
        return new HNAnnotationCall(
                new HNTypeTokenSpecialAnnotation(
                        token
                ),
                new HNode[0],
                jTokenBoundsBuilder
        );
    }
    public static List<String> filterModifierAnnotations(HNAnnotationCall[] calls,String ...modifiers){
        Set<String> s=new HashSet<>(Arrays.asList(modifiers));
        return Arrays.stream(calls)
                .map(x->HNodeUtils.getModifierAnnotation(x))
                .filter(x->s.contains(x))
                .collect(Collectors.toList());
    }

    public static String getModifierAnnotation(HNAnnotationCall call){
        return (call.getName() instanceof HNTypeTokenSpecialAnnotation)?
                (((HNTypeTokenSpecialAnnotation)call.getName()).getTypename().name()) : null;
    }

    public static boolean isModifierAnnotation(HNAnnotationCall[] call,String ... any){
        for (HNAnnotationCall hnAnnotationCall : call) {
            if(isModifierAnnotation(hnAnnotationCall,any)){
                return true;
            }
        }
        return false;
    }

    public static boolean isModifierAnnotation(HNAnnotationCall call){
        return call.getName() instanceof HNTypeTokenSpecialAnnotation;
    }
    
    public static boolean isModifierAnnotation(HNAnnotationCall call,String ... any){
        return call.getName() instanceof HNTypeTokenSpecialAnnotation
                && Arrays.asList(any).contains(((HNTypeTokenSpecialAnnotation)call.getName()).getTypename().name());
    }

    public static Object toAnnotationValue(HNode node) {
        if (node instanceof HNLiteral) {
            return ((HNLiteral) node).getValue();
        }else if (node instanceof HNBrackets) {
            HNBrackets b=(HNBrackets) node;
            HNode[] items = b.getItems();
            Object[] values = new Object[items.length];
            for (int i = 0; i < values.length; i++) {
                values[i]=toAnnotationValue(items[i]);
            }
            return values;
        }else if (node instanceof HNAnnotationCall) {
            HNAnnotationCall call = (HNAnnotationCall) node;
            if (call.getName() instanceof HNTypeTokenSpecialAnnotation) {
                HNTypeTokenSpecialAnnotation a = (HNTypeTokenSpecialAnnotation) call.getName();
                switch (a.getTypename().name()) {
                    case "public":
                        return JPrimitiveModifierAnnotationInstance.PUBLIC;
                    case "private":
                        return JPrimitiveModifierAnnotationInstance.PRIVATE;
                    case "protected":
                        return JPrimitiveModifierAnnotationInstance.PROTECTED;
                    case "abstract":
                        return JPrimitiveModifierAnnotationInstance.ABSTRACT;
                    case "const":
                        return JPrimitiveModifierAnnotationInstance.CONST;
                    case "final":
                        return JPrimitiveModifierAnnotationInstance.FINAL;
                    case "enum":
                        return JPrimitiveModifierAnnotationInstance.ENUM;
                    case "exception":
                        return JPrimitiveModifierAnnotationInstance.EXCEPTION;
                    case "interface":
                        return JPrimitiveModifierAnnotationInstance.INTERFACE;
                    case "native":
                        return JPrimitiveModifierAnnotationInstance.NATIVE;
                    case "static":
                        return JPrimitiveModifierAnnotationInstance.STATIC;
                    case "strictfp":
                        return JPrimitiveModifierAnnotationInstance.STRICTFP;
                    case "synchronized":
                        return JPrimitiveModifierAnnotationInstance.SYNCHRONIZED;
                    case "transient":
                        return JPrimitiveModifierAnnotationInstance.TRANSIENT;
                    case "volatile":
                        return JPrimitiveModifierAnnotationInstance.VOLATILE;
                    default: {
                        throw new IllegalArgumentException("Expected type annotation " + call);
                    }
                }
            }else{
                HNTypeToken a=(HNTypeToken) call.getName();
                JAnnotationType tv = (JAnnotationType) a.getTypeVal();
                Map<String,Object> values=new HashMap<>();
                HNode[] args = call.getArgs();
                for (int i = 0, argsLength = args.length; i < argsLength; i++) {
                    HNode arg = args[i];
                    String aname;
                    if (arg instanceof HNamedNode){
                        aname=((HNamedNode) arg).getName().sval;
                    }else{
                        aname=tv.getAnnotationFields()[i].getName();
                    }
                    values.put(aname,toAnnotationValue(arg));
                }
                return tv.newInstance(values);
            }
        }else{
            throw new IllegalArgumentException("Expected type annotation " + node);
        }
    }

    public static JAnnotationInstance toAnnotation(HNAnnotationCall call){
        if(call.getName() instanceof HNTypeToken){
            return (JAnnotationInstance) toAnnotationValue(call);
        }else{
            throw new IllegalArgumentException("Expected type annotation "+call);
        }
    }

    public static JAnnotationInstance[] toAnnotations(HNAnnotationCall[] calls){
        JAnnotationInstance[] r=new JAnnotationInstance[calls.length];
        for (int i = 0; i < calls.length; i++) {
            r[i]=toAnnotation(calls[i]);
        }
        return r;
    }
}
