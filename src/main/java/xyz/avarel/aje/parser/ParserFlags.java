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

package xyz.avarel.aje.parser;

public class ParserFlags {
    private boolean allowRanges = true;
    private boolean allowCollections = true;
    private boolean allowVariables = true;
    private boolean allowControlFlow = true;
    private boolean allowFunctionCreation = true;
    private boolean allowInvocation = true;
    private boolean allowLoops = true;

    public boolean allowRanges() {
        return allowRanges;
    }

    public void setAllowRanges(boolean flag) {
        this.allowRanges = flag;
    }

    public boolean allowCollections() {
        return allowCollections;
    }

    public void setAllowCollections(boolean flag) {
        this.allowCollections = flag;
    }

    public boolean allowVariables() {
        return allowVariables;
    }

    public void setAllowVariables(boolean flag) {
        this.allowVariables = flag;
    }

    public boolean allowFunctionCreation() {
        return allowFunctionCreation;
    }

    public void setAllowFunctionCreation(boolean flag) {
        this.allowFunctionCreation = flag;
    }

    public boolean allowInvocation() {
        return allowInvocation;
    }

    public void setAllowInvocation(boolean flag) {
        this.allowInvocation = flag;
    }

    public boolean allowControlFlows() {
        return allowControlFlow;
    }

    public void setAllowControlFlows(boolean flag) {
        this.allowControlFlow = flag;
    }

    public boolean allowLoops() {
        return allowLoops;
    }

    public void setAllowLoops(boolean flag) {
        this.allowLoops = flag;
    }
}
