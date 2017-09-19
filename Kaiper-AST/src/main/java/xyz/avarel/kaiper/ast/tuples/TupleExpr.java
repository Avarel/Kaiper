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

package xyz.avarel.kaiper.ast.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TupleExpr extends Single {
    private final List<Single> unnamedElements;
    private final Map<String, Single> namedElements;

    public TupleExpr(Position position, Single element) {
        this(position, Collections.singletonList(element), Collections.emptyMap());
    }

    public TupleExpr(Position position, String name, Single element) {
        this(position, Collections.emptyList(), Collections.singletonMap(name, element));
    }

    public TupleExpr(Position position, List<Single> unnamedElements, Map<String, Single> namedElements) {
        super(position);
        this.unnamedElements = unnamedElements;
        this.namedElements = namedElements;
    }

    public List<Single> getUnnamedElements() {
        return unnamedElements;
    }

    public Map<String, Single> getNamedElements() {
        return namedElements;
    }

    public int size() {
        return unnamedElements.size() + namedElements.size();
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return unnamedElements.toString() + namedElements.toString();
    }
}
