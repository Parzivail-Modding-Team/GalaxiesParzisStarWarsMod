package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts the number of elements in a list or map record component.
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface CodecSize
{
	/**
	 * The minimum inclusive number of elements. Defaults to zero.
	 */
	int min() default 0;

	/**
	 * The maximum inclusive number of elements.
	 */
	int max() default Integer.MAX_VALUE;
}
