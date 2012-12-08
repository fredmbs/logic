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
public class Predicate extends Formula implements NonLogicalSymbol {

    private boolean value = false;
    private String lexSymbol= ""; 

    @Override
    public void setLexeme(String sym) {
        lexSymbol = sym;
        
    }

    @Override
    public int getSize() {
        return 1; 
    };

    @Override
    public String getLexeme() {
        return lexSymbol;
    }


    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public int getValence() {
        return 0;
    }
    
    @Override
    public boolean evaluate() {
        return this.value;
    }

    @Override
    public boolean fullEvaluate() {
        lastEval = this.value;
        return lastEval;
    }

    @Override
    public String toString() {
        return lexSymbol;
    }

    @Override
    public String toPrefix() {
        return "(" + lexSymbol + ")";
    }

    @Override
    public Predicate clone(SymbolTable<? extends Symbol> st) {
        Predicate clonedObj;
        try {
            clonedObj = (Predicate)st.occurrence(Predicate.class, this.lexSymbol);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
        clonedObj.value = this.value;
        return clonedObj;
    }


    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

}
