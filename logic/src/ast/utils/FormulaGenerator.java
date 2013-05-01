package ast.utils;


public class FormulaGenerator {
    
    private StringBuffer f = new StringBuffer();
    private int terms, predicates;
    private int lastTermIndex, lastPredicateIndex;
    private String[] s = {"", "!"};
    private String[] c = {"v", "^", "->", "<->"};
    private int maxC = (c.length - 1); 
    int[] is, ic, ip;
    boolean hasFormula = true;
    
    public FormulaGenerator(int terms, int predicates) {
        this.terms = terms;
        this.predicates = predicates;
        this.lastTermIndex = terms - 1;
        this.lastPredicateIndex = predicates - 1;
        this.is = new int[terms];
        this.ic = new int[lastTermIndex];
        this.ip = new int[terms];
        for (int i = 0; i < terms; i++) {
            is[i] = 0;
        }
        for (int i = 0; i < lastTermIndex; i++) {
            ic[i] = 0;
        }
        for (int i = 0; i < terms; i++) {
            ip[i] = 0;
        }
    }
    
    private void newFormula(int n) {
        if (ip[n] == lastPredicateIndex) {
            ip[n] = 0;
            if (is[n] == 1) {
                is[n] = 0;
                if (n < lastTermIndex) {
                    if (ic[n] == maxC) {
                        ic[n] = 0;
                        if (n == 0) 
                            hasFormula = false;
                        else
                            newFormula(n - 1);
                    } else {
                        ic[n] += 1;
                    }
                } else {
                    newFormula(n - 1);
                }
            } else {
                is[n] = 1;
            }
        } else {
            ip[n] += 1;
        }
    }
    
    public boolean hasFormula() {
        return hasFormula;
    }
    
    public double getNumFormulas() {
        return ((predicates*2)^terms)*4^(terms-1);
    }
    
    public StringBuffer nextFormula() {
        f = new StringBuffer();
        int p;
        for (int n = 0; n < lastTermIndex; n++) {
            p = (65+ip[n]);
            f.append(s[is[n]]);
            f.append((char)p);
            f.append(c[ic[n]]);
        }
        p = (65+ip[lastTermIndex]);
        f.append(s[is[lastTermIndex]]);
        f.append((char)p);
        newFormula(lastTermIndex);
        return f;
    }
    
}

