package com.parzivail.util.registry;

import net.minecraft.util.Identifier;

@FunctionalInterface
public interface RegistryMethod<T>
{
	void accept(T instance, Identifier registryName, boolean ignoreTab);
}
