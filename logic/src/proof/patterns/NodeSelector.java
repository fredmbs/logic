package proof.patterns;

import tableau.Node;

public interface NodeSelector {
    public void add(Node node);
    public Node select();
    public void regress(Node node);
}
