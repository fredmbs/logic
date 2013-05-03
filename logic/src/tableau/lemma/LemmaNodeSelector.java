/**
 * 
 */
package tableau.lemma;

import tableau.BranchEngine;
import tableau.Node;
import tableau.patterns.PriorityNodeSelector;

/**
 * @author dev
 *
 */
public class LemmaNodeSelector extends PriorityNodeSelector {

    public LemmaNodeSelector(BranchEngine engine) {
        super(engine);
    }

    public LemmaNodeSelector(BranchEngine engine, LemmaNodeSelector from) {
        super(engine, from);
    }

    @Override
    public Node select() {
        Node node;
        Node[] fulfill;
        int blocks;
        // Otimização de regularidade: não expande nós realizado (fulfilled)
        // contudo os nós lema não devem impedir a expanção
        do {
            node = unexpandedNodes.poll();
            if (node == null)
                return null;
            fulfill = engine.getBranch().fulfilledBy(node);
            blocks = 2;
            for (int i = 0; i < 2; i++) 
                if (fulfill[i] == null) {
                    blocks--;
                } else {
                    if (fulfill[i].getExplanation().toString().equals("{lemma}"))
                        blocks--;
                }
        } while (blocks > 0);
        return node;
    }

}
