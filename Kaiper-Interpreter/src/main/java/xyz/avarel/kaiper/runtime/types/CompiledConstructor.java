///*
// *  Copyright 2017 An Tran and Adrian Todt
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package xyz.avarel.kaiper.runtime.types;
//
//import xyz.avarel.kaiper.GenericScope;
//import xyz.avarel.kaiper.ast.Expr;
//import xyz.avarel.kaiper.ast.pattern.PatternBinder;
//import xyz.avarel.kaiper.ast.pattern.PatternCase;
//import xyz.avarel.kaiper.exceptions.ComputeException;
//import xyz.avarel.kaiper.exceptions.InterpreterException;
//import xyz.avarel.kaiper.interpreter.ExprInterpreter;
//import xyz.avarel.kaiper.runtime.Obj;
//import xyz.avarel.kaiper.runtime.Tuple;
//import xyz.avarel.kaiper.scope.Scope;
//
//public class CompiledConstructor extends Constructor {
//    private final PatternCase pattern;
//    private final ExprInterpreter visitor;
//    private final Scope scope;
//    private final Expr expr;
//
//    public CompiledConstructor(PatternCase pattern, Expr expr, ExprInterpreter visitor, Scope scope) {
//        this.pattern = pattern;
//        this.visitor = visitor;
//        this.scope = scope;
//        this.expr = expr;
//    }
//
//    @Override
//    public int getArity() {
//        return pattern.size();
//    }
//
//    public PatternCase getPattern() {
//        return pattern;
//    }
//
//    @Override
//    public CompiledObj invoke(Tuple arguments) {
//        if (!(targetType instanceof CompiledType)) {
//            throw new ComputeException("Internal error");
//        }
//
//        GenericScope<String, Obj> constructorScope = this.scope.subPool();
//
//        if (!new PatternBinder(pattern, visitor, constructorScope).declareFrom(arguments)) {
//            throw new InterpreterException("Could not match arguments (" + arguments + ") to " + getName() + "(" + pattern + ")");
//        }
//
//        CompiledObj instance = new CompiledObj((CompiledType) targetType, constructorScope);
//
//        ExprInterpreter.declare(constructorScope, "this", instance);
//
//        expr.accept(visitor, constructorScope);
//
//        return instance;
//    }
//
//}