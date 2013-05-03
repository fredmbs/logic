package tableau.ke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import proof.explanation.ExplanationSingle;
import tableau.Branch;
import tableau.BranchEngine;
import tableau.Node;
import ast.Connective;
import ast.Formula;

public class KePbSelector {
    
    Branch branch;
    HashSet<Formula> disconsider;
    HashMap<Formula, Integer> countMap;
    int maxCount;
    Formula selectedFormula;
    int selectedOpenBetaIndex;
    int minFormulaSize;
    
    KePbSelector(BranchEngine engine) {
        this.branch = engine.getBranch();
        this.disconsider = new HashSet<Formula>();
        this.countMap = new HashMap<Formula, Integer>();
    }
    
    private boolean isFulfilled(Node openBeta) {
        Connective formula = (Connective)openBeta.getFormula();
        // Verifica se deve desconsiderar fórmulas já consideradas
        if (disconsider.contains(formula.getLeft()) || 
                disconsider.contains(formula.getRight())) { 
            // Fórmula desconsiderada 
            return true;
        }
        // Verifica se a fórmula é fulfilled
        boolean fulfilled = false;
        Node[] fulfill = this.branch.fulfilledBy(openBeta);
        for (int i = 0; i < 2; i++) 
            if (fulfill[i] != null) {
                disconsider.add(fulfill[i].getFormula());
                fulfilled = true;
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
        // Verifica se é a fórmula mínima que ocorre mais vezes até o momento
        if (fSize < minFormulaSize) {
            minFormulaSize = fSize;
            maxCount = count;
            selectedFormula = formula;
            selectedOpenBetaIndex = index;
        } else if (fSize == minFormulaSize ){
            if (count > maxCount) {
                maxCount = count;
                selectedFormula = formula;
                selectedOpenBetaIndex = index;
            }
        }
    }
    
    Node select(ArrayList<Node> openBetas) {
        // inicialização
        this.disconsider.clear();
        this.countMap.clear();
        selectedFormula = null;
        maxCount = 0;
        selectedOpenBetaIndex = -1;
        minFormulaSize = Integer.MAX_VALUE;
        // seleção
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
            Node selectedPB;
            selectedPB = new Node(selectedFormula);
            selectedPB.setType(Node.Type.UNCLASSIFIED);
            selectedPB.setExplanation(new ExplanationSingle(
                    openBetas.get(selectedOpenBetaIndex), "PB"));
            return selectedPB;
        }
        return null;
    }
}
