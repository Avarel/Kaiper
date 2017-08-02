package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PatternParser {
    private final KaiperParser parser;
    private boolean requireName;
    private boolean requireDef;

    public PatternParser(KaiperParser parser) {
        this.parser = parser;
    }

    public PatternCase parsePatternSet() {
        List<Pattern> patterns = new ArrayList<>();

        do {
            patterns.add(parsePattern());
        } while (parser.match(TokenType.COMMA));

        return new PatternCase(patterns);
    }

    private Pattern parsePattern() {
        Pattern basePattern;

        if (parser.nextIs(TokenType.IDENTIFIER)) {
            requireName = true;
            Token id = parser.eat(TokenType.IDENTIFIER);
            String name = id.getString();

            if (parser.match(TokenType.COLON)) {
                Pattern pattern;
                if (parser.nextIs(TokenType.IDENTIFIER)) {
                    Token innerId = parser.eat(TokenType.IDENTIFIER);
                    String innerName = innerId.getString();
                    pattern = new VariablePattern(innerId.getPosition(), innerName);
                } else {
                    pattern = parseSimplePattern();
                }

                basePattern = new TuplePattern(id.getPosition(), name, pattern);
            } else {
                basePattern = new VariablePattern(id.getPosition(), name);
            }

            if (parser.match(TokenType.ASSIGN)) {
                requireDef = true;

                Single def = parser.parseSingle(Precedence.TUPLE_PAIR);
                basePattern = new DefaultPattern(parser.getLast().getPosition(), (NamedPattern) basePattern, def);
            } else if (requireDef) {
                throw new SyntaxException("All parameters after the first default requires a default",
                        parser.peek(0).getPosition());
            }
        } else if (parser.match(TokenType.REST)) {
            requireName = true;
            Token id = parser.eat(TokenType.IDENTIFIER);
            String name = id.getString();

            if (parser.match(TokenType.COMMA)) {
                throw new SyntaxException("Rest parameters must be the last parameter", parser.peek(0).getPosition());
            }

            basePattern = new RestPattern(id.getPosition(), name);
        } else {
            if (requireName) {
                throw new SyntaxException("Can not mix positional and named patterns", parser.peek(0).getPosition());
            }
            basePattern = parseSimplePattern();
        }

        return basePattern;
    }

    private Pattern parseSimplePattern() {
        if (parser.nextIsAny(
                TokenType.INT, TokenType.NUMBER, TokenType.STRING,
                TokenType.NULL, TokenType.BOOLEAN, TokenType.LEFT_BRACKET
        ) || parser.match(TokenType.EQUALS)) {
            Single value = parser.parseSingle(Precedence.TUPLE);
            return new ValuePattern(value.getPosition(), value);
        } else if (parser.match(TokenType.UNDERSCORE)) {
            return WildcardPattern.INSTANCE;
        } else if (parser.match(TokenType.LEFT_PAREN)) {
            PatternCase nested = parsePatternSet();
            parser.eat(TokenType.RIGHT_PAREN);
            return nested;
        } else {
            Token unexpected = parser.peek(0);
            throw new SyntaxException("Unexpected pattern " + unexpected.getType(), unexpected.getPosition());
        }
    }
}
