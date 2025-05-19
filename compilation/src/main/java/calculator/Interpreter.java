package calculator;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 解释器
 */
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final Map<String, Object> environment = new HashMap<>();
    
    public Interpreter() {
        // 初始化数学常量
        environment.put("PI", 3.141592653589793);
        environment.put("E", 2.718281828459045);
        // 不再打印常量初始化信息
    }
    
    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            System.err.println("运行时错误: " + error.getMessage());
        }
    }
    
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        Object value = evaluate(stmt.expression);
        // 输出表达式的结果
        if (value != null) {
            System.out.println(stringify(value));
        }
        return null;
    }
    
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
    
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        
        environment.put(stmt.name.lexeme, value);
        return null;
    }
    
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        
        if (!environment.containsKey(expr.name.lexeme)) {
            throw new RuntimeError(expr.name, "未定义的变量 '" + expr.name.lexeme + "'.");
        }
        
        environment.put(expr.name.lexeme, value);
        return value;
    }
    
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return asDouble(left) - asDouble(right);
            case PLUS:
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                
                if (left instanceof Double && right instanceof Double) {
                    return asDouble(left) + asDouble(right);
                }
                
                throw new RuntimeError(expr.operator, "操作数必须是数字或字符串.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if (asDouble(right) == 0) {
                    throw new RuntimeError(expr.operator, "除数不能为零.");
                }
                return asDouble(left) / asDouble(right);
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return asDouble(left) * asDouble(right);
            case POW:
                checkNumberOperands(expr.operator, left, right);
                return Math.pow(asDouble(left), asDouble(right));
        }
        
        // 不应该到达这里
        return null;
    }
    
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }
    
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }
    
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -asDouble(right);
        }
        
        // 不应该到达这里
        return null;
    }
    
    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        String name = expr.name.lexeme;
        
        // 特殊处理常量，但不再打印访问信息
        if (expr.isConstant) {
            if (environment.containsKey(name)) {
                Object value = environment.get(name);
                return value;
            }
        }
        
        if (environment.containsKey(name)) {
            Object value = environment.get(name);
            return value;
        }
        
        throw new RuntimeError(expr.name, "未定义的变量 '" + name + "'.");
    }
    
    @Override
    public Object visitTrigonometricExpr(Expr.Trigonometric expr) {
        Object argument = evaluate(expr.argument);
        checkNumberOperand(expr.function, argument);
        double value = asDouble(argument);
        
        switch (expr.function.type) {
            case SIN:
                return Math.sin(value);
            case COS:
                return Math.cos(value);
            case TAN:
                return Math.tan(value);
            case SQRT:
                if (value < 0) {
                    throw new RuntimeError(expr.function, "不能对负数求平方根.");
                }
                return Math.sqrt(value);
            case LOG:
                if (value <= 0) {
                    throw new RuntimeError(expr.function, "对数函数的参数必须为正数.");
                }
                return Math.log(value);
            case ABS:
                return Math.abs(value);
        }
        
        // 不应该到达这里
        return null;
    }
    
    @Override
    public Object visitPowerExpr(Expr.Power expr) {
        Object base = evaluate(expr.base);
        Object exponent = evaluate(expr.exponent);
        
        checkNumberOperands(expr.function, base, exponent);
        
        double baseValue = asDouble(base);
        double exponentValue = asDouble(exponent);
        
        return Math.pow(baseValue, exponentValue);
    }
    
    @Override
    public Object visitTriangleAreaExpr(Expr.TriangleArea expr) {
        // 计算三边长
        Object aObj = evaluate(expr.a);
        Object bObj = evaluate(expr.b);
        Object cObj = evaluate(expr.c);
        
        // 检查参数类型
        checkNumberOperand(expr.function, aObj);
        checkNumberOperand(expr.function, bObj);
        checkNumberOperand(expr.function, cObj);
        
        double a = asDouble(aObj);
        double b = asDouble(bObj);
        double c = asDouble(cObj);
        
        // 检查三角形条件
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new RuntimeError(expr.function, "三角形边长必须为正数.");
        }
        
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new RuntimeError(expr.function, "三边长不满足三角形条件 (任意两边之和必须大于第三边).");
        }
        
        // 使用海伦公式计算面积
        double p = (a + b + c) / 2;
        double area = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        
        return area;
    }
    
    private void execute(Stmt stmt) {
        if (stmt != null) {
            stmt.accept(this);
        }
    }
    
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
    
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "操作数必须是数字.");
    }
    
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "操作数必须是数字.");
    }
    
    private double asDouble(Object object) {
        return (Double)object;
    }
    
    private String stringify(Object object) {
        if (object == null) return "nil";
        
        if (object instanceof Double) {
            double value = (Double)object;
            
            // 处理接近0的数值
            if (Math.abs(value) < 1e-14) {
                return "0";
            }
            
            // 处理接近整数的小数
            if (Math.abs(value - Math.round(value)) < 1e-14) {
                return String.valueOf(Math.round(value));
            }
            
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        
        return object.toString();
    }
    
    public static class RuntimeError extends RuntimeException {
        final Token token;
        
        RuntimeError(Token token, String message) {
            super(message);
            this.token = token;
        }
    }
} 