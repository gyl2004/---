package calculator;

/**
 * @author ：亮
 * 表达式抽象语法树
 */
public abstract class Expr {
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitAssignExpr(Assign expr);
        R visitTrigonometricExpr(Trigonometric expr);
        R visitPowerExpr(Power expr);
        R visitTriangleAreaExpr(TriangleArea expr);
    }
    
    public abstract <R> R accept(Visitor<R> visitor);
    
    public static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;
        
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }
    
    public static class Grouping extends Expr {
        final Expr expression;
        
        Grouping(Expr expression) {
            this.expression = expression;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }
    
    public static class Literal extends Expr {
        final Object value;
        
        Literal(Object value) {
            this.value = value;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }
    
    public static class Unary extends Expr {
        final Token operator;
        final Expr right;
        
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }
    
    public static class Variable extends Expr {
        final Token name;
        final boolean isConstant;
        
        Variable(Token name) {
            this.name = name;
            // 检查是否是内置常量
            this.isConstant = name.lexeme.equals("PI") || name.lexeme.equals("E");
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }
    
    public static class Assign extends Expr {
        final Token name;
        final Expr value;
        
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }
    
    public static class Trigonometric extends Expr {
        final Token function;
        final Expr argument;
        
        Trigonometric(Token function, Expr argument) {
            this.function = function;
            this.argument = argument;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitTrigonometricExpr(this);
        }
    }
    
    public static class Power extends Expr {
        final Token function;
        final Expr base;
        final Expr exponent;
        
        Power(Token function, Expr base, Expr exponent) {
            this.function = function;
            this.base = base;
            this.exponent = exponent;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPowerExpr(this);
        }
    }
    
    public static class TriangleArea extends Expr {
        final Token function;
        final Expr a;
        final Expr b;
        final Expr c;
        
        TriangleArea(Token function, Expr a, Expr b, Expr c) {
            this.function = function;
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitTriangleAreaExpr(this);
        }
    }
} 