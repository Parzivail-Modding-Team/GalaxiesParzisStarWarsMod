package com.parzivail.pswg.container.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ClientRegistryData
{
	boolean isConnected() default false;

	RenderLayerHint renderLayer() default RenderLayerHint.NONE;
}
