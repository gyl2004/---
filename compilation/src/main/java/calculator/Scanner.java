package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词法分析器
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    
    private int start = 0;
    private int current = 0;
    private int line = 1;
    
    private static final Map<String, TokenType> keywords;
    
    static {
        keywords = new HashMap<>();
        keywords.put("int", TokenType.INT);
        keywords.put("double", TokenType.DOUBLE);
        keywords.put("print", TokenType.PRINT);
        keywords.put("sin", TokenType.SIN);
        keywords.put("cos", TokenType.COS);
        
        // 新增数学函数
        keywords.put("tan", TokenType.TAN);
        keywords.put("sqrt", TokenType.SQRT);
        keywords.put("pow", TokenType.POW);
        keywords.put("log", TokenType.LOG);
        keywords.put("abs", TokenType.ABS);
        keywords.put("triangleArea", TokenType.TRIANGLE_AREA);
    }
    
    public Scanner(String source) {
        this.source = source;
    }
    
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
    
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            
            case '/':
                if (match('/')) {
                    // 注释处理
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
                
            case '=': addToken(TokenType.EQUAL); break;
            
            case ' ':
            case '\r':
            case '\t':
                // 忽略空白字符
                break;
                
            case '\n':
                line++;
                break;
                
            case '"': string(); break;
            
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // 中文字符通常Unicode值大于127
                    if (c > 127) {
                        // 忽略中文字符，避免报错
                        System.err.println("Warning Line " + line + ": Non-ASCII character: " + c + " (ignored)");
                    } else {
                        System.err.println("Line " + line + ": Unexpected character: " + c);
                    }
                }
                break;
        }
    }
    
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        
        addToken(type);
    }
    
    private void number() {
        while (isDigit(peek())) advance();
        
        // 处理小数点部分
        if (peek() == '.' && isDigit(peekNext())) {
            // 消费小数点
            advance();
            
            while (isDigit(peek())) advance();
        }
        
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }
    
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        
        if (isAtEnd()) {
            System.err.println("Line " + line + ": Unterminated string.");
            return;
        }
        
        // 消费闭合的 "
        advance();
        
        // 去掉两边的引号
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        
        current++;
        return true;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    private char advance() {
        return source.charAt(current++);
    }
    
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
} 