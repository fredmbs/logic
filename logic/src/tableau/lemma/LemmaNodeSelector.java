/**
 * 
 */
package tableau.lemma;

import java.util.PriorityQueue;
import tableau.BranchEngine;
import tableau.Node;
import tableau.patterns.NodeSelector;

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
        Node fulfill;
        int blocks;
        // Otimização de regularidade: não expande nós realizado (fulfilled)
        do {
            node = unexpandedNodes.poll();
            blocks = 2;
            if (node == null)
                return null;
            fulfill = engine.getBranch().searchFulfilledRight(node);
            if (fulfill == null) {
                blocks--;
            } else {
                if (fulfill.getExplanation().toString().equals("{lemma}"))
                    blocks--;
            }
            fulfill = engine.getBranch().searchFulfilledLeft(node);
            if (fulfill == null) {
                blocks--;
            } else {
                if (fulfill.getExplanation().toString().equals("{lemma}"))
                    blocks--;
            }
        } while (blocks > 0);
        return node;
    }

    @Override
    public void regress(Node node) {
        this.add(node);
    }

}
