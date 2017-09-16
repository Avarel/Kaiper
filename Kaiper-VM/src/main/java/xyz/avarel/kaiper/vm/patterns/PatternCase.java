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

package xyz.avarel.kaiper.vm.patterns;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// multiple of patterns
public class PatternCase implements Pattern {
    public static final PatternCase EMPTY = new PatternCase(Collections.emptyList());

    private final List<Pattern> patterns;

    public PatternCase(Pattern... patterns) {
        this(Arrays.asList(patterns));
    }

    public PatternCase(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public String toString() {
        if (patterns.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
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

        return sb.toString();
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public int size() {
        return patterns.size();
    }
}
