/**
 * 
 */
package tableau.ke;

import proof.patterns.NodeSelector;
import proof.patterns.NodeSelectorFactory;
import tableau.BranchEngine;

/**
 * @author dev
 *
 */
public class KeNodeSelectorFactory 
implements NodeSelectorFactory<BranchEngine> 
{

    @Override
    public NodeSelector newNodeSelector(BranchEngine engine) {
        return new KeNodeSelector(engine);
    }

    @Override
    public NodeSelector newNodeSelector(NodeSelector from) {
        if (from instanceof KeNodeSelector)
            return new KeNodeSelector((KeNodeSelector)from);
        return null;
    }

}
