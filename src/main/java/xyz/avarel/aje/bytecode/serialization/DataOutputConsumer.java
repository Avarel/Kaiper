/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package xyz.avarel.aje.bytecode.serialization;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface DataOutputConsumer {

    void writeInto(DataOutput output) throws IOException;

    default DataOutputConsumer andThen(DataOutputConsumer after) {
        Objects.requireNonNull(after);
        return (DataOutput t) -> {
            writeInto(t);
            after.writeInto(t);
        };
    }

}
