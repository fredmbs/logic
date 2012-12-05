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
public class Equivalent extends Connective {

    public Equivalent() {
        super();
    }

    public Equivalent(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate() {
        return this.left.evaluate() == this.right.evaluate();
    }; 

    @Override
    public boolean fullEvaluate() {
        lastEval = (this.left.fullEvaluate() == this.right.fullEvaluate());
        return lastEval;
    }; 

    @Override
    public String getLexeme() { return "<->"; }; 
    

    @Override
    public Equivalent clone(SymbolTable<? extends Symbol> st) {
        Equivalent clonedObj = new Equivalent();
        clonedObj.right = this.right.clone(st);
        clonedObj.left = this.left.clone(st);
        return clonedObj;    
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }
}
