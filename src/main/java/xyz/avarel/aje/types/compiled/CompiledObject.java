package xyz.avarel.aje.types.compiled;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Type;

public class CompiledObject implements Any<CompiledObject> {
    private final Type<CompiledObject> type;

    public CompiledObject(CompiledObject prototype) {
        type = prototype.type;
    }

    @Override
    public Type<CompiledObject> getType() {
        return type;
    }

}
