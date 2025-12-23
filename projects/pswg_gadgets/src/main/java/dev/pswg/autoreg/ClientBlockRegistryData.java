package dev.pswg.autoreg;

import dev.pswg.datagen.BlockRenderLayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientBlockRegistryData
{
	BlockRenderLayer renderLayer() default BlockRenderLayer.Default;
}
