package tableau.ke;

import tableau.patterns.NodeClassifier;
import tableau.patterns.NodeClassifierFactory;

public class KeTableauNodeClassifierFactory implements NodeClassifierFactory {

    @Override
    public NodeClassifier newNodeClassifier() {
        return new KeTableauNodeClassifier();
    }

}
