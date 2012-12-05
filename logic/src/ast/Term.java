/**
 * 
 */
package ast;

import ast.utils.SymbolTable;

/**
 * @author dev
 *
 */
public abstract class Term implements Symbol {

    protected String lexSymbol = "";
    
    public void setLexeme(String sym) {
        lexSymbol = sym.trim();
        
    }

    public String getLexeme() {
        return lexSymbol;
    }

    abstract public Term clone(SymbolTable<? extends Symbol> st);
    abstract public String toPrefix();
    
}
