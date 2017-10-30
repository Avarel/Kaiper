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

package xyz.avarel.kaiper.runtime.runtime_pattern;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PatternCase implements Comparable<PatternCase> {
    public static final PatternCase EMPTY = new PatternCase(Collections.emptyList());

    private final List<Pattern> patterns;

    public PatternCase(Pattern... patterns) {
        this(Arrays.asList(patterns));
    }

    public PatternCase(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public PatternCase subList(int start) {
        return subList(start, size());
    }

    public PatternCase subList(int start, int end) {
        return new PatternCase(patterns.subList(start, end));
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "()";
        }

        StringBuilder sb = new StringBuilder("(");
        Iterator<Pattern> iterator = patterns.iterator();

        while (true) {
            Pattern pattern = iterator.next();
            sb.append(pattern);
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        sb.append(")");

        return sb.toString();
    }

    public int size() {
        return patterns.size();
    }

    public int arity() {
        int sum = 0;
        for (Pattern pattern : patterns) {
            sum += pattern.optional() ? 0 : 1;
        }
        return sum;
    }

    @Override
    public int compareTo(PatternCase other) {
        if (other.size() != size()) {
            return Integer.compare(other.size(), size());
        }

        for (int i = 0; i < size(); i++) {
            int b = patterns.get(i).compareTo(other.patterns.get(i));
            if (b != 0) {
                return b;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PatternCase && this.compareTo((PatternCase) obj) == 0;
    }
}
