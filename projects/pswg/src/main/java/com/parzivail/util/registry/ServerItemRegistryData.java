package com.parzivail.util.registry;

import java.lang.annotation.Annotation;

public @interface ServerItemRegistryData
{
	String itemGroup() default "";
}

record ServerItemRegistryDataImpl(String itemGroup) implements ServerItemRegistryData {
	@Override
	public Class<ServerItemRegistryData> annotationType()
	{
		return ServerItemRegistryData.class;
	}
}
