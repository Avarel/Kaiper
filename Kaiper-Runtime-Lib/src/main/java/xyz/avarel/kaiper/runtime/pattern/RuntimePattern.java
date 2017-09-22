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

public abstract class RuntimePattern implements Comparable<RuntimePattern> {
    private final String name;

    public RuntimePattern(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(RuntimePattern other) {
        if (name.equals("value") && !other.getName().equals("value")) {
            return -1;
        } else if (other.getName().equals("value")) {
            return 1;
        }

        return getName().compareTo(other.getName());
    }


    public abstract <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope);
}
