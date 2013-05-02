/**
 * 
 */
package tableau;

import ast.Formula;
import ast.LogicalSystem;
import proof.LogicalReasoning;
import proof.explanation.ExplanationSelf;
import proof.patterns.InferenceFactory;
import proof.utils.DotTreeGenerator;
import tableau.patterns.NodeClassifierFactory;
import tableau.patterns.NodeSelectorFactory;
import tableau.simple.PriorityNodeSelectorFactory;
import tableau.simple.SimpleTableauInferenceFactory;
import tableau.simple.TableauNodeClassifierFactory;


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
    private boolean checkContradiction = true;
    
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
    
    public boolean isCheckContradiction() {
        return checkContradiction;
    }

    public void setCheckContradiction(boolean checkContradiction) {
        this.checkContradiction = checkContradiction;
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
        else {
            setResult(TruthType.NOT_TAUTOLOGY);
            if (checkContradiction) {// && 
                    //engine.getOpenBranchesCount() == engine.getBranchesCount()) {
                
                Tableau t; 
                try {
                    t = new Tableau(this.getLogicalSystem().cloneInverted(), 
                            this.inferenceFactory,
                            this.nodeClassifierFactory,
                            this.nodeSelectorFactory);
                    t.setCheckContradiction(false);
                    time += t.solve();
                    //System.out.println("###################################");
                    //t.print();
                    //System.out.println("Solução da verificação = " + t.getResultName());
                    //System.out.println("###################################");
                    if (t.isTautology()) 
                        setResult(TruthType.CONTRADICTION);
                    else
                        setResult(TruthType.SATISFIABLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return time;
    }
    
    public void print() {
        System.out.println("Inference rules = " + this.inferenceFactory);
        System.out.println("Node selector   = " + this.nodeSelectorFactory);
        engine.getTree().print();
        System.out.println("Total de vertices = " + 
                engine.getTree().countNodes());
    }
    
    public boolean toDot(String arq) {
        DotTreeGenerator dg = new DotTreeGenerator(engine.getTree());
        return dg.generate(arq);
    }

}
