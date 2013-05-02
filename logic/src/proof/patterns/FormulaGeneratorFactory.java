package proof.patterns;

import proof.FormulaGenerator;

public interface FormulaGeneratorFactory {
    public FormulaGenerator newFormulaGenerator(int t, int p);
}
