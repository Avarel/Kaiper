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
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.RuntimeMultimethod;
import xyz.avarel.kaiper.runtime.pattern.RuntimePatternCase;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KaiperREPL {
    public static void main(String[] args) throws IOException {
        KaiperEvaluator evaluator = new KaiperEvaluator();

        evaluator.getScope().put("println", new RuntimeMultimethod("println")
                .addCase(new RuntimePatternCase("value"), scope -> {
                    System.out.print(scope.get("value"));
                    return Null.VALUE;
                })
        );

        new KaiperREPL(evaluator).loop();
    }

    private final ConsoleReader reader;
    private final IndentContext indentContext = new IndentContext();

    private final KaiperEvaluator evaluator;

    public KaiperREPL(KaiperEvaluator evaluator) {
        this.evaluator = evaluator;

        try {
            reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        indentContext.add('{', '}');
        indentContext.add('[', ']');
        indentContext.add('(', ')');

        reader.setHistoryEnabled(false);

        reader.addTriggeredAction('\b', e -> {
            if (reader.getCursorBuffer().buffer.length() == 0) return;
            char deletedChar = reader.getCursorBuffer().buffer.charAt(reader.getCursorBuffer().cursor - 1);

            switch (deletedChar) {
                case ' ': if (isSpaces(sbuffer(reader))) {
                    int spaceBefore = spacesPrefix(sbuffer(reader));
                    if (spaceBefore != 0) {
                        int spacesToDelete = spaceBefore % 4;
                        spacesToDelete = spacesToDelete == 0 ? 4 : spacesToDelete;

                        for (int i = 0; i < spacesToDelete; i++)
                            backspace(reader);
                    }
                }
                default: indentContext.processDeleted(deletedChar);
            }

            backspace(reader);
            flush(reader);
        });
        reader.addTriggeredAction('\t', e -> {
            putString(reader, "    ");
            flush(reader);
        });
    }

    public void loop() {
        while (true) {
            String s = readREPLInput();

            if (s.equals("/quit")) {
                break;
            }

            try {
                Obj result = evaluator.eval(s);
                System.out.println(result);
            } catch (KaiperException e) {
                System.out.print("!!! ");
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                } else {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println();
        }
    }

    public String readREPLInput() {
        StringJoiner j = new StringJoiner("\n");

        reader.setPrompt(">>> ");
        do {
            for (int i = 0; i < indentContext.indentLevel(); i++) {
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
        } while (indentContext.indentLevel() > 0);

        return j.toString();
    }

    private class IndentContext {
        private final Map<Character, Integer> indentPairs = new HashMap<>();
        private final Map<Character, Integer> dedentPairs = new HashMap<>();
        private final List<AtomicInteger> indentLevels = new ArrayList<>();

        public void add(final char indent, final char dedent) {
            indentPairs.put(indent, indentLevels.size());
            dedentPairs.put(dedent, indentLevels.size());
            indentLevels.add(new AtomicInteger());

            // hack for now until find better solution
            reader.addTriggeredAction(indent, e -> {
                indent(indent);
                putString(reader, String.valueOf(indent));
                flush(reader);
            });
            reader.addTriggeredAction(dedent, e -> {
                dedent(dedent);

                if (isSpaces(sbuffer(reader))) {
                    while (sbuffer(reader).length() > indentContext.indentLevel() * 4) {
                        backspace(reader);
                    }
                }

                putString(reader, String.valueOf(dedent));
                flush(reader);
            });
        }

        public void processDeleted(char input) {
            Integer i = indentPairs.get(input);
            if (i == null) {
                i = dedentPairs.get(input);
                if (i == null) return;
                indentLevels.get(i).incrementAndGet();
            }
            indentLevels.get(i).decrementAndGet();
        }

        public void indent(char indent) {
            indentLevels.get(indentPairs.get(indent)).incrementAndGet();
        }

        public void dedent(char dedent) {
            indentLevels.get(dedentPairs.get(dedent)).decrementAndGet();
        }

        private int indentLevel() {
            int sum = 0;
            for (AtomicInteger i : indentLevels) {
                sum += i.get();
            }
            return sum;
        }
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