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
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.vm.KaiperVM;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class KaiperBytecodeRepl {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean running = true;

        Scope scope = DefaultScope.INSTANCE.copy();

        scope.declare("print", new NativeFunc("print", Parameter.of("string")) {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        while (running) {
            try {
                System.out.print("\u25b6 ");

                String input = sc.nextLine();

                switch (input) {
                    case "exit":
                        running = false;
                        continue;
                }


                Obj result;

                try {
                    result = new KaiperVM().executeCompressedBytecode(KaiperCompiler.compileCompressed(new ExprParser(new KaiperLexer(input)).parse()), scope);
                } catch (KaiperException | IOException e) {
                    System.out.println("! " + e.getMessage());
                    e.printStackTrace();
                    result = Null.VALUE;
                }

                System.out.println("\u25c0 " + result + " : " + result.getType());

                System.out.println();
            } catch (RuntimeException e) {
                System.out.println("\u25c0 Exception: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
}