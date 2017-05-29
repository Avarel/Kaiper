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

package xyz.avarel.aje.exceptions;

import xyz.avarel.aje.parser.lexer.Position;

public class SyntaxException extends AJEException {
    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(String msg, Position position) {
        this(msg + position);
    }

    public SyntaxException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SyntaxException(String s, Position position, Throwable throwable) {
        super(s + position, throwable);
    }

    public SyntaxException(Throwable throwable) {
        super(throwable);
    }
}
