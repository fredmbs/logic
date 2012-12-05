package tableau.lemma;

import proof.Inference;
import proof.patterns.InferenceFactory;
import tableau.BranchEngine;

public class LemmaTableauInferenceFactory 
implements InferenceFactory<BranchEngine>
{

    @Override
    public Inference newInference(BranchEngine branchEngine) {
        return new LemmaTableauInference(branchEngine);
    }

    @Override
    public String toString() {
        return "Lemma Tableau";
    }
    
}
