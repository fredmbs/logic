package tableau.ke;

import proof.patterns.NodeClassifier;
import proof.patterns.NodeClassifierFactory;

public class KeTableauNodeClassifierFactory implements NodeClassifierFactory {

    @Override
    public NodeClassifier newNodeClassifier() {
        return new KeTableauNodeClassifier();
    }

}
