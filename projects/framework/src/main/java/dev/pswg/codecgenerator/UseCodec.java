package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the codecs to use for the given record component
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface UseCodec
{
	/**
	 * The standard codec to use, or {@link GenStandardCodec#AUTOMATIC} to pick
	 * a well-suited codec based on the property type
	 */
	GenStandardCodec codec() default GenStandardCodec.AUTOMATIC;

	/**
	 * The packet codec to use, or {@link GenPacketCodec#AUTOMATIC} to pick
	 * a well-suited packet codec based on the property type
	 */
	GenPacketCodec packet() default GenPacketCodec.AUTOMATIC;
}
