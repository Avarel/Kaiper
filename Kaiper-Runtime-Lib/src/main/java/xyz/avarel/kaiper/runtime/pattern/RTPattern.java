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

public abstract class RTPattern implements Comparable<RTPattern> {
    private final String name;

    public RTPattern(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(RTPattern other) {
        int compare = 0;
        if (name != null && other.name != null) {
            if (name.equals("value") && !other.name.equals("value")) {
                return -1;
            } else if (other.name.equals("value")) {
                return 1;
            }
            compare = name.compareTo(other.name);
        }

        if (compare == 0) {
            int weight = Integer.compare(nodeWeight(), other.nodeWeight());
            return weight == 0 && !this.equals(other) ? -1 : weight;
        }

        return compare;
    }

    public boolean optional() {
        return false;
    }

    public int nodeWeight() {
        return 0;
    }

    public abstract <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope);
}
