package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a list to contain unique values, optionally compared by an element accessor.
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface CodecUnique
{
	/**
	 * Element accessor used as the unique key; empty compares the whole element.
	 */
	String key() default "";
}
