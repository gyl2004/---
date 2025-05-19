package calculator;

import java.io.IOException;

/**
 * @author ：亮
 * 基础测试程序 - 逐一测试各项功能
 */
public class BasicTest {
    public static void main(String[] args) throws IOException {
        // 测试基本计算
        testFeature("基本计算", "1 + 2;");
        
        // 测试变量
        testFeature("变量声明和使用", "int x = 10; x + 5;");
        
        // 测试常量PI
        testFeature("常量PI", "double pi = 3.14159; pi;");
        
        // 测试内置常量PI (直接访问)
        testFeature("内置常量PI", "PI;");
        
        // 测试sin函数
        testFeature("Sin函数", "sin(30);");
        
        // 测试cos函数
        testFeature("Cos函数", "cos(60);");
        
        // 测试tan函数
        testFeature("Tan函数", "tan(45);");
        
        // 测试sqrt函数
        testFeature("Sqrt函数", "sqrt(16);");
        
        // 测试log函数
        testFeature("Log函数", "log(10);");
        
        // 测试绝对值函数
        testFeature("绝对值函数", "abs(-5);");
        
        // 测试幂函数
        testFeature("幂函数", "pow(2, 3);");
    }
    
    private static void testFeature(String name, String code) {
        System.out.println("\n===== 测试: " + name + " =====");
        System.out.println("代码: " + code);
        System.out.println("结果:");
        try {
            Calculator.run(code);
            System.out.println("✓ 成功");
        } catch (Exception e) {
            System.out.println("✗ 失败: " + e.getMessage());
            e.printStackTrace(System.out);
        }
        System.out.println("====================");
    }
} 