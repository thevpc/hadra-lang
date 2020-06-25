package net.hl.compiler.stages.generators.java;

import net.vpc.common.jeep.*;
import net.hl.compiler.core.elements.HNElementMethod;
import net.hl.compiler.parser.ast.*;
import net.hl.compiler.parser.ast.extra.HXInvokableCall;
import net.hl.compiler.parser.ast.extra.HXNew;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;
import net.hl.lang.UncheckedCallable;

import java.util.ArrayList;
import java.util.Arrays;

public class HGenUtils {
    public static HNode prefixUnop(String op, HNode node){
        return new HNOpUnaryCall(HTokenUtils.createToken(op),node,true,null,null);
    }

    public static HNode call(JInvokable invokable, HNode base, HNode[] args){
        return new HXInvokableCall(invokable, base,
                 args, null, null)
                .setElement(new HNElementMethod(invokable).setArgNodes(args))
                ;
    }
    public static HNode callStatic(JInvokable invokable, JType base, HNode[] args){
        return call(invokable, HNodeUtils.createTypeToken(base),args);
    }
    public static HNode callNoBase(JInvokable invokable, HNode[] args){
        return call(invokable, (HNode) null,args);
    }

    public static HNode not(HNode node){
        return prefixUnop("!",node);
    }
    public static HNode neg(HNode node){
        return prefixUnop("-",node);
    }
    public static HNode binop(String op,HNode[] args){
        return new HNOpBinaryCall(HTokenUtils.createToken(op),args[0],args[1],null,null);
    }

    public static HNode New(JType type, HNode[] args){
        return new HXNew(
                HNodeUtils.createTypeToken(type),
                args
        );
    }
    public static HNode Cast(JType type, HNode expr){
        return new HNCast(
                HNodeUtils.createTypeToken(type),
                expr,new JToken[0],null,null
        );
    }

    public static HNode Throw(HNode e) {
        return new HNThrow(e,null,null);
    }
    public static HNTypeToken type(JType value) {
        return HNodeUtils.createTypeToken(value);
    }

    public static HNode localBlock(HNode ... body) {
        return new HNBlock(HNBlock.BlocType.LOCAL_BLOC, body,null,null);
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

    public static HNTryCatch.CatchBranch Catch(HNTypeToken[] exceptionTypes,HNDeclareTokenIdentifier identifier,
                                                 HNode doNode) {
        return new HNTryCatch.CatchBranch(
                exceptionTypes, identifier, doNode, false,null,null,new JToken[0]
        );
    }

    public static HNTryCatch.CatchBranch Catch(HNTypeToken exceptionTypes,HNDeclareTokenIdentifier identifier,
                                                 HNode doNode) {
        return new HNTryCatch.CatchBranch(
                new HNTypeToken[]{exceptionTypes}, identifier, doNode, false,null,null,new JToken[0]
        );
    }

    public static HNTryCatch.CatchBranch Catch(JType exceptionTypes,HNDeclareTokenIdentifier identifier,
                                                 HNode doNode) {
        return new HNTryCatch.CatchBranch(
                new HNTypeToken[]{type(exceptionTypes)}, identifier, doNode, false,null,null,new JToken[0]
        );
    }

    public static HNode EnsureReturn(HNode body) {
        if(body instanceof HNReturn){
            return body;
        }
        if(body instanceof HNBlock){
            HNBlock b = (HNBlock) body;
            if(b.getStatements().size()==1){
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
                body,null,null
        );
    }

    public static HNode Callable(JType resultType,HNode body) {
        JTypes types = resultType.types();
        JType UncheckedCallableType = ((JRawType) types.forName(UncheckedCallable.class.getName())).parametrize(
                resultType
        );
        return new HXInvokableCall(
                UncheckedCallableType.getDeclaredMethod("call"),
                Cast(
                UncheckedCallableType,
                new HNLambdaExpression(HTokenUtils.createToken("->"),null,null)
                .setArguments(new ArrayList<>())
                .setBody(body)
                ),
                new HNode[0],
                null,null
        );
    }

}
