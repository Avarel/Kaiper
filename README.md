[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5549252b44484f4fa7cf892db272c5da)](https://www.codacy.com/app/Avarel/AJE?utm_source=github.com&utm_medium=referral&utm_content=Avarel/AJE&utm_campaign=badger)
AJE [![Download](https://api.bintray.com/packages/avarel/maven/AJE/images/download.svg)](https://bintray.com/avarel/maven/AJE/_latestVersion) [![Build Status](https://travis-ci.org/Avarel/AJE.svg?branch=master)](https://travis-ci.org/Avarel/AJE)
===
**What is AJE?** AJE is an easy powerful mathematical evaluator for the Java programming language. It has built-in
    support for complex numbers, list literals, functions expressions and other programmatic quirks. 
    The parser support expressions provided from texts, readers, or input streams.
    
AJE was built as a challenge to evaluate programming constructs that can be operated on like
    mathematical values. The result of which was that AJE can support natural mathematical syntax
    such as implicit multiplication (ie. `2pi` `(2)(3)`), built-in syntax for writing complex numbers
    as if they were on paper (ie. `2 + 5i`) while providing variable and function declarations that
    are reminiscent of several languages that inspired AJE's syntax.

### REPL Demonstrations
Try evaluating AJE expressions by running the [`AJERepl.java`](/src/test/java/xyz/avarel/aje/loops/AJERepl.java)
    main method.

#### Complex Numbers
```
▶ sqrt(-1)
◀ i : complex

▶ sqrt(i)
◀ 0.7071068 + 0.7071068i : complex

▶ (1+i)^2
◀ 2.0i : complex

▶ (8+2i)(5i+3)
◀ 14.0 + 46.0i : complex
```

#### Vectors
```
▶ [1,2,3][1]
◀ 2 : integer

▶ [1..3] + 2
◀ [3, 4, 5] : vector

▶ var x = [50..60]; x[::-1]
◀ [60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50] : vector

▶ var x = [100..200]; x[25:30]
◀ [125, 126, 127, 128, 129] : vector

▶ [1..10][2:8]
◀ [3, 4, 5, 6, 7, 8] : vector

▶ [1..10][2:8:-2]
◀ [8, 6, 4] : vector
```

#### Flow Control
```
▶ var x = 50
◀ undefined : undefined

▶ return if (x > 10) i else pi
◀ i : complex

▶ func fib(n: Int) = return if (n <= 1) n else fib(n - 1) + fib(n - 2)
◀ func(n: Int) : function

▶ fib(14)
◀ 377 : integer

▶ var a = 0
◀ 0 : integer

▶ for (n in 1..10) { a += n }
◀ undefined : undefined

▶ a
◀ 55 : integer
```

#### First Class Functions
```
▶ [1..10] |> map(_ ^ 2)
◀ [1, 4, 9, 16, 25, 36, 49, 64, 81, 100] : vector

▶ var add = { x, y -> x + y }; [1..10] |> fold(0, add)
◀ 55 : integer

▶ func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)
◀ [2, 4, 6, 8, 10, 12, 14, 16, 18, 20] : vector

▶ [1..10] |> fold(1, _ * __)
◀ 3628800 : integer

▶ ["h", "e", "l", "l", "o"].map("hello".indexOf)
◀ [0, 1, 2, 2, 4] : Vector
```

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
|Feature| | |Examples|
|---|---|---|---:|
|Integer numbers|`integer`|`Integer`|`1` `42` `1+2^3` `2*(3+4)`|
|Decimal numbers|`decimal`|`Double`|`1.235` `-2.0/17` `3.0+2.5`|
|Complex numbers|`complex`| |`i^2` `3i` `(8+2i)(5i+3)`|
|Boolean logic|`truth`|`Boolean`|`3 >= 2` `true && false`|
|Ranges|`range`|`List<Integer>`|`1..10` `10..<1`|
|Vectors|`vector`|`List<Obj>`|`[1,2,3] == [1..3]` `[1,2,3] + [1]`|
|Dictionaries|`dictionary`|`Map<Object, Object>`|`[:]` `["hello":"there"]`|
|Strings|`string`|`String`|`"Hello there!`|
|Functions (first-class)|`function`| |`func(x) { x + 2 }` `{ x, y -> x ^ y }`|
|Flow control| | |`if (true) { 1 } else { 2 }`<br>`return 2`|

### Usage 
```java
import xyz.avarel.aje.Expression;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.List;

class AJETest {
    public static void main(String[] args) {
        // Base expression.
        Expression exp = new Expression("sum(tau,2,i)");

        // Add a constant.
        exp.add("tau", new Expression("2 * pi"));

        // Add a normal function.
        exp.add("double", new NativeFunction(Numeric.TYPE) {
            @Override
            protected Obj eval(List<Obj> arguments) {
                return arguments.get(0).times(2);
            }
        });

        // Add a varargs function.
        exp.add("sum", new NativeFunction(true, Numeric.TYPE) {
            @Override
            protected Obj eval(List<Obj> arguments) {
                if (arguments.isEmpty()) return Int.of(0);
                Obj accumulator = arguments.get(0);
                for (int i = 1; i < arguments.size(); i++) {
                    accumulator = accumulator.plus(arguments.get(i));
                }
                return accumulator;
            }
        });

        // Calculate into AJE object.
        Obj result = exp.compute();

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
Though ranges are of a different prototype, `range`, it is recommended that 
    ranges are wrapped into a vector using `[range]` syntax. 

|Symbol|Description|Example|
|---|---|---:|
|`..`|Range creation -`integer` only|`a..b`|
|`[item, items...]`|List creation|`[a,b,c,d,e,f,g]`|
|`[index]`|Get item at index - negative indexes also work.|`list[i]`|
|`[start:end:step]`|Slice operation from Python.|`list[::]` `list[1:-3:]` `list[3:5:2]`|

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
##### Declaration and Assignment
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
**Note:** Due to AJE having built in support for complex numbers, the identifier `i` is a globally assigned
    variable. If a variable is declared with the name of `i`, complex number notation will be unavailable for
    that scope.

##### Optional Assignment
There is an optional assignment operator that only assigns a value to a property if it is undefined.
    This works with variables, vectors and dictionaries.
```
name ?= expression

x ?= 3
list[2] ?= "there"

map["hello"] = 2
map["hello"] ?= "there" // evaluates to 2 instead of "there"
```

##### Compound Assignment
Variables can use the short syntax-sugar compound assignments. They provide a shorter syntax to perform
    the operation of the two operands and assign them to the first. Ex: `+=` `-=` `*=`
```
name (binary operator)= expression

x += 5
y %= 2.0
```

### Defining Functions
Functions are first class objects that can be invoked using the __invocation operator__.

`complex` arguments can be either `integer`, `decimal`, or `complex`.

`decimal` arguments can be either `integer`, or `decimal`.

#### Declaring a Function
##### Traditional `func f(it) { it + 1 }`
Traditional functions can be declared with the syntax shown below.
    If the optional name field is present, then the function will be 
    available to used as an invocable variable with that name. If the 
    name field is not present, the function be an anonymous function expression.
```
func [name]([param,...]) { [statements] }
func [name]([param,...]) = expression

func f(x) = x + 2; f(2) == 4
func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)
```
###### Parameter Types and Defaults
Functions can declare parameters with runtime prototype checking by appending the 
    prototype name to the parameter name. Parameters can also specify default 
    expressions that are evaluated at invocation.
```
func f(x = 0, y: Int = 2) {
    x + y
}

[f(3), f(i), f(3,2), f(), f(true)] == [5, 2.0 + 1.0i, 5, 2, undefined]
```

##### Lambda `{ it -> it + 1 }`
Alternatively, functions can also be declared using the following syntax.
    These functions can be passed into arguments and used as variables.
    They are anonymous but variables can be set to this function, of which 
    they can be then invoked by the user.
```
{ [param,...] -> [statements] }

add = { x, y -> x + y }; [1..10] |> fold(0, add) == 55
[1..10] |> fold(1, { x, y -> x * y })
```
    
##### Implicits `_ + 1`
These functions are basically anonymous alternatives that uses a shorter syntax.
    This allows functions to be used in higher-order functions in a concise manner.
    
The implicit parameters are based on the underscore length, for example: `_ + _` will
    reference the same implicit parameter and add them together, much like `func(a) = a + a`.
    However, adding more consecutive underscores will allow for more complex implicits, such as
    `_ + __ * ___` which translate into `func(a, b, c) = a + b * c`.

```
_ ...expression

[[1, 2, 3], [1, 5, 8, 9, 10], [1..50]] |> map(_.size)
map([1..10], _ ^ 2) == [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
```

#### Native Functions
These are functions that are built into AJE. They include both higher-order and simple/multi-argument functions.

|Symbol|Description|Arguments|Example|
|---|---|---|---:|
|`compose`|Create a composition of two functions|(`function(x)`,`function(x)`)|`compose(asin, sin)`|
|`each`|List iteration action function|(`list`, `function(x)`)|<code>var x = 0;<br>[0..9] &#124;> each {it-> x += it }</code>|
|`map`|List transform function|(`list`, `function(x)`)|`map([1..10], _ ^ 2)`|
|`filter`|List filter function|(`list`, `function(x)`)|`filter([1..10], _ % 2 == 0)`|
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