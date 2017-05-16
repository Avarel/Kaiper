AJE
===
**What is AJE?** AJE is an expression evaluator for the Java programming language.
It is a dynamic and functional math-based language.

**How does it work?** AJE compiles the script into AST trees and then evaluates it.

## Features
|Feature|AJE Type|Java Type|Examples|
|---|---|---|---:|
|Simple arithmetic|`integer`|`Integer`|`1` `42` `1+2^3` `2*(3+4)`|
|Decimals|`decimal`|`Double`|`1.235` `-2/17` `3.0+2.5`|
|Boolean logic|`truth`|`Boolean`|`3 >= 2` `true && false`|
|Imaginary calculations|`complex`|`Complex*`|`i^2` `3i` `(8+2i)*(5i+3)`|
|Lists operations|`slice`|`List<Any>`|`[1,2,3] == [1..3]` `[1,2,3] + [1]`|
|First class functions|`function`|`Function*`|`fun(x) = {x + 2}` `{ (x,y) -> x^y }`|

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
##### Numeric Operators `integer` `decimal` `complex` `slice`
|Symbol|Description|Example|
|---|---|---:|
|`+`|Addition|`a + b`|
|`-`|Subtraction|`a - b`|
|`*`|Multiplication|`a * b`|
|`/`|Division|`a / b`|
|`^`|Exponentiation|`a ^ b`|
|`%`|Modulus|`a % b`|
|`-`|Negation|`-a`|

##### Boolean and Relational Operators `boolean`
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

##### Functional Operators `function`
|Symbol|Description|Example|
|---|---|---:|
|`function(args...)`|Invocation|`atan2(3.6, 2.5)`|
|<code>&#124;></code>|Pipe forward as first argument|<code>5 &#124;> sin() &#124;> cos()</code>|
|`+`|Sum of functions|`(f+g)(x) == f(x) + g(x)`|
|`-`|Difference of functions|`(f-g)(x) == f(x) - g(x)`|
|`*`|Product of functions|`(f*g)(x) == f(x) * g(x)`|
|`/`|Quotient of functions|`(f/g)(x) == f(x) / g(x)`|

### Functions
`complex` arguments can be either `integer`, `decimal`, or `complex`.

`decimal` arguments can be either `integer`, or `decimal`.

|Symbol|Description|Arguments|Example|
|---|---|---|---:|
|`sqrt`|Square root function|(`complex`)|`sqrt(x)`|
|`cbrt`|Cube root function|(`complex`)|`cbrt(x)`|
|`exp`|Exponential function|(`complex`)|`exp(x)`|
|`log`|Log (base 10) function|(`complex`)|`log(x)`|
|`ln`|Log (base e) function|(`complex`)|`ln(x)`|
|`floor`|Floor function|(`complex`)|`floor(x)`|
|`ceil`|Ceiling function|(`complex`)|`ceil(x)`|
|`sum`|Summation function|(`complex...`)|`sum(x, y, z)`|
|`product`|Product function|(`complex...`)|`product(x, y, z)`|
|`sin`|Trigonomic sine function|(`complex`)|`sin(x)`|
|`cos`|Trigonomic cosine function|(`complex`)|`cos(x)`|
|`tan`|Trigonomic tangent function|(`complex`)|`tan(x)`|
|`csc`|Trigonomic cosecant function|(`complex`)|`csc(x)`|
|`sec`|Trigonomic secant function|(`complex`)|`sec(x)`|
|`cot`|Trigonomic cotangent function|(`complex`)|`cot(x)`|
|`sinh`|Trigonomic hyperbolic sine function|(`complex`)|`sinh(x)`|
|`cosh`|Trigonomic hyperbolic cosine function|(`complex`)|`cosh(x)`|
|`tanh`|Trigonomic hyperbolic tangent function|(`complex`)|`tanh(x)`|
|`csch`|Trigonomic hyperbolic cosecant function|(`complex`)|`csch(x)`|
|`sech`|Trigonomic hyperbolic secant function|(`complex`)|`sech(x)`|
|`coth`|Trigonomic hyperbolic cotangent function|(`complex`)|`coth(x)`|
|`asin`|Inverse trigonomic sine function|(`decimal`)|`asin(x)`|
|`acos`|Inverse trigonomic cosine function|(`decimal`)|`acos(x)`|
|`atan`|Inverse trigonomic tangent function|(`decimal`)|`atan(x)`|
|`acsc`|Inverse trigonomic cosecant function|(`decimal`)|`acsc(x)`|
|`asec`|Inverse trigonomic secant function|(`decimal`)|`asec(x)`|
|`acot`|Inverse trigonomic cotangent function|(`decimal`)|`acot(x)`|
|`atan2`|Inverse trigonomic<br>four-quadrant tangent function|(`decimal`,`decimal`)|`atan2(x,y)`|
|`map`|List transform function|(`slice`, `function`)|`map([1..10], {it ^ 2})`|
|`filter`|List filter function|(`slice`, `function`)|`filter([1..10], {it%2==0})`|
|`fold`|List accumulation function|(`slice`, `value`, `function`)|`fold([1..10], 0, {a, b -> a + b})`|

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

  REPL | x = [100..200]; x[25,50,75]
Result | [125, 150, 175]
```
##### First Class Functions
```
  REPL | [1..10] |> map({ it ^ 2 })
Result | [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

  REPL | add = { (x, y) -> x + y }; [1..10] |> fold(0, add)
Result | 55

  REPL | fun isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)
Result | [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

  REPL | [1..10] |> fold(1, { (x, y) -> x * y })
Result | 3628800
```