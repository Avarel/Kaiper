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

import xyz.avarel.aje.ast.flow.ReturnException;
import xyz.avarel.aje.exceptions.AJEException;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.lexer.AJELexer;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

import java.io.*;

/**
 * Used to programmatically evaluate AJE scripts in a read-eval-print-loop manner. Each call to {@link #eval} will
 * affect the state of the evaluator and the following calls. All of the changes to the evaluator are accumulated in the
 * {@link #scope} field.
 */
public class Evaluator {
    private final ExprInterpreter visitor;
    private final Scope scope;
    private Obj answer;

    /**
     * Creates a new Evaluator instantiated with default values and functions copied from {@link DefaultScope}.
     */
    public Evaluator() {
        this(DefaultScope.INSTANCE.copy());
    }

    /**
     * Creates a new Evaluator instantiated with default values and functions copied from {@code scope}.
     *
     * @param   scope
     *          The initial {@link Scope} values to copy from.
     */
    public Evaluator(Scope scope) {
        this.scope = scope;
        this.visitor = new ExprInterpreter();
        this.answer = Undefined.VALUE;
    }

    /**
     * Creates a sub-evaluator that can affect the parent evaluator, but any declared variables/functions will not
     * affect the parent evaluator.
     *
     * @param   parent
     *          The parent {@link Evaluator}.
     */
    public Evaluator(Evaluator parent) {
        this(parent.scope.subPool());
    }

    /**
     * Evaluates a string.
     * This method changes the state of the scope.
     *
     * @param   script
     *          The {@link String string} of AJE expressions.
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
     *          The {@link File file} containing AJE expressions.
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
            return answer = Undefined.VALUE;
        }
    }

    /**
     * Evaluates the script read by a reader.
     * This method changes the state of the scope.
     *
     * @param   reader
     *          The {@link Reader reader} instance that reads AJE expressions.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(Reader reader) {
        return eval(new AJELexer(reader));
    }

    /**
     * Evaluates a stream of tokens from a lexer. This method changes the state of the scope.
     *
     * @param   lexer
     *          The {@link AJELexer lexer} object that outputs AJE tokens.
     * @return  The {@link Obj} answer to the script.
     *
     * @throws  ComputeException
     *          Error during the execution of the expression.
     * @throws  SyntaxException
     *          Error during the lexing or parsing process of the expression.
     */
    public Obj eval(AJELexer lexer) {
        try {
            visitor.resetTimeout();
            return answer = new AJEParser(lexer).compile().accept(visitor, scope);
        } catch (ReturnException re) {
            return answer = re.getValue();
        } catch (AJEException re) {
            re.printStackTrace();
        } catch (RuntimeException re) {
            new ComputeException(re).printStackTrace();
        }

        return answer = Undefined.VALUE;
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
