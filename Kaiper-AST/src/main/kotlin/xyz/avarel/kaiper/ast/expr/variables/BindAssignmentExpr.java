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

package xyz.avarel.kaiper.ast.expr.variables;

//public class BindAssignmentExpr extends Expr {
//    private final PatternCase patternCase;
//    private final Expr expr;
//
//    public BindAssignmentExpr(Position position, PatternCase patternCase, Expr expr) {
//        super(position);
//        this.patternCase = patternCase;
//        this.expr = expr;
//    }
//
//    public PatternCase getPatternCase() {
//        return patternCase;
//    }
//
//    public Expr getExpr() {
//        return expr;
//    }
//
//    @Override
//    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
//        return visitor.visit(this, context);
//    }
//
//    @Override
//    public void ast(StringBuilder builder, String indent, boolean isTail) {
//        builder.append(indent).append(isTail ? "└── " : "├── ").append("declare");
//
//        builder.append('\n');
//        builder.append(indent).append(isTail ? "    " : "│   ").append("├── pattern ").append(patternCase);
//
//        builder.append('\n');
//        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
//    }
//
//    @Override
//    public String toString() {
//        return patternCase + " = " + expr;
//    }
//}
