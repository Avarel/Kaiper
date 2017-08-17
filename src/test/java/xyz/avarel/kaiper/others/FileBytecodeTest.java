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

package xyz.avarel.kaiper.others;

import xyz.avarel.kaiper.KaiperCompiler;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.scope.DefaultScope;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.KaiperVM;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FileBytecodeTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Kaiper FILE");
        System.out.println();

        System.out.println(new KaiperLexer(new FileReader(new File("script.kip"))).getTokens().toString());

        Scope scope = DefaultScope.INSTANCE.copy();

        scope.declare("println", new NativeFunc("print","string") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }
        });

        Expr expr = new KaiperParser(new KaiperLexer(new FileReader(new File("script.kip")))).parse();

        StringBuilder sb = new StringBuilder();
        expr.ast(sb, "", true);
        System.out.println(sb);

        Future<Obj> future = CompletableFuture.supplyAsync(() -> {
            try {
                return new KaiperVM().executeBytecode(KaiperCompiler.compile(expr), scope);
            } catch (ReturnException r) {
                return r.getValue();
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("\n\t\tRESULT |> " + future.get(500, TimeUnit.MILLISECONDS));
    }
}