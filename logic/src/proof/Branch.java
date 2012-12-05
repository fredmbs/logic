/**
 * 
 */
package proof;


/**
 * @author dev
 *
 */
public class Branch {
    
    public enum Status { OPEN, CLOSED, EXHAUSTED, ABANDONED };
    
    private Node leaf;
    private Status status;
    
    public Branch(Node leaf) {
        this.status = Status.OPEN;
        this.leaf = leaf;
    }
    
    public Branch(Node previus, Node leaf) {
        this.status = Status.OPEN;
        this.leaf = leaf;
        this.leaf.setPrevious(previus);
        previus.setBranch(leaf);
    }
    
    public Status getStatus() {
        return status;
    }

    public Node getLeaf() {
        return leaf;
    }
    
    public void close() {
        leaf.setNext(new Node("-X-")); 
        status = Status.CLOSED;
    }

    public void exhaust() {
        leaf.setNext(new Node("-O-")); 
        status = Status.EXHAUSTED;
    }

    public void abandon() {
        leaf.setNext(new Node("!O!"));; 
        status = Status.ABANDONED;
    }
    
    public void add(Node node) {
        if (status == Status.OPEN) {
            leaf.setNext(node);
            node.setPrevious(leaf);
            leaf = node;
        }
    }
    
}
