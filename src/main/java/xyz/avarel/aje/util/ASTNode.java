package xyz.avarel.aje.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASTNode {
    private final String name;
    private final List<ASTNode> children;

    public ASTNode(String name) {
        this(name, new ArrayList<>());
    }

    public ASTNode(String name, List<ASTNode> children) {
        this.name = name;
        this.children = children;
    }

    public void ast(StringBuilder builder) {
        ast(builder, "", true);
    }

    public ASTNode add(ASTNode... nodes) {
        children.addAll(Arrays.asList(nodes));
        return this;
    }

    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(name);
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).ast(builder, prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).ast(builder, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}