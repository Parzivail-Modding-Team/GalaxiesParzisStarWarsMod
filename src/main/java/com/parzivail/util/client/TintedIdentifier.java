package com.parzivail.util.client;

import net.minecraft.util.Identifier;

public class TintedIdentifier extends Identifier
{
	private final int tint;

	public TintedIdentifier(String namespace, String path, int tint)
	{
		super(namespace, path);
		this.tint = tint;
	}

	public int getTint()
	{
		return tint;
	}
}
