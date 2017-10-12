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

package xyz.avarel.kaiper.repl;

import com.kantenkugel.consoleutils.ConsoleUtils;
import xyz.avarel.kaiper.KaiperEvaluator;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.RuntimeMultimethod;
import xyz.avarel.kaiper.runtime.pattern.RuntimePatternCase;

import java.io.IOException;

public class KaiperRepl {
    public static void main(String[] args) throws IOException {
        //Scanner sc = new Scanner(System.in);

        KaiperEvaluator interpreter = new KaiperEvaluator();

        interpreter.getScope().put("print", new RuntimeMultimethod("println")
                .addCase(new RuntimePatternCase("value"), scope -> {
                    System.out.print(scope.get("value"));
                    return Null.VALUE;
                })
                .addCase(new RuntimePatternCase("ln"), scope -> {
                    System.out.println(scope.get("ln"));
                    return Null.VALUE;
                })
        );

        while (true) {
            System.out.print("kip \u2502 ");

            int openBrackets = 0;

            StringBuilder entryBuffer = new StringBuilder();

            do {
                if (openBrackets != 0) {
                    entryBuffer.append('\n');
                    System.out.print("    \u2502 ");
                }

                StringBuilder initialBuffer = new StringBuilder();
                for (int i = 0; i < openBrackets; i++) {
                    initialBuffer.append("    ");
                }

                String line = ConsoleUtils.readWithInitialBuffer(initialBuffer.toString());
                System.out.println();

                entryBuffer.append(line);
                openBrackets += countMatches(line, '{') - countMatches(line, '}');
            } while (openBrackets > 0);

            String input = entryBuffer.toString();

            if (input.equals("/quit")) {
                break;
            }

            Obj result;

            try {
                result = interpreter.eval(input);

                System.out.println("    \u2514\u2500\u2500 " + result + " : " + result.getType());
            } catch (KaiperException e) {
                System.out.println("err \u2514\u2500\u2500 " + e.getMessage());
                e.printStackTrace(System.out);
            }

            System.out.println();
        }
    }

    private static int countMatches(String target, char character) {
        int count = 0;
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == character) count++;
        }
        return count;
    }
}