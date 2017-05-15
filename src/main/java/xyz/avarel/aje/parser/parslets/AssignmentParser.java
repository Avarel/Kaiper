package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Variable;

public class AssignmentParser extends BinaryParser<Variable, Any> {
    public AssignmentParser() {
        super(Precedence.ASSIGNMENT, true, true);
    }

    @Override
    public Any parse(AJEParser parser, Variable left, Token token) {
        Any right = parser.parse(getPrecedence() - (isLeftAssoc() ? 0 : 1));
        return left.set(right);
    }
}