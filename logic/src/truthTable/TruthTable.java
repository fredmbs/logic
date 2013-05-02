/**
 * 
 */
package truthTable;

import java.util.ArrayList;
import java.util.Collections;

import proof.LogicalReasoning;
import proof.utils.LogicalSystemException;
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
    private long trueCount, falseCount;
    
    public TruthTable(LogicalSystem lsys) throws Exception {
        super(lsys);
        if (lsys.getLogicSystemType() != LogicalSystem.LogicalSystemType.FORMULA) {
            throw new LogicalSystemException("A versão atual da tabela verdade lida apenas com fórmula.");
        } else {
            this.formula = lsys.getConclusionFormula();
        }
    }

    /**
     * @return the formula
     */
    public Formula getFormula() {
        return formula;
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
        System.out.print(formulaStr + "|");
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

    private void setResult(boolean tautology, long trueCount) {
        //if (tautology)
        //    System.out.println("A fórmula representa uma tautologia.");
        if (tautology)
            setResult(TruthType.TAUTOLOGY);
        else if (trueCount > 0)
            setResult(TruthType.SATISFIABLE);
        else
            setResult(TruthType.CONTRADICTION);
    }
    
    public long getTrueCount() {
        return trueCount;
    }

    public long getFalseCount() {
        return falseCount;
    }

    public long print() {
        long start = System.nanoTime();
        trueCount = 0;
        falseCount = 0;
        init();
        initPredicates();
        printTitle();
        printSeparator('|', '+', '|');
        boolean tautology = true;
        do  {
            if (formula.fullEvaluate()) {
                trueCount++;
            } else {
                falseCount++;
            }
            printLine();
        } while (hasNextInterpretation());
        tautology =  (falseCount == 0);
        printSeparator('\\', '-', '/');
        setResult(tautology, trueCount);
        return System.nanoTime() - start;
    }

    @Override
    public long solve() {
        long start = System.nanoTime();
        trueCount = 0;
        falseCount = 0;
        init();
        initPredicates();
        boolean tautology = true;
        do  {
            if (formula.evaluate()) {
                trueCount++;
            } else {
                falseCount++;
            }
        } while (hasNextInterpretation());
        tautology =  (falseCount == 0);
        setResult(tautology, trueCount);
        return System.nanoTime() - start;
    }

    public long solve2() {
        long start = System.nanoTime();
        trueCount = 0;
        falseCount = 0;
        init();
        initPredicates();
        boolean tautology = true;
        do  {
            if (formula.fullEvaluate()) {
                trueCount++;
            } else {
                falseCount++;
            }
        } while (hasNextInterpretation());
        tautology =  (falseCount == 0);
        setResult(tautology, trueCount);
        return System.nanoTime() - start;
    }
}
