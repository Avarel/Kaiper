package xyz.hexav.aje.expressions;

import xyz.hexav.aje.Function;

import java.util.Arrays;
import java.util.List;

public class FunctionExpression implements Expression {
    private final Function function;
    private final List<Expression> args;

    public FunctionExpression(Function function, List<Expression> args) {
        this.function = function;
        this.args = args;
    }

    @Override
    public double[] evalList() {
        double[][] lists = new double[args.size()][];

        int length = 1;
        int index = 0;
        for (Expression exp : args) {
            double[] arr = exp.evalList();

            if (exp instanceof SpreadExpression) {
                if (arr.length == 1) {
                    lists[index] = arr;
                    index++;
                    continue;
                }

                // Expand and spread the argument-pool to include
                // each of the list's arguments.
                lists = Arrays.copyOf(lists, lists.length + arr.length - 1);

                for (double item : arr) {
                    lists[index] = new double[]{item};
                    index++;
                }
            } else {
                // Result = size of lowest list of which size is not 1.

                lists[index] = arr;

                if (arr.length != 1 && length == 1) length = arr.length;
                else if (arr.length < length) length = arr.length;
                index++;
            }
        }

        double[] results = new double[length];

        index = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < lists.length; j++) {
                function.input(j, lists[j][i]);
            }

            double[] res = function.evalList();

            if (res.length == 1) {
                results[i] = res[0];
                index++;
            } else {
                results = Arrays.copyOf(results, results.length + res.length - 1);

                for (double item : res) {
                    results[index] = item;
                    index++;
                }
            }
        }

        return results;
    }

}
