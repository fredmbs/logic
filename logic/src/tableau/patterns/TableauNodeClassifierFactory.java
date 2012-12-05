package tableau.patterns;

import proof.patterns.NodeClassifier;
import proof.patterns.NodeClassifierFactory;

public class TableauNodeClassifierFactory implements NodeClassifierFactory {

    @Override
    public NodeClassifier newNodeClassifier() {
        return new TableauNodeClassifier();
    }

}
