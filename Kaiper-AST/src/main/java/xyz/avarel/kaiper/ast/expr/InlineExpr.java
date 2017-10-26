//package xyz.avarel.kaiper.ast.expr;
//
//import xyz.avarel.kaiper.ast.ExprVisitor;
//
//import java.util.List;
//
//public class InlineExpr extends Expr {
//    private Expr expr;
//    private final List<Expr> inline;
//
//    public InlineExpr(Expr expr, List<Expr> inline) {
//        super(null);
//        this.expr = expr;
//        this.inline = inline;
//    }
//
//    public Expr getExpr() {
//        return expr;
//    }
//
//    public List<Expr> getInline() {
//        return inline;
//    }
//
//    @Override
//    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
//        throw new UnsupportedOperationException("Internal error");
//    }
//}
