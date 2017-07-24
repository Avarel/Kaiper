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

package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.exceptions.ComputeException;

public class GlobalVisitorSettings {
    public static int ITERATION_LIMIT = -1;
    public static int SIZE_LIMIT = -1;
    public static long MILLISECONDS_LIMIT = -1;
    public static int RECURSION_DEPTH_LIMIT = -1;

    public static void checkIterationLimit(int iter) {
        if (ITERATION_LIMIT != -1 && iter > ITERATION_LIMIT) {
            throw new ComputeException("Iteration limit");
        }
    }

    public static void checkRecursionDepthLimit(int depth) {
        if (RECURSION_DEPTH_LIMIT != -1 && depth > RECURSION_DEPTH_LIMIT) {
            throw new ComputeException("Recursion depth limit");
        }
    }

    public static void checkSizeLimit(int size) {
        if (SIZE_LIMIT != -1 && size > SIZE_LIMIT) {
            throw new ComputeException("Size limit");
        }
    }

    public static void checkTimeout(long timeout) {
        if (MILLISECONDS_LIMIT != -1 && System.currentTimeMillis() >= timeout) {
            throw new ComputeException("Computation timeout");
        }
    }
}
