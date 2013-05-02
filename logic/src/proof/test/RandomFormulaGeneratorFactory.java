package proof.test;

import proof.FormulaGenerator;
import proof.patterns.FormulaGeneratorFactory;

public class RandomFormulaGeneratorFactory implements FormulaGeneratorFactory {

    @Override
    public FormulaGenerator newFormulaGenerator(int t, int p) {
        return new RandomFormulaGenerator(t,p); 
    }
    
}
