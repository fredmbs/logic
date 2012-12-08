/**
 * 
 */
package tableau.ke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ast.Connective;
import ast.Formula;
import proof.Node;
import proof.Tree;
import proof.explanation.ExplanationSingle;
import tableau.BranchEngine;
import tableau.patterns.PriorityNodeSelector;

/**
 * @author dev
 *
 */
public class KeNodeSelector extends PriorityNodeSelector {

    private ArrayList<Node> openBetas;
    private BranchEngine engine;

    public KeNodeSelector(BranchEngine engine) {
        super();
        this.engine = engine;
        this.openBetas = new ArrayList<Node>(); 
    }
    
    public KeNodeSelector(BranchEngine engine, KeNodeSelector from) {
        super((PriorityNodeSelector)from);
        this.engine = engine;
        this.openBetas = new ArrayList<Node>(from.openBetas); 
    }
    
    public void regressOpenBetas() {
        // retorna os nós beta abertos
        int numOpenBetas = openBetas.size();
        //System.err.println("Regressando nós betas abertos:" + numOpenBetas);
        if (numOpenBetas > 0) {
            Node returned;
            for (int i = 0; i < numOpenBetas; i++) {
                returned = openBetas.get(i);
                if (returned != null)
                    super.add(returned);
            }
            openBetas.clear();
        }
    }

    public void add(Node node) {
        //System.err.println("Adicionando nó:" + node);
        regressOpenBetas();
        // adiciona o nó na fila de prioridade
        super.add(node);
    }
    @Override
    public Node select() {
        //System.err.println("--- Selecionando nó");
        Node node = super.select();
        if (node == null) {
            if (!openBetas.isEmpty()) {
                //System.err.println("Tentando um PB entre os BETA aberto");
                SelectPB selector = new SelectPB(openBetas, engine);
                if (selector.select()) {
                    node = selector.getSelectedPB();
                    openBetas.set(selector.getSelectedOpenBetaIndex(), null);
                    //System.err.println("Encontrou PB");
                    if (node != null) 
                        regressOpenBetas();
                }
            } 
            else { 
                //System.err.println("Não existem nós BETA abertos...");
            }
        }
        //System.err.println("--- Nó selecionado = "  + node);
        return node;
    }

    @Override
    public void regress(Node node) {
        //System.err.println("--- Regressando o nó " + node);
        Node.Type type = node.getType();
        if (type == Node.Type.BETA) {
            openBetas.add(node);
        } else { 
            super.regress(node);
        }
    }

    private class SelectPB {

        ArrayList<Node> openBetas;
        HashSet<Formula> disconsider;
        Tree tree;
        Node leaf;
        HashMap<Formula, Integer> countMap;
        int maxCount = 0;
        Formula selectedFormula = null;
        int selectedOpenBetaIndex = -1;
        Node selectedPB = null;
        //int minFormulaSize = Integer.MAX_VALUE;
        int maxFormulaSize = Integer.MIN_VALUE;
        
        SelectPB(ArrayList<Node> openBetas, BranchEngine engine) {
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
        
        public int getSelectedOpenBetaIndex() {
            return this.selectedOpenBetaIndex;
        }
        
        private void consider(int index, Formula formula) {
            //System.err.println("... Considerando fórmula " + formula + " " + formula.hashCode());
            if (disconsider.contains(formula)) { 
                //System.err.println("... Fórmula desconsiderada " + formula + " " + formula.hashCode());
                return;
            }
            Integer count = countMap.get(formula);
            if (count == null) {
                //System.err.println("... Nova contabilidade da fórmula " + formula + " " + formula.hashCode());
                Node node = tree.searchEqual(leaf, formula);
                if (node != null) {
                    //System.err.println("... Fórmula ja existe na árvore " + formula + " " + formula.hashCode());
                    disconsider.add(formula);
                    return;
                }
                count = 1;
            } else {
                count++;
                //System.err.println("... Fórmula " + formula + " ocorre " + count + " vezes"  + " " + formula.hashCode());
            }
            countMap.put(formula, count);
            int fSize = formula.toString().length();
            //if (fSize < minFormulaSize) {
            if (fSize > maxFormulaSize) {
                //System.err.println("... Fórmula mínima " + formula + " ocorre mais vezes até o momento"  + " " + formula.hashCode());
                //minFormulaSize = fSize;
                maxFormulaSize = fSize;
                maxCount = count;
                selectedFormula = formula;
                selectedOpenBetaIndex = index;
            //} else if (fSize == minFormulaSize ){
            } else if (fSize == maxFormulaSize ){
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
            Formula formula;
            Connective connective;
            for (int i = 0; i < numOpenBetas; i++) {
                openBeta = openBetas.get(i);
                if (openBeta != null) {
                    formula = openBeta.getFormula();
                    if (formula instanceof Connective){
                        connective = (Connective)formula;
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

}
