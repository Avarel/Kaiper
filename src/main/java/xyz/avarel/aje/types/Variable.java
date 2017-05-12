package xyz.avarel.aje.types;

import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

import java.util.List;

public class Variable implements Any<Any<? extends Any>> {
    private Any value;

    public Variable(Any value) {
        this.value = value;
    }

    @Override
    public Any identity() {
        return value;
    }

    @Override
    public Any<?> set(Any other) {
        return value = other;
    }

    @Override
    public Any<?> get(String name) {
        return identity();
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
    public Any<?> minus(Any other) {
        return identity().minus(other);
    }

    @Override
    public Any<?> times(Any other) {
        return identity().times(other);
    }

    @Override
    public Any<?> divide(Any other) {
        return identity().divide(other);
    }

    @Override
    public Any<?> mod(Any other) {
        return identity().mod(other);
    }

    @Override
    public Any<?> pow(Any other) {
        return identity().pow(other);
    }

    @Override
    public Any<?> root(Any other) {
        return identity().root(other);
    }

    @Override
    public Any<?> negative() {
        return identity().negative();
    }

    @Override
    public Any<?> plus(double other) {
        return identity().plus(other);
    }

    @Override
    public Any<?> minus(double other) {
        return identity().minus(other);
    }

    @Override
    public Any<?> times(double other) {
        return identity().times(other);
    }

    @Override
    public Any<?> divide(double other) {
        return identity().divide(other);
    }

    @Override
    public Any<?> mod(double other) {
        return identity().mod(other);
    }

    @Override
    public Any<?> pow(double other) {
        return identity().pow(other);
    }

    @Override
    public Any<?> root(double other) {
        return identity().root(other);
    }

    @Override
    public Truth equals(Any other) {
        return identity().equals(other);
    }

    @Override
    public Truth greaterThan(Any other) {
        return identity().greaterThan(other);
    }

    @Override
    public Truth lessThan(Any other) {
        return identity().lessThan(other);
    }

    @Override
    public Truth greaterThanOrEqual(Any other) {
        return identity().greaterThanOrEqual(other);
    }

    @Override
    public Truth lessThanOrEqual(Any other) {
        return identity().lessThanOrEqual(other);
    }

    @Override
    public Slice rangeTo(Any other) {
        return identity().rangeTo(other);
    }

    @Override
    public Any<?> invoke(List<Any> args) {
        return identity().invoke(args);
    }

    @Override
    public String toString() {
        return "Variable(" + identity().toString() + ")";
    }
}
