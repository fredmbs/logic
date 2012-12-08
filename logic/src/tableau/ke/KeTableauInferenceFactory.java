package tableau.ke;

import proof.Inference;
import proof.patterns.InferenceFactory;
import tableau.BranchEngine;

public class KeTableauInferenceFactory 
implements InferenceFactory<BranchEngine>
{

    @Override
    public Inference newInference(BranchEngine branchEngine) {
        return new KeTableauInference(branchEngine);
    }

    @Override
    public String toString() {
        return "KE Tableau";
    }

    @Override
    public Inference newInference(BranchEngine branchEngine, BranchEngine from){
        return new KeTableauInference(branchEngine);
    }

}
