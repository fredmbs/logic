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
public class Equality extends Formula {

    private Term left, right;

    public Equality() {
        super();
        this.left = null;
        this.right = null;
    }
    
    public Equality(Term left, Term right) {
        super();
        this.left = left;
        this.right = right;
    }
    
    public String getSymbol() { return "="; }

    public Term getLeft() {
        return left;
    }

    public void setLeft(Term left) {
        this.left = left;
    }

    public Term getRight() {
        return right;
    }

    public void setRight(Term right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + getSymbol() + right + ")";
    }

    @Override
    public String toPrefix() {
        return getSymbol() + " " + left.toPrefix() + " " + right.toPrefix() + " ";
    }; 

    @Override
    public boolean evaluate() {
        return true;
    }

    @Override
    public boolean fullEvaluate() {
        return true;
    }
    
    @Override
    public Equality clone(SymbolTable<? extends Symbol> st) {
        Equality clonedObj = new Equality();
        clonedObj.right = this.right.clone(st);
        clonedObj.left = this.left.clone(st);
        return clonedObj;
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

}
