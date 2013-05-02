package proof.test;

import proof.FormulaGenerator;


public class CombinatoryFormulaGenerator extends FormulaGenerator {
    
    private int[] is, ic, ip;
    private boolean hasFormula = true;
    
    public CombinatoryFormulaGenerator(int terms, int predicates) {
        super(terms, predicates);
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
    
    @Override
    public boolean hasFormula() {
        return hasFormula && super.hasFormula();
    }
    
    @Override
    public long getFormulas() {
        return  (long)
                (Math.pow((this.getPredicates()*2),this.getTerms()) *
                        Math.pow(4,(this.getTerms()-1)));
    }
    
    @Override
    public StringBuffer nextFormula() {
        StringBuffer f = new StringBuffer();
        for (int n = 0; n < lastTermIndex; n++) {
            f.append(s[is[n]]);
            f.append(p[ip[n]]);
            f.append(c[ic[n]]);
        }
        f.append(s[is[lastTermIndex]]);
        f.append(p[ip[lastTermIndex]]);
        newFormula(lastTermIndex);
        super.addCount();
        return f;
    }

    @Override
    public String getName() {
        return "Combinatory";
    }

}

