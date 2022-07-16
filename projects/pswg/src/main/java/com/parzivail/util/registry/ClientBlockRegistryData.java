package com.parzivail.util.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ClientBlockRegistryData
{
	boolean isConnected() default false;

	RenderLayerHint renderLayer() default RenderLayerHint.NONE;
}
