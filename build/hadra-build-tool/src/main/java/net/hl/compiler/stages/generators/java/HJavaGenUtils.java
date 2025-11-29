package net.hl.compiler.stages.generators.java;

import net.thevpc.jeep.*;
import net.hl.compiler.core.elements.HNElementMethod;
import net.hl.compiler.ast.*;
import net.hl.compiler.ast.extra.HXInvokableCall;
import net.hl.compiler.ast.extra.HXNew;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;
import net.hl.lang.UncheckedCallable;

import java.util.ArrayList;
import java.util.Arrays;
import net.hl.compiler.core.elements.HNElementExpr;
import net.thevpc.jeep.util.JTypeUtils;

public class HJavaGenUtils {

    public static HNode prefixUnop(String op, HNode node) {
        return new HNOpUnaryCall(HTokenUtils.createToken(op), node, true, null, null);
    }

    public static HNode call(JInvokable invokable, HNode base, HNode[] args) {
        return new HXInvokableCall(invokable, base,
                args, null, null)
                .setElement(new HNElementMethod(invokable).setArgNodes(args));
    }

    public static HNode callStatic(JInvokable invokable, JType base, HNode[] args) {
        return call(invokable, HNodeUtils.createTypeToken(base), args);
    }

    public static HNode callNoBase(JInvokable invokable, HNode[] args) {
        return call(invokable, (HNode) null, args);
    }

    public static HNode not(HNode node) {
        return prefixUnop("!", node);
    }

    public static HNode neg(HNode node) {
        return prefixUnop("-", node);
    }

    public static HNode binop(String op, HNode[] args) {
        return new HNOpBinaryCall(HTokenUtils.createToken(op), args[0], args[1], null, null);
    }

    public static HNode New(JType type, HNode[] args) {
        return new HXNew(
                HNodeUtils.createTypeToken(type),
                args
        );
    }

    public static HNode Cast(JType type, HNode expr) {
        return new HNCast(
                HNodeUtils.createTypeToken(type),
                expr, new JToken[0], null, null
        );
    }

    public static HNode Throw(HNode e) {
        return new HNThrow(e, null, null);
    }

    public static HNTypeToken type(JType value) {
        return HNodeUtils.createTypeToken(value);
    }

    public static HNode localBlock(HNode... body) {
        return new HNBlock(HNBlock.BlocType.LOCAL_BLOC, body, null, null);
    }

    public static HNIdentifier identifier(String id) {
        return new HNIdentifier(
                HTokenUtils.createToken(id)
        );
    }

    public static HNDeclareTokenIdentifier tokenIdentifier(String id) {
        return new HNDeclareTokenIdentifier(
                HTokenUtils.createToken(id)
        );
    }

    public static HNTryCatch.CatchBranch Catch(HNTypeToken[] exceptionTypes, HNDeclareTokenIdentifier identifier,
            HNode doNode) {
        return new HNTryCatch.CatchBranch(
                exceptionTypes, identifier, doNode, false, null, null, new JToken[0]
        );
    }

    public static HNTryCatch.CatchBranch Catch(HNTypeToken exceptionTypes, HNDeclareTokenIdentifier identifier,
            HNode doNode) {
        return new HNTryCatch.CatchBranch(
                new HNTypeToken[]{exceptionTypes}, identifier, doNode, false, null, null, new JToken[0]
        );
    }

    public static HNTryCatch.CatchBranch Catch(JType exceptionTypes, HNDeclareTokenIdentifier identifier,
            HNode doNode) {
        return new HNTryCatch.CatchBranch(
                new HNTypeToken[]{type(exceptionTypes)}, identifier, doNode, false, null, null, new JToken[0]
        );
    }

    public static HNode EnsureReturn(HNode body) {
        if (body instanceof HNReturn) {
            return body;
        }
        if (body instanceof HNBlock) {
            HNBlock b = (HNBlock) body;
            if (b.getStatements().size() == 1) {
                b.setStatements(
                        Arrays.asList(
                                EnsureReturn(
                                        b.getStatements().get(0)
                                )
                        )
                );
            }
            return body;
        }
        return new HNReturn(
                body, null, null
        );
    }

    public static HNDeclareIdentifier declareIdentifier(String name, JType type) {
        return new HNDeclareIdentifier(
                new HNDeclareTokenIdentifier(HTokenUtils.createToken(name))
                , null, HNodeUtils.createTypeToken(type), HTokenUtils.createToken("="), null, null);
    }
    
    public static HNode lambda(HNDeclareIdentifier[] args, HNode body) {
        return new HNLambdaExpression(HTokenUtils.createToken("->"), null, null)
                .setArguments(Arrays.asList(args))
                .setBody(body);
    }

    public static HNode Callable(JType resultType, HNode body) {
        JTypes types = resultType.getTypes();
        JType jType = types.forName(UncheckedCallable.class.getName());
        JType UncheckedCallableType = jType.replaceParameter(
                jType.getTypeParameters()[0].getVName(),
                resultType
        );
        return new HXInvokableCall(
                UncheckedCallableType.getDeclaredMethod("call"),
                Cast(
                        UncheckedCallableType,
                        new HNLambdaExpression(HTokenUtils.createToken("->"), null, null)
                                .setArguments(new ArrayList<>())
                                .setBody(body)
                ),
                new HNode[0],
                null, null
        );
    }

    public static HNode Null(JTypes types) {
        return new HNLiteral(null, HTokenUtils.createToken("null")).setElement(
                new HNElementExpr(JTypeUtils.forNull(types))
        );
    }

    static HNode Literal(Object value,JTypes types) {
        if(value==null){
            return Null(types);
        }
        return new HNLiteral(value, HTokenUtils.createToken(String.valueOf(value))).setElement(
                new HNElementExpr(types.forName(value.getClass().getName()))
        );
    }

}
