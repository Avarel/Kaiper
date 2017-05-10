package xyz.avarel.aje;

import java.util.ArrayList;
import java.util.List;

public class RecursiveDescentParser<T> {
    private final List<Procedure<T>> procedures = new ArrayList<>();

    public T parse() {
        Procedure<T> p = procedures.get(0);
        return p.parse(p, this);
    }

    public T go(int precedence) {
        Procedure<T> p = procedures.get(precedence);
        return p.parse(p, this);
    }

    public T next(Procedure<T> procedure) {
        Procedure<T> p = procedures.get(procedures.indexOf(procedure) + 1);
        return p.parse(p, this);
    }

    public void addProcedure(Procedure<T> procedure) {
        procedures.add(procedure);
    }

    public void addProcedure(int pos, Procedure<T> procedure) {
        procedures.add(pos, procedure);
    }

    @FunctionalInterface
    public interface Procedure<T> {
        T parse(Procedure<T> self, RecursiveDescentParser<T> parser);
    }
}
