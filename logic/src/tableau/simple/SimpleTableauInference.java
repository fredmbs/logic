/**
 * 
 */
package tableau.simple;

import proof.Inference;
import proof.explanation.ExplanationSingle;
import tableau.BranchEngine;
import tableau.Node;
import tableau.TreeEngine;
import ast.*;
import ast.patterns.FormulaVisitor;

/**
 * @author dev
 * Baseado no Smullyan's Tableau (adicionando regra para equivalência <=>)
 */
public class SimpleTableauInference implements Inference, FormulaVisitor {

    private Node node;
    private boolean expanded;
    private BranchEngine engine;
    private TreeEngine treeEngine;

    protected SimpleTableauInference(BranchEngine engine) {
        this.engine = engine;
        this.treeEngine = engine.getTreeEngine();
    }

    @Override
    public boolean infer(Node n) {
        this.expanded = false;
        this.node = n;
        //
        this.node.getFormula().accept(this);
        //
        return expanded;
    }

    @Override
    public void visit(And and) {
        if(node.isSignT()) {
            engine.add(and.getLeft(), true)
                .setExplanation(new ExplanationSingle(node, "T^:l"));
            engine.add(and.getRight(), true)
                .setExplanation(new ExplanationSingle(node, "T^:r"));;
        } else {
            treeEngine.add(engine, 
                and.getLeft(), false, new ExplanationSingle(node, "F^:l"),
                and.getRight(), false, new ExplanationSingle(node, "F^:r"));
        }
        expanded = true;
    }

    @Override
    public void visit(Or or) {
        if(node.isSignT()) {
            treeEngine.add(engine, 
                or.getLeft(), true, new ExplanationSingle(node, "Tv:l"), 
                or.getRight(), true, new ExplanationSingle(node, "Tv:r"));
        } else {
            engine.add(or.getLeft(), false)
                .setExplanation(new ExplanationSingle(node, "Fv:l"));
            engine.add(or.getRight(), false)
                .setExplanation(new ExplanationSingle(node, "Fv:r"));
        }
        expanded = true;
    }

    @Override
    public void visit(Not not) {
        if(node.isSignT()) {
            engine.add(not.getFormula(), false)
                .setExplanation(new ExplanationSingle(node, "T~"));
        } else {
            engine.add(not.getFormula(), true)
                .setExplanation(new ExplanationSingle(node, "F~"));
        }
        expanded = true;
    }

    @Override
    public void visit(Implies imp) {
        if(node.isSignT()) {
            treeEngine.add(engine, 
                imp.getLeft(), false, new ExplanationSingle(node, "T->:l"),
                imp.getRight(), true, new ExplanationSingle(node, "T->:r"));
        } else {
            engine.add(imp.getLeft(), true)
                .setExplanation(new ExplanationSingle(node, "F->:l"));
            engine.add(imp.getRight(), false)
                .setExplanation(new ExplanationSingle(node, "F->:r"));
        }
        expanded = true;
    }

    @Override
    public void visit(Equivalent equ) {
        if(node.isSignT()) {
            BranchEngine newEngine = treeEngine.add(engine,
                equ.getLeft(), true, new ExplanationSingle(node, "T<->:l1"), 
                equ.getLeft(), false, new ExplanationSingle(node, "T<->:l2"));
            engine.add(equ.getRight(), true)
                .setExplanation(new ExplanationSingle(node, "T<->:r1"));
            newEngine.add(equ.getRight(), false)
                .setExplanation(new ExplanationSingle(node, "T<->:r2"));
        } else {
            BranchEngine newEngine = treeEngine.add(engine,
                equ.getLeft(), true, new ExplanationSingle(node, "F<->:l1"), 
                equ.getLeft(), false, new ExplanationSingle(node, "F<->:l2"));
            engine.add(equ.getRight(), false)
                .setExplanation(new ExplanationSingle(node, "T<->:r1"));
            newEngine.add(equ.getRight(), true)
                .setExplanation(new ExplanationSingle(node, "T<->:r2"));
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
