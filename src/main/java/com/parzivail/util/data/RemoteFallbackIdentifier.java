package com.parzivail.util.data;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RemoteFallbackIdentifier extends Identifier
{
	private final List<Runnable> callbacks = new ArrayList<>();

	public RemoteFallbackIdentifier(String namespace, String path)
	{
		super(namespace, path);
	}

	public void addCallback(Runnable callback)
	{
		callbacks.add(callback);
	}

	public void pollCallbacks()
	{
		callbacks.forEach(Runnable::run);
	}
}
