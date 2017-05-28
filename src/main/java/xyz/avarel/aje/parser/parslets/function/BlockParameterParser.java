package xyz.avarel.aje.parser.parslets.function;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockParameterParser extends BinaryParser {
    public BlockParameterParser() {
        super(Precedence.INVOCATION);
    }

    @Override // [1..10] |> fold(0) { a, b -> a + b }
    public Expr parse(AJEParser parser, Expr left, Token token) { // [1..10] |> filter { it -> it % 2 == 0 }
        Expr block = parser.getPrefixParsers().get(token.getType()).parse(parser, token);

        if (left instanceof InvocationExpr) {
            ((InvocationExpr) left).getArguments().add(block);
        } else if (left instanceof FunctionAtom || left instanceof NameAtom) {
            List<Expr> args = new ArrayList<>();
            args.add(block);
            return new InvocationExpr(left, args);
        }

        throw parser.error("Block-pass requires the left operand to be either: invocation, function, or name.");
    }
}
