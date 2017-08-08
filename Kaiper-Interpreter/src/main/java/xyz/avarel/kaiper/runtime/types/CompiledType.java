package xyz.avarel.kaiper.runtime.types;

import xyz.avarel.kaiper.runtime.Obj;

public class CompiledType extends Type {
    public CompiledType(String name, CompiledConstructor constructor) {
        super(Obj.TYPE, name, constructor);
    }

    @Override
    public CompiledConstructor getConstructor() {
        return (CompiledConstructor) super.getConstructor();
    }
}
