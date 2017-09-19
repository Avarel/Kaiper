///*
// * Licensed under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//package xyz.avarel.kaiper.parser.parslets.tuples;
//
//import xyz.avarel.kaiper.Precedence;
//import xyz.avarel.kaiper.ast.Expr;
//import xyz.avarel.kaiper.ast.Single;
//import xyz.avarel.kaiper.ast.tuples.TupleExpr;
//import xyz.avarel.kaiper.ast.variables.Identifier;
//import xyz.avarel.kaiper.exceptions.SyntaxException;
//import xyz.avarel.kaiper.lexer.Token;
//import xyz.avarel.kaiper.parser.BinaryParser;
//import xyz.avarel.kaiper.parser.KaiperParser;
//
//public class TupleColonParser extends BinaryParser {
//    public TupleColonParser() {
//        super(Precedence.TUPLE_PAIR);
//    }
//
//    @Override
//    public Expr parse(KaiperParser parser, Single left, Token token) {
//        if (!(left instanceof Identifier)) {
//            throw new SyntaxException("Tuple entry names must be valid identifiers", left.getPosition());
//        }
//
//        Single value = parser.parseSingle(getPrecedence());
//
//        return new TupleExpr(left.getPosition(), ((Identifier) left).getName(), value);
//    }
//}
