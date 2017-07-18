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

package xyz.avarel.aje.loops;

import xyz.avarel.aje.AJECompiler;
import xyz.avarel.aje.exceptions.AJEException;
import xyz.avarel.aje.lexer.AJELexer;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;
import xyz.avarel.aje.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AJEBytecodeRepl {
    public static void main(String[] args) {
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
                System.out.print("\u25b6 ");

                String input = sc.nextLine();

                switch (input) {
                    case "exit":
                        running = false;
                        continue;
                }


                Obj result;

                try {
                    result = new VirtualMachine().executeCompressedBytecode(new AJECompiler().compileCompressed(new AJEParser(new AJELexer(input)).compile()), scope);
                } catch (AJEException | IOException e) {
                    System.out.println("! " + e.getMessage());
                    e.printStackTrace();
                    result = Undefined.VALUE;
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