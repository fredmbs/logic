/**
 * 
 */
package tableau.patterns;

import java.util.PriorityQueue;
import proof.Node;
import proof.patterns.NodeSelector;

/**
 * @author dev
 *
 */
public class PriorityNodeSelector implements NodeSelector {

    private PriorityQueue<Node> unexpandedNodes;
    
    public PriorityNodeSelector() {
        this.unexpandedNodes = new PriorityQueue<Node>();
    }

    public PriorityNodeSelector(PriorityNodeSelector from) {
        this.unexpandedNodes = new PriorityQueue<Node>(from.unexpandedNodes);
    }

    public void add(Node node) {
        if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal())
            unexpandedNodes.add(node);
    }

    public Node select() {
        return unexpandedNodes.poll();
    }

    @Override
    public void regress(Node node) {
        this.add(node);
    }

}
