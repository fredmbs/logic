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
    
    public Node searchEqual(Node node) {
        Node searchNode = node;
        while(searchNode.getPrevious() != null) {
            searchNode = searchNode.getPrevious();
            if (searchNode.equals(node))
                return searchNode;
        }
        return null;
    }

    public Node searchEqual(Node leaf, Formula formula) {
        Node searchNode = leaf;
        while(searchNode.getPrevious() != null) {
            if (searchNode.getFormula().equals(formula))
                return searchNode;
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

    public int print() {
        return print(this.getRoot(), "| ", 1);
    }
    
    protected int print(Node n, String prefix, int count) {
        while (n != null) {
            System.out.println(String.format(
                    "%6s %s %s",
                    count++, prefix, n.toString()));
            if (n.getBranch() != null) 
                count = print(n.getBranch(), prefix + "| ", count);
            n = n.getNext();
        }
        return count;
    }
}

