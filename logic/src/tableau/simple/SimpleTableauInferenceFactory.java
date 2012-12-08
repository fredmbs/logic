package tableau.simple;

import proof.Inference;
import proof.patterns.InferenceFactory;
import tableau.BranchEngine;

public class SimpleTableauInferenceFactory 
implements InferenceFactory<BranchEngine>
{

    @Override
    public Inference newInference(BranchEngine branchEngine) {
        return new SimpleTableauInference(branchEngine);
    }

    @Override
    public String toString() {
        return "Smullyan’s Tableau";
    }

    @Override
    public Inference newInference(BranchEngine branchEngine, BranchEngine from){
        return new SimpleTableauInference(branchEngine);
    }
    
}
