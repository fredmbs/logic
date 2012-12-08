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
public abstract class Formula {
    
    // informa se é uma sentença, ou seja, se é uma wff
    public boolean isSentence() {
        return true;
    }
    
    protected boolean lastEval = true;
    
    abstract public String toPrefix();
    abstract public boolean evaluate();
    abstract public boolean fullEvaluate();
    abstract public Formula clone(SymbolTable<? extends Symbol> st);
    abstract public void accept(FormulaVisitor visitor);
    abstract public int getSize();

    public boolean getLastFullEvaluation() {
        return lastEval;
    }

}
