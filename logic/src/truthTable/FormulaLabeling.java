/**
 * 
 */
package truthTable;

import utils.StringUtils;
import ast.*;
import ast.patterns.FormulaVisitor;
/**
 * @author dev
 *
 */
public class FormulaLabeling implements FormulaVisitor {

    private StringBuilder label = new StringBuilder();

    public String getLine(Formula f) {
        f.accept(this);
        return label.toString();
    }
    
    private void visitConnective(Connective c) {
        label.append("(");
        c.getLeft().accept(this); 
        label.append(StringUtils.center(c.getLexeme(), 
                c.getLexeme().length()+2)); 
        c.getRight().accept(this);
        label.append(")");
    }

    @Override
    public void visit(And and) {
        visitConnective(and);
    }

    @Override
    public void visit(Or or) {
        visitConnective(or);
    }

    @Override
    public void visit(Not not) {
        // TODO Auto-generated method stub
        label.append("~(");
        not.getFormula().accept(this);
        label.append(")");
    }

    @Override
    public void visit(Implies imp) {
        visitConnective(imp);
    }

    @Override
    public void visit(Equivalent equ) {
        visitConnective(equ);
    }

    @Override
    public void visit(Equality equal) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Exists ex) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Forall fa) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(Predicate p) {
        label.append(StringUtils.center(p.getLexeme(), p.getLexeme().length()));
    }

}
