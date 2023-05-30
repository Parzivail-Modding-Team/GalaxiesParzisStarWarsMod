package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Objects;

public final class BlittableDynamicAsset implements IBlittable
{
	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int sourceWidth;
	private final int sourceHeight;

	private Identifier source;

	public BlittableDynamicAsset(int u, int v, int width, int height, int sourceWidth, int sourceHeight)
	{
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.sourceWidth = sourceWidth;
		this.sourceHeight = sourceHeight;
	}

	public void setSource(Identifier source)
	{
		this.source = source;
	}

	@Override
	public void blit(DrawContext context, int destX, int destY)
	{
		blit(context, destX, destY, width, height);
	}

	@Override
	public void blit(DrawContext context, int destX, int destY, int destWidth, int destHeight)
	{
		context.drawTexture(source, destX, destY, destWidth, destHeight, u, v, width, height, sourceWidth, sourceHeight);
	}

	public int u()
	{
		return u;
	}

	public int v()
	{
		return v;
	}

	@Override
	public int width()
	{
		return width;
	}

	@Override
	public int height()
	{
		return height;
	}

	public int sourceWidth()
	{
		return sourceWidth;
	}

	public int sourceHeight()
	{
		return sourceHeight;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (BlittableDynamicAsset)obj;
		return this.u == that.u &&
		       this.v == that.v &&
		       this.width == that.width &&
		       this.height == that.height &&
		       this.sourceWidth == that.sourceWidth &&
		       this.sourceHeight == that.sourceHeight;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(u, v, width, height, sourceWidth, sourceHeight);
	}

	@Override
	public String toString()
	{
		return "BlittableDynamicAsset[" +
		       "u=" + u + ", " +
		       "v=" + v + ", " +
		       "width=" + width + ", " +
		       "height=" + height + ", " +
		       "sourceWidth=" + sourceWidth + ", " +
		       "sourceHeight=" + sourceHeight + ']';
	}
}
