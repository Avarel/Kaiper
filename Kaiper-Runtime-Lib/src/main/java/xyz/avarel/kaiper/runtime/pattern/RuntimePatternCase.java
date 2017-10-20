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
import java.util.stream.Collectors;

public class RuntimePatternCase implements Comparable<RuntimePatternCase> {
    public static final RuntimePatternCase EMPTY = new RuntimePatternCase(Collections.emptyList());

    private final List<RTPattern> patterns;

    public RuntimePatternCase(String... variables) {
        this(Arrays.stream(variables).map(VariableRTPattern::new).collect(Collectors.toList()));
    }

    public RuntimePatternCase(RTPattern... patterns) {
        this(Arrays.asList(patterns));
    }

    public RuntimePatternCase(List<RTPattern> patterns) {
        this.patterns = patterns;
    }

    public List<RTPattern> getPatterns() {
        return patterns;
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<RTPattern> iterator = patterns.iterator();

        while (true) {
            RTPattern pattern = iterator.next();
            sb.append(pattern);
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        return sb.toString();
    }

    public int size() {
        return patterns.size();
    }

    @Override
    public int compareTo(RuntimePatternCase other) {
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
        return obj instanceof RuntimePatternCase && this.compareTo((RuntimePatternCase) obj) == 0;
    }
}
