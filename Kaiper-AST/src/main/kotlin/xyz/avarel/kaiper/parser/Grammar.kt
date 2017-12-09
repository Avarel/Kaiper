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

package xyz.avarel.kaiper.parser

import xyz.avarel.kaiper.lexer.TokenType
import java.util.*

open class Grammar @JvmOverloads constructor(
        val prefixParsers: MutableMap<TokenType, PrefixParser> = HashMap(),
        val infixParsers: MutableMap<TokenType, InfixParser> = HashMap()
) {
    fun prefix(token: TokenType, parselet: PrefixParser) {
        if (prefixParsers.containsKey(token)) {
            throw IllegalStateException("INTERNAL: attempting to override existing $token=")
        }
        prefixParsers.put(token, parselet)
    }

    fun infix(token: TokenType, parselet: InfixParser) {
        if (infixParsers.containsKey(token)) {
            throw IllegalStateException("INTERNAL: attempting to override existing $token=")
        }
        infixParsers.put(token, parselet)
    }


    fun copy(): Grammar {
        return Grammar(HashMap(prefixParsers), HashMap(infixParsers))
    }
}
