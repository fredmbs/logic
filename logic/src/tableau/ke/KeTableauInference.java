package tableau.ke;

import ast.*;
import ast.patterns.FormulaVisitor;
import proof.Inference;
import proof.Node;
import tableau.BranchEngine;
import tableau.TreeEngine;

public class KeTableauInference implements Inference, FormulaVisitor {

    private Node node;
    private boolean expanded;
    private BranchEngine engine;
    private TreeEngine treeEngine;

    protected KeTableauInference(BranchEngine engine) {
        this.engine = engine;
        this.treeEngine = engine.getTreeEngine();
        engine.setClosureOnNonAtomicNodes(true);
    }
    
    @Override
    public boolean infer(Node n) {
        // se o nó é um princípio de bivalência, ramifica a árvore
        if (n.getType() == Node.Type.PB) {
            treeEngine.add(engine, n.getFormula(), true, n.getFormula(), false);
            return false;
        }
        // senão, tenta uma eliminação
        this.expanded = false;
        this.node = n;
        //
        this.node.getFormula().accept(this);
        //
        return expanded;
    }
    
    private boolean searchFormula(Formula subBeta, boolean signT) {
        Node leaf = engine.getBranch().getLeaf();
        Node node = treeEngine.getTree().searchEqual(leaf, subBeta);
        if (node != null) {
            if (node.isSignT() == signT)
                return true;
        }
        return false;
    }
    
    @Override
    public void visit(And and) {
        if(node.isSignT()) {
            engine.add(and.getLeft(), true);
            engine.add(and.getRight(), true);
        } else {
            if (searchFormula(and.getLeft(), true))
                engine.add(and.getRight(), false);
            else if (searchFormula(and.getRight(), true))
                engine.add(and.getLeft(), false);
            else return;
        }
        expanded = true;
    }

    @Override
    public void visit(Or or) {
        if(node.isSignT()) {
            if (searchFormula(or.getLeft(), false))
                engine.add(or.getRight(), true);
            else if (searchFormula(or.getRight(), false))
                engine.add(or.getLeft(), true);
            else return;
        } else {
            engine.add(or.getLeft(), false);
            engine.add(or.getRight(), false);
        }
        expanded = true;
    }

    @Override
    public void visit(Not not) {
        if(node.isSignT()) {
            engine.add(not.getFormula(), false);
        } else {
            engine.add(not.getFormula(), true);
        }
        expanded = true;
    }

    @Override
    public void visit(Implies imp) {
        if(node.isSignT()) {
            if (searchFormula(imp.getLeft(), true))
                engine.add(imp.getRight(), true);
            else if (searchFormula(imp.getRight(), false))
                engine.add(imp.getLeft(), false);
            else return;
        } else {
            engine.add(imp.getLeft(), true);
            engine.add(imp.getRight(), false);
        }
        expanded = true;
    }

    @Override
    public void visit(Equivalent equ) {
        if(node.isSignT()) {
            engine.add(new Implies(equ.getLeft(), equ.getRight()), true);
            engine.add(new Implies(equ.getRight(), equ.getLeft()), true);
        } else {
            engine.add(new Implies(equ.getLeft(), equ.getRight()), false);
            engine.add(new Implies(equ.getRight(), equ.getLeft()), false);
        }
        expanded = true;
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
        expanded = true;
    }

}
