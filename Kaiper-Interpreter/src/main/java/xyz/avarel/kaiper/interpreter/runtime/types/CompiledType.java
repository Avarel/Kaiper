package xyz.avarel.kaiper.interpreter.runtime.types;

import xyz.avarel.kaiper.runtime.types.Type;

public class CompiledType extends Type {
    public CompiledType(Type parent, String name, CompiledConstructor constructor) {
        super(parent, name, constructor);
    }

    @Override
    public CompiledConstructor getConstructor() {
        return (CompiledConstructor) super.getConstructor();
    }
}
