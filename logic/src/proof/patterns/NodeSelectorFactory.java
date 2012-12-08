/**
 * 
 */
package proof.patterns;


/**
 * @author dev
 *
 */
public interface NodeSelectorFactory<T> {
    public NodeSelector newNodeSelector(T engine);
    public NodeSelector newNodeSelector(T engine, NodeSelector from);
}
