package calculator;

import java.util.List;
import java.util.ArrayList;

/**
 * 语法分析器
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    
    private static class ParseError extends RuntimeException {}
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            Stmt stmt = declaration();
            if (stmt != null) {
                statements.add(stmt);
            }
        }
        
        return statements;
    }
    
    private Stmt declaration() {
        try {
            if (match(TokenType.INT, TokenType.DOUBLE)) {
                return varDeclaration();
            }
            
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    
    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "期望变量名.");
        
        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }
        
        consume(TokenType.SEMICOLON, "期望 ';' 在变量声明后.");
        return new Stmt.Var(name, initializer);
    }
    
    private Stmt statement() {
        if (match(TokenType.PRINT)) return printStatement();
        
        return expressionStatement();
    }
    
    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "期望 ';' 在打印语句后.");
        return new Stmt.Print(value);
    }
    
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "期望 ';' 在表达式后.");
        return new Stmt.Expression(expr);
    }
    
    private Expr expression() {
        return assignment();
    }
    
    private Expr assignment() {
        Expr expr = term();
        
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            
            error(equals, "无效的赋值目标.");
        }
        
        return expr;
    }
    
    private Expr term() {
        Expr expr = factor();
        
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expr factor() {
        Expr expr = unary();
        
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expr unary() {
        if (match(TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        
        return trigonometric();
    }
    
    private Expr trigonometric() {
        if (match(TokenType.SIN, TokenType.COS, TokenType.TAN, TokenType.SQRT, TokenType.ABS, TokenType.LOG)) {
            Token function = previous();
            consume(TokenType.LEFT_PAREN, "期望 '(' 在函数调用后.");
            Expr arg = expression();
            consume(TokenType.RIGHT_PAREN, "期望 ')' 在函数参数后.");
            return new Expr.Trigonometric(function, arg);
        } else if (match(TokenType.POW)) {
            Token function = previous();
            consume(TokenType.LEFT_PAREN, "期望 '(' 在函数调用后.");
            Expr base = expression();
            consume(TokenType.COMMA, "期望 ',' 分隔函数参数.");
            Expr exponent = expression();
            consume(TokenType.RIGHT_PAREN, "期望 ')' 在函数参数后.");
            return new Expr.Power(function, base, exponent);
        } else if (match(TokenType.TRIANGLE_AREA)) {
            Token function = previous();
            consume(TokenType.LEFT_PAREN, "期望 '(' 在函数调用后.");
            Expr a = expression();
            consume(TokenType.COMMA, "期望 ',' 分隔函数参数.");
            Expr b = expression();
            consume(TokenType.COMMA, "期望 ',' 分隔函数参数.");
            Expr c = expression();
            consume(TokenType.RIGHT_PAREN, "期望 ')' 在函数参数后.");
            return new Expr.TriangleArea(function, a, b, c);
        }
        
        return primary();
    }
    
    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        }
        
        if (match(TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "期望 ')' 在表达式后.");
            return new Expr.Grouping(expr);
        }
        
        throw error(peek(), "期望表达式.");
    }
    
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        
        return false;
    }
    
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        
        throw error(peek(), message);
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private ParseError error(Token token, String message) {
        System.err.println("Line " + token.line + " at '" + token.lexeme + "': " + message);
        return new ParseError();
    }
    
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;
            
            switch (peek().type) {
                case INT:
                case DOUBLE:
                case PRINT:
                    return;
            }
            
            advance();
        }
    }
} 