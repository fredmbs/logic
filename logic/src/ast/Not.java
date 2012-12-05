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
public class Not extends Formula {
	
	private Formula formula;
	
	public Not(Formula f){
		formula = f;
	}

    public Formula getFormula() {
        return formula;
    }

    @Override
    public boolean evaluate() {
        return !formula.evaluate(); 
    };
    
    @Override
    public boolean fullEvaluate() {
        lastEval = !formula.fullEvaluate();
        return lastEval; 
    };
    
    @Override
    public String toString() {
        return "~(" + formula + ")";
    }

    @Override
    public String toPrefix() {
        return "~" + formula.toPrefix();
    }

    @Override
    public Not clone(SymbolTable<? extends Symbol> st) {
        Not clonedObj = new Not(this.formula.clone(st));
        return clonedObj;
    }


    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formula == null) ? 0 : formula.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Not other = (Not) obj;
        if (formula == null) {
            if (other.formula != null)
                return false;
        } else if (!formula.equals(other.formula))
            return false;
        return true;
    }

}
