package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a string-representable codec interface for the annotated enum.
 * The generated interface is placed in {@code dev.pswg.generated.codecs} and
 * must be implemented by the enum.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateEnumCodec
{
}
