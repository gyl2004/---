package calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 计算器图形界面
 * 完整集成解释器功能，支持所有计算器功能
 */
public class CalculatorGUI extends JFrame {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JPanel functionButtonsPanel;
    
    public CalculatorGUI() {
        // 设置窗口基本属性
        setTitle("高级计算器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建输入区域
        inputArea = new JTextArea(10, 50);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("输入表达式:"));
        
        // 创建输出区域
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("计算结果:"));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton calculateButton = new JButton("计算");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
        buttonPanel.add(calculateButton);
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
            }
        });
        buttonPanel.add(clearButton);
        
        JButton helpButton = new JButton("帮助");
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        buttonPanel.add(helpButton);
        
        // 创建函数按钮面板
        createFunctionButtonsPanel();
        
        // 将所有组件添加到主面板
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(inputScrollPane);
        centerPanel.add(outputScrollPane);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(functionButtonsPanel, BorderLayout.EAST);
        
        // 设置主面板
        setContentPane(mainPanel);
    }
    
    /**
     * 创建函数按钮面板
     */
    private void createFunctionButtonsPanel() {
        functionButtonsPanel = new JPanel(new GridLayout(12, 1, 5, 5));
        functionButtonsPanel.setBorder(BorderFactory.createTitledBorder("函数"));
        
        // 数学函数按钮
        addFunctionButton("sin(x)");
        addFunctionButton("cos(x)");
        addFunctionButton("tan(x)");
        addFunctionButton("sqrt(x)");
        addFunctionButton("log(x)");
        addFunctionButton("abs(x)");
        addFunctionButton("pow(x,y)");
        addFunctionButton("triangleArea(a,b,c)");
        
        // 常量按钮
        addFunctionButton("PI");
        addFunctionButton("E");
        
        // 变量声明按钮
        addFunctionButton("int x = ");
        addFunctionButton("double y = ");
    }
    
    /**
     * 添加函数按钮
     */
    private void addFunctionButton(final String function) {
        JButton button = new JButton(function);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.insert(function, inputArea.getCaretPosition());
            }
        });
        functionButtonsPanel.add(button);
    }
    
    /**
     * 计算表达式
     */
    private void calculate() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("请输入表达式");
            return;
        }
        
        // 处理多行输入
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // 自动添加分号（如果没有）
            if (!line.trim().endsWith(";")) {
                line = line + ";";
            }
            
            try {
                // 重定向标准输出到StringBuffer
                CustomOutputStream outputStream = new CustomOutputStream();
                System.setOut(new java.io.PrintStream(outputStream));
                
                // 使用解释器进行计算
                Calculator.run(line);
                
                // 获取输出结果
                String result = outputStream.getContent();
                if (!result.trim().isEmpty()) {
                    output.append(result);
                }
                
                // 重置标准输出
                System.setOut(System.out);
            } catch (Exception e) {
                output.append("错误: ").append(e.getMessage()).append("\n");
            }
        }
        
        // 显示结果
        outputArea.setText(output.toString());
    }
    
    /**
     * 显示帮助信息
     */
    private void showHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("===== 计算器语言帮助 =====\n");
        helpText.append("语法规则:\n");
        helpText.append("  1. 每个语句以分号(;)结束(可选)\n");
        helpText.append("  2. 支持基本算术运算: +, -, *, /\n");
        helpText.append("  3. 支持数学函数: sin, cos, tan, sqrt, pow, log, abs, triangleArea\n");
        helpText.append("  4. 支持变量声明: int x = 10; 或 double y = 3.14;\n");
        helpText.append("  5. 支持打印语句: print \"结果是:\" + 变量;\n\n");
        
        helpText.append("可用数学常量:\n");
        helpText.append("  PI - 圆周率(3.141592...)\n");
        helpText.append("  E - 自然对数底数(2.718281...)\n\n");
        
        helpText.append("函数使用示例:\n");
        helpText.append("  sin(45) - 计算45度的正弦值\n");
        helpText.append("  cos(30) - 计算30度的余弦值\n");
        helpText.append("  tan(60) - 计算60度的正切值\n");
        helpText.append("  sqrt(16) - 计算16的平方根\n");
        helpText.append("  pow(2, 3) - 计算2的3次方\n");
        helpText.append("  log(10) - 计算10的自然对数\n");
        helpText.append("  abs(-5) - 计算-5的绝对值\n");
        helpText.append("  triangleArea(3, 4, 5) - 根据三边长计算三角形面积\n\n");
        
        helpText.append("多行输入示例:\n");
        helpText.append("  double a = 3;\n");
        helpText.append("  double b = 4;\n");
        helpText.append("  double c = 5;\n");
        helpText.append("  triangleArea(a, b, c);\n");
        
        JOptionPane.showMessageDialog(this, helpText.toString(), "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 自定义输出流，用于捕获System.out输出
     */
    private class CustomOutputStream extends java.io.ByteArrayOutputStream {
        public String getContent() {
            return toString();
        }
    }
    
    /**
     * 主方法，启动应用程序
     */
    public static void main(String[] args) {
        // 使用事件调度线程创建和显示GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CalculatorGUI ui = new CalculatorGUI();
                ui.setVisible(true);
            }
        });
    }
} 