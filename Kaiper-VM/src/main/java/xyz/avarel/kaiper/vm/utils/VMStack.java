/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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