package xyz.avarel.kaiper.vm.utils;

import java.util.Stack;
import java.util.Vector;

/**
 * A slightly modified version of the standard {@link Vector} implementation. Supports locking a part of the stack.
 *
 * @param <E>
 */
public class VMStack<E> extends Stack<E> {
    private int writeLock = -1;

    public synchronized E pop() {
        E obj;
        int len = size();

        if (!canPop()) throw new IllegalStateException("Stack Locked");

        obj = peek();

        removeElementAt(len - 1);

        return obj;
    }

    public int getLock() {
        return writeLock;
    }

    public synchronized void unlock() {
        writeLock = -1;
    }

    public synchronized int lock() {
        return setLock(size());
    }

    public synchronized int setLock(int writeLock) {
        if (writeLock < -1) writeLock = -1;

        int old = this.writeLock;
        this.writeLock = writeLock;
        return old;
    }

    public synchronized int setRelativeLock(int depth) {
        return setLock(size() - depth);
    }

    public synchronized void popToLock() {
        if (writeLock == -1) {
            clear();
            return;
        }

        int len = size();
        removeRange(writeLock, len);
    }

    public synchronized boolean canPop() {
        return size() - 1 >= writeLock;
    }
}