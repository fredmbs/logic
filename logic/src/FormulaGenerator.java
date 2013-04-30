

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import proof.LogicalReasoning.TruthType;
import proof.utils.LogicalSystemException;

import tableau.Tableau;
import tableau.ke.KeNodeSelectorFactory;
import tableau.ke.KeTableauInferenceFactory;
import tableau.ke.KeTableauNodeClassifierFactory;
import tableau.lemma.LemmaTableauInferenceFactory;
import tableau.patterns.PriorityNodeSelectorFactory;
import tableau.patterns.TableauNodeClassifierFactory;
import truthTable.TruthTable;

import ast.LogicalSystem;

public class FormulaGenerator {

	StringBuffer f = new StringBuffer();
	int numTerms, numConnectives, numPredicates;
	int lastTermIndex, lastPredicateIndex;
	String[] s = {"", "!"};
	String[] c = {"v", "^", "->"};
	int[] is, ic, ip;
	boolean hasFormula = true;
	

	public FormulaGenerator(int terms, int predicates) {
		this.numPredicates = predicates;
		this.numTerms = terms;
		this.lastTermIndex = terms - 1;
		this.lastPredicateIndex = predicates - 1;
		this.numConnectives = numTerms - 1;
		this.is = new int[numTerms];
		this.ic = new int[numConnectives];
		this.ip = new int[numTerms];
		for (int i = 0; i < numTerms; i++) {
			is[i] = 0;
		}
		for (int i = 0; i < numConnectives; i++) {
			ic[i] = 0;
		}
		for (int i = 0; i < numTerms; i++) {
			ip[i] = 0;
		}
	}

	private void newFormula(int n) {
		if (ip[n] == lastPredicateIndex) {
			ip[n] = 0;
			if (is[n] == 1) {
				is[n] = 0;
				if (n < lastTermIndex) {
					if (ic[n] == 2) {
						ic[n] = 0;
						if (n == 0) 
							hasFormula = false;
						else
							newFormula(n - 1);
					} else {
						ic[n] += 1;
					}
				} else {
					newFormula(n - 1);
				}
			} else {
				is[n] = 1;
			}
		} else {
			ip[n] += 1;
		}
	}
	
	public boolean hasFormula() {
		return hasFormula;
	}
	
	public StringBuffer nextFormula() {
		f = new StringBuffer();
		int p;
		for (int n = 0; n < lastTermIndex; n++) {
			p = (65+ip[n]);
			f.append(s[is[n]]);
			f.append((char)p);
			f.append(c[ic[n]]);
		}
		p = (65+ip[lastTermIndex]);
		f.append(s[is[lastTermIndex]]);
		f.append((char)p);
		newFormula(lastTermIndex);
		return f;
	}

    ProofApp proofApp;
    LogicalSystem logicalSystem = null;
	byte[] bytes;

	private boolean doSolveAndLog(Tableau t, TruthType baseResult) {
        long spentTime;
        spentTime = t.solve();
        TruthType tResult = t.getResult();
        System.out.print(";" + t.getResultName());
        System.out.print(";" + t.getOpenBranchesCount());
        System.out.print(";" + t.getClosedBranchesCount());
        System.out.print(";" + t.getAbandonedBranches());
        System.out.print(";" + t.getBranchesCount());
        System.out.print(";" + t.getNodesCount());
        System.out.print(";" + spentTime);
        if (tResult == baseResult) {
            System.out.print(";PASS");
            return true;
        } else {
            System.out.print(";FAIL");
            return false;
        }
	}
	
	private boolean verify(String formula) {
		boolean ok = true;
		bytes = formula.getBytes();
		logicalSystem = proofApp.compile(new ByteArrayInputStream(bytes));
        if (logicalSystem == null) {
            System.err.println("Falha na compilação da fórmula " + formula);
       } else {
    	   TruthType baseResult = TruthType.UNKNOWN;
           TruthTable ttt;
           try {
               ttt = new TruthTable(logicalSystem);
               System.out.print(";" + ttt.getFormula());
               long spentTime = ttt.solve2();
               baseResult = ttt.getResult();
               System.out.print(";" + ttt.getResultName());
               System.out.print(";" + spentTime);
           } catch (LogicalSystemException e1) {
               System.err.println(e1.getMessage());
           } catch (Exception e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
           }
           try {
               ok = ok & doSolveAndLog(new Tableau(logicalSystem), baseResult);
           } catch (LogicalSystemException e1) {
               System.err.println(e1.getMessage());
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }

           try {
        	   ok = ok & doSolveAndLog(new Tableau(logicalSystem, 
                       new LemmaTableauInferenceFactory(),
                       new TableauNodeClassifierFactory(),
                       new PriorityNodeSelectorFactory()),
                       baseResult);
           } catch (LogicalSystemException e1) {
               System.err.println(e1.getMessage());
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }

           try {
        	   ok = ok &  doSolveAndLog(new Tableau(logicalSystem, 
                       new KeTableauInferenceFactory(),
                       new KeTableauNodeClassifierFactory(),
                       new KeNodeSelectorFactory()),
                       baseResult);
           } catch (LogicalSystemException e1) {
               System.err.println(e1.getMessage());
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }           
       }
        System.out.println();
       return ok;
	}
	
    public static void main(String argv[]) {
    	FormulaGenerator teste = new FormulaGenerator(4, 2);
    	teste.proofApp = new ProofApp(argv);
		int count = 0, acertos = 0, erros = 0;
		String formula;
   		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	while (teste.hasFormula()) {
    		count += 1;
            formula = teste.nextFormula().toString();
            System.out.print(count);
			if (teste.verify(formula)) {
				acertos++;
			} else {
	    		//System.err.println("Falha de verificação #" + erros + " na fómrula #" + count);
				erros++;
			}
        }
   		System.err.println("######################################");
   		System.err.println("Acertos = " + acertos);
   		System.err.println("Erros   = " + erros);
   		System.err.println("Total   = " + count);
   		long time, sumTime = 0, loops = 1000, efectiveLoops = 0, maxTime = -1, minTime = 1000000;
   		for (int i = 0; i < loops; i++) {
   			time = System.nanoTime();
   			time = System.nanoTime() - time;
   			if (time > 0) {
   				efectiveLoops++;
   				sumTime += time;
   				if (time > maxTime)
   					maxTime = time;
   				if (time < minTime)
   					minTime = time;
   			}
   		}
   		System.err.println("Precisão média  = " + (sumTime/efectiveLoops) + " ns");
   		System.err.println("Precisão máxima = " + maxTime + " ns");
   		System.err.println("Precisão mínima = " + minTime + " ns");
    }
	
}

