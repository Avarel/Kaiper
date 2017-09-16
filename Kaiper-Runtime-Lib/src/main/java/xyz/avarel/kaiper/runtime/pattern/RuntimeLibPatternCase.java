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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// multiple of patterns
public class RuntimeLibPatternCase implements RuntimeLibPattern {
    public static final RuntimeLibPatternCase EMPTY = new RuntimeLibPatternCase(Collections.emptyList());

    private final List<RuntimeLibPattern> patterns;

    public RuntimeLibPatternCase(RuntimeLibPattern... patterns) {
        this(Arrays.asList(patterns));
    }

    public RuntimeLibPatternCase(List<RuntimeLibPattern> patterns) {
        this.patterns = patterns;
    }

    public List<RuntimeLibPattern> getPatterns() {
        return patterns;
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<RuntimeLibPattern> iterator = patterns.iterator();

        while (true) {
            RuntimeLibPattern pattern = iterator.next();
            sb.append(pattern);
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        return sb.toString();
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    public int size() {
        return patterns.size();
    }
}
