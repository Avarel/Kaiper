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

package xyz.avarel.kaiper.bytecode;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * Chain of consumers that write bytes into a {@link DataOutput}.
 *
 * @author AdrianTodt
 */
@FunctionalInterface
public interface DataOutputConsumer {
    DataOutputConsumer NO_OP = new DataOutputConsumer() {
        @Override
        public void writeInto(DataOutput out) throws IOException {
        }

        @Override
        public DataOutputConsumer andThen(DataOutputConsumer after) {
            return Objects.requireNonNull(after);
        }
    };

    void writeInto(DataOutput output) throws IOException;

    default DataOutputConsumer andThen(DataOutputConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            writeInto(t);
            after.writeInto(t);
        };
    }
}
