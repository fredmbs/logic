/**
 * 
 */
package tableau.lemma;

import proof.Inference;
import proof.explanation.ExplanationSelf;
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
public class LemmaTableauInference implements Inference, FormulaVisitor {

    private Node node;
    private boolean expanded;
    private BranchEngine engine;
    private TreeEngine treeEngine;

    protected LemmaTableauInference(BranchEngine engine) {
        this.engine = engine;
        this.treeEngine = engine.getTreeEngine();
    }
    
    protected LemmaTableauInference(BranchEngine engine, BranchEngine from) {
        this(engine);
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

    private void lemmaBranching(Formula fL, boolean sL, 
            Formula fR, boolean sR, String explanation){
        BranchEngine newBE = treeEngine.add(engine, 
                fL, sL, new ExplanationSingle(node, explanation + ":l"),
                fR, sR, new ExplanationSingle(node, explanation + ":r"));
        if (newBE != null) {
            // Dica: verificar qual é a menor fómrula e usá-la como lema
            if (fL.getSize() < fR.getSize()) {
                //Node lemma = new Node(fL, !sL);
                Node lemma = Node.removeNot(fL, !sL);
                lemma.setExplanation(new ExplanationSelf("lemma"));
                lemma.setType(Node.Type.ATOMIC);
                newBE.add(lemma);
            } else {
                //Node lemma = new Node(fR, !sR);
                Node lemma = Node.removeNot(fR, !sR);
                lemma.setExplanation(new ExplanationSelf("lemma"));
                lemma.setType(Node.Type.ATOMIC);
                engine.add(lemma);
            }
        }
    }

    @Override
    public void visit(And and) {
        if (and.getLeft().equals(and.getRight())) {
            engine.add(and.getLeft(), node.isSignT())
            .setExplanation(new ExplanationSingle(node, "Ident."));
        } else {
            if(node.isSignT()) {
                engine.add(and.getLeft(), true)
                .setExplanation(new ExplanationSingle(node, "T^:l"));
                engine.add(and.getRight(), true)
                .setExplanation(new ExplanationSingle(node, "T^:r"));;
            } else {
                lemmaBranching(and.getLeft(), false,and.getRight(), false, "F^");
                
            }
        }
        expanded = true;
    }

    @Override
    public void visit(Or or) {
        if (or.getLeft().equals(or.getRight())) {
            engine.add(or.getLeft(), node.isSignT())
            .setExplanation(new ExplanationSingle(node, "Ident."));
        } else { 
            if(node.isSignT()) {
                lemmaBranching(or.getLeft(), true, or.getRight(), true, "Tv");
            } else {
                engine.add(or.getLeft(), false)
                .setExplanation(new ExplanationSingle(node, "Fv:l"));
                engine.add(or.getRight(), false)
                .setExplanation(new ExplanationSingle(node, "Fv:r"));
            }
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
            lemmaBranching(imp.getLeft(), false, imp.getRight(), true, "T->");
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
