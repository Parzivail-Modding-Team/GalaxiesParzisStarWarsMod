package com.parzivail.util.client.screen.blit;

import net.minecraft.client.util.math.MatrixStack;

public class HoverableBlittableAsset implements IBlittable, IHoverable
{
	private final IBlittable normal;
	private final IBlittable hover;
	private boolean hovering;

	public HoverableBlittableAsset(IBlittable normal, IBlittable hover)
	{
		this.normal = normal;
		this.hover = hover;
	}

	@Override
	public void setHovering(boolean hovering)
	{
		this.hovering = hovering;
	}

	@Override
	public boolean isHovering()
	{
		return hovering;
	}

	@Override
	public int width()
	{
		return (hovering ? hover : normal).width();
	}

	@Override
	public int height()
	{
		return (hovering ? hover : normal).height();
	}

	@Override
	public void blit(MatrixStack matrices, int destX, int destY)
	{
		(hovering ? hover : normal).blit(matrices, destX, destY);
	}

	@Override
	public void blit(MatrixStack matrices, int destX, int destY, int destWidth, int destHeight)
	{
		(hovering ? hover : normal).blit(matrices, destX, destY, destWidth, destHeight);
	}
}
