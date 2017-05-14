AJE
===
AJE is a math-centered scripting language/expression evaluator for the Java programming language.

## Features
|Feature|AJE Type|Examples|
|---|---|---:|
|Simple arithmetic|`integer`|`1` `42` `1+2^3` `2*(3+4)`|
|Decimals|`decimal`|`1.235` `-2/17` `3.0+2.5`|
|Boolean logic|`boolean`|`3 >= 2` `true && false`|
|Imaginary calculations|`complex`|`i^2` `3i` `(8+2i)*(5i+3)`|
|Slices/lists operations|`slice`|`[1,2,3] == [1..3]`|
|Functions|`function`|`sin(2)` `fun(x) = {x + 2}` `{x, y -> x^y}`|

### Usage 
API redesign in progress. Check out `AJERepl.java` for examples right now.

### Operators
##### Numeric Operators `integer` `decimal` `complex` `slice`


|Symbol|Description|Example|
|---|---|---:|
|`+`|Addition|`a + b`|
|`-`|Subtraction|`a - b`|
|`*`|Multiplication|`a * b`|
|`/`|Division|`a / b`|
|`/`|Exponentiation|`a ^ b`|
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
|`function(...args)`|Invocation|`atan2(3.6, 2.5)`|

### Functions
`numeric` arguments can be either `integer`, `decimal`, or `complex`.

`decimal` arguments can be either `integer`, or `decimal`.

|Symbol|Description|Arguments|Example|
|---|---|---|---:|
|`sqrt`|Square root function|(`numeric`)|`sqrt(x)`|
|`cbrt`|Cube root function|(`numeric`)|`cbrt(x)`|
|`exp`|Exponential function|(`numeric`)|`exp(x)`|
|`log`|Log (base 10) function|(`numeric`)|`log(x)`|
|`ln`|Log (base e) function|(`numeric`)|`ln(x)`|
|`floor`|Floor function|(`numeric`)|`floor(x)`|
|`ceil`|Ceiling function|(`numeric`)|`ceil(x)`|
|`sin`|Trigonomic sine function|(`numeric`)|`sin(x)`|
|`cos`|Trigonomic cosine function|(`numeric`)|`cos(x)`|
|`tan`|Trigonomic tangent function|(`numeric`)|`tan(x)`|
|`csc`|Trigonomic cosecant function|(`numeric`)|`csc(x)`|
|`sec`|Trigonomic secant function|(`numeric`)|`sec(x)`|
|`cot`|Trigonomic cotangent function|(`numeric`)|`cot(x)`|
|`sinh`|Trigonomic hyperbolic sine function|(`numeric`)|`sinh(x)`|
|`cosh`|Trigonomic hyperbolic cosine function|(`numeric`)|`cosh(x)`|
|`tanh`|Trigonomic hyperbolic tangent function|(`numeric`)|`tanh(x)`|
|`asin`|Inverse trigonomic sine function|(`decimal`)|`asin(x)`|
|`acos`|Inverse trigonomic cosine function|(`decimal`)|`acos(x)`|
|`atan`|Inverse trigonomic tangent function|(`decimal`)|`atan(x)`|
|`acsc`|Inverse trigonomic cosecant function|(`decimal`)|`acsc(x)`|
|`asec`|Inverse trigonomic secant function|(`decimal`)|`asec(x)`|
|`acot`|Inverse trigonomic cotangent function|(`decimal`)|`acot(x)`|
|`atan2`|Inverse trigonomic<br>four-quadrant tangent function|(`decimal`,`decimal`)|`atan2(x,y)`|
|`map`|List transform function|(`slice`, `function`)|`map([1..10], {it ^ 2})`<br>`[1..10].map({it ^ 2})`|
|`filter`|List filter function|(`slice`, `function`)|`filter([1..10], {it%2==0})`<br>`[1..10].filter({it%2==0})`|
|`fold`|List accumulation function|(`slice`, `numeric`, `function`)|`fold([1..10], 0, {a, b -> a + b})`<br>`[1..10].fold(0, {a, b -> a + b})`|

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
  REPL | [1..10].map({ it ^ 2 })
Result | [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

  REPL | add = { x, y -> x + y }; [1..10].fold(0, add)
Result | 55

  REPL | fun isEven(x) { x % 2 == 0 }; [1..20].filter(isEven)
Result | [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

  REPL | [1..10].fold(1, { x, y -> x * y })
Result | 3628800
```