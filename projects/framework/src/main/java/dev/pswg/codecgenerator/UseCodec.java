package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the codecs to use for the given record component. If a {@link SelfCodec}
 * annotation is defined, it will take precedence.
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface UseCodec
{
	/**
	 * The standard codec to use, or {@link GenStandardCodec#AUTOMATIC} to pick
	 * a well-suited codec based on the property type. If a custom codec
	 * is defined in the {@link #customCodec()} parameter, it will take
	 * precedence over the value of this parameter.
	 */
	GenStandardCodec codec() default GenStandardCodec.AUTOMATIC;

	/**
	 * The packet codec to use, or {@link GenPacketCodec#AUTOMATIC} to pick
	 * a well-suited packet codec based on the property type. If a custom packet
	 * codec is defined in the {@link #customPacket()} parameter, it will take
	 * precedence over the value of this parameter.
	 */
	GenPacketCodec packet() default GenPacketCodec.AUTOMATIC;

	/**
	 * The custom codec to use. Takes precedence over {@link #codec()} if both are
	 * defined.
	 */
	CodecSource customCodec() default @CodecSource(source = Void.class, member = "");

	/**
	 * The custom packet codec to use. Takes precedence over {@link #packet()} if both
	 * are defined.
	 */
	CodecSource customPacket() default @CodecSource(source = Void.class, member = "");
}

