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
    private FormulaList aFormula  = new FormulaList(); 
    private FormulaList aBaseFormula = new FormulaList(); 
    
    // estrutura de dados da tabela de símbolos
    private SymbolTable<Symbol> symbolTable = new SymbolTable<Symbol>();

    
    public SymbolTable<Symbol> getSymbolTable() {
        return symbolTable;
    }

    public LogicalSystem clone() {
        LogicalSystem newLogicalSystem = new LogicalSystem();
        SymbolTable<Symbol> newST = newLogicalSystem.getSymbolTable();
        newLogicalSystem.symbolTable = newST;
        for (Formula f: aFormula)
            if (f != null)
                newLogicalSystem.aFormula.add(f.clone(newST));
        for (Formula f: aBaseFormula)
            if (f != null)
                newLogicalSystem.aBaseFormula.add(f.clone(newST));
        newLogicalSystem.logicSystemType = this.logicSystemType; 
        newLogicalSystem.logicType = this.logicType; 
        newLogicalSystem.logicalTypeReason = new StringBuilder(this.logicalTypeReason);
        return newLogicalSystem;
    }

    public void addLeft(Formula formula) {
        aBaseFormula.add(formula);
    }
    
    public void addRight(Formula formula) {
        aFormula.add(formula);
    }
    
    public Formula getFormula() {
        return aFormula.generateFormula();
    }

    public Formula getBaseFormula() {
        return aBaseFormula.generateFormula();
    }

    public FormulaList getLeft() {
        return aBaseFormula;
    }

    public FormulaList getRight() {
        return aFormula;
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
            aFormula.add(f);
        } else
            throw new Exception("Nesta versão, o software aceita penas um tipo de verificação lógica.");
    }

    public void setDerivationSystem(FormulaList left, FormulaList right) throws Exception {
        if (logicSystemType == LogicalSystemType.UNKNOWN) {
            logicSystemType = LogicalSystemType.DERIVATION;
            aBaseFormula = left;
            aFormula = right;
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
            return aFormula.toString();
        case DERIVATION:
            return aBaseFormula + " |- " + aFormula;
        case ENTAILMENT:
            return aBaseFormula + " |= " + aFormula;
        default:
        }
        return "";
    }
    
}
