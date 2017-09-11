package xyz.avarel.kaiper.vm.runtime.types;

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.types.Type;

public class CompiledType extends Type {
    public CompiledType(String name, CompiledConstructor constructor) {
        super(Obj.TYPE, name, constructor);
    }

    @Override
    public CompiledConstructor getConstructor() {
        return (CompiledConstructor) super.getConstructor();
    }
}
