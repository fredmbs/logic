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
public class Or extends Commutative {

    public Or() {
        super();
    }

    public Or(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate() {
        return left.evaluate() || right.evaluate(); 
    };

    @Override
    public boolean fullEvaluate() {
        lastEval = left.fullEvaluate() | right.fullEvaluate();
        return lastEval; 
    };

    @Override
    public String getLexeme() { return "v"; }; 
    
    @Override
    public Or clone(SymbolTable<? extends Symbol> st) {
        Or clonedObj = new Or();
        clonedObj.right = this.right.clone(st);
        clonedObj.left = this.left.clone(st);
        return clonedObj;
    }; 

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }
}
