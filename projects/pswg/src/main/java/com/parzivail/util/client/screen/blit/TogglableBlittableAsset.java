package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawContext;

public class TogglableBlittableAsset implements IBlittable, IHoverable
{
	private final IBlittable off;
	private final IBlittable on;
	private boolean toggled;

	public TogglableBlittableAsset(IBlittable off, IBlittable on)
	{
		this.off = off;
		this.on = on;
	}

	@Override
	public void setHovering(boolean hovering)
	{
		if (off instanceof IHoverable h)
			h.setHovering(hovering);
		if (on instanceof IHoverable h)
			h.setHovering(hovering);
	}

	@Override
	public boolean isHovering()
	{
		if (toggled)
			return on instanceof IHoverable h && h.isHovering();
		return off instanceof IHoverable h && h.isHovering();
	}

	public void setToggled(boolean toggled)
	{
		this.toggled = toggled;
	}

	public boolean isToggled()
	{
		return toggled;
	}

	@Override
	public int width()
	{
		return (toggled ? on : off).width();
	}

	@Override
	public int height()
	{
		return (toggled ? on : off).height();
	}

	@Override
	public void blit(DrawContext context, int destX, int destY)
	{
		(toggled ? on : off).blit(context, destX, destY);
	}

	@Override
	public void blit(DrawContext context, int destX, int destY, int destWidth, int destHeight)
	{
		(toggled ? on : off).blit(context, destX, destY, destWidth, destHeight);
	}
}
