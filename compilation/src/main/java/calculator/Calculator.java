package calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author ：亮
 * 计算器主类
 */
public class Calculator {
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    
    public static void main(String[] args) throws IOException {
        System.out.println("简易计算器 ");
        System.out.println("支持 +, -, *, /, sin(), cos(), tan(), sqrt(), pow(), log(), abs(), triangleArea() 运算");
        System.out.println("支持数学常量 PI 和 E");
        System.out.println("支持变量声明 (例如: int a = 5; 或 double b = 3.14;)");
        System.out.println("支持打印语句 (例如: print \"结果是: \" + 10;)");
        System.out.println("输入 'help' 获取更多帮助， 'exit' 退出程序");
        System.out.println();
        
        runPrompt();
    }
    
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equals("exit")) break;
            
            // 处理特殊命令
            if (line.equals("help")) {
                printHelp();
                continue;
            }
            
            // 自动添加分号（如果没有）
            if (!line.trim().isEmpty() && !line.trim().endsWith(";")) {
                line = line + ";";
            }
            
            run(line);
            System.out.println(); // 在结果后打印一个空行，增加可读性
            hadError = false;
        }
    }
    
    private static void printHelp() {
        System.out.println("\n===== 计算器语言帮助 =====");
        System.out.println("语法规则:");
        System.out.println("  1. 每个语句以分号(;)结束,(不用也可以)");
        System.out.println("  2. 支持基本算术运算: +, -, *, /");
        System.out.println("  3. 支持数学函数: sin, cos, tan, sqrt, pow, log, abs, triangleArea");
        System.out.println("  4. 支持变量声明: int x = 10; 或 double y = 3.14;");
        System.out.println("  5. 支持打印语句: print \"结果是:\" + 变量;");
        
        System.out.println("\n可用数学常量:");
        System.out.println("  PI - 圆周率(3.141592...)");
        System.out.println("  E - 自然对数底数(2.718281...)");
        
        System.out.println("\n函数使用示例:");
        System.out.println("  sin(45) - 计算45度的正弦值");
        System.out.println("  cos(30) - 计算30度的余弦值");
        System.out.println("  tan(60) - 计算60度的正切值");
        System.out.println("  sqrt(16) - 计算16的平方根");
        System.out.println("  pow(2, 3) - 计算2的3次方");
        System.out.println("  log(10) - 计算10的自然对数");
        System.out.println("  abs(-5) - 计算-5的绝对值");
        System.out.println("  triangleArea(3, 4, 5) - 根据三边长计算三角形面积");
        
        System.out.println("\n特殊命令:");
        System.out.println("  help - 显示此帮助信息");
        System.out.println("  exit - 退出程序");
        System.out.println("=======================\n");
    }
    
    public static void run(String source) {
        try {
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            
            // 如果有语法错误，停止执行
            if (hadError) {
                System.err.println("语法错误，程序未执行。");
                return;
            }
            
            // 如果没有语句，不执行
            if (statements.isEmpty()) {
                System.err.println("没有可执行的语句。");
                return;
            }
            
            interpreter.interpret(statements);
        } catch (Exception e) {
            System.err.println("执行错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    static void error(int line, String message) {
        report(line, "", message);
    }
    
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
    
    static void runtimeError(Interpreter.RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        System.err.println("在 '" + error.token.lexeme + "' 处发生错误。");
        hadRuntimeError = true;
    }
    
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
} 