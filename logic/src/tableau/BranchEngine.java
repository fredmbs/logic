/**
 * 
 */
package tableau;

import proof.Branch;
import proof.Inference;
import proof.Node;
import proof.Tree;
import proof.patterns.NodeClassifier;
import proof.patterns.NodeSelector;
import ast.Formula;

/**
 * @author dev
 *
 */
public class BranchEngine {

    private TreeEngine treeEngine;
    private Branch branch;
    private Inference rules;
    private Tree tree;
    
    private NodeClassifier nodeClassifier;
    private boolean closureOnAllNodes = true;
    private NodeSelector nodeSelector;
    
    public BranchEngine(TreeEngine engine) {
        this.treeEngine = engine;
        this.tree = this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory().newNodeSelector(this);
        rules = tableau.getInferenceFactory().newInference(this);
        this.branch = new Branch(this.treeEngine.getTree().getHead());
    }

    public BranchEngine(BranchEngine from, Formula formula, boolean signT) {
        this.treeEngine = from.treeEngine;
        this.tree = this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory()
                .newNodeSelector(from.nodeSelector);
        rules = tableau.getInferenceFactory().newInference(this);
        Node leaf = new Node(formula, signT);
        nodeClassifier.classify(leaf);
        this.branch = new Branch(from.branch.getLeaf(), leaf);
        this.newNode(leaf);
    }

    
    public TreeEngine getTreeEngine() {
        return treeEngine;
    }

    public Branch getBranch() {
        return branch;
    }

    public Branch.Status expand() {
        if (branch.getStatus() == Branch.Status.OPEN) {
            Node node = nodeSelector.select();
            if (node == null) {
                this.branch.exhaust();
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
    
    public void add(Formula formula, boolean signT) {
        // INICIO otimização opcional: não inserir nó duplicado no branch
        Node searchNode = tree.searchEqual(branch.getLeaf(), formula);
        if (searchNode != null && searchNode.isSignT() == signT)
            return;
        // -- FIM Otimização
        Node node = new Node(formula, signT);
        nodeClassifier.classify(node);
        this.branch.add(node);
        newNode(node);
        return;
    }

    protected void newNode(Node node) {
        nodeSelector.add(node);
        if (closureOnAllNodes || node.getType() == Node.Type.ATOMIC) 
            verifyClosure(node);
    }
    
    protected void fullVerification(Node node) {
        if (node.getType() != Node.Type.ATOMIC) {
            nodeSelector.add(node);
        };
        verifyClosure(node);
    }

    private void verifyClosure(Node node) {
        Node searchNode = this.treeEngine.getTree().searchEqual(node);
        if (searchNode != null) {
            if (node.isSignT() != searchNode.isSignT()) {
                this.branch.close();
            }
        }
    }
    

}
