/**
 * 
 */
package proof;

import ast.*;

/**
 * @author dev
 *
 */
public class Node implements Comparable<Node> {

    // ATENÇÃO: a ordem desse tipo pode definir a ordem de expanção dos nós 
    public enum Type { 
        UNCLASSIFIED, PB, ATOMIC, ALFA, BETA, DELTA, GAMMA, MARK }; 
    
    private Node previous, next, branch;
    private Formula formula;
    private boolean signT;
    private Type type;
    private String strLabel;
    private int priority;

    public Node(Formula f) {
        this(f, true);
    }
    
    public Node(Formula f, boolean sT) {
        this.setPrevious(null);
        this.next = null;
        this.branch = null; 
        this.formula = f;
        this.signT = sT;
        this.type = Node.Type.UNCLASSIFIED;
        this.priority = 0;
    }
    
    protected Node(String mark) {
        this.setPrevious(null);
        this.next = null;
        this.branch = null; 
        this.formula = null;
        this.signT = true;
        this.strLabel = mark;
        this.type = Type.MARK;
    }

    protected void setPrevious(Node previous) {
        this.previous = previous;
    }

    protected void setBranch(Node branch) {
        this.branch = branch;
    }

    protected void setNext(Node next) {
        this.next = next;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    protected void setSignT(boolean signT) {
        this.signT = signT;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public int getPriority() {
        return priority;
    }

    public Node getNext() {
        return next;
    }

    public Node getBranch() {
        return branch;
    }

    public Node getPrevious() {
        return previous;
    }

    public Formula getFormula() {
        return formula;
    }

    public boolean isSignT() {
        return signT;
    }

    public Type getType() {
        return type;
    }

    public String getLabel() {
        if (this.formula == null) {
            return strLabel;
        }
        return (signT ? "(T) " : "(F) ") + formula.toString();
    }

    @Override
    public String toString() {
        if (formula == null)
            return strLabel;
        return (signT?"(T) ":"(F) ") + formula.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formula == null) ? 0 : formula.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (formula == null) {
            if (other.formula != null)
                return false;
        } else if (!formula.equals(other.formula))
            return false;
        return true;
    }

    @Override
    public int compareTo(Node other) {
        return other.priority - this.priority;
    }

}
