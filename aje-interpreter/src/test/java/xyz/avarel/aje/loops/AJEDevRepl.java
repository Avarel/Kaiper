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

import xyz.avarel.aje.Expression;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.interop.JavaModel;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.java.JavaObject;

import java.util.Scanner;

public class AJEDevRepl {
    public static void main(String[] args) {
        System.out.println("AJE REPL - Developer");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while (running) {
            try {
                System.out.print("  REPL | ");

                String input = sc.nextLine();

                switch (input) {
                    case "exit":
                        running = false;
                        continue;
                }

                Expression exp = new Expression(input);
                exp.add("model", new JavaObject(new JavaModel("hello world! how are you")));

                long start = System.nanoTime();
                Expr expr = exp.compile();
                Obj result = expr.compute();
                long end = System.nanoTime();

                long ns =  (end - start);
                double ms = ns / 1000000D;

                System.out.println("RESULT | " + result + " : " + result.getType());
                System.out.println("  TIME | " + ms + "ms " + ns + "ns" );

                StringBuilder builder = new StringBuilder();

                expr.ast(builder, "\t\t ", true);

                System.out.println("   AST > +\n" + builder);

                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(" ERROR | " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}