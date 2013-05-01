/**
 * 
 */
package tableau.lemma;

import java.util.PriorityQueue;
import proof.patterns.NodeSelector;
import tableau.BranchEngine;
import tableau.Node;

/**
 * @author dev
 *
 */
public class LemmaNodeSelector implements NodeSelector {

    private PriorityQueue<Node> unexpandedNodes;
    private BranchEngine engine;
    
    public LemmaNodeSelector(BranchEngine engine) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>();
    }

    public LemmaNodeSelector(BranchEngine engine, LemmaNodeSelector from) {
        this.engine = engine;
        this.unexpandedNodes = new PriorityQueue<Node>(from.unexpandedNodes);
    }

    @Override
    public void add(Node node) {
        if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal())
            unexpandedNodes.add(node);
    }

/*    @Override
    public Node select() {
        return unexpandedNodes.poll();
    }
*/
    @Override
    public Node select() {
        Node node;
        Node[] fulfill = new Node[2];
        // Otimização de regularidade: não expande nós realizado (fulfilled)
        do {
            node = unexpandedNodes.poll();
            if (node == null)
                return null;
            fulfill = engine.getBranch().searchtFulfillNodes(node);
            if (fulfill[0] != null) {
                if (fulfill[0].getExplanation().toString().equals("{lemma}"))
                    return node;
            }
            if (fulfill[1] != null) {
                if (fulfill[1].getExplanation().toString().equals("{lemma}"))
                    return node;
            }
        } while (true);
    }

    @Override
    public void regress(Node node) {
        this.add(node);
    }

}
