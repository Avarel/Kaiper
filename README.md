
AJE
===
AJE is a math-centered scripting language/expression evaluator for the Java programming language.

### Features
|Feature|AJE Type|Java Type|Example|
|---|---|---|---:|
|Simple arithmetic|`integer`| `Integer` |`1` `42` `1+2^3` `2*(3+4)`|
|Decimals|`decimal`| `Double` |`1.235` `-2/17` `3.0+2.5`|
|Boolean logic|`boolean`| `Boolean` |`3 >= 2` `true && false`|
|Imaginary calculations|`complex`| `Double` |`i^2` `3i` `(8+2i)*(5i+3)`|
|Slices/lists operations|`slice`| `List<Any>` |`[1,2,3] == [1..3]`|
|Functions|`function`| `Function<List<Any>, Any>` |`sin(2)` `fun(x) = {x + 2}` `{x -> x^2}`|