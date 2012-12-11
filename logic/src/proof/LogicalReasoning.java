package proof;

import proof.utils.LogicalSystemException;
import ast.LogicalSystem;
import ast.Symbol;
import ast.utils.SymbolTable;

public abstract class LogicalReasoning {

    public enum TruthType { UNKNOWN, TAUTOLOGY, SATISFIABLE, CONTRADICTION };
    
    private TruthType result = TruthType.UNKNOWN;
    private SymbolTable<Symbol> symbolTable;

    public LogicalReasoning(LogicalSystem lsys) throws Exception {
        if (lsys.getLogicType() != LogicalSystem.LogicalType.PROPOSITIONAL){
            throw new LogicalSystemException("A versão atual do software " +
                "soluciona apenas fórmula em lógica proposicional.");
        };
        this.symbolTable = lsys.getSymbolTable();
    }

    abstract public long solve();

    public TruthType getResult() {
        return result;
    }
    
    protected void setResult(TruthType  r) {
        result = r;
    }

    public SymbolTable<Symbol> getSymbolTable() {
        return symbolTable;
    }
    
    public String getResultName() {
        switch(result) {
        case UNKNOWN:
            return "UNKNOWN";
        case TAUTOLOGY:
            return "TAUTOLOGY";
        case SATISFIABLE:
            return "SATISFIABLE";
        case CONTRADICTION:
            return "CONTRADICTION";
        }
        return "";
    }
    
}
