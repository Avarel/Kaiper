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

package xyz.avarel.aje;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.flow.ReturnException;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.exceptions.AJEException;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.ParserFlags;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

import java.io.Reader;

/**
 * Used to preset values and functions using {@link #add}, compile and compute an AJE expression. All values are stored
 * in the {@link #scope}. The expression not compiled until the invocation of either {@link #compile()} or
 * {@link #compute()} explicitly; {@link AJEException} and its derivatives would not be thrown until then.
 */
public class Expression {
    private final AJEParser parser;
    private final Scope scope;
    private Expr expr;

    /**
     * Creates an expression based on a string. The expression uses a copy of the {@link DefaultScope default scope}
     * and its values. This constructor is a shortcut for
     * {@link Expression#Expression(String, Scope) Expression(String, DefaultScope.INSTANCE.copy())}
     *
     * @param   script
     *          The {@link String string} of AJE expression.
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
     *          The {@link Reader reader} instance that reads AJE expressions.\
     */
    public Expression(Reader reader) {
        this(reader, DefaultScope.INSTANCE.copy());
    }

    /**
     * Creates an expression based on a string.
     *
     * @param   script
     *          The {@link String string} of AJE expression.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(String script, Scope scope) {
        this(new AJELexer(script), scope);
    }

    /**
     * Creates an expression based on the character stream from a reader.
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads AJE expressions.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(Reader reader, Scope scope) {
        this(new AJELexer(reader), scope);
    }

    /**
     * Creates an expression based on a stream of tokens from a lexer.
     *
     * @param   lexer
     *          The {@link AJELexer lexer} object that outputs AJE tokens.
     * @param   scope
     *          The {@link Scope scope} to evaluate the expression from.
     */
    public Expression(AJELexer lexer, Scope scope) {
        this.parser = new AJEParser(lexer);
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
    public Expr compile() {
        if (expr == null) {
            expr = new ExpressionExpr(parser.compile());
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

    /**
     * Sets the parser flags, which controls what features of AJE are enabled for this expression.
     * Useful for limiting end-user's abilities to ensure that performance-expensive features are not abused.
     *
     * @see ParserFlags
     */
    public void setParserFlags(int flags) {
        parser.setParserFlags(new ParserFlags(flags));
    }

    private class ExpressionExpr implements Expr {
        private final Expr expr;

        public ExpressionExpr(Expr expr) {
            this.expr = expr;
        }

        @Override
        public Expr andThen(Expr after) {
            if (expr instanceof Statements) {
                ((Statements) expr).getExprs().add(after);
                return this;
            }
            return new ExpressionExpr(new Statements(expr, after));
        }

        @Override
        public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
            try {
                return expr.accept(visitor, scope);
            } catch (ReturnException re) {
                // hope for the best
                return (R) re.getValue();
            } catch (AJEException re) {
                throw re;
            } catch (RuntimeException re) {
                throw new ComputeException(re);
            }
        }

        @Override
        public Obj compute() {
            return accept(new ExprInterpreter(), scope.copy());
        }

        @Override
        public void ast(StringBuilder builder, String indent, boolean isTail) {
            expr.ast(builder, indent, isTail);
        }
    }
}
