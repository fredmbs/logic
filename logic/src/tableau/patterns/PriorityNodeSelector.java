/**
 * 
 */
package tableau.patterns;

import java.util.PriorityQueue;
import tableau.BranchEngine;
import tableau.Node;

/**
 * @author dev
 *
 */
public class PriorityNodeSelector implements NodeSelector {

    protected PriorityQueue<Node> unexpandedNodes;
    protected BranchEngine engine;
    
    public PriorityNodeSelector(BranchEngine engine) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>();
    }

    public PriorityNodeSelector(BranchEngine engine, PriorityNodeSelector from) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>(from.unexpandedNodes);
    }

    @Override
    public void add(Node node) {
        if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal())
            unexpandedNodes.add(node);
    }

    @Override
    public Node select() {
        Node node;
        // Otimização de regularidade: não expande nós realizado (fulfilled)
        do {
            node = unexpandedNodes.poll();
        } while (node != null && engine.getBranch().isFulfilled(node));
        return node;
    }

    @Override
    public void regress(Node node) {
        this.add(node);
    }

}
