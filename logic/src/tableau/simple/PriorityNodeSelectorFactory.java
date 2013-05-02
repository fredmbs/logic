/**
 * 
 */
package tableau.simple;

import tableau.BranchEngine;
import tableau.patterns.NodeSelector;
import tableau.patterns.NodeSelectorFactory;


/**
 * @author dev
 *
 */
public class PriorityNodeSelectorFactory
implements NodeSelectorFactory<BranchEngine>
{

    @Override
    public String toString() {
        return "Priority Node Selector";
    }

    @Override
    public NodeSelector newNodeSelector(BranchEngine engine) {
        return new PriorityNodeSelector(engine);
    }

    @Override
    public NodeSelector newNodeSelector(BranchEngine engine, NodeSelector from){
        if (from instanceof PriorityNodeSelector)
            return new PriorityNodeSelector(engine, (PriorityNodeSelector)from);
        return null;
    }

}
