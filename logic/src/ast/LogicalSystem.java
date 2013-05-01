 package ast;

import ast.utils.SymbolTable;
/**
 * 
 */


/**
 * @author dev
 *
 */
public class LogicalSystem {

    public enum LogicalType {
        UNKNOWN, PROPOSITIONAL, PREDICATE 
    }
    
    public enum LogicalSystemType {
        UNKNOWN, FORMULA, DERIVATION, ENTAILMENT 
    }
    
    private LogicalSystemType logicSystemType = LogicalSystemType.UNKNOWN; 
    private LogicalType logicType = LogicalType.UNKNOWN; 
    private StringBuilder logicalTypeReason = new StringBuilder();
    private FormulaList conclusion  = new FormulaList(); 
    private FormulaList premisse = new FormulaList(); 
    
    // estrutura de dados da tabela de símbolos
    private SymbolTable<Symbol> symbolTable = new SymbolTable<Symbol>();

    
    public SymbolTable<Symbol> getSymbolTable() {
        return symbolTable;
    }

    public LogicalSystem clone() {
        LogicalSystem newLogicalSystem = new LogicalSystem();
        SymbolTable<Symbol> newST = newLogicalSystem.getSymbolTable();
        newLogicalSystem.symbolTable = newST;
        if (conclusion != null)
            for (Formula f: conclusion)
                if (f != null)
                    newLogicalSystem.conclusion.add(f.clone(newST));
        if (premisse != null)
            for (Formula f: premisse)
                if (f != null)
                    newLogicalSystem.premisse.add(f.clone(newST));
        newLogicalSystem.logicSystemType = this.logicSystemType; 
        newLogicalSystem.logicType = this.logicType; 
        newLogicalSystem.logicalTypeReason = new StringBuilder(this.logicalTypeReason);
        return newLogicalSystem;
    }

    public LogicalSystem cloneInverted() {
        LogicalSystem newLogicalSystem = new LogicalSystem();
        SymbolTable<Symbol> newST = newLogicalSystem.getSymbolTable();
        newLogicalSystem.symbolTable = newST;
        if (conclusion != null)
            for (Formula f: conclusion)
                if (f != null)
                    newLogicalSystem.premisse.add(f.clone(newST));
        if (premisse != null)
            for (Formula f: premisse)
                if (f != null)
                    newLogicalSystem.conclusion.add(f.clone(newST));
        newLogicalSystem.logicSystemType = LogicalSystem.LogicalSystemType.DERIVATION; 
        newLogicalSystem.logicType = this.logicType; 
        newLogicalSystem.logicalTypeReason = new StringBuilder(this.logicalTypeReason);
        return newLogicalSystem;
    }

    public void addLeft(Formula formula) {
        premisse.add(formula);
    }
    
    public void addRight(Formula formula) {
        conclusion.add(formula);
    }
    
    public Formula getConclusionFormula() {
        return conclusion.generateFormula();
    }

    public Formula getPremisseFormula() {
        return premisse.generateFormula();
    }

    public FormulaList getPremisse() {
        return premisse;
    }

    public boolean hasPremisse() {
        return premisse.size() > 0;
    }

    public FormulaList getConclusion() {
        return conclusion;
    }

    public boolean hasConclusion() {
        return conclusion.size() > 0;
    }

    public LogicalType getLogicType() {
        return logicType;
    }

    public LogicalSystemType getLogicSystemType() {
        return logicSystemType;
    }

    public void setFormulaSystem(Formula f) throws Exception {
        if (logicSystemType == LogicalSystemType.UNKNOWN) {
            logicSystemType = LogicalSystemType.FORMULA;
            conclusion.add(f);
        } else
            throw new Exception("Nesta versão, o software aceita penas um tipo de verificação lógica.");
    }

    public void setDerivationSystem(FormulaList left, FormulaList right) throws Exception {
        if (logicSystemType == LogicalSystemType.UNKNOWN) {
            logicSystemType = LogicalSystemType.DERIVATION;
            if (left != null)
                premisse = left;
            if (right != null)
                conclusion = right;
        } else
            throw new Exception("Nesta versão, o software aceita penas um tipo de verificação lógica.");
    }

    public void setEntailmentSystem(FormulaList left, FormulaList right) throws Exception {
        throw new Exception("Essa versão não aceita ENTAILMENT.");
    }

    public void setPropositionalLogic(String reason) throws Exception {
        if (this.logicType == LogicalType.UNKNOWN) {
            this.logicType = LogicalType.PROPOSITIONAL;
        }
        this.logicalTypeReason.append("Propositional:" + reason + '\n');
    }

    public void setPredicateLogic(String reason) throws Exception {
        if (this.logicType == LogicalType.UNKNOWN ||
                this.logicType == LogicalType.PROPOSITIONAL) {
            this.logicType = LogicalType.PREDICATE;
        }
        if (this.logicType == LogicalType.PREDICATE)
            this.logicalTypeReason.append("Predicte:" + reason + '\n');
        else
            throw new Exception("Uso de sintaxe de lógica de predicados não permitido na fórmula."
                    + "\nNão é possível '" + reason + "', pois:\n" 
                    + logicalTypeReason.toString());
    }

    @Override
    public String toString() {
        switch(logicSystemType) {
        case UNKNOWN:
            return "?";
        case FORMULA:
            return conclusion.toString();
        case DERIVATION:
            return premisse + " |- " + conclusion;
        case ENTAILMENT:
            return premisse + " |= " + conclusion;
        default:
        }
        return "";
    }
    
}
