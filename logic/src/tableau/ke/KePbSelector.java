package tableau.ke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import proof.Node;
import proof.Tree;
import proof.explanation.ExplanationSingle;
import tableau.BranchEngine;
import ast.And;
import ast.Connective;
import ast.Formula;
import ast.Implies;
import ast.Or;

public class KePbSelector {
    
    ArrayList<Node> openBetas;
    HashSet<Formula> disconsider;
    Tree tree;
    Node leaf;
    HashMap<Formula, Integer> countMap;
    int maxCount = 0;
    Formula selectedFormula = null;
    int selectedOpenBetaIndex = -1;
    Node selectedPB = null;
    int minFormulaSize = Integer.MAX_VALUE;
    //int maxFormulaSize = Integer.MIN_VALUE;
    
    KePbSelector(ArrayList<Node> openBetas, BranchEngine engine) {
        this.disconsider = new HashSet<Formula>();
        this.openBetas = openBetas; 
        this.tree = engine.getTreeEngine().getTree();
        this.leaf = engine.getBranch().getLeaf();
        //System.err.println("###### ENGINE =" + engine);
        //System.err.println("###### LEAF =" + this.leaf);
        this.countMap = new HashMap<Formula, Integer>();
    }
    
    public Node getSelectedPB() {
        return this.selectedPB;
    }
    
    private boolean isFulfilled(Node openBeta) {
        Connective formula = (Connective)openBeta.getFormula();
        //System.err.println("... Considerando fórmula " + formula + " " + formula.hashCode());
        if (disconsider.contains(formula.getLeft()) || 
                disconsider.contains(formula.getRight())) { 
            //System.err.println("... Fórmula desconsiderada " + formula + " " + formula.hashCode());
            return true;
        }
        Node nodeSearchBl = Node.removeNot(formula.getLeft());
        Node nodeSearchBr = Node.removeNot(formula.getRight());
        Node nodeBl = Tree.searchFormula(leaf, nodeSearchBl.getFormula());
        Node nodeBr = Tree.searchFormula(leaf, nodeSearchBr.getFormula());
        //Node nodeB1 = tree.searchEqual(leaf, (formula.getLeft()));
        //Node nodeB2 = tree.searchEqual(leaf, (formula.getRight()));
        boolean fulfilled = false;
        if (openBeta.isSignT() && formula instanceof Or) { 
            if ((nodeBl != null) && (nodeBl.isSignT() == nodeSearchBl.isSignT())) {
                disconsider.add(formula.getLeft());
                fulfilled = true;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                disconsider.add(formula.getRight());
                fulfilled = true;
            }
        } else if (!openBeta.isSignT() && formula instanceof And) {
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                disconsider.add(formula.getLeft());
                fulfilled = true;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() != nodeSearchBr.isSignT())) {
                disconsider.add(formula.getRight());
                fulfilled = true;
            }
        } else if (openBeta.isSignT() && formula instanceof Implies) {
            if ((nodeBl != null) && (nodeBl.isSignT() != nodeSearchBl.isSignT())) {
                disconsider.add(formula.getLeft());
                fulfilled = true;
            }
            if ((nodeBr != null) && (nodeBr.isSignT() == nodeSearchBr.isSignT())) {
                disconsider.add(formula.getRight());
                fulfilled = true;
            }
        } else {
            System.err.println("Falha grave: Node não é do tipo tipo beta = " + openBeta);
            new Exception().getStackTrace();
            System.exit(1);
        }
        return fulfilled;
    }
    
    private void consider(int index, Formula formula) {
        Integer count = countMap.get(formula);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        countMap.put(formula, count);
        int fSize = formula.getSize();//.toString().length();
        if (fSize < minFormulaSize) {
            //if (fSize > maxFormulaSize) {
            //System.err.println("... Fórmula mínima " + formula + " ocorre mais vezes até o momento"  + " " + formula.hashCode());
            minFormulaSize = fSize;
            //maxFormulaSize = fSize;
            maxCount = count;
            selectedFormula = formula;
            selectedOpenBetaIndex = index;
        } else if (fSize == minFormulaSize ){
            //} else if (fSize == maxFormulaSize ){
            if (count > maxCount) {
                //System.err.println("... Fórmula mínima" + formula + " ocorre mais vezes até o momento"  + " " + formula.hashCode());
                maxCount = count;
                selectedFormula = formula;
                selectedOpenBetaIndex = index;
            }
        }
    }
    
    boolean select() {
        //if (leaf.getType() == Node.Type.PB) // && openBetas.isEmpty()) 
        //    return false;
        int numOpenBetas = openBetas.size();
        Node openBeta;
        Connective connective;
        for (int i = 0; i < numOpenBetas; i++) {
            openBeta = openBetas.get(i);
            if (openBeta != null) {
                if (!isFulfilled(openBeta)) {
                    connective = (Connective)openBeta.getFormula();
                    consider(i, connective.getLeft());
                    consider(i, connective.getRight());
                }
            }
        }
        if (selectedFormula != null) {
            selectedPB = new Node(selectedFormula);
            selectedPB.setType(Node.Type.UNCLASSIFIED);
            selectedPB.setExplanation(new ExplanationSingle(
                    openBetas.get(selectedOpenBetaIndex), "PB"));
            return true;
        }
        return false;
    }
}
