package xyz.avarel.kaiper.runtime.functions.pattern;

import xyz.avarel.kaiper.runtime.Obj;

import java.util.Map;

public class NativeBinder {
    private final Map<String, Obj> scope;

    private int position = 0;

    public NativeBinder(Map<String, Obj> scope) {
        this.scope = scope;
    }
}
