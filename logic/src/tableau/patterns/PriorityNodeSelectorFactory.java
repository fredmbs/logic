/**
 * 
 */
package tableau.patterns;

import proof.patterns.NodeSelector;
import proof.patterns.NodeSelectorFactory;
import tableau.BranchEngine;


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
        return new PriorityNodeSelector();
    }

    @Override
    public NodeSelector newNodeSelector(NodeSelector from) {
        if (from instanceof PriorityNodeSelector)
            return new PriorityNodeSelector((PriorityNodeSelector)from);
        return null;
    }

}
