package dev.pswg.codecgenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that standard and packet codecs should be generated for
 * the annotated type. The {@code GenerateCodec} annotation is processed
 * by the {@code CodecGenerationProcessor}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateCodec
{
}

