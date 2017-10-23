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

package xyz.avarel.kaiper.others;

import xyz.avarel.kaiper.KaiperScript;
import xyz.avarel.kaiper.ScriptExpr;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.functions.RuntimeMultimethod;
import xyz.avarel.kaiper.runtime.pattern.RuntimePatternCase;

import java.io.File;
import java.io.FileReader;

public class FileTest {
    public static void main(String[] args) throws Exception {
        KaiperScript exp = new KaiperScript(new FileReader(new File("script.kip")));

        exp.getScope().put("println", new RuntimeMultimethod("println")
                .addCase(new RuntimePatternCase("value"), scope -> {
                    System.out.println(scope.get("value"));
                    return Null.VALUE;
                })
        );

        ScriptExpr expr = exp.compile();

        StringBuilder sb = new StringBuilder();
        expr.ast(sb, "", true);

        System.out.println(expr.compute());
    }
}