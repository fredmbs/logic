/**
 * 
 */
package tableau.lemma;

import tableau.BranchEngine;
import tableau.patterns.NodeSelector;
import tableau.patterns.NodeSelectorFactory;


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
        if (from instanceof LemmaNodeSelector) {
            return new LemmaNodeSelector(engine, (LemmaNodeSelector)from);
        }
        return null;
    }

}
