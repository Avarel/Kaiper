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

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.StringJoiner;

public class KaiperREPL {
    public static void main(String[] args) throws IOException {
        new KaiperREPL().readREPLInput();
    }

    private final ConsoleReader reader;
    private final IndentContext bracketIndent = new IndentContext();

    public KaiperREPL() {
        try {
            reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        reader.addTriggeredAction('{', e -> {
            bracketIndent.indentLevel++;
            putString(reader, "{");
            flush(reader);
        });
        reader.addTriggeredAction('}', e -> {
            bracketIndent.indentLevel--;

            if (isSpaces(sbuffer(reader))) {
                while (sbuffer(reader).length() > bracketIndent.indentLevel * 4) {
                    backspace(reader);
                }
            }

            putString(reader, "}");
            flush(reader);
        });
        reader.addTriggeredAction('\b', e -> {
            if (reader.getCursorBuffer().buffer.length() == 0) return;
            char deletedChar = reader.getCursorBuffer().buffer.charAt(reader.getCursorBuffer().cursor - 1);

            switch (deletedChar) {
                case '{': bracketIndent.indentLevel--; break;
                case '}': bracketIndent.indentLevel++; break;
                case ' ': if (isSpaces(sbuffer(reader))) {
                    int spaceBefore = spacesPrefix(sbuffer(reader));
                    if (spaceBefore != 0) {
                        int spacesToDelete = spaceBefore % 4;
                        spacesToDelete = spacesToDelete == 0 ? 4 : spacesToDelete;

                        for (int i = 0; i < spacesToDelete; i++)
                            backspace(reader);
                    }
                }
            }

            backspace(reader);
            flush(reader);
        });
        reader.addTriggeredAction('\t', e -> {
            putString(reader, "    ");
            flush(reader);
        });
    }

    public String readREPLInput() {
        StringJoiner j = new StringJoiner("\n");

        reader.setPrompt(">>> ");
        do {
            for (int i = 0; i < bracketIndent.indentLevel; i++) {
                reader.getCursorBuffer().write("    ");
            }

            String input;
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            j.add(input);

            reader.setPrompt("... ");
        } while (bracketIndent.indentLevel > 0);

        return j.toString();
    }
//    public static void main(String[] args) throws IOException {
//        //Scanner sc = new Scanner(System.in);
//
//        KaiperEvaluator interpreter = new KaiperEvaluator();
//
//        interpreter.getScope().put("println", new RuntimeMultimethod("println")
//                .addCase(new RuntimePatternCase("value"), scope -> {
//                    System.out.print(scope.get("value"));
//                    return Null.VALUE;
//                })
//        );
//
//        outer: while (true) {
//            System.out.print("kip \u2502 ");
//
//            int openBrackets = 0;
//
//            StringBuilder entryBuffer = new StringBuilder();
//
//            do {
//                if (openBrackets != 0) {
//                    entryBuffer.append('\n');
//                    System.out.print("    \u2502 ");
//                }
//
//                StringBuilder initialBuffer = new StringBuilder();
//                for (int i = 0; i < openBrackets; i++) {
//                    initialBuffer.append("    ");
//                }
//
//                String line = readInternal(initialBuffer.toString(), 4, openBrackets);
//
//                if (line == null) break outer;
//
//                entryBuffer.append(line);
//                openBrackets += countMatches(line, '{') - countMatches(line, '}');
//            } while (openBrackets > 0);
//
//            String input = entryBuffer.toString();
//
//            if (input.equals("/quit")) {
//                break;
//            }
//
//            Obj result;
//
//            try {
//                result = interpreter.eval(input);
//
//                System.out.println("    \u2514\u2500\u2500 " + result + " : " + result.getType());
//            } catch (KaiperException e) {
//                System.out.println("err \u2514\u2500\u2500 " + e.getMessage());
//            }
//
//            System.out.println();
//        }
//    }

    private static class IndentContext {
        private int indentLevel = 0;
    }

    private static void flush(ConsoleReader reader) {
        try {
            reader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void putString(ConsoleReader reader, String s) {
        try {
            reader.putString(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void backspace(ConsoleReader reader) {
        try {
            reader.backspace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static StringBuilder sbuffer(ConsoleReader reader) {
        return reader.getCursorBuffer().buffer;
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
}