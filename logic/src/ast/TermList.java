package ast;

import java.util.ArrayList;

import ast.utils.SymbolTable;

public class TermList extends ArrayList<Term> {

    private static final long serialVersionUID = -5964347978867127712L;
    
    @Override
    public String toString() {
        StringBuffer strFormula = new StringBuffer();
        for (Term t: this) {
            if (t != null) {
                if (strFormula.length() > 0)
                    strFormula.append(",");
                strFormula.append(t);
            }
        }
        return strFormula.toString();
    }

    public TermList clone(SymbolTable<? extends Symbol> st) {
        TermList cloneObj = new TermList();
        int size = this.size();
        for (int i = (size - 1); i >= 0; i--) {
            cloneObj.add(this.get(i).clone(st));
        }
        return cloneObj;
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
        TermList other = (TermList) obj;
        int size = this.size();
        if (size != other.size())
            return false;
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i)))
                return false;
        }
        return true;
    }; 

    

}
