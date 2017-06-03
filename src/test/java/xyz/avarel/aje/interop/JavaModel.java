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

package xyz.avarel.aje.interop;/*
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

import java.util.Arrays;
import java.util.List;

public class JavaModel {
    public final String name;

    public final int size;

    public JavaModel(String name) {
        this.name = name;
        this.size = name.length();
    }

    public JavaModel me() {
        return this;
    }


    public Object getNull() {
        return null;
    }

    public String name() {
        return name + "IM SPECIA";
    }

    public String substring(int i) {
        return name.substring(i);
    }

    public String substring(int start, Integer end) {
        return name.substring(start, end);
    }

    public int indexOf(String i) {
        return name.indexOf(i);
    }

    public int indexOf(int b) {
        return name.indexOf(Integer.toString(b));
    }

    public void printAllFrom(List<Object> objects) {
        for (Object obj : objects) {
            System.out.println(obj);
        }
    }

    public List<Integer> getList() {
        return Arrays.asList(1,2,3,4,5);
    }
}
