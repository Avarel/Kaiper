/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.lib.std.DefaultScope;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.io.Reader;

/**
 * Used to compile and compute an Kaiper expression.
 */
public class KaiperScript {
    private final Expr expr;
    private ExprInterpreter interpreter;
    private Scope<String, Obj> scope;

    /**
     * Creates a script based on a string. The expression uses a copy of the {@link DefaultScope default scope}
     * and its values.
     *
     * @param   script
     *          The {@link String string} of Kaiper expression.
     */
    public KaiperScript(String script) {
        this(new ExprParser(new KaiperLexer(script)), null, null);
    }

    /**
     * Creates a script based on the character stream from a reader. The expression uses a copy of the
     * {@link DefaultScope default scope} and its values.
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads Kaiper expressions.\
     */
    public KaiperScript(Reader reader) {
        this(new ExprParser(new KaiperLexer(reader)), null, null);
    }

    /**
     * Creates a script based on a stream of tokens from a lexer and parser.
     *
     * @param   parser
     *          The {@link ExprParser parser} object that parses Kaiper scripts.
     * @param   interpreter
     *          The {@link ExprInterpreter interpreter}.
     * @param   scope
     *          The {@link Scope scope} to evaluate the script from.
     */
    public KaiperScript(ExprParser parser, ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this(parser.parse(), interpreter, scope);
    }

    /**
     * Creates a script based an AST structure.
     *
     * @param   expr
     *          The {@link Expr AST} structure.
     * @param   interpreter
     *          The {@link ExprInterpreter interpreter}.
     * @param   scope
     *          The {@link Scope scope} to evaluate the script from.
     */
    public KaiperScript(Expr expr, ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this.expr = expr;
        this.interpreter = interpreter;
        this.scope = scope;
    }

    /**
     * Declare a variable with the name and the {@link Obj} parameter.
     *
     * @param   name
     *          The name of the variable to assign the value to.
     * @param   object
     *          The {@link Obj} value.
     * @return  The current {@link KaiperScript} builder instance. Useful for chaining.
     */
    public KaiperScript add(String name, Obj object) {
        scope.put(name, object);
        return this;
    }

    /**
     * Declare a variable with the name and the {@link Obj} value from the evaluated {@link KaiperScript}. Note that
     * the {@link KaiperScript} parameter has its own {@link Scope} and can not access any values of the current
     * expression.
     *
     * @param   name
     *          The name of the variable to assign the value to.
     * @param   script
     *          The {@link KaiperScript} value which is then computed as an {@link Obj}.
     * @return  The current {@link KaiperScript} builder instance. Useful for chaining.
     */
    public KaiperScript add(String name, KaiperScript script) {
        scope.put(name, new KaiperScript(script.expr, null, script.scope.copyWithParent(scope)).compute());
        return this;
    }

    public ExprInterpreter getInterpreter() {
        if (interpreter == null) this.interpreter = new ExprInterpreter();
        return interpreter;
    }

    public Scope<String, Obj> getScope() {
        if (scope == null) scope = new DefaultScope(interpreter);
        return scope;
    }

    /**
     * Compile if this expression has not been already compiled and compute its value.
     *
     * @return  Returns the computed value of this expression.

     * @throws  ComputeException
     *          Error during the execution of the expression.
     */
    public Obj compute() {
        try {
            return expr.accept(getInterpreter(), getScope());
        } catch (ReturnException re) {
            return re.getValue();
        } catch (KaiperException re) {
            throw re;
        } catch (RuntimeException re) {
            throw new ComputeException(re);
        }
    }
}
