package proof.explanation;

import tableau.Node;

public class ExplanationSingle implements Explanation {

    Node from;
    String reasonLabel;
    
    public ExplanationSingle(Node from, String reason) {
        this.from = from;
        this.reasonLabel = reason;
    }

    /**
     * @return the from
     */
    public Node getFrom() {
        return from;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + from.getId() + " " + reasonLabel + "}";
    }
}
