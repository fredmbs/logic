package proof.test;

import proof.FormulaGenerator;

public class RandomFormulaGenerator extends FormulaGenerator {

    public RandomFormulaGenerator(int terms, int predicates) {
        super(terms, predicates);
    }

    @Override
    public StringBuffer nextFormula() {
        StringBuffer f = new StringBuffer();
        for (int n = 0; n < lastTermIndex; n++) {
            f.append(s[(((int)(Math.random()*4)) == 1 ? 1: 0)]);
            f.append(p[((int)(Math.random()*this.predicates))]);
            f.append(c[((int)(Math.random()*this.maxC))]);
        }
        f.append(s[(((int)(Math.random()*4)) == 1 ? 1: 0)]);
        f.append(p[((int)(Math.random()*this.predicates))]);
        super.addCount();
        return f;
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public long getFormulas() {
        return  (long)
                (Math.pow((this.getPredicates()*2),this.getTerms()) *
                        Math.pow(4,(this.getTerms()-1)));
    }
    
}
