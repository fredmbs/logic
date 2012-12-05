package ast;

public abstract class Commutative extends Connective {

    public Commutative(Formula left, Formula right) {
        super(left, right);
    }

    public Commutative() {
        super();
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
        } 
        if (right == null) {
            if (other.right != null)
                return false;
        } 
        if (left.equals(other.left))
            return (right.equals(other.right));
        if (left.equals(other.right))
            return (right.equals(other.left));
        return false;
    }; 

}
