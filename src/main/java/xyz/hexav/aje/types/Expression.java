package xyz.hexav.aje.types;

import java.util.function.BinaryOperator;

/*
 * Every operation results in a number.
 * Everything must be returned in the form of AJENumber.
 *      Except for eval();
 */
public interface Expression {
    Expression NaN = ofValue(Double.NaN);

    Expression TRUE = ofValue(1);
    Expression FALSE = ofValue(0);

    double value();

    default Expression compile(BinaryOperator<Expression> operation) {
        return compile(operation, null);
    }

    default Expression compile(BinaryOperator<Expression> operation, Expression number) {
        return operation.apply(this, number);
    }

    default Expression andThen(Expression number) {
        return () -> {
            value();
            return number.value();
        };
    }

    default String asString() {
        return String.valueOf(value());
    }

    static Expression ofValue(double value) {
        return () -> value;
    }
}

//    default AJEValue add(AJEValue number) {
//        return () -> this.value() + number.value();
//    }
//
//    default AJEValue minus(AJEValue number) {
//        return () -> this.value() - number.value();
//    }
//
//    default AJEValue times(AJEValue number) {
//        return () -> this.value() * number.value();
//    }
//
//    default AJEValue divide(AJEValue number) {
//        return () -> this.value() / number.value();
//    }
//
//    default AJEValue rem(AJEValue number) {
//        return () -> this.value() % number.value();
//    }
//
//    default AJEValue mod(AJEValue number) {
//        return () -> {
//            double val1 = this.value();
//            double val2 = number.value();
//            return (val1 % val2 + val2) % val2;
//        };
//    }
//
//    default AJEValue isEqualTo(Object obj) {
//        if (obj instanceof AJEValue) {
//            AJEValue number = (AJEValue) obj;
//            return () -> this.value() == number.value() ? TRUE.value() : FALSE.value();
//        } else {
//            return AJEValue.FALSE;
//        }
//    }
//
//    default AJEValue greaterThan(Object obj) {
//        if (obj instanceof AJEValue) {
//            AJEValue number = (AJEValue) obj;
//            return () -> this.value() > number.value() ? TRUE.value() : FALSE.value();
//        } else {
//            return AJEValue.FALSE;
//        }
//    }
//
//    default AJEValue lessThan(Object obj) {
//        if (obj instanceof AJEValue) {
//            AJEValue number = (AJEValue) obj;
//            return () -> this.value() < number.value() ? TRUE.value() : FALSE.value();
//        } else {
//            return AJEValue.FALSE;
//        }
//    }