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
    
    public KeNodeSelector(KeNodeSelector from) {
        super((PriorityNodeSelector)from);
        this.engine = from.engine;
        this.openBetas = new ArrayList<Node>(from.openBetas); 
    }
    
    public void regressOpenBetas() {
        // retorna os nós beta abertos
        int numOpenBetas = openBetas.size();
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
        regressOpenBetas();
        // adiciona o nó na fila de prioridade
        super.add(node);
    }
    @Override
    public Node select() {
        Node node = super.select();
        if (node == null) {
            if (!openBetas.isEmpty()) {
                SelectPB selector = new SelectPB(openBetas, engine);
                if (selector.select()) {
                    node = selector.getSelectedPB();
                    if (node != null) 
                        regressOpenBetas();
                }
            }
        }
        return node;
    }

    @Override
    public void regress(Node node) {
        Node.Type type = node.getType();
        if (type == Node.Type.BETA) {
            openBetas.add(node);
        } else if (!(type == Node.Type.PB)){ 
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
        Node selectedPB;
        
        SelectPB(ArrayList<Node> openBetas, BranchEngine engine) {
            this.disconsider = new HashSet<Formula>();
            this.openBetas = openBetas; 
            this.tree = engine.getTreeEngine().getTree();
            this.leaf = engine.getBranch().getLeaf();
            this.countMap = new HashMap<Formula, Integer>();
            this.selectedPB = new Node(null);
            this.selectedPB.setType(Node.Type.PB);
        }
        
        public Node getSelectedPB() {
            return this.selectedPB;
        }
        
        private void consider(int index, Formula formula) {
            if (disconsider.contains(formula)) 
                return;
            Integer count = countMap.get(formula);
            if (count == null) {
                Node node = tree.searchEqual(leaf, formula);
                if (node != null) {
                    disconsider.add(formula);
                    return;
                }
                count = 1;
            } else {
                count++;
            }
            countMap.put(formula, count);
            if (count > maxCount) {
                maxCount = count;
                selectedFormula = formula;
            }
        }
        
        boolean select() {
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
                selectedPB.setFormula(selectedFormula);
                return true;
            }
            return false;
        }
    }

}
