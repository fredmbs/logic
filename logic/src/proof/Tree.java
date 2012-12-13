/**
 * 
 */
package proof;

import ast.Formula;

/**
 * @author dev
 *
 */
public class Tree {

    protected Node head;
    
    
    public Tree() {
        this.head = new Node("-TRUE-");
    }

    public Node getHead() {
        return head;
    }
    
    public Node getRoot() {
        return head.getNext();
    }
    
    public Node searchFormula(Node leaf) {
        Node searchNode = leaf;
        while(searchNode.getPrevious() != null) {
            searchNode = searchNode.getPrevious();
            if (searchNode.formulaEquals(leaf.getFormula()))
                return searchNode;
        }
        return null;
    }

    public Node searchEqual(Node leaf, Node node) {
        Node searchNode = leaf;
        while(searchNode.getPrevious() != null) {
            if (searchNode.equals(node))
                return searchNode;
            searchNode = searchNode.getPrevious();
        }
        return null;
    }

    public Node searchFormula(Node leaf, Formula formula) {
        Node searchNode = leaf;
        while(searchNode.getPrevious() != null) {
            if (searchNode.getFormula().equals(formula)) {
                return searchNode;
            }
            searchNode = searchNode.getPrevious();
        }
        return null;
    }

    
    public int countNodes() {
        return countNodesFrom(head);
    }

    private int countNodesFrom(Node node) {
        int count = 0;
        // n.next, para evitar a contagem do node de marcação do fim do brnach
        while (node.getNext() != null) {
            count++;
            if (node.getBranch() != null)
                count += countNodesFrom(node.getBranch());
            node = node.getNext();
        }
        return count;
    }

    public void numberingNodes() {
        indetifyNodeByCount(this.getRoot(), 1);
    }

    public int print() {
        return print(this.getRoot(), "| ", 1);
    }
    
    protected int print(Node n, String prefix, int count) {
        while (n != null) {
            String col1 = String.format(
                    "%6s %s%-30s",
                    count++, prefix, n.toString());
            System.out.println(String.format(
                    "%-45s %s", col1, n.getExplanation()));
            if (n.getBranch() != null) 
                count = print(n.getBranch(), prefix + "| ", count);
            n = n.getNext();
        }
        return count;
    }

    protected int indetifyNodeByCount(Node n, int count) {
        while (n != null) {
            n.setId(count++);
            if (n.getBranch() != null) 
                count = indetifyNodeByCount(n.getBranch(), count);
            n = n.getNext();
        }
        return count;
    }
}

