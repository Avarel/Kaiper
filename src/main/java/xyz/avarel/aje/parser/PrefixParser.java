package xyz.avarel.aje.parser;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.pool.ObjectPool;

public interface PrefixParser {
    Expr parse(AJEParser parser, ObjectPool pool, Token token);
}