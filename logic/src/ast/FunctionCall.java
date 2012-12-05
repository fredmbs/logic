package ast;

import ast.utils.SymbolTable;

public class FunctionCall extends Function {

    protected Function function;
    protected TermList arguments;
    
    public FunctionCall(Function function, TermList args) {
        this.arguments = args;
        this.function = function;
        this.setLexeme(function.getLexeme());
    }

    @Override
    public int getValence() {
        return arguments.size();
    }

    @Override
    public FunctionCall clone(SymbolTable<? extends Symbol> st) {
        return new FunctionCall(function.clone(st), arguments.clone(st));
    }

    @Override
    public String toPrefix() {
        return this.getLexeme() + "(" + arguments + ")";
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
                + ((function == null) ? 0 : function.hashCode());
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
        FunctionCall other = (FunctionCall) obj;
        if (function == null) {
            if (other.function != null)
                return false;
        } else if (!function.equals(other.function))
            return false;
        if (arguments == null) {
            if (other.arguments != null)
                return false;
        } else if (!arguments.equals(other.arguments))
            return false;
        return true;
    }; 

}
