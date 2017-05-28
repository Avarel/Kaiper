package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

import java.util.Collections;

public class PipeForwardParser extends BinaryParser {
    public PipeForwardParser() {
        super(Precedence.PIPE_FORWARD);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence());

        if (right instanceof InvocationExpr) {
            ((InvocationExpr) right).getArguments().add(0, left);
            return right;
        } else if (right instanceof FunctionAtom || right instanceof NameAtom) {
            return new InvocationExpr(right, Collections.singletonList(left));
        }

        throw parser.error("Pipe-forward requires the right operand to be either: invocation, function, or name.");
    }
}
