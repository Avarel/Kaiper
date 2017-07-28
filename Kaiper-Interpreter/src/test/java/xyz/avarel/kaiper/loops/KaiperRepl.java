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

import xyz.avarel.kaiper.KaiperREPL;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.interop.IJavaModel;
import xyz.avarel.kaiper.interop.JavaModel;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.java.JavaType;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Scanner;

public class KaiperRepl {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean running = true;

        KaiperREPL interpreter = new KaiperREPL();

        interpreter.getScope().declare("println", new NativeFunc("print", "string") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        interpreter.getScope().declare("IJavaModel", new JavaType(IJavaModel.class));
        interpreter.getScope().declare("JavaClass", new JavaType(Class.class));
        interpreter.getScope().declare("JavaModel", new JavaType(JavaModel.class));
        interpreter.getScope().declare("Gambiarra", new JavaType(Proxy.getProxyClass(KaiperRepl.class.getClassLoader(), IJavaModel.class, Runnable.class)));

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
                    result = interpreter.eval(input);
                } catch (KaiperException e) {
                    System.out.println("! " + e.getMessage());
                    //e.printStackTrace();
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