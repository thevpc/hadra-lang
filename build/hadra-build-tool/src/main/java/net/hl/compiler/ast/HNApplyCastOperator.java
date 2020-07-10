/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * cast types .
 * We distinguish three types of cast : implicit cast and explicit cast and nullable cast.
 * <p>Explicit cast is a bare checking (equivalent to java's cast)</p>
 * <p>Implicit cast can convert one type to another using in order one of
 * the following methods when available. Lets take the example of casting
 * {@code a} of type {@code A} to {@code class B}
 * <ul>
 *  <li> instance method in A that returns B<pre>def B A.as(Class&lt;B>)</pre></li>
 *  <li> constructor in B <pre>B(A a)</pre></li>
 *  <li> static method that returns B<pre>static B cast(A a,Class&lt;B>)</pre></li>
 *  </ul>
 * </p>
 * Examples of explicit casts
 * <ol>
 *   <li>Example 1:
 *   <pre>
 *      Car c=Maseratti();
 *      Maseratti m=(Maseratti)c; //here is the explicit cast
 *      Volvo v=(Volvo?)c; //this will return zero without exception
 *                         //this is an explicit nullable cast
 *      </pre>
 *   </li>
 *   <li>Example 2:
 *   <pre>
 *      double c=2.0;
 *      int m=(int)c; //actually this is an implicit cast done
 *                  //by the jvm/java so it is considered as
 *                  //explicit cast
 *      </pre>
 * </ol>
 * Examples of implicit casts
 * <ol>
 *   <li>Example 1:
 *   <pre>
 *      Car c=Maseratti();
 *      Maseratti m=c; // here is the implicit cast
 *                     // that will check through
 *                     // the three possibilities
 *      </pre>
 *      Note that
 *   <li>Example 2:
 *   <pre>
 *      Car c=Maseratti();
 *      Maseratti m=c.as(Maseratti); // here is the implicit cast
 *                                 // that will check through
 *                                 // the three possibilities as well
 *      </pre>
 *   </li>
 *   <li>Example 3:
 *   <pre>
 *      Car c=Maseratti();
 *      Volvo m=c.as(Volvo?); // here is the implicit cast
 *                                 // that will check through
 *                                 // the three possibilities as well
 *                                 //but will return null if unavailable cast
 *      </pre>
 *   <li>Example 4:
 *   <pre>
 *      def sum(Complex a,Complex b){
 *          return a+b;
 *      }
 *      class Complex(double real,double imag){
 *          constructor(double real)->this(real,0);
 *          def Complex +(Complex o)->Complex(real+o.real,imag+o.imag);
 *          def Complex reverse_+(Complex o)->Complex(real+o.real,imag+o.imag);
 *      }
 *      int a=3;
 *      var c=a+Complex(2,3); // here is an implicit cast
 *                            // that will check through
 *                            // the tree possibilities
 *                            //and will choose #2 (constructor)
 *      Complex equivalent_c= Complex(2,3).reverse_plus(Complex((double)a));
 *      </pre>
 * </ol>
 *
 */
public class HNApplyCastOperator extends HNUnused {

    private HNode expr;
    private JTypeName castType;

    private HNApplyCastOperator() {
        super(HNNodeId.H_APPLY_CAST_OPERATOR);
    }

    public HNApplyCastOperator(HNode expr, JTypeName castType, JToken startToken, JToken endToken) {
        this();
        this.castType = castType;
        setExpr(expr);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JTypeName getCastType() {
        return castType;
    }

    public HNode getExpr() {
        return expr;
    }

    public HNApplyCastOperator setExpr(HNode expr) {
        this.expr = JNodeUtils.bind(this,expr,"expr");
        return this;
    }

    public HNApplyCastOperator setCastType(JTypeName castType) {
        this.castType = castType;
        return this;
    }
    //    @Override
//    public JType getType(JContext context) {
//        return context.types().forName(Object.class);
//    }

    @Override
    public String toString() {
        return "(" + castType.toString() + ")" + "(" + expr.toString() + ")";
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getExpr,this::setExpr);
    }

    @Override
    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNApplyCastOperator) {
            HNApplyCastOperator o = (HNApplyCastOperator) node;
            this.castType = (o.castType);
            this.expr = JNodeUtils.bindCopy(this, copyFactory, o.expr);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(expr);
    }
}
