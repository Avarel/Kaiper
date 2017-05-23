package xyz.avarel.aje.parser;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.parser.lexer.Token;

public interface InfixParser {
    Expr parse(AJEParser parser, Expr left, Token token);

    int getPrecedence();
}