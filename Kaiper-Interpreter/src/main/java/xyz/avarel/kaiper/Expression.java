/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.ParserFlags;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;

import java.io.Reader;

/**
 * Used to preset values and functions using {@link #add}, compile and compute an Kaiper expression. All values are stored
 * in the {@link #scope}. The expression not compiled until the invocation of either {@link #compile()} or
 * {@link #compute()} explicitly; {@link KaiperException} and its derivatives would not be thrown until then.
 */
public class Expression {
    private final KaiperParser parser;
    private final Scope scope;
    private CompiledExpr expr;

    /**
     * Creates an expression based on a string. The expression uses a copy of the {@link DefaultScope default scope}
     * and its values. This constructor is a shortcut for
     * {@link Expression#Expression(String, Scope) Expression(String, DefaultScope.INSTANCE.copy())}
     *
     * @param   script
     *          The {@link String string} of Kaiper expression.
     */
    public Expression(String script) {
        this(script, DefaultScope.INSTANCE.copy());
    }

    /**
     * Creates an expression based on the character stream from a reader. The expression uses a copy of the
     * {@link DefaultScope default scope} and its values. This constructor is a shortcut for
     * {@link Expression#Expression(Reader, Scope) Expression(Reader, DefaultScope.INSTANCE.copy())}
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads Kaiper expressions.\
     */
    public Expression(Reader reader) {
        this(reader, DefaultScope.INSTANCE.copy());
    }

    /**
     * Creates an expression based on a string.
     *
     * @param   script
     *          The {@link String string} of Kaiper expression.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(String script, Scope scope) {
        this(new KaiperLexer(script), scope);
    }

    /**
     * Creates an expression based on the character stream from a reader.
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads Kaiper expressions.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(Reader reader, Scope scope) {
        this(new KaiperLexer(reader), scope);
    }

    /**
     * Creates an expression based on a stream of tokens from a lexer.
     *
     * @param   lexer
     *          The {@link KaiperLexer lexer} object that outputs Kaiper tokens.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(KaiperLexer lexer, Scope scope) {
        this.parser = new KaiperParser(lexer);
        this.scope = scope;
    }

    /**
     * Declare a variable with the name and the {@link Obj} parameter.
     *
     * @param   name
     *          The name of the variable to assign the value to.
     * @param   object
     *          The {@link Obj} value.
     * @return  The current {@link Expression} builder instance. Useful for chaining.
     */
    public Expression add(String name, Obj object) {
        scope.declare(name, object);
        return this;
    }

    /**
     * Declare a variable with the name and the {@link Obj} value from the evaluated {@link Expression}. Note that
     * the {@link Expression} parameter has its own {@link Scope} and can not access any values of the current
     * expression.
     *
     * @param   name
     *          The name of the variable to assign the value to.
     * @param   object
     *          The {@link Expression} value which is then computed as an {@link Obj}.
     * @return  The current {@link Expression} builder instance. Useful for chaining.
     */
    public Expression add(String name, Expression object) {
        scope.declare(name, object.compute());
        return this;
    }

    /**
     * Parse the expression into an AST structure of {@link Expr} nodes.
     *
     * @return  The compiled {@link Expr}.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public CompiledExpr compile() {
        if (expr == null) {
            expr = new CompiledExpr(scope, parser.compile());
        }
        return expr;
    }

    /**
     * Compile if this expression has not been already compiled and compute its value.
     *
     * @return  Returns the computed value of this expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression. Thrown by the invocation of
     *          {@link #compile()}.
     * @throws  ComputeException
     *          Error during the execution of the expression.
     */
    public Obj compute() {
        return compile().compute();
    }

    public Scope getScope() {
        return scope;
    }

    /**
     * Sets the parser flags, which controls what features of Kaiper are enabled for this expression.
     * Useful for limiting end-user's abilities to ensure that performance-expensive features are not abused.
     *
     * @param flags The flags to be set
     * @see ParserFlags
     */
    public void setParserFlags(short flags) {
        parser.setParserFlags(new ParserFlags(flags));
    }

}
