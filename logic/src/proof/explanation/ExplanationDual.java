package proof.explanation;

import proof.Node;

public class ExplanationDual implements Explanation {

    Node first, second;
    String reasonLabel;
    
    public ExplanationDual(Node from1, Node from2, String reason) {
        this.first = from1;
        this.second = from2;
        this.reasonLabel = reason;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + first.getId() + "," + second.getId() + " " + reasonLabel + "}";
    }
    
    
}
