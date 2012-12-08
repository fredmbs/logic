/**
 * 
 */
package ast;

/**
 * @author dev
 *
 */
public abstract class Quantifier extends Formula {

    protected Formula formula;
    protected Variable variable;

    public Quantifier(Variable v, Formula f) {
        this.formula = f;
        this.variable = v;
    }
    
    @Override
    public int getSize() {
        return 1 + formula.getSize(); 
    };

    public void setSymbol(String sym) {
    }

    public String getSymbol() {
        return "";
    }
    
    @Override
    public String toPrefix() {
        return getSymbol() + ":" + variable + "(" + formula.toPrefix() + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getSymbol() + ":" + variable + "(" + formula + ")";
    }; 

}
