package xyz.avarel.aje.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Represents a Feature that is currently being Incubated.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({TYPE, FIELD, METHOD})
@Documented
public @interface Incubating {
}
