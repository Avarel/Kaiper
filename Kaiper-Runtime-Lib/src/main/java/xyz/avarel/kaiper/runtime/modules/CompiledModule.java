package xyz.avarel.kaiper.runtime.modules;

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

public class CompiledModule extends Module {
    private final String name;
    public Scope scope;

    public CompiledModule(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj getAttr(String name) {
        Obj obj = scope.directLookup(name);
        return obj == null ? Null.VALUE : obj;
    }

    @Override
    public String toString() {
        return "module " + name;
    }
}
