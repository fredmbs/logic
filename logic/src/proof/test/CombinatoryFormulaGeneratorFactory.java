package proof.test;

import proof.FormulaGenerator;
import proof.patterns.FormulaGeneratorFactory;

public class CombinatoryFormulaGeneratorFactory 
implements FormulaGeneratorFactory {

    @Override
    public FormulaGenerator newFormulaGenerator(int t, int p) {
        return new CombinatoryFormulaGenerator(t,p); 
    }
    
}
