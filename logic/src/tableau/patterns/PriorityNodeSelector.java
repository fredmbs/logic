/**
 * 
 */
package tableau.patterns;

import java.util.PriorityQueue;
import proof.Node;
import proof.patterns.NodeSelector;
import tableau.BranchEngine;

/**
 * @author dev
 *
 */
public class PriorityNodeSelector implements NodeSelector {

    private PriorityQueue<Node> unexpandedNodes;
    private BranchEngine engine;
    
    public PriorityNodeSelector(BranchEngine engine) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>();
    }

    public PriorityNodeSelector(BranchEngine engine, PriorityNodeSelector from) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>(from.unexpandedNodes);
    }

    public void add(Node node) {
        if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal())
            unexpandedNodes.add(node);
    }

    public Node select() {
        Node node;
        do {
            node = unexpandedNodes.poll();
        } while (node != null && engine.getBranch().isFulfilled(node));
        return node;
    }

    public Node select2() {
        return unexpandedNodes.poll();
    }

    @Override
    public void regress(Node node) {
        this.add(node);
    }

}
