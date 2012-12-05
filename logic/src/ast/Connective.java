/**
 * 
 */
package ast;

/**
 * @author dev
 *
 */
public abstract class Connective extends Formula implements Symbol {

    protected Formula left, right;
   
    public void setLexeme(String sym) {
    }

    public String getLexeme() {
        return "";
    }
    
    public Connective() {
        super();
        this.left = null;
        this.right = null;
    }
    
    public Connective(Formula left, Formula right) {
        super();
        this.left = left;
        this.right = right;
    }
    
    public Formula getLeft() {
        return left;
    }

    public void setLeft(Formula left) {
        this.left = left;
    }

    public Formula getRight() {
        return right;
    }

    public void setRight(Formula right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + getLexeme() + right + ")";
    }

    @Override
    public String toPrefix() {
        return getLexeme() + left.toPrefix() + right.toPrefix();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
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
        Connective other = (Connective) obj;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
            return false;
        return true;
    }; 

    
}
