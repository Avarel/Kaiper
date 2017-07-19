/*
 * Licensed under the Apache License, Version 2.0 (the
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

package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Grammar {
    private final Map<TokenType, PrefixParser> prefixParsers;
    private final Map<TokenType, InfixParser> infixParsers;

    public Grammar() {
        this(new HashMap<>(), new HashMap<>());
    }

    public Grammar(Map<TokenType, PrefixParser> prefixParsers, Map<TokenType, InfixParser> infixParsers) {
        this.prefixParsers = new HashMap<>(prefixParsers);
        this.infixParsers = new HashMap<>(infixParsers);
    }

    public void prefix(TokenType token, PrefixParser parselet) {
        if (prefixParsers.containsKey(token)) {
            throw new IllegalStateException("INTERNAL: attempting to override existing " + token + " parser");
        }
        prefixParsers.put(token, parselet);
    }

    public void infix(TokenType token, InfixParser parselet) {
        if (infixParsers.containsKey(token)) {
            throw new IllegalStateException("INTERNAL: attempting to override existing " + token + " parser");
        }
        infixParsers.put(token, parselet);
    }

    public Map<TokenType, PrefixParser> getPrefixParsers() {
        return prefixParsers;
    }

    public Map<TokenType, InfixParser> getInfixParsers() {
        return infixParsers;
    }

    public Grammar copy() {
        return new Grammar(prefixParsers, infixParsers);
    }
}
