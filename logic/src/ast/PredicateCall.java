package ast;

import ast.utils.SymbolTable;

public class PredicateCall extends Predicate {

    protected Predicate predicate;
    protected TermList arguments;
    
    public PredicateCall(Predicate predicate, TermList args) {
        this.arguments = args;
        this.predicate = predicate;
        this.setLexeme(predicate.getLexeme());
    }

    @Override
    public int getValence() {
        return arguments.size();
    }

    @Override
    public String toPrefix() {
        return this.getLexeme() + "(" + arguments + ")";
    }

    @Override
    public PredicateCall clone(SymbolTable<? extends Symbol> st) {
        return new PredicateCall(predicate.clone(st), arguments.clone(st));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getLexeme() + "(" + arguments + ")";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime * result
                + ((predicate == null) ? 0 : predicate.hashCode());
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
        PredicateCall other = (PredicateCall) obj;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (arguments == null) {
            if (other.arguments != null)
                return false;
        } else if (!arguments.equals(other.arguments))
            return false;
        return true;
    }; 
}
