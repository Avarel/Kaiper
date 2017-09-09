package xyz.avarel.kaiper.bytecode;

public class ReservedOpcode implements Opcode {
    private final int id;

    public ReservedOpcode(int id) {
        this.id = id;
    }

    @Override
    public int code() {
        return id;
    }

    @Override
    public String name() {
        return String.format("RESERVED_%03d", id);
    }
}
