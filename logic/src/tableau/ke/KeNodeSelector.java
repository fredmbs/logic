/**
 * 
 */
package tableau.ke;

import java.util.ArrayList;
import tableau.BranchEngine;
import tableau.Node;
import tableau.patterns.PriorityNodeSelector;

/**
 * @author dev
 *
 */
public class KeNodeSelector extends PriorityNodeSelector {
    
    private ArrayList<Node> openBetas;
    KePbSelector selector;
    
    public KeNodeSelector(BranchEngine engine) {
        super(engine);
        this.openBetas = new ArrayList<Node>();
        selector = new KePbSelector(engine);
    }
    
    public KeNodeSelector(BranchEngine engine, KeNodeSelector from) {
        super(engine, (PriorityNodeSelector)from);
        this.openBetas = new ArrayList<Node>(from.openBetas); 
        selector = new KePbSelector(engine);
    }
    
    public void regressOpenBetas() {
        // retorna os nós beta abertos
        int numOpenBetas = openBetas.size();
        if (numOpenBetas > 0) {
            Node returned;
            for (int i = 0; i < numOpenBetas; i++) {
                returned = openBetas.get(i);
                if (returned != null) {
                    super.add(returned);
                }
            }
            openBetas.clear();
        }
    }
    
    @Override
    public void add(Node node) {
        regressOpenBetas();
        super.add(node);
    }
    
    @Override
    public Node select() {
        Node node = super.select();
        if (node == null) {
            if (!openBetas.isEmpty()) {
                node = selector.select(openBetas);
                if (node != null) 
                        regressOpenBetas();
            } 
        }
        return node;
    }
    
    @Override
    public void regress(Node node) {
        Node.Type type = node.getType();
        if (type == Node.Type.BETA) {
            openBetas.add(node);
        } else { 
            super.regress(node);
        }
    }
    
}
