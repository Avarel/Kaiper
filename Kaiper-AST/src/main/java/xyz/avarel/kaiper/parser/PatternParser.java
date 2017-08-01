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
    public static PatternSet parsePatternSet(KaiperParser parser) {
        List<Pattern> patterns = new ArrayList<>();

        do {
            patterns.add(parsePattern(parser));
        } while (parser.match(TokenType.COMMA));

        return new PatternSet(patterns);
    }

    private static Pattern parsePattern(KaiperParser parser) {
        Pattern basePattern;

        if (parser.nextIs(TokenType.IDENTIFIER)) {
            Identifier id = parser.parseIdentifier();

            if (parser.match(TokenType.COLON)) {
                Pattern pattern = parseSimplePattern(parser);
                basePattern = new TuplePattern(id.getPosition(), id.getName(), pattern);
            } else if (parser.match(TokenType.IS)) {
                Single type = parser.parseSingle();
                Pattern pattern = new TypePattern(type.getPosition(), type);
                basePattern = new VariablePattern(id.getPosition(), id.getName(), pattern);
            } else {
                basePattern = new VariablePattern(id.getPosition(), id.getName(), null);
            }
        } else {
            basePattern = parseSimplePattern(parser);
        }

        if (parser.match(TokenType.ASSIGN)) {
            Single def = parser.parseSingle(Precedence.TUPLE_PAIR);
            basePattern = new DefaultPattern(parser.getLast().getPosition(), basePattern, def);
        }

        return basePattern;
    }

    private static Pattern parseSimplePattern(KaiperParser parser) {
        if (parser.nextIsAny(
                TokenType.INT, TokenType.NUMBER, TokenType.STRING,
                TokenType.NULL, TokenType.BOOLEAN, TokenType.LEFT_BRACKET
        ) || parser.match(TokenType.EQUALS)) {
            Single value = parser.parseSingle(Precedence.TUPLE);
            return new ValuePattern(value.getPosition(), value);
        } else if (parser.match(TokenType.IS)) {
            Single type = parser.parseSingle(Precedence.TUPLE);
            return new TypePattern(type.getPosition(), type);
        } else if (parser.match(TokenType.UNDERSCORE)) {
            return WildcardPattern.INSTANCE;
        } else {
            Token unexpected = parser.peek(0);
            throw new SyntaxException("Unexpected pattern " + unexpected.getType(), unexpected.getPosition());
        }
    }

    private static SyntaxException mixException(KaiperParser parser) {
        return new SyntaxException("Can not mix positional and named patterns", parser.peek(0).getPosition());
    }
}
