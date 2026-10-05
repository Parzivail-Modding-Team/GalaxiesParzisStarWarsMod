package dev.pswg.codecgenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a static codec field to use for a record component.
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface CodecSource
{
	/**
	 * The class declaring the codec field.
	 */
	Class<?> source();

	/**
	 * The name of the static codec field.
	 */
	String member();
}
