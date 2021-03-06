package proof;

import proof.utils.LogicalSystemException;
import ast.LogicalSystem;
import ast.Symbol;
import ast.utils.SymbolTable;

public abstract class LogicalReasoning {
    
    public enum TruthType { UNKNOWN, TAUTOLOGY, NOT_TAUTOLOGY, 
        SATISFIABLE, CONTRADICTION, NOT_CONTRADICTION };
        
        private TruthType result = TruthType.UNKNOWN;
        private LogicalSystem logicalSystem;
        private SymbolTable<Symbol> symbolTable;
        
        public LogicalReasoning(LogicalSystem lsys) throws Exception {
            if (lsys.getLogicType() != LogicalSystem.LogicalType.PROPOSITIONAL){
                throw new LogicalSystemException("A versão atual do software " +
                        "soluciona apenas fórmula em lógica proposicional.");
            };
            this.logicalSystem = lsys;
            this.symbolTable = lsys.getSymbolTable();
        }
        
        abstract public long solve();
        
        public TruthType getResult() {
            return result;
        }
        
        protected void setResult(TruthType  r) {
            result = r;
        }
        
        public LogicalSystem getLogicalSystem() {
            return logicalSystem;
        }

        public SymbolTable<Symbol> getSymbolTable() {
            return symbolTable;
        }
        
        static public String getResultName(int t) {
            TruthType tt = TruthType.values()[t];
            return LogicalReasoning.getResultName(tt);
        }
        
        static public String getResultName(TruthType result) {
            switch(result) {
            case UNKNOWN:
                return "UNKNOWN";
            case TAUTOLOGY:
                return "TAUTOLOGY";
            case NOT_TAUTOLOGY:
                return "NOT_TAUTOLOGY";
            case SATISFIABLE:
                return "SATISFIABLE";
            case CONTRADICTION:
                return "CONTRADICTION";
            case NOT_CONTRADICTION:
                return "NOT_CONTRADICTION";
            }
            return "";
        }
        
        public String getResultName() {
            return LogicalReasoning.getResultName(result);
        }
        
        public boolean compareResult(TruthType with) {
            if (with == TruthType.UNKNOWN)
                return false;
            switch(result) {
            case UNKNOWN:
                return false;
            case TAUTOLOGY:
            case SATISFIABLE:
            case CONTRADICTION:
                return (with == result);
            case NOT_TAUTOLOGY:
                return (with == TruthType.SATISFIABLE ||
                with == TruthType.CONTRADICTION);
            case NOT_CONTRADICTION:
                return (with == TruthType.SATISFIABLE ||
                with == TruthType.TAUTOLOGY);
            }
            return false;
        }
        
}
