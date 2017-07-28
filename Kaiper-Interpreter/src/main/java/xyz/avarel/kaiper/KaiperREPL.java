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
import xyz.avarel.kaiper.exceptions.*;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;

import java.io.*;

/**
 * Used to programmatically evaluate Kaiper scripts in a read-eval-print-loop manner. Each call to {@link #eval} will
 * affect the state of the evaluator and the following calls. All of the changes to the evaluator are accumulated in the
 * {@link #scope} field.
 */
public class KaiperREPL {
    private final ExprInterpreter visitor;
    private final Scope scope;
    private Obj answer;

    /**
     * Creates a new Evaluator instantiated with default values and functions copied from {@link DefaultScope}.
     */
    public KaiperREPL() {
        this(DefaultScope.INSTANCE.copy());
    }

    /**
     * Creates a new Evaluator instantiated with default values and functions copied from {@code scope}.
     *
     * @param   scope
     *          The initial {@link Scope} values to copy from.
     */
    public KaiperREPL(Scope scope) {
        this.scope = scope;
        this.visitor = new ExprInterpreter();
        this.answer = Null.VALUE;
    }

    /**
     * Creates a sub-evaluator that can affect the parent evaluator, but any declared variables/functions will not
     * affect the parent evaluator.
     *
     * @param   parent
     *          The parent {@link KaiperREPL}.
     */
    public KaiperREPL(KaiperREPL parent) {
        this(parent.scope.subPool());
    }

    /**
     * Evaluates a string.
     * This method changes the state of the scope.
     *
     * @param   script
     *          The {@link String string} of Kaiper expressions.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(String script) {
        return eval(new StringReader(script));
    }

    /**
     * Evaluates a file.
     * This method changes the state of the scope.
     *
     * @param   file
     *          The {@link File file} containing Kaiper expressions.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(File file) {
        try {
            return eval(new FileReader(file));
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return answer = Null.VALUE;
        }
    }

    /**
     * Evaluates the script read by a reader.
     * This method changes the state of the scope.
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads Kaiper expressions.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(Reader reader) {
        return eval(new KaiperLexer(reader));
    }

    /**
     * Evaluates the script object.
     * This method changes the state of the scope.
     *
     * @param   script
     *          The {@link KaiperScript Kaiper script} object.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(KaiperScript script) {
        return eval(new KaiperScript(script.getParser(), getScope().combine(scope)).compile());
    }

    /**
     * Evaluates a stream of tokens from a lexer.
     * This method changes the state of the scope.
     *
     * @param   lexer
     *          The {@link KaiperLexer lexer} object that outputs Kaiper tokens.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(KaiperLexer lexer) {
        return eval(new KaiperParser(lexer).parse());
    }

    // comment
    /**
     * Evaluates an AST node.
     * This method changes the state of the scope.
     *
     * @param   expr
     *          The {@link Expr expression} object.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(Expr expr) {
        try {
            visitor.resetTimeout();
            return answer = expr.accept(visitor, scope);
        } catch (ReturnException re) {
            return answer = re.getValue();
        } catch (ComputeException re) {
            throw new InterpreterException(re.getMessage(), expr.getPosition());
        } catch (KaiperException re) {
            throw re;
        } catch (RuntimeException re) {
            throw new InterpreterException(re);
        }
    }

    /**
     * @return  The last {@link Obj} answer evaluated by this evaluator.
     */
    public Obj getAnswer() {
        return answer;
    }

    /**
     * @return  The current {@link Scope} of the evaluator.
     */
    public Scope getScope() {
        return scope;
    }
}
