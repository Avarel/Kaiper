package xyz.avarel.aje.types.compiled;

import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;

public class CompiledObject implements AJEObject<CompiledObject> {
    private final AJEType<CompiledObject> type;

    public CompiledObject(CompiledObject prototype) {
        type = prototype.type;
    }

    @Override
    public AJEType<CompiledObject> getType() {
        return type;
    }

}
