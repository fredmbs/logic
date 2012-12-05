/**
 * 
 */
package ast;

import ast.utils.SymbolTable;

/**
 * @author dev
 *
 */
public class Function extends Term implements NonLogicalSymbol {

    @Override
    public int getValence() {
        return 0;
    }

    @Override
    public String toPrefix() {
        return getLexeme();
    }

    @Override
    public Function clone(SymbolTable<? extends Symbol> st) {
        Function clonedObj;
        try {
            clonedObj = (Function)st.occurrence(Function.class, this.lexSymbol);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
        return clonedObj;
    }

}
