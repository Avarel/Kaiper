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

package xyz.avarel.kaiper;

import org.junit.Test;
import xyz.avarel.kaiper.ast.pattern.DefaultPattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.VariablePattern;
import xyz.avarel.kaiper.ast.value.NullNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PatternOrderTest {
    public List<PatternCase> patterns = Arrays.asList(
            new PatternCase(new DefaultPattern(new VariablePattern("value"), NullNode.VALUE)),
            new PatternCase(new VariablePattern("x")),
            new PatternCase(new VariablePattern("y")),
            new PatternCase(new VariablePattern("z")),
            new PatternCase(new VariablePattern("a")),
            new PatternCase(new VariablePattern("b")),
            new PatternCase(new VariablePattern("c")),
            new PatternCase(new VariablePattern("x"), new VariablePattern("x")),
            new PatternCase(new VariablePattern("x"), new VariablePattern("y")),
            new PatternCase(new VariablePattern("y"), new VariablePattern("z")),
            new PatternCase(new VariablePattern("y"), new VariablePattern("a")),
            new PatternCase(new VariablePattern("z"), new VariablePattern("b")),
            new PatternCase(new VariablePattern("z"), new VariablePattern("c")),
            new PatternCase(new VariablePattern("value"), new VariablePattern("c")),
            new PatternCase(new VariablePattern("value"), new VariablePattern("b"), new VariablePattern("lol")),
            new PatternCase(new VariablePattern("value"), new VariablePattern("c"), new VariablePattern("lol")),
            new PatternCase(new VariablePattern("c"), new VariablePattern("c"), new VariablePattern("lol")),
            new PatternCase(new VariablePattern("b"), new VariablePattern("c"), new VariablePattern("lol")),
            new PatternCase(new VariablePattern("a"), new VariablePattern("c"), new VariablePattern("lol"))
    );

    @Test
    public void ordering() {
        List<PatternCase> copy = new ArrayList<>(patterns);
        copy.sort(Comparator.naturalOrder());
        for (PatternCase patternCase : copy) {
            System.out.println(patternCase);
        }
    }
}
