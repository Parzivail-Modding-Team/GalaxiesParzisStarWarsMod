package com.parzivail.util.data;

import net.minecraft.util.Identifier;

public class FallbackIdentifier extends Identifier
{
	private final Identifier source;

	public FallbackIdentifier(String namespace, String path, Identifier source)
	{
		super(namespace, path);
		this.source = source;
	}

	public Identifier getSource()
	{
		return source;
	}
}
