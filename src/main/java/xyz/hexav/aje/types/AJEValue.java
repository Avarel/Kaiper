package xyz.hexav.aje.types;

/*
 * Every operation results in a number.
 * Everything must be returned in the form of AJENumber.
 *      Except for eval();
 */
public interface AJEValue {
    AJEValue NaN = ofValue(Double.NaN);

    AJEValue TRUE = ofValue(1);
    AJEValue FALSE = ofValue(0);

    double value();

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

    default AJEValue andThen(AJEValue number) {
        return () -> {
            value();
            return number.value();
        };
    }

    default String asString() {
        return String.valueOf(value());
    }

//    default AJENumber invoke() {
//
//    }
//
//    default AJENumber invoke(List<AJENumber> args) {
//        return NOTHING;
//    }



    static AJEValue ofValue(double value) {
        return () -> value;
    }
}
