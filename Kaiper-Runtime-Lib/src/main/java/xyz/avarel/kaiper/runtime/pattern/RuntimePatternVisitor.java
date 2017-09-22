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

package xyz.avarel.kaiper.runtime.pattern;

/**
 * Visitor patterns for {@link RuntimePattern patern} AST classes.
 * Each {@link RuntimePattern pattern} implements the accept method which
 * is normally {@code visitor.visit(this, context)}.
 *
 * @param <R> Return type.
 * @param <C> Context type.
 *
 * @see RuntimePattern
 * @author Avarel
 */
public interface RuntimePatternVisitor<R, C> {
    R visit(VariableRuntimePattern pattern, C context);
    R visit(TupleRuntimePattern pattern, C context);
    R visit(DefaultRuntimePattern pattern, C context);
}
