package com.parzivail.pswg.container.registry;

import net.minecraft.util.Identifier;

@FunctionalInterface
public interface RegistryMethod<T>
{
	void accept(T instance, Identifier registryName, boolean ignoreTab);
}
