package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

public class ScopeUtils {
    //
    public static boolean assign(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            target.put(key, value);
            return true;
        } else for (Scope parent : target.getParents()) {
            if (assign(parent, key, value)) {
                return true;
            }
        }
        return false;
    }

    public static void declare(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        target.put(key, value);
    }
}
