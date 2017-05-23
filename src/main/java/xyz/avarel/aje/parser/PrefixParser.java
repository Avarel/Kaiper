package xyz.avarel.aje.parser;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.parser.lexer.Token;

public interface PrefixParser {
    Expr parse(AJEParser parser, Token token);
}