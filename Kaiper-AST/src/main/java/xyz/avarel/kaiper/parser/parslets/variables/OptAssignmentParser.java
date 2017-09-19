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
import xyz.avarel.kaiper.ast.flow.ConditionalExpr;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

public class OptAssignmentParser extends BinaryParser {
    public OptAssignmentParser() {
        super(Precedence.ASSIGNMENT);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        Single value = parser.parseSingle();

        Single setOp;
        if (left instanceof Identifier) {
            setOp = new AssignmentExpr(
                    parser.getLast().getPosition(),
                    (Identifier) left,
                    value
            );

        } else if (left instanceof GetOperation) {
            setOp = new SetOperation(
                    parser.getLast().getPosition(),
                    (GetOperation) left,
                    value
            );
        } else {
            throw new SyntaxException("Invalid assignment target", left.getPosition());
        }

        return new ConditionalExpr(
                parser.getLast().getPosition(),
                new BinaryOperation(
                        left.getPosition(),
                        left,
                        NullNode.VALUE,
                        BinaryOperatorType.EQUALS),
                setOp,
                left
        );
    }
}
