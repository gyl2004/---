package calculator;

/**
 * 词法单元类型
 */
public enum TokenType {
    // 单字符标记
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
    
    // 单个或双字符标记
    EQUAL,
    
    // 字面量
    IDENTIFIER, STRING, NUMBER,
    
    // 关键字
    INT, DOUBLE, PRINT, SIN, COS,
    
    // 新增数学函数
    TAN, SQRT, POW, LOG, ABS, TRIANGLE_AREA,
    
    EOF
} 