package xyz.avarel.kaiper.runtime.modules;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

public class CompiledModule extends Module {
    public final Scope scope;

    public CompiledModule(String name, Scope scope) {
        super(name);
        this.scope = scope;
    }

    @Override
    public Obj getAttr(String name) {
        Obj obj = scope.getMap().get(name);
        return obj == null ? Null.VALUE : obj;
    }
}
