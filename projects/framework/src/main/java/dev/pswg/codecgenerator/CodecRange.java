package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts a numeric record component to an inclusive range.
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface CodecRange
{
	/**
	 * The inclusive minimum value.
	 */
	double min();

	/**
	 * The inclusive maximum value. When omitted, generated codecs use the component's numeric type maximum.
	 * The declared default is a finite placeholder; the processor distinguishes omitted and authored bounds.
	 */
	double max() default Float.MAX_VALUE;
}
