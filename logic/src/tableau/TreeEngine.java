package tableau;

import java.util.Stack;
import ast.Formula;
import proof.Branch;
import proof.Tree;

public class TreeEngine {

    private Tableau tableau;
    private Tree tree;
    private Stack<BranchEngine> activeBranches;
    private BranchEngine start;
    private int openBranchesCount = 0;
    private int closedBranchesCount = 0;
    private int branchesCount = 0;
    private int abandonedBranches = 0;

    public TreeEngine(Tableau tableau) {
        this.activeBranches = new Stack<BranchEngine>();
        this.tableau = tableau;
        this.tree = new Tree();
        this.start = new BranchEngine(this);
        this.activeBranches.add(this.start);
    }
    
    public Tableau getTableau() {
        return tableau;
    }

    public Tree getTree() {
        return tree;
    }

    public BranchEngine getStart() {
        return start;
    }
    
    public int getOpenBranchesCount() {
        return openBranchesCount;
    }

    public int getClosedBranchesCount() {
        return closedBranchesCount;
    }

    public int getAbandonedBranches() {
        return abandonedBranches;
    }

    public int getBranchesCount() {
        return branchesCount;
    }

    public BranchEngine add(BranchEngine from, 
            Formula formulaLeft, boolean signTLeft,
            Formula formulaRight, boolean signTRight) 
    {
        BranchEngine newBE = new BranchEngine(from, formulaRight, signTRight);
        branchesCount++;
        if (newBE.getBranch().getStatus() == Branch.Status.OPEN)
            activeBranches.add(newBE);
        from.add(formulaLeft, signTLeft);
        return newBE;
    }

    public boolean expand() {
            BranchEngine branchEngine = activeBranches.peek();
            //
            branchEngine.expand();
            //
            verifyBranch(branchEngine);
            return !activeBranches.empty();
    }

    private void verifyBranch(BranchEngine branchEngine) {
        switch (branchEngine.getBranch().getStatus()) {
        case ABANDONED:
            activeBranches.remove(branchEngine);
            openBranchesCount++;
            abandonedBranches++; 
            break;
        case CLOSED:
            activeBranches.remove(branchEngine);
            closedBranchesCount++;
            break;
        case EXHAUSTED:
            activeBranches.remove(branchEngine);
            openBranchesCount++;
            break;
        case OPEN:
            break;
        default:
            break;
        }
    }

}
