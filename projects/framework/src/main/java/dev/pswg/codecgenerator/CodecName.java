package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the serialized name for a record component or enum constant.
 */
@Target({ ElementType.FIELD, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.SOURCE)
public @interface CodecName
{
	/**
	 * The name used for the component in the serialized record.
	 */
	String value();
}
