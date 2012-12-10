/**
 * 
 */
package truthTable;

import ast.*;
import ast.patterns.FormulaVisitor;

/**
 * @author dev
 *
 */
public class FormulaInterpretation implements FormulaVisitor {

    private int level;
    private StringBuffer s = new StringBuffer();
    

    public String getLine(Formula f) {
        level = 0;
        f.accept(this);
        return s.toString();
    }
    
    private void visitConnective(Connective c) {
        level++;
        int localLevel = level;
        s.append(" ");
        c.getLeft().accept(this);
        s.append(StringUtils.center(c.getLastFullEvaluation(), 
                c.getLexeme().length() + 2, localLevel));
        c.getRight().accept(this); 
        s.append(" ");
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
        level++;
        int localLevel = level;
        s.append(StringUtils.center(not.getLastFullEvaluation(), 2, localLevel));
        not.getFormula().accept(this);
        s.append(" ");
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
        level++;
        s.append(StringUtils.repeat(' ', p.getLexeme().length()));
    }

}
