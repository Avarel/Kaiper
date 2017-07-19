package xyz.avarel.aje.runtime.modules;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.Scope;

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
        return obj == null ? Undefined.VALUE : obj;
    }

    @Override
    public String toString() {
        return "module " + name;
    }
}
