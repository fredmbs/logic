package proof;

public abstract class FormulaGenerator {

    protected int terms, predicates;
    private long timeOut = 0, maxTests = 0, count = 0, time;
    protected String[] p;
    protected String[] s = {"", "!"};
    protected String[] c = {"v", "^", "->", "<->"};
    protected int maxC = (c.length - 1); 
    protected int lastTermIndex, lastPredicateIndex;

    public abstract long getFormulas();
    public abstract StringBuffer nextFormula();
    public abstract String getName();

    public FormulaGenerator(int terms, int predicates) {
        this.terms = terms;
        this.predicates = predicates;
        this.lastTermIndex = terms - 1;
        this.lastPredicateIndex = predicates - 1;
        this.time = System.currentTimeMillis();
        this.p = new String[predicates];
        StringBuffer s = new StringBuffer();
        for(int i = 0; i < predicates; i++) {
            s.setLength(0);
            generatePredName(i,s);
            p[i] = s.toString();
        }
    }

    private void generatePredName(int p, StringBuffer s) {
        if (p >= 26) {
            generatePredName(p/26-1, s);
            p = p%26;
        }
        s.append((char)((65+p)));
    }
    
    public int getTerms() {
        return this.terms;
    }

    public int getPredicates() {
        return this.predicates;
    }
    
    public long getMaxTests() {
        return maxTests;
    }

    public void setMaxTests(long maxTests) {
        this.maxTests = maxTests;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public boolean hasFormula() {
        if (timeOut > 0) {
            if (System.currentTimeMillis() - time >= timeOut)
                return false;
        }
        return ((maxTests <= 0) || (count < maxTests));
    }

    public long getCount() {
        return count;
    }
    
    protected void addCount() {
        count++;
    }
    
}
