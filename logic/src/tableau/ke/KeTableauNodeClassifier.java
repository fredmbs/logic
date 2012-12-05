package tableau.ke;

import proof.Node;
import proof.patterns.NodeClassifier;
import ast.*;
import ast.patterns.FormulaVisitor;

public class KeTableauNodeClassifier 
implements NodeClassifier,FormulaVisitor 
{

    private Node node;
    
    @Override
    public void classify(Node node) {
        this.node = node;
        node.getFormula().accept(this);
    }

    @Override
    public void visit(And and) {
        if (node.isSignT()) {
            node.setType(Node.Type.ALFA);
            node.setPriority(6);
        } else { 
            node.setType(Node.Type.BETA);
            node.setPriority(2);
        }
    }

    @Override
    public void visit(Or or) {
        if (node.isSignT()) {
            node.setType(Node.Type.BETA);
            node.setPriority(1);
        } else { 
            node.setType(Node.Type.ALFA);
            node.setPriority(7);
        }
    }

    @Override
    public void visit(Not not) {
        node.setType(Node.Type.ALFA);
        node.setPriority(10);
    }

    @Override
    public void visit(Implies imp) {
        if (node.isSignT()) {
            node.setType(Node.Type.BETA);
            node.setPriority(3);
        } else { 
            node.setType(Node.Type.ALFA);
            node.setPriority(8);
        }
    }

    @Override
    public void visit(Equivalent equ) {
        node.setType(Node.Type.ALFA);
        node.setPriority(9);
    }

    @Override
    public void visit(Equality equal) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Exists ex) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Forall fa) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Predicate p) {
        // TODO Auto-generated method stub
        node.setType(Node.Type.ATOMIC);
    }

}
