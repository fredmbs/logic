/**
 * 
 */
package tableau;

import proof.Inference;
import proof.explanation.Explanation;
import proof.explanation.ExplanationDual;
import proof.explanation.ExplanationSelf;
import tableau.patterns.NodeClassifier;
import tableau.patterns.NodeSelector;
import ast.Formula;

/**
 * @author dev
 *
 */
public class BranchEngine {

    private TreeEngine treeEngine;
    private Branch branch;
    private Inference rules;
    private NodeClassifier nodeClassifier;
    private boolean closureOnAllNodes = true;
    private NodeSelector nodeSelector;
    private boolean enforceRegularity = true;
    private boolean selectAllNodes = false;
    
    public BranchEngine(TreeEngine engine) {
        this.treeEngine = engine;
        this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory().newNodeSelector(this);
        rules = tableau.getInferenceFactory().newInference(this);
        this.branch = new Branch(this.treeEngine.getTree().getHead());
    }

    public BranchEngine(BranchEngine from, 
            Formula formula, boolean signT, Explanation explanation) {
        this.treeEngine = from.treeEngine;
        this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory()
                .newNodeSelector(this, from.nodeSelector);
        rules = tableau.getInferenceFactory().newInference(this, from);
        Node leaf = new Node(formula, signT);
        leaf.setExplanation(explanation);
        nodeClassifier.classify(leaf);
        this.branch = new Branch(from.branch.getLeaf(), leaf);
        this.newNode(leaf);
    }
    
    public BranchEngine(BranchEngine from, Node leaf) {
        this.treeEngine = from.treeEngine;
        this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory()
                .newNodeSelector(this, from.nodeSelector);
        rules = tableau.getInferenceFactory().newInference(this, from);
        this.branch = new Branch(from.branch.getLeaf(), leaf);
        this.newNode(leaf);
    }

    
    public TreeEngine getTreeEngine() {
        return treeEngine;
    }

    public Branch getBranch() {
        return branch;
    }

    public Inference getRules() {
        return rules;
    }

    public Branch.Status expand() {
        if (branch.getStatus() == Branch.Status.OPEN) {
            Node node = nodeSelector.select();
            if (node == null) {
                this.branch.exhaust()
                    .setExplanation(new ExplanationSelf("exhausted"));;
            } else {
                if (!rules.infer(node))
                    nodeSelector.regress(node);
            }
        }
        return this.branch.getStatus();
    }
    
    public boolean isClosureOnNonAtomicNodes() {
        return closureOnAllNodes;
    }

    public void setClosureOnNonAtomicNodes(boolean closureOnLiteral) {
        this.closureOnAllNodes = closureOnLiteral;
    }
    
    public Node add(Formula formula, boolean signT) {
        return add(formula, signT, null);
    }

    public Node  add(Formula formula, boolean signT, Explanation explanation) {
        Node node = new Node(formula, signT);
        nodeClassifier.classify(node);
        node.setExplanation(explanation);
        this.add(node);
        return node;
    }

    
    
    public boolean isEnforceRegularity() {
        return enforceRegularity;
    }

    public void setEnforceRegularity(boolean enforceRegularity) {
        this.enforceRegularity = enforceRegularity;
    }

    public void add(Node node) {
        // --INICIO otimização de regularidade: não inserir nó duplicado
        if (enforceRegularity) {
            Node searchNode = Tree.searchFormula(branch.getLeaf(), node.getFormula());
            if (searchNode != null && searchNode.isSignT() == node.isSignT())
                return;
        }
        // -- FIM Otimização */
        this.branch.add(node);
        newNode(node);
        return;
    }

    public boolean isSelectAllNodes() {
        return selectAllNodes;
    }

    public void setSelectAllNodes(boolean selectAllNodes) {
        this.selectAllNodes = selectAllNodes;
    }

    private void newNode(Node node) {
        if (selectAllNodes || node.getType().ordinal() > Node.Type.ATOMIC.ordinal()) { 
            nodeSelector.add(node);
        };
        if (node.getType() == Node.Type.ATOMIC || closureOnAllNodes) 
            verifyClosure(node);
    }
    
    //protected void fullVerification(Node node) {
    //    if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal()) {
    //        nodeSelector.add(node);
    //    };
    //    verifyClosure(node);
    //}

    private void verifyClosure(Node node) {
        Node searchNode = Tree.searchFormula(node);
        if (searchNode != null) {
            if (node.isSignT() != searchNode.isSignT()) {
                this.branch.close()
                    .setExplanation(new ExplanationDual(node, searchNode, "closure"));
            }
        }
    }
    

}
