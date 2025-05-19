# 简易计算器编程语言

这是一个简化版的计算器编程语言，支持基本的算术运算、变量声明和打印功能，并提供图形化界面。

## 功能特性

1. 支持基本算术运算符：+、-、*、/
2. 支持数学函数：sin()、cos()、tan()、sqrt()、pow()、log()、abs()
3. 支持变量声明：整型(int)和浮点型(double)
4. 支持打印字符串常量
5. 支持三角形面积计算函数 triangleArea()
6. 提供图形化界面，支持语法高亮、代码补全等功能
7. 支持高精度数值处理（自动处理接近0的微小数值）

## 项目结构

- `calculator.Scanner` - 词法分析器，将源代码转换为标记流
- `calculator.Parser` - 语法分析器，将标记转换为抽象语法树
- `calculator.Interpreter` - 解释器，执行抽象语法树
- `calculator.Expr` - 表达式抽象语法树节点
- `calculator.Stmt` - 语句抽象语法树节点
- `calculator.Token` - 词法单元
- `calculator.TokenType` - 词法单元类型
- `calculator.Calculator` - 主类，提供交互式环境
- `calculator.CalculatorGUI` - 基本图形界面


## 如何使用

1. 运行 `Calculator` 类进入交互式命令行环境
2. 运行 `CalculatorGUI` 类启动图形界面

## 语法示例

```
// 变量声明
int x = 10;
double y = 3.14;

// 算术表达式
x = x + 5;
y = y * 2;

// 数学函数
double sinValue = sin(30);
double cosValue = cos(60);
double tanValue = tan(45);
double squareRoot = sqrt(16);
double power = pow(2, 3);
double logValue = log(10);
double absValue = abs(-5);

// 三角形面积计算
double a = 3;
double b = 4;
double c = 5;
double area = triangleArea(a, b, c);

// 打印语句
print "x = " + x;
print "y = " + y;
print "sin(30) = " + sinValue;
print "三角形面积 = " + area;
```



### 三角形面积计算

使用海伦公式(Heron's formula)计算三角形面积：
```
S = √(p(p-a)(p-b)(p-c))
```
其中，p = (a+b+c)/2 是半周长，a、b、c是三角形的三边长。

使用方法：
```
triangleArea(3, 4, 5)  // 结果: 6
```

### 高精度数值处理

自动处理接近0的浮点计算结果，如sin(PI)显示为0而不是接近0的极小数值。

## 项目作者

- liangliang
