package proof.patterns;

import proof.Node;

public interface NodeSelector {
    public void add(Node node);
    public Node select();
    public void regress(Node node);
}
