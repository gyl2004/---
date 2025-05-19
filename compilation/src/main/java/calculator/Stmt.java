package calculator;

/**
 * 语句抽象语法树
 */
public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }
    
    public abstract <R> R accept(Visitor<R> visitor);
    
    public static class Expression extends Stmt {
        final Expr expression;
        
        Expression(Expr expression) {
            this.expression = expression;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }
    
    public static class Print extends Stmt {
        final Expr expression;
        
        Print(Expr expression) {
            this.expression = expression;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }
    
    public static class Var extends Stmt {
        final Token name;
        final Expr initializer;
        
        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }
} 