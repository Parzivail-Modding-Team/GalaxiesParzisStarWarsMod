package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that standard and, optionally, packet codecs should be generated
 * for the annotated record. The {@code GenerateCodec} annotation is processed
 * by the {@code CodecGenerationProcessor}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateCodec
{
	/**
	 * Whether a packet codec should also be generated. Defaults to {@code true}.
	 */
	boolean packetCodec() default true;

	/**
	 * Whether generated standard codecs should reject unknown fields and convert
	 * decoder exceptions into codec errors.
	 */
	boolean strict() default false;
}

