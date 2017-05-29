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

package xyz.avarel.aje;

public class Precedence {
    public static final int INVOCATION = 14;
    public static final int ATTRIBUTE = 13;
    public static final int PIPE_FORWARD = 12;
    public static final int POSTFIX = 11;
    public static final int EXPONENTIAL = 10;
    public static final int PREFIX = 9;
    public static final int MULTIPLICATIVE = 8;
    public static final int ADDITIVE = 7;
    public static final int RANGE_TO = 6;
    public static final int INFIX = 5;
    public static final int COMPARISON = 4;
    public static final int EQUALITY = 3;
    public static final int CONJUNCTION = 2;
    public static final int DISJUNCTION = 1;
}
