package xyz.avarel.kaiper.parser.parslets.variables;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.collections.GetOperation;
import xyz.avarel.kaiper.ast.collections.SetOperation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.BindAssignmentExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.pattern.Pattern;
import xyz.avarel.kaiper.pattern.PatternCase;
import xyz.avarel.kaiper.pattern.VariablePattern;

import java.util.ArrayList;
import java.util.List;

public class AssignmentParser extends BinaryParser {
    public AssignmentParser() {
        super(Precedence.ASSIGNMENT);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        Single value = parser.parseSingle();

        if (left instanceof Identifier) {
            return new AssignmentExpr(
                    token.getPosition(),
                    (Identifier) left,
                    value
            );
        } else if (left instanceof GetOperation) {
            return new SetOperation(
                    token.getPosition(),
                    (GetOperation) left,
                    value
            );
        } else if (left instanceof TupleExpr) {
            TupleExpr tupleExpr = (TupleExpr) left;

            if (!tupleExpr.getNamedElements().isEmpty() || tupleExpr.getUnnamedElements().isEmpty()) {
                throw new SyntaxException("Invalid assignment target", left.getPosition());
            }

            List<Pattern> patterns = new ArrayList<>();

            for (Single expr : tupleExpr.getUnnamedElements()) {
                if (expr instanceof Identifier) {
                    Identifier identifier = (Identifier) expr;
                    patterns.add(new VariablePattern(identifier.getName()));
                } else {
                    throw new SyntaxException("Invalid assignment target", left.getPosition());
                }
            }

            PatternCase patternCase = new PatternCase(patterns);

            return new BindAssignmentExpr(
                    token.getPosition(),
                    patternCase,
                    value
            );
        } else {
            throw new SyntaxException("Invalid assignment target", left.getPosition());
        }
    }
}
