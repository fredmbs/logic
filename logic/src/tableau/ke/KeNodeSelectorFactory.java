/**
 * 
 */
package tableau.ke;

import tableau.BranchEngine;
import tableau.patterns.NodeSelector;
import tableau.patterns.NodeSelectorFactory;

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
    public NodeSelector newNodeSelector(BranchEngine engine, NodeSelector from){
        if (from instanceof KeNodeSelector)
            return new KeNodeSelector(engine, (KeNodeSelector)from);
        return null;
    }

    @Override
    public String toString() {
        return "KE Tableau";
    }
}
