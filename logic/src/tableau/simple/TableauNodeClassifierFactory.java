package tableau.simple;

import tableau.patterns.NodeClassifier;
import tableau.patterns.NodeClassifierFactory;


public class TableauNodeClassifierFactory implements NodeClassifierFactory {

    @Override
    public NodeClassifier newNodeClassifier() {
        return new TableauNodeClassifier();
    }

}
