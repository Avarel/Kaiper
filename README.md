AJE [![Download](https://api.bintray.com/packages/avarel/maven/AJE/images/download.svg)](https://bintray.com/avarel/maven/AJE/_latestVersion) [![Build Status](https://travis-ci.org/Avarel/AJE.svg?branch=master)](https://travis-ci.org/Avarel/AJE)
===
**What is AJE?** AJE is powerful and expressive mathematical evaluator for
    the Java programming language. It features **flexible syntax, complex numbers, 
    booleans, first-class functions, user-defined variables**, along with mimicking 
    useful functional programming language features in order to provide a rich end-user experience.

**TL;DR:** AJE is a math evaluator with a programming twist.

### Download [![Download](https://api.bintray.com/packages/avarel/maven/AJE/images/download.svg)](https://bintray.com/avarel/maven/AJE/_latestVersion)
Be sure to replace the VERSION key below with the latest version shown above!

##### Maven
```xml
<dependency>
    <groupId>xyz.avarel</groupId>
    <artifactId>AJE</artifactId>
    <version>VERSION</version>
</dependency>
```
```xml
<repository>
    <id>jcenter</id>
    <name>jcenter-bintray</name>
    <url>http://jcenter.bintray.com</url>
</repository>
```

##### Gradle
```gradle
dependencies {
    compile 'xyz.avarel:AJE:VERSION'
}

repositories {
    jcenter()
}
```

### Features
|Feature|AJE Type|Java Type|Examples|
|---|---|---|---:|
|Simple arithmetic|`integer`|`Integer`|`1` `42` `1+2^3` `2*(3+4)`|
|Decimals|`decimal`|`Double`|`1.235` `-2.0/17` `3.0+2.5`|
|Boolean logic|`truth`|`Boolean`|`3 >= 2` `true && false`|
|Imaginary calculations|`complex`|`Complex*`|`i^2` `3i` `(8+2i)(5i+3)`|
|Lists operations|`vector`|`List<Any>`|`[1,2,3] == [1..3]` `[1,2,3] + [1]`|
|First class functions|`function`|`Function*`|`func(x) = { x + 2 }` `{ x, y -> x ^ y }`|

`*` Mapped to AJE object.

### Usage 
```java
class AJETest {
    public static void go() {
        // Base expression.
        Expression exp = new Expression("sum(tau,2,i)");
        
        // Add a constant.
        exp.add("tau", new Expression("2 * pi"));
        
        // Add a normal function.
        exp.add("double", new NativeFunction(Numeric.TYPE) {
            @Override
            protected Any eval(List<Any> arguments) {
                return arguments.get(0).times(2); 
                // Only works for decimals/complex.
                // Check out DefaultFunction.java to handle integers.
            }
        });
        
        // Add a varargs function.
        exp.add("sum", new NativeFunction(true, Numeric.TYPE) {
            @Override
            protected Any eval(List<Any> arguments) {
                if (arguments.isEmpty()) return Int.of(0);
                Any accumulator = arguments.get(0);
                for (int i = 1; i < arguments.size(); i++) {
                    accumulator = Numeric.process(accumulator, arguments.get(i), Any::plus);
                }
                return accumulator;
            }
        });
        
        // Calculate into AJE object.
        Any result = exp.compute();
        
        // Get the native representation of the object.
        // Each AJE object is mapped to a native object.
        Object obj = result.toNative();
        
        // Prints the result.
        System.out.println(result);
    }
}
```

### Operators
###### Numeric Operators `integer` `decimal` `complex` `list`
|Symbol|Description|Example|
|---|---|---:|
|`+`|Addition|`a + b`|
|`-`|Subtraction|`a - b`|
|`*`|Multiplication|`a * b`|
| |Implicit Multiplication|`a(b)`|
|`/`|Division|`a / b`|
|`^`|Exponentiation|`a ^ b`|
|`%`|Modulus|`a % b`|
|`-`|Negation|`-a`|

###### Boolean and Relational Operators `boolean`
|Symbol|Description|Example|
|---|---|---:|
|`~` `!`|Negation|`~a` `~true` `!false`|
|`&&` `/\ ` `and`|And - Logical conjunction|`a && b` `a /\ b` `a and b`|
|<code>&#124;&#124;</code> `\/` `or`|Or - Logical disjunction|`a && b` `a /\ b` `a and b`|
|`==`|Equality|`a == b`|
|`!=`|Inequality|`a == b`|
|`>`|Greater than|`a > b`|
|`<`|Less than|`a < b`|
|`>=`|Greater than or equal to|`a >= b`|
|`<=`|Less than or equal to|`a <= b`|

###### List and Ranges Operators `vector`
Though ranges are of a different type, `range`, it is recommended that 
    ranges are wrapped into a vector using `[range]` syntax. 

|Symbol|Description|Example|
|---|---|---:|
|`..`|Range creation -`integer` and `decimal` only|`a..b`|
|`..<`|Exclusive range -`integer` and `decimal` only|`a..<b` `[a..<b]`|
|`[item, items...]`|List creation|`[a,b,c,d,e,f,g]`|
|`[index]`|Get item at index.|`list[i]`|
|`[start:end:step]`|Slice operation, search it up.|`list[::]` `list[1:-3:]` `list[3:5:2]`|

###### Functional Operators `function`
|Symbol|Description|Example|
|---|---|---:|
|`function(args...)`|Invocation|`atan2(3.6, 2.5)`|
|<code>&#124;></code>|Pipe forward as first argument, note that this is not a composition.|<code>5 &#124;> sin() &#124;> cos()</code>|
|`+`|Sum of functions|`(f+g)(x) == f(x) + g(x)`|
|`-`|Difference of functions|`(f-g)(x) == f(x) - g(x)`|
|`*`|Product of functions|`(f*g)(x) == f(x) * g(x)`|
|`/`|Quotient of functions|`(f/g)(x) == f(x) / g(x)`|

### Defining Variables
###### Declaration
Variables are names with information that you can use to store values and use them throughout
    the script. They can be declared using the following syntax:
```
// Declaration
var name = expression

// Assignment - requires a declaration.
name = expression
```

```
var x = 10
x = 20
sin(x)
```

###### Compound Assignment
Variables can use the short syntax-sugar compound assignments. They provide a shorter syntax to perform
    the operation of the two operands and assign them to the first. Ex: `+=` `-=` `*=`
```
name (binary operator)= expression

x += 5
y %= 2.0
```

### Defining Functions
Functions are expressions that can be invoked using the __invocation operator__.

`complex` arguments can be either `integer`, `decimal`, or `complex`.

`decimal` arguments can be either `integer`, or `decimal`.

##### Declaring a Function
###### Traditional `func f(it) { it + 1 }`
Traditional functions can be declared with the syntax shown below.
    If the optional name field is present, then the function will be 
    available to used as an invocable variable with that name. If the 
    name field is not present, the function be an anonymous function expression.
```
func [name]([param,...]) [=] { [statements] }
func [name]([param,...]) = expression

func f(x) = x + 2; f(2) == 4
func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)
```

###### Lambda `{ it -> it + 1 }`
Alternatively, functions can also be declared using the following syntax.
    These functions can be passed into arguments and used as variables.
    They are anonymous but variables can be set to this function, of which 
    they can be then invoked by the user.
```
{ [param,...] -> [statements] }

add = { x, y -> x + y }; [1..10] |> fold(0, add) == 55
[1..10] |> fold(1, { x, y -> x * y })
```
    
###### Quick `_ + 1`
These functions are basically anonymous alternatives that only takes in one argument.
    Beware that in this quick expression, you can only reference the implied parameter
    once. However you may use as many operators as you want until the end of the expression.
    This allows functions to be used in higher-order functions in a concise manner.
```
_ ...expression

[[1, 2, 3], [1, 5, 8, 9, 10], [1..50]] |> map(_.size)
map([1..10], _ ^ 2) == [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
```

##### Native Functions
These are functions that are built into AJE. They include both higher-order and simple/multi-argument functions.

|Symbol|Description|Arguments|Example|
|---|---|---|---:|
|`compose`|Create a composition of two functions|(`function(x)`,`function(x)`)|`compose(asin, sin)`|
|`map`|List iteration action function|(`list`, `function(x)`)|<code>var x = 0; [0..<9] &#124;> each(func(it) { x += it }); x</code>|
|`map`|List transform function|(`list`, `function(x)`)|`map([1..10], {it ^ 2})`|
|`filter`|List filter function|(`list`, `function(x)`)|`filter([1..10], {it%2==0})`|
|`fold`|List accumulation function|(`list`, `value`, `function(x, y)`)|`fold([1..10], 0, {a, b -> a + b})`|
|`sqrt`|Square root function|(`complex`)|`sqrt(x)`|
|`cbrt`|Cube root function|(`complex`)|`cbrt(x)`|
|`exp`|Exponential function|(`complex`)|`exp(x)`|
|`log`|Log (base 10) function|(`complex`)|`log(x)`|
|`ln`|Log (base e) function|(`complex`)|`ln(x)`|
|`round` `floor` `ceil`|Rounding functions|(`complex`)|`floor(x)`|
|`sum`|Summation function|(`complex...`)|`sum(x, y, z)`|
|`product`|Product function|(`complex...`)|`product(x, y, z)`|
|`sin` `cos` `tan`<br>`csc` `sec` `cot`|Trigonomic functions|(`complex`)|`sin(x)`|
|`sinh` `cosh` `tanh`<br>`csch` `sech` `coth`|Trigonomic hyperbolic functions|(`complex`)|`sinh(x)`|
|`asin` `acos` `atan`<br>`acsc` `asec` `acot`|Inverse trigonomic functions|(`decimal`)|`asin(x)`|
|`atan2`|Inverse trigonomic<br>four-quadrant tangent function|(`decimal`,`decimal`)|`atan2(x,y)`|

### REPL Demonstrations
##### Complex Numbers
```
  REPL | sqrt(-1)
Result | i

  REPL | sqrt(i)
Result | 0.7071068 + 0.7071068i 

  REPL | (1+i)^2
Result | 2.0i

  REPL | (8+2i)*(5i+3)
Result | 14.0 + 46.0i
```
##### Slices and Lists
```
  REPL | [1,2,3][1]
Result | 2

  REPL | [1..3] + [2]
Result | [3, 4, 5]

  REPL | x = [50..60]; x[5]
Result | 55

  REPL | x = [100..200]; x[25:30]
Result | [125, 126, 127, 128, 129]

  REPL | [1..10][2:8]
Result | [3, 4, 5, 6, 7, 8]

  REPL | [1..10][2:8:-2]
Result | [8, 6, 4]
```
##### First Class Functions
```
  REPL | [1..10] |> map(_ ^ 2)
Result | [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

  REPL | add = { x, y -> x + y }; [1..10] |> fold(0, add)
Result | 55

  REPL | func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)
Result | [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

  REPL | [1..10] |> fold(1, { x, y -> x * y })
Result | 3628800
```
