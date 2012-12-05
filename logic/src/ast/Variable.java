/**
 * 
 */
package ast;

import ast.utils.SymbolTable;

/**
 * @author dev
 *
 */
public class Variable extends Term {

    public enum Quantifier {Free, Forall, Exists};
    
    private Variable bind;
    private Variable.Quantifier quantifier; 
    

    public Variable () {
        bind = null;
        quantifier = Variable.Quantifier.Free; 
    }
    
    public Variable getBind() {
        return bind;
    }


    public Variable.Quantifier getQuantifier() {
        return quantifier;
    }

    @Override
    public String toString() {
        return getLexeme();
    }

    @Override
    public String toPrefix() {
        // TODO Auto-generated method stub
        return getLexeme();
    }

    @Override
    public Variable clone(SymbolTable<? extends Symbol> st) {
        Variable clonedObj = new Variable();
        try {
            clonedObj = (Variable)st.occurrence(Variable.class, this.lexSymbol);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
        return clonedObj;    
    }

}
