package proof.explanation;

public class ExplanationSelf implements Explanation {

    String reasonLabel;
    
    public ExplanationSelf(String reason) {
        this.reasonLabel = reason;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + reasonLabel + "}";
    }    

}
