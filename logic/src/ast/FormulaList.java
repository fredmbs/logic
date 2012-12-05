/**
 * 
 */
package ast;

import java.util.ArrayList;


/**
 * @author dev
 *
 */
public class FormulaList extends ArrayList<Formula> {

    private static final long serialVersionUID = 2477539857617393054L;

    public Formula generateFormula() {
        Formula formula = null;
        // verifica se existe alguma formula na lista
        if (this.size() > 0) {
            // salta todas as fórmulas nulas no início da lista
            int i = 0;
            do {
                formula = this.get(i++);
            } while ((formula == null) && (i < this.size()));
            // se ainda existe formula na lista, adiciona na formula
            Formula other;
            for (; i < this.size(); i++) {
                other = this.get(i);
                if (other != null) {
                    formula = new And(formula, other);
                }
            }
        }
        return formula;
    }

    
    @Override
    public String toString() {
        StringBuffer strFormula = new StringBuffer();
        for (Formula f: this) {
            if (f != null) {
                if (strFormula.length() > 0)
                    strFormula.append(",");
                strFormula.append(f);
            }
        }
        return strFormula.toString();
    }


}
