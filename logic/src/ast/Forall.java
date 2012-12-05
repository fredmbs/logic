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
public class Forall extends Quantifier {

    public Forall(Variable v, Formula f) {
        super(v, f);
    }

    @Override
    public boolean evaluate() {
        return false;
    }

    @Override
    public boolean fullEvaluate() {
        return false;
    }

    @Override
    public String getSymbol() { return "Fa"; }

    @Override
    public Forall clone(SymbolTable<? extends Symbol> st) {
        Forall clonedObj = new Forall(variable.clone(st), formula.clone(st));
        return clonedObj;    
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }
}
