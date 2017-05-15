package xyz.avarel.aje.runtime.types.numbers;

import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Type;

import java.util.function.BinaryOperator;

public abstract class Numeric extends Number implements Any<Numeric> {
    public static final Type<Numeric> TYPE = new Type<>("number");

    public Type<Numeric> getType() {
        return TYPE;
    }

    public static Any process(Any a, Any b, BinaryOperator<Any> function) {
        if (a.getType() == b.getType()) {
            return function.apply(a, b);
        }

        if (a instanceof Int) {
            if (b instanceof Decimal) {
                a = Decimal.of(((Int) a).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Int) a).value());
            }
        } else if (a instanceof Decimal) {
            if (b instanceof Int) {
                b = Decimal.of(((Int) b).value());
            } else if (b instanceof Complex) {
                a = Complex.of(((Decimal) a).value());
            }
        } else if (a instanceof Complex) {
            if (b instanceof Int) {
                b = Complex.of(((Int) b).value());
            } else if (b instanceof Decimal) {
                b = Complex.of(((Decimal) b).value());
            }
        }

        return function.apply(a, b);
   }

   @SuppressWarnings("unchecked")
   public static <T> T convert(Any a, Type<T> type) {
       if (a instanceof Int) {
           if (type == Int.TYPE) {
               return (T) a;
           } else if (type == Decimal.TYPE) {
               return (T) Decimal.of(((Int) a).value());
           } else if (type == Complex.TYPE) {
               return (T) Complex.of(((Int) a).value());
           }
       } else if (a instanceof Decimal) {
           if (type == Int.TYPE) {
               return (T) Int.of((int) ((Decimal) a).value());
           } else if (type == Decimal.TYPE) {
               return (T) a;
           } else if (type == Complex.TYPE) {
               return (T) Complex.of(((Decimal) a).value());
           }
       } else if (a instanceof Complex) {
           if (type == Int.TYPE) {
               return (T) Int.of((int) ((Complex) a).real());
           } else if (type == Decimal.TYPE) {
               return (T) Decimal.of(((Complex) a).real());
           } else if (type == Complex.TYPE) {
               return (T) a;
           }
       }

       return (T) a; // hope for the best.
   }
}
