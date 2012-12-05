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
public class Exists extends Quantifier {

    public Exists(Variable v, Formula f) {
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
    public String getSymbol() { return "Ex"; }

    @Override
    public Exists clone(SymbolTable<? extends Symbol> st) {
        Exists clonedObj = new Exists(variable.clone(st), formula.clone(st));
        return clonedObj;    
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }
}
