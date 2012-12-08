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
    
    public Node close() {
        Node node = new Node("-X-");
        leaf.setNext(node); 
        status = Status.CLOSED;
        return node;
    }

    public Node exhaust() {
        Node node = new Node("-O-");
        leaf.setNext(node); 
        status = Status.EXHAUSTED;
        return node;
    }

    public Node abandon() {
        Node node = new Node("!O!");
        leaf.setNext(node);; 
        status = Status.ABANDONED;
        return node;
    }
    
    public void add(Node node) {
        if (status == Status.OPEN) {
            leaf.setNext(node);
            node.setPrevious(leaf);
            leaf = node;
        }
    }
    
    public int printDebug() {
        Node n = leaf; String prefix = "|"; int count = 0;
        while (n != null) {
            System.err.println(String.format(
                    "%6s %s%s",
                    count++, prefix, n.toString()));
            n = n.getPrevious();
        }
        return count;
    }
}
