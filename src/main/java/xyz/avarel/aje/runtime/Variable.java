package xyz.avarel.aje.runtime;

import java.util.List;

public class Variable implements Any {
    private Any value;

    public Variable(Any value) {
        this.value = value;
    }

    @Override
    public Any identity() {
        return value;
    }

    @Override
    public Any set(Any other) {
        return value = other;
    }

    @Override
    public Any attribute(String name) {
        return identity().attribute(name);
    }

    @Override
    public Type getType() {
        return identity().getType();
    }

    @Override
    public boolean isNativeObject() {
        return identity().isNativeObject();
    }

    @Override
    public Object toNative() {
        return identity().toNative();
    }

    @Override
    public Any plus(Any other) {
        return identity().plus(other);
    }

    @Override
    public Any minus(Any other) {
        return identity().minus(other);
    }

    @Override
    public Any times(Any other) {
        return identity().times(other);
    }

    @Override
    public Any divide(Any other) {
        return identity().divide(other);
    }

    @Override
    public Any mod(Any other) {
        return identity().mod(other);
    }

    @Override
    public Any pow(Any other) {
        return identity().pow(other);
    }

    @Override
    public Any negative() {
        return identity().negative();
    }

    @Override
    public Any plus(double other) {
        return identity().plus(other);
    }

    @Override
    public Any minus(double other) {
        return identity().minus(other);
    }

    @Override
    public Any times(double other) {
        return identity().times(other);
    }

    @Override
    public Any divide(double other) {
        return identity().divide(other);
    }

    @Override
    public Any mod(double other) {
        return identity().mod(other);
    }

    @Override
    public Any pow(double other) {
        return identity().pow(other);
    }

    @Override
    public Any isEqualTo(Any other) {
        return identity().isEqualTo(other);
    }

    @Override
    public Any greaterThan(Any other) {
        return identity().greaterThan(other);
    }

    @Override
    public Any lessThan(Any other) {
        return identity().lessThan(other);
    }

    @Override
    public Any rangeTo(Any other) {
        return identity().rangeTo(other);
    }

    @Override
    public Any invoke(List<Any> args) {
        return identity().invoke(args);
    }

    @Override
    public String toString() {
        return "Variable(" + identity().toString() + ")";
    }
}
