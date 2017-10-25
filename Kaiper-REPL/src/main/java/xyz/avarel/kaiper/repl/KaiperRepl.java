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

import biz.source_code.utils.RawConsoleInput;
import com.kantenkugel.consoleutils.CharConstants;
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

        interpreter.getScope().put("println", new RuntimeMultimethod("println")
                .addCase(new RuntimePatternCase("value"), scope -> {
                    System.out.println(scope.get("value"));
                    return Null.VALUE;
                })
        );

        outer: while (true) {
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

                String line = readInternal(initialBuffer.toString(), 4, openBrackets);

                if (line == null) break outer;

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

    private static String readInternal(String initialBuffer, int indentUnit, int indentLevel) throws IOException {
        if(initialBuffer != null) System.out.print(initialBuffer);
        StringBuilder b = initialBuffer == null ? new StringBuilder() : new StringBuilder(initialBuffer);
        int read;
        while ((read = RawConsoleInput.read(true)) != -1) {
            if (!ConsoleUtils.isPrintableChar((char) read)) {
                if (read == CharConstants.CHAR_BACKSPACE) {
                    if (b.length() == 0) continue;
                    b.setLength(b.length() - 1);
                    ConsoleUtils.backspace();
                    continue;
                }
                if (read == CharConstants.CHAR_CTRL_C) {
                    return null;
                }
                break;
            }

            if (read == '}') {
                dedent(b, indentUnit);
            }

            b.append((char) read);
            System.out.print((char) read);
        }
        System.out.print('\n');
        return b.toString();
    }

    private static int spacesPrefix(CharSequence s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                return i;
            }
        }
        return s.length();
    }

    private static boolean isSpaces(CharSequence s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    private static void dedent(StringBuilder b, int indentUnit) {
        int spaceBefore = spacesPrefix(b);
        if (spaceBefore != 0 && isSpaces(b)) {
            int spacesToDelete = spaceBefore % indentUnit;
            deleteBuffer(b, spacesToDelete == 0 ? indentUnit : spacesToDelete);
        }
    }

    private static void deleteBuffer(StringBuilder b, int amount) {
        b.setLength(b.length() - amount);
        for (int i = 0; i < amount; i++) {
            ConsoleUtils.backspace();
        }
    }
}