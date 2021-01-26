package com.parzivail.datagen.tarkin;

import net.minecraft.util.Identifier;

public class IdentifierUtil
{
	public static Identifier concat(String prefix, Identifier identifier)
	{
		return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
	}

	public static Identifier concat(String prefix, Identifier identifier, String suffix)
	{
		return new Identifier(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
	}

	public static Identifier concat(Identifier identifier, String suffix)
	{
		return new Identifier(identifier.getNamespace(), identifier.getPath() + suffix);
	}
}
