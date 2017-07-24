package xyz.avarel.kaiper.ast;

import xyz.avarel.kaiper.lexer.Position;

public class AnyNode extends Expr {
    public AnyNode(Position position) {
        super(position);
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return null;
    }
}
