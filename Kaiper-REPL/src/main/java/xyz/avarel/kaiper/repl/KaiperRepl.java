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

import xyz.avarel.kaiper.KaiperEvaluator;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;

import java.util.Map;
import java.util.Scanner;

public class KaiperRepl {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        KaiperEvaluator interpreter = new KaiperEvaluator();

        interpreter.getScope().put("println", new NativeFunc("println", "obj") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                System.out.println(arguments.get("obj"));
                return null;
            }
        });

        while (true) {
            System.out.print("kip \u2502 ");

            int openBrackets = 0;

            StringBuilder buffer = new StringBuilder();

            do {
                if (openBrackets != 0) {
                    buffer.append('\n');
                    System.out.print("    \u2502 ");
                }

                String line = sc.nextLine();

                buffer.append(line);
                openBrackets += countMatches(line, '{') - countMatches(line, '}');
            } while (openBrackets > 0);


            String input = buffer.toString();

            if (input.equals("quit")) {
                break;
            }

            try {
                Obj result = interpreter.eval(input);

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