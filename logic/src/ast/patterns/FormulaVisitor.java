package ast.patterns;

import ast.*;

public interface FormulaVisitor {
    void visit(And and);
    void visit(Or or);
    void visit(Not not);
    void visit(Implies imp);
    void visit(Equivalent equ);
    void visit(Equality equal);
    void visit(Exists ex);
    void visit(Forall fa);
    void visit(Predicate p);
}
