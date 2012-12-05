/**
 * 
 */
package truthTable;

import java.util.ArrayList;
import java.util.Collections;

import proof.LogicalReasoning;
import utils.StringUtils;
import ast.*;
import ast.utils.SymbolTable;

/**
 * @author dev
 *
 */
public class TruthTable extends LogicalReasoning {

    private Formula formula;
    private ArrayList<Integer> colSize; 
    private ArrayList<Predicate> predicate; 
    private int lineSize;
    
    public TruthTable(LogicalSystem lsys) throws Exception {
        super(lsys);
        if (lsys.getLogicSystemType() == LogicalSystem.LogicalSystemType.DERIVATION) {
            this.formula = new Implies(lsys.getBaseFormula(), lsys.getFormula());
        } else {
            this.formula = lsys.getFormula();
        }
    }

    private void init() {
        // Adiciona os predicados à tabela verdade
        colSize = new ArrayList<Integer>(); 
        predicate = new ArrayList<Predicate>(); 
        SymbolTable<Symbol> symbolTable = this.getSymbolTable();
        ArrayList<String> ids = new ArrayList<String>(symbolTable.keySet());
        Collections.sort(ids);
        for (String key : ids) {
            Symbol p = symbolTable.get(key);
            if (p instanceof Predicate) {
                predicate.add((Predicate)p);
                colSize.add(p.toString().length());
            }
        }
        colSize.add(0);
    }

    private void initPredicates() {
        for (int i = 0; i < predicate.size(); i++) {
            predicate.get(i).setValue(true);
        }
    }

    private void printTitle() {
        //
        FormulaLabeling label = new FormulaLabeling();
        String formulaStr = label.getLine(formula);
        lineSize = formulaStr.length(); 
        printSeparator('/', '-', '\\');
        System.out.print("|");
        for (int i = 0; i < predicate.size(); i++) {
            System.out.print(predicate.get(i).toString()+"|");
        }
        System.out.print( formulaStr + "|");
        System.out.println("");
    }
    
    private void printSeparator(char lb, char joint, char rb) {
        //
        System.out.print(lb);
        for (int i = 0; i < predicate.size(); i++) {
            System.out.print(StringUtils.repeat('-', colSize.get(i))+joint);
        }
        System.out.print(StringUtils.repeat('-', lineSize) + rb);
        System.out.println("");
    }

    private void printLine() {
        //
        System.out.print("|");
        for (int i = 0; i < predicate.size(); i++) {
            System.out.print(StringUtils.center(predicate.get(i).getValue(), 
                    colSize.get(i))+"|");
        }
        FormulaInterpretation interpretation = new FormulaInterpretation();
        System.out.print(interpretation.getLine(formula) + "|");
        System.out.println("");
    }
    
    private boolean hasNextInterpretation() {
        for (int i = predicate.size() - 1; i >= 0; i--) {
            Predicate p = predicate.get(i); 
            if (p.getValue()) {
                p.setValue(false);
                return true;
            } else {
                p.setValue(true);
            }
        }
        return false;
    }

    private void setResult(boolean tautology, int trueCount) {
        if (tautology)
            System.out.println("A fórmula representa uma tautologia.");
        if (tautology)
            setResult(TruthType.TAUTOLOGY);
        else if (trueCount > 0)
            setResult(TruthType.SATISFIABLE);
        else
            setResult(TruthType.CONTRADICTION);
    }
    
    public long print() {
        long start = System.currentTimeMillis();
        int trueCount = 0;
        init();
        initPredicates();
        printTitle();
        printSeparator('|', '+', '|');
        boolean tautology = true;
        do  {
            if (formula.fullEvaluate()) {
                trueCount++;
            } else {
                tautology =  false;
            }
            printLine();
        } while (hasNextInterpretation());
        printSeparator('\\', '-', '/');
        setResult(tautology, trueCount);
        return System.currentTimeMillis() - start;
    }

    @Override
    public long solve() {
        long start = System.currentTimeMillis();
        int trueCount = 0;
        init();
        initPredicates();
        boolean tautology = true;
        do  {
            if (formula.evaluate()) {
                trueCount++;
            } else {
                tautology =  false;
            }
        } while (hasNextInterpretation());
        setResult(tautology, trueCount);
        return System.currentTimeMillis() - start;
    }

    public long solve2() {
        long start = System.currentTimeMillis();
        int trueCount = 0;
        init();
        initPredicates();
        boolean tautology = true;
        do  {
            if (formula.fullEvaluate()) {
                trueCount++;
            } else {
                tautology =  false;
            }
        } while (hasNextInterpretation());
        setResult(tautology, trueCount);
        return System.currentTimeMillis() - start;
    }
}
