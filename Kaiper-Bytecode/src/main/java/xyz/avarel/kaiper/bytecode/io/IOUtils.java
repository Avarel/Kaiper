package xyz.avarel.kaiper.bytecode.io;

import java.io.DataInput;
import java.io.DataOutput;

public class IOUtils {
    public static ByteDataInput wrap(DataInput input) {
        return new ByteDataInput(input);
    }

    public static ByteDataOutput wrap(DataOutput output) {
        return new ByteDataOutput(output);
    }
}
