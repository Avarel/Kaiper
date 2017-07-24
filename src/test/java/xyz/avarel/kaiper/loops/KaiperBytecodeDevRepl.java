/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.loops;

import xyz.avarel.kaiper.KaiperCompiler;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.tools.bytecode.BytecodeOutliner;
import xyz.avarel.kaiper.vm.KaiperVM;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class KaiperBytecodeDevRepl {
    public static void main(String[] args) {
        System.out.println("Kaiper REPL - Developer/Bytecode");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        Scope scope = DefaultScope.INSTANCE.copy();

        scope.declare("print", new NativeFunc("print", Parameter.of("string")) {
            @Override
            protected Obj eval(List<Obj> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        while (running) {
            try {
                System.out.print("   REPL | ");

                String input = sc.nextLine();

                switch (input) {
                    case "exit":
                        running = false;
                        continue;
                }

                byte[] bytecode = KaiperCompiler.compile(new KaiperParser(new KaiperLexer(input)).parse());
                long start = System.nanoTime();
                Obj result = new KaiperVM().executeBytecode(bytecode, scope);
                long end = System.nanoTime();

                long ns = (end - start);
                double ms = ns / 1000000D;

                System.out.println(" RESULT | " + result + " : " + result.getType());
                System.out.println("   TIME | " + ms + "ms " + ns + "ns");


                System.out.println("OUTLINE > \n" + new BytecodeOutliner().doOutline(bytecode));
            } catch (IOException | RuntimeException e) {
                System.out.println("  ERROR | " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}