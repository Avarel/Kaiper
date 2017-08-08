package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.runtime.types.Type;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


// Pattern - the anti tuple / destructive
//
// Wildcard
//      _
// matches no matter what
//
// Literal Pattern
//      [], [:], 123, "string"
// Match if equals
//
// Positional Bind Pattern
//      a, b, c
// Matches if the tuple has a _# where # is the position of the field in the tuple
// will also match if the tuple has a field with the same name as it
//
// Field pattern
//      a: (literal or type pattern)
//
// Type Pattern
//      (positional pattern) is (TYPE)
//      a is Int, b is String, c is Array
// Matches/binds only if type matches
//
// Default Pattern
//      (any pattern except default) = (default)
//      a = 2               binds 2 to a if a doesn't match
//      a is Int = 2        binds 2 to a if a isn't Int
// If no matches, bind pattern to default
// Should always return true for a match
//
// Tuples
// 1, 2, 3          is basically _1: 1, _2: 2, _3: 3
// 1, 2, x: 3       is basically _1: 1, _2: 2, x: 3
// x: 2, y: 3       LEGAL
// x: 2, 3          ILLEGAL
//

public class Tuple implements Obj {
    public static final Type<Bool> TYPE = new Type<>("Tuple");

    // todo empty static one

    private final Map<String, Obj> map;

    public Tuple() {
        this(Collections.emptyMap());
    }

    public Tuple(Obj value) {
        this(Collections.singletonMap("_0", value));
    }

    public Tuple(Map<String, Obj> map) {
       this.map = map;
    }

    public static Tuple of(Obj... fields) {
        Map<String, Obj> map = new LinkedHashMap<>();

        int position = 0;
        for (Obj field : fields) {
            map.put("_" + position, field);
            position++;
        }

        return new Tuple(map);
    }

    public boolean hasAttr(String name) {
        return map.containsKey(name);
    }

    @Override
    public Obj getAttr(String name) {
        return map.get(name);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        if (map.isEmpty()) {
            return "()";
        }

        StringBuilder builder = new StringBuilder();

        int position = 0;

        Iterator<Map.Entry<String, Obj>> iterator = map.entrySet().iterator();
        while (true) {
            Map.Entry<String, Obj> entry = iterator.next();

            if (!entry.getKey().equals("_" + position)) {
                builder.append(entry.getKey()).append(": ");
            } else {
                position++;
            }

            boolean isTuple = entry.getValue() instanceof Tuple;

            if (isTuple) builder.append('(');
            builder.append(entry.getValue());
            if (isTuple) builder.append(')');

            if (iterator.hasNext()) {
                builder.append(", ");
            } else {
                break;
            }
        }

        return builder.toString();
    }
}
