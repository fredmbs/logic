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
public class Implies extends Connective {

    public Implies() {
        super();
    }

    public Implies(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate() {
        return !this.left.evaluate() || this.right.evaluate();
    }; 

    @Override
    public boolean fullEvaluate() {
        lastEval = !this.left.fullEvaluate() | this.right.fullEvaluate();
        return lastEval;
    }; 

    @Override
    public String getLexeme() { return "->"; }

    @Override
    public Implies clone(SymbolTable<? extends Symbol> st) {
        Implies clonedObj = new Implies();
        clonedObj.right = this.right.clone(st);
        clonedObj.left = this.left.clone(st);
        return clonedObj;    
    }; 
    
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }
}
