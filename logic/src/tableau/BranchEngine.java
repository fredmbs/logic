/**
 * 
 */
package tableau;

import proof.Branch;
import proof.Inference;
import proof.Node;
import proof.explanation.Explanation;
import proof.explanation.ExplanationDual;
import proof.explanation.ExplanationSelf;
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
    private NodeClassifier nodeClassifier;
    private boolean closureOnAllNodes = true;
    private NodeSelector nodeSelector;
    
    public BranchEngine(TreeEngine engine) {
        this.treeEngine = engine;
        this.treeEngine.getTree();
        Tableau tableau = this.treeEngine.getTableau();
        nodeClassifier = tableau.getNodeClassifierFactory().newNodeClassifier();
        nodeSelector = tableau.getNodeSelectorFactory().newNodeSelector(this);
        rules = tableau.getInferenceFactory().newInference(this);
        this.branch = new Branch(this.treeEngine.getTree().getHead());
    }

    public BranchEngine(BranchEngine from, Formula formula, boolean signT) {
        this(from, formula, signT, null);
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

    static int test = 0;
    public Branch.Status expand() {
        //if (BranchEngine.test++ > 50) System.exit(1);
        if (branch.getStatus() == Branch.Status.OPEN) {
            //System.err.println("=== BRANCH ENGINE " + this);
            //this.branch.printDebug();
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

    public void add(Node node) {
        // INICIO otimização opcional: não inserir nó duplicado no branch
        Node searchNode = treeEngine.getTree().searchEqual(branch.getLeaf(), 
                node.getFormula());
        if (searchNode != null && searchNode.isSignT() == node.isSignT())
            return;
        // -- FIM Otimização */
        this.branch.add(node);
        newNode(node);
        return;
    }

    protected void newNode(Node node) {
        //System.err.println("+++ BRANCH ENGINE " + this);
        nodeSelector.add(node);
        Node.Type type = node.getType();
        if (type == Node.Type.ATOMIC || closureOnAllNodes) 
            verifyClosure(node);
    }
    
    protected void fullVerification(Node node) {
        if (node.getType().ordinal() > Node.Type.ATOMIC.ordinal()) {
            //System.err.println("+++ BRANCH ENGINE " + this);
            nodeSelector.add(node);
        };
        verifyClosure(node);
    }

    private void verifyClosure(Node node) {
        Node searchNode = this.treeEngine.getTree().searchEqual(node);
        if (searchNode != null) {
            if (node.isSignT() != searchNode.isSignT()) {
                this.branch.close()
                    .setExplanation(new ExplanationDual(node, searchNode, "closure"));
            }
        }
    }
    

}
