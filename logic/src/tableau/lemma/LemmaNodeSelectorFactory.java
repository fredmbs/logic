/**
 * 
 */
package tableau.lemma;

import proof.patterns.NodeSelector;
import proof.patterns.NodeSelectorFactory;
import tableau.BranchEngine;


/**
 * @author dev
 *
 */
public class LemmaNodeSelectorFactory
implements NodeSelectorFactory<BranchEngine>
{

    @Override
    public String toString() {
        return "Priority Node Selector";
    }

    @Override
    public NodeSelector newNodeSelector(BranchEngine engine) {
        return new LemmaNodeSelector(engine);
    }

    @Override
    public NodeSelector newNodeSelector(BranchEngine engine, NodeSelector from){
        if (from instanceof LemmaNodeSelector)
            return new LemmaNodeSelector(engine, (LemmaNodeSelector)from);
        return null;
    }

}
