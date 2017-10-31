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

package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.exceptions.ComputeException;

public class VisitorSettings {
    public static final VisitorSettings DEFAULT = new VisitorSettings(-1, -1, -1, -1);

    private final int loopLimit;
    private final int sizeLimit;
    private final long msLimit;
    private final int recursionDepthLimit;

    public VisitorSettings(int loopLimit, int sizeLimit, long msLimit, int recursionDepthLimit) {
        this.loopLimit = loopLimit;
        this.sizeLimit = sizeLimit;
        this.msLimit = msLimit;
        this.recursionDepthLimit = recursionDepthLimit;
    }

    public int getLoopLimit() {
        return loopLimit;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public long getMsLimit() {
        return msLimit;
    }

    public int getRecursionDepthLimit() {
        return recursionDepthLimit;
    }

    public void checkIterationLimit(int iteration) {
        if (loopLimit != -1 && iteration > loopLimit) {
            throw new ComputeException("Iteration limit");
        }
    }

    public void checkRecursionDepthLimit(int recursionDepth) {
        if (recursionDepthLimit != -1 && recursionDepth > recursionDepthLimit) {
            throw new ComputeException("Recursion depth limit");
        }
    }

    public void checkSizeLimit(int size) {
        if (sizeLimit != -1 && size > sizeLimit) {
            throw new ComputeException("Size limit");
        }
    }

    public void checkTimeout(long timeout) {
        if (msLimit != -1 && System.currentTimeMillis() >= timeout) {
            throw new ComputeException("Computation timeout");
        }
    }
}
