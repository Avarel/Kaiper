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

package xyz.avarel.kaiper.loops;

import xyz.avarel.kaiper.KaiperScript;
import xyz.avarel.kaiper.ScriptExpr;
import xyz.avarel.kaiper.interop.JavaModel;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.java.JavaType;
import xyz.avarel.kaiper.runtime.java.JavaUtils;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;

import java.util.Map;
import java.util.Scanner;

public class KaiperDevRepl {
    public static void main(String[] args) {
        System.out.println("Kaiper REPL - Developer");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        Scope scope = DefaultScope.INSTANCE.copy();

        scope.declare("println", new NativeFunc("print", "string") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                System.out.println(arguments.get("string"));
                return null;
            }
        });

        scope.declare("JavaModel", JavaUtils.JAVA_PROTOTYPES.computeIfAbsent(JavaModel.class, JavaType::new));

        while (running) {
            try {
                System.out.print("  REPL | ");

                String input = sc.nextLine();

                switch (input) {
                    case "exit":
                        running = false;
                        continue;
                }

                KaiperScript exp = new KaiperScript(input, scope);
                ScriptExpr expr = exp.compile();

                StringBuilder builder = new StringBuilder();
                expr.ast(builder, "\t\t ", true);
                System.out.println("   AST > +\n" + builder);

                long start = System.nanoTime();
                Obj result = expr.compute();
                long end = System.nanoTime();

                long ns =  (end - start);
                double ms = ns / 1000000D;

                System.out.println("RESULT | " + result + " : " + result.getType());
                System.out.println("  TIME | " + ms + "ms " + ns + "ns" );



                System.out.println();
            } catch (RuntimeException e) {
                System.out.println(" ERROR | " + e.getMessage() + "\n");
                e.printStackTrace();
                return;
            }
        }
    }
}