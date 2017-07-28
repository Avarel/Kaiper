package xyz.avarel.kaiper.optimizer;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.ModuleNode;
import xyz.avarel.kaiper.ast.TypeNode;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;


// IN PROGRESS
public class ExprOptimizer implements ExprVisitor<Expr, Void> {
    @Override
    public Expr visit(Statements expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(FunctionNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(Identifier expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(Invocation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BinaryOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(UnaryOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(RangeNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ArrayNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(SliceOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(AssignmentExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(GetOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(SetOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ReturnExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ConditionalExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ForEachExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DictionaryNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(NullNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(IntNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DecimalNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BooleanNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(StringNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DeclarationExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ModuleNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(TypeNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(WhileExpr expr, Void scope) {
        return null;
    }
}
