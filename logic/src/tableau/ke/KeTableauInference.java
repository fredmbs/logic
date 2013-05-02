package tableau.ke;

import ast.*;
import ast.patterns.FormulaVisitor;
import proof.Inference;
import proof.explanation.ExplanationDual;
import proof.explanation.ExplanationSingle;
import tableau.BranchEngine;
import tableau.Node;
import tableau.Tree;
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
        engine.setSelectAllNodes(true);
    }
    
    @Override
    public boolean infer(Node n) {
        // se o nó é um princípio de bivalência, ramifica a árvore
        if (n.getType() == Node.Type.UNCLASSIFIED) {
            Node from = ((ExplanationSingle)n.getExplanation()).getFrom();
            treeEngine.add(engine, 
                    n.getFormula(), true, new ExplanationSingle(from, "PB-T"),
                    n.getFormula(), false, new ExplanationSingle(from, "PB-F"));
            return true;
        }
        // senão, tenta uma eliminação
        this.expanded = false;
        this.node = n;
        //System.err.println("### Inferindo nó " + n);
        //
        this.node.getFormula().accept(this);
        //
        return expanded;
    }
    
    private Node searchFormula(Formula subBeta, boolean signT) {
        Node leaf = engine.getBranch().getLeaf();
        Node equivalentNode = Node.removeNot(subBeta, signT);
        Node nodeSearch = Tree.searchEqual(leaf, equivalentNode);
        return nodeSearch;
    }
    
    private boolean betaCut(Formula formula, boolean signT, 
            Formula newFormula,  boolean newSignT, String explain) {
        //System.err.println("???? Tentando Bcut na fórmula (" + signT + ")" + formula);
        Node nodeCut = searchFormula(formula, signT);
        if (nodeCut != null) {
            //System.err.println("!!!! Encontrado nó " + nodeCut);
            engine.add(newFormula, newSignT)
            .setExplanation(new ExplanationDual(node, nodeCut, explain));
            return true;
        }
        //System.err.println(".... Não encontrado nó");
        return false;
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
                .setExplanation(new ExplanationSingle(node, "T^:r"));
            } else {
                if (!betaCut(and.getLeft(), true, and.getRight(), false, "F^-cut:l")) {
                    if(!betaCut(and.getRight(), true, and.getLeft(), false, "F^-cut:r"))
                        return;
                }
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
                if (!betaCut(or.getLeft(), false, or.getRight(), true, "Tv-cut:l"))
                    if(!betaCut(or.getRight(), false, or.getLeft(), true, "Tv-cut:r"))
                        return;
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
            if (!betaCut(imp.getLeft(), true, imp.getRight(), true, "T->cut:l"))
                if(!betaCut(imp.getRight(), false, imp.getLeft(), false, "T->cut:r"))
                    return;
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
            engine.add(new And(new Implies(equ.getLeft(), equ.getRight()), 
                    new Implies(equ.getRight(), equ.getLeft())),true)
                    .setExplanation(new ExplanationSingle(node, "T<->"));
        } else {
            engine.add(new And(new Implies(equ.getLeft(), equ.getRight()), 
                    new Implies(equ.getRight(), equ.getLeft())),false)
                    .setExplanation(new ExplanationSingle(node, "F<->"));
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
