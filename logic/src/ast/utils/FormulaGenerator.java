package ast.utils;


public class FormulaGenerator {
    
    StringBuffer f = new StringBuffer();
    int numTerms, numConnectives, numPredicates;
    int lastTermIndex, lastPredicateIndex;
    String[] s = {"", "!"};
    String[] c = {"v", "^", "->"};
    int[] is, ic, ip;
    boolean hasFormula = true;
    
    public FormulaGenerator(int terms, int predicates) {
        this.numPredicates = predicates;
        this.numTerms = terms;
        this.lastTermIndex = terms - 1;
        this.lastPredicateIndex = predicates - 1;
        this.numConnectives = numTerms - 1;
        this.is = new int[numTerms];
        this.ic = new int[numConnectives];
        this.ip = new int[numTerms];
        for (int i = 0; i < numTerms; i++) {
            is[i] = 0;
        }
        for (int i = 0; i < numConnectives; i++) {
            ic[i] = 0;
        }
        for (int i = 0; i < numTerms; i++) {
            ip[i] = 0;
        }
    }
    
    private void newFormula(int n) {
        if (ip[n] == lastPredicateIndex) {
            ip[n] = 0;
            if (is[n] == 1) {
                is[n] = 0;
                if (n < lastTermIndex) {
                    if (ic[n] == 2) {
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

