package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PatternParser {
    private final KaiperParser parser;
    private boolean requireName;

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

            Identifier id = parser.parseIdentifier();

            if (parser.match(TokenType.COLON)) {
                Pattern pattern = parseSimplePattern();
                basePattern = new TuplePattern(id.getPosition(), id.getName(), pattern);
            } else {
                basePattern = new VariablePattern(id.getPosition(), id.getName());
            }

            if (parser.match(TokenType.ASSIGN)) {
                Single def = parser.parseSingle(Precedence.TUPLE_PAIR);
                basePattern = new DefaultPattern(parser.getLast().getPosition(), (NamedPattern) basePattern, def);
            }
        } else {
            if (requireName) {
                throw mixException();
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
        } else {
            Token unexpected = parser.peek(0);
            throw new SyntaxException("Unexpected pattern " + unexpected.getType(), unexpected.getPosition());
        }
    }

    private SyntaxException mixException() {
        return new SyntaxException("Can not mix positional and named patterns", parser.peek(0).getPosition());
    }
}
