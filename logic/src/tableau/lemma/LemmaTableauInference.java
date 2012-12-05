/**
 * 
 */
package tableau.lemma;

import proof.Inference;
import proof.Node;
import tableau.BranchEngine;
import tableau.TreeEngine;
import ast.*;
import ast.patterns.FormulaVisitor;

/**
 * @author dev
 * Baseado no Smullyan's Tableau (adicionando regra para equivalência <=>)
 */
public class LemmaTableauInference implements Inference, FormulaVisitor {

    private Node node;
    private boolean expanded;
    private BranchEngine engine;
    private TreeEngine treeEngine;

    protected LemmaTableauInference(BranchEngine engine) {
        this.engine = engine;
        this.treeEngine = engine.getTreeEngine();
    }
    
    public boolean infer(Node n) {
        this.expanded = false;
        this.node = n;
        //
        this.node.getFormula().accept(this);
        //
        return expanded;
    }

    private void lemmaBranching(Formula fL, boolean sL, Formula fR, boolean sR){
        BranchEngine newBE = treeEngine.add(engine, fL, sL, fL, !sL);
        newBE.add(fR, sR);
    }

    @Override
    public void visit(And and) {
        if(node.isSignT()) {
            engine.add(and.getLeft(), true);
            engine.add(and.getRight(), true);
        } else {
            lemmaBranching(and.getLeft(), false,and.getRight(), false);
            
        }
        expanded = true;
    }

    @Override
    public void visit(Or or) {
        if(node.isSignT()) {
            lemmaBranching(or.getLeft(), true, or.getRight(), true);
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
            lemmaBranching(imp.getLeft(), false, imp.getRight(), true);
        } else {
            engine.add(imp.getLeft(), true);
            engine.add(imp.getRight(), false);
        }
        expanded = true;
    }

    @Override
    public void visit(Equivalent equ) {
        if(node.isSignT()) {
            BranchEngine newEngine = treeEngine.add(engine,
                    equ.getLeft(), true, equ.getLeft(), false);
            engine.add(equ.getRight(), true);
            newEngine.add(equ.getRight(), false);
        } else {
            BranchEngine newEngine = treeEngine.add(engine,
                    equ.getLeft(), true, equ.getLeft(), false);
            engine.add(equ.getRight(), false);
            newEngine.add(equ.getRight(), true);
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
