/**
 * 
 */
package tableau;

import ast.Formula;
import ast.LogicalSystem;
import proof.LogicalReasoning;
import proof.explanation.ExplanationSelf;
import proof.patterns.InferenceFactory;
import proof.patterns.NodeClassifierFactory;
import proof.patterns.NodeSelectorFactory;
import proof.utils.DotTreeGenerator;
import tableau.patterns.PriorityNodeSelectorFactory;
import tableau.patterns.TableauNodeClassifierFactory;
import tableau.simple.SimpleTableauInferenceFactory;


/**
 * @author dev
 *
 */
public class Tableau 
extends LogicalReasoning 
{

    private TreeEngine engine;
    private InferenceFactory<BranchEngine> inferenceFactory;
    private NodeSelectorFactory<BranchEngine> nodeSelectorFactory;
    private NodeClassifierFactory nodeClassifierFactory;
    
    public Tableau(LogicalSystem lsys, 
            InferenceFactory<BranchEngine> inferFactory,
            NodeClassifierFactory nodeClassifierFactory,
            NodeSelectorFactory<BranchEngine> nodeSelectorFactory) 
    throws Exception 
    {
        super(lsys);
        this.inferenceFactory = inferFactory;
        this.nodeClassifierFactory = nodeClassifierFactory;
        this.nodeSelectorFactory = nodeSelectorFactory;
        this.engine = new TreeEngine(this);
        if (lsys.getLogicSystemType() == LogicalSystem.LogicalSystemType.DERIVATION) {
            if (lsys.hasPremisse())
                for (Formula f: lsys.getPremisse())
                    engine.getStart().add(f, true)
                    .setExplanation(new ExplanationSelf("H:l"));
        }
        if (lsys.hasConclusion())
//            engine.getStart().add(lsys.getConclusionFormula(), false)
//            .setExplanation(new ExplanationSelf("H"));
            for (Formula f: lsys.getConclusion())
                engine.getStart().add(f, false)
                .setExplanation(new ExplanationSelf("H:r"));
    }
    

    public Tableau(LogicalSystem lsys) throws Exception {
        this(lsys, new SimpleTableauInferenceFactory(),
                   new TableauNodeClassifierFactory(),
                   new PriorityNodeSelectorFactory());
    }
    
    public InferenceFactory<BranchEngine> getInferenceFactory() {
        return inferenceFactory;
    }

    public NodeSelectorFactory<BranchEngine> getNodeSelectorFactory() {
        return nodeSelectorFactory;
    }

    public NodeClassifierFactory getNodeClassifierFactory() {
        return nodeClassifierFactory;
    }

    public boolean isTautology() {
        return (engine.getOpenBranchesCount() == 0);
    }
    
    public boolean isSatisfiable() {
        return (engine.getClosedBranchesCount() > 0);
    }
    
    public int getOpenBranchesCount() {
        return engine.getOpenBranchesCount();
    }
    
    public int getClosedBranchesCount() {
        return engine.getClosedBranchesCount();
    }
    
    public int getAbandonedBranches() {
        return engine.getAbandonedBranches();
    }
    
    public int getBranchesCount() {
        return engine.getBranchesCount();
    }

    public int getNodesCount() {
        return engine.getTree().countNodes();
    }
    
    @Override
    public long solve() {
        setResult(TruthType.UNKNOWN);
        
        long time = System.nanoTime();
        while (engine.expand());
        time = System.nanoTime() - time;

        engine.getTree().numberingNodes();
        
        if (isTautology())
            setResult(TruthType.TAUTOLOGY);
        else if (isSatisfiable())
            setResult(TruthType.SATISFIABLE);
        else
            setResult(TruthType.NOT_TAUTOLOGY);
        
        return time;
    }
    
    public void print() {
        System.out.println("Inference rules = " + this.inferenceFactory);
        System.out.println("Node selector   = " + this.nodeSelectorFactory);
        engine.getTree().print();
        System.out.println("Total de vertices = " + 
                engine.getTree().countNodes());
        if (isTautology())
            System.out.println("TAUTOLOGY!");
        else if (isSatisfiable())
            System.out.println("Satisfiable...");
    }
    
    public boolean toDot(String arq) {
        DotTreeGenerator dg = new DotTreeGenerator(engine.getTree());
        return dg.generate(arq);
    }

}
