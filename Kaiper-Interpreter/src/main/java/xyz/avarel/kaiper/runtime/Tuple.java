package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.Pair;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.List;

public class Tuple implements Obj {
    public static final Type<Bool> TYPE = new Type<>("Tuple");

    private final List<Pair<String, Obj>> fields;

    public Tuple(List<Pair<String, Obj>> fields) {
        this.fields = fields;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
