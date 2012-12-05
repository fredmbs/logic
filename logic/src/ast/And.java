/**
 * 
 */
package ast;

import ast.patterns.FormulaVisitor;
import ast.utils.SymbolTable;
/**
 * @author dev
 *
 */
public class And extends Commutative {

    public And() {
        super();
    }

    public And(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate() {
        return left.evaluate() && right.evaluate(); 
    };

    @Override
    public boolean fullEvaluate() {
        lastEval = left.fullEvaluate() & right.fullEvaluate();
        return lastEval; 
    };
    @Override
    public String getLexeme() { return "^"; }

    @Override
    public And clone(SymbolTable<? extends Symbol> st) {
        And clonedObj = new And();
        clonedObj.right = this.right.clone(st);
        clonedObj.left = this.left.clone(st);
        return clonedObj;
    }

    
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

}
