package com.parzivail.util.data;

import net.minecraft.util.Identifier;

public class TintedIdentifier extends Identifier
{
	public enum Mode
	{
		Multiply,
		Add,
		Overlay
	}

	private final int tint;
	private final Mode tintMode;

	public TintedIdentifier(String namespace, String path, int tint)
	{
		this(namespace, path, tint, Mode.Multiply);
	}

	public TintedIdentifier(String namespace, String path, int tint, Mode mode)
	{
		super(namespace, path);
		this.tint = tint;
		this.tintMode = mode;
	}

	public TintedIdentifier(Identifier other, int tint, Mode mode)
	{
		super(other.getNamespace(), other.getPath());
		this.tint = tint;
		this.tintMode = mode;
	}

	public int getTint()
	{
		return tint;
	}

	public Mode getTintMode()
	{
		return tintMode;
	}
}
