
AJE
===
AJE is a math-centered scripting language/expression evaluator for the Java programming language.

### Features
|Feature|AJE Type|Example|
|---|---|---:|
|Simple arithmetic|`integer`|`1` `42` `1+2^3` `2*(3+4)`|
|Decimals|`decimal`|`1.235` `-2/17` `3.0+2.5`|
|Boolean logic|`boolean`|`3 >= 2` `true && false`|
|Imaginary calculations|`complex`|`i^2` `3i` `(8+2i)*(5i+3)`|
|Slices/lists operations|`slice`|`[1,2,3] == [1..3]`|
|Functions|`function`|`sin(2)` `fun(x) = {x + 2}` `{x, y -> x^y}`|

#### Complex Numbers
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

#### Slices and Lists
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
#### First Class Functions
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