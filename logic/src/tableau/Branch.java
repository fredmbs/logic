/**
 * 
 */
package tableau;

import ast.And;
import ast.Connective;
import ast.Implies;
import ast.Or;


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
    
    public boolean isFulfilled(Node node) {
        if (!(node.getFormula() instanceof Connective)) {
            return false;
        }
        Connective conective = (Connective)node.getFormula();
        boolean signT = node.isSignT(); 
        if (signT && conective instanceof Or) { 
            Node nodeSearchBl = Node.removeNot(conective.getLeft());
            Node nodeBl = Tree.searchFormula(this.leaf, nodeSearchBl.getFormula());
            if ((nodeBl != null) && (nodeBl.isSignT() == nodeSearchBl.isSignT())) {
                return true;
            }
            Node nodeSearchBr = Node.removeNot(conective.getRight());
            Node nodeBr = Tree.searchFormula(this.leaf, nodeSearchBr.getFormula());
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                return true;
            }
        } else if (!signT && conective instanceof And) {
            Node nodeSearchBl = Node.removeNot(conective.getLeft());
            Node nodeBl = Tree.searchFormula(this.leaf, nodeSearchBl.getFormula());
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                return true;
            }
            Node nodeSearchBr = Node.removeNot(conective.getRight());
            Node nodeBr = Tree.searchFormula(this.leaf, nodeSearchBr.getFormula());
            if ((nodeBr != null) && (nodeBr.isSignT() != nodeSearchBr.isSignT())) {
                return true;
            }
        } else if (signT && conective instanceof Implies) {
            Node nodeSearchBl = Node.removeNot(conective.getLeft());
            Node nodeBl = Tree.searchFormula(this.leaf, nodeSearchBl.getFormula());
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                return true;
            }
            Node nodeSearchBr = Node.removeNot(conective.getRight());
            Node nodeBr = Tree.searchFormula(this.leaf, nodeSearchBr.getFormula());
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                return true;
            }
        } 
        return false;
    }

    public Node[] fulfilledBy(Node node) {
        Node[] nodes = new Node[2];
        if (!(node.getFormula() instanceof Connective)) {
            return nodes;
        }
        Connective conective = (Connective)node.getFormula();
        Node nodeSearchBl = Node.removeNot(conective.getLeft());
        Node nodeBl = Tree.searchFormula(this.leaf, nodeSearchBl.getFormula());
        Node nodeSearchBr = Node.removeNot(conective.getRight());
        Node nodeBr = Tree.searchFormula(this.leaf, nodeSearchBr.getFormula());
        boolean signT = node.isSignT(); 
        if (signT && conective instanceof Or) { 
            if ((nodeBl != null) && (nodeBl.isSignT() == nodeSearchBl.isSignT())) {
                nodes[0] = nodeBl;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                nodes[1] = nodeBr;
            }
        } else if (!signT && conective instanceof And) {
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                nodes[0] = nodeBl;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() != nodeSearchBr.isSignT())) {
                nodes[1] = nodeBr;
            }
        } else if (signT && conective instanceof Implies) {
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                nodes[0] = nodeBl;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                nodes[1] = nodeBr;
            }
        } 
        return nodes;
    }

}
