/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.parser.parslets.variables;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.collections.GetOperation;
import xyz.avarel.kaiper.ast.collections.SetOperation;
import xyz.avarel.kaiper.ast.pattern.Pattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.VariablePattern;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.BindAssignmentExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

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
