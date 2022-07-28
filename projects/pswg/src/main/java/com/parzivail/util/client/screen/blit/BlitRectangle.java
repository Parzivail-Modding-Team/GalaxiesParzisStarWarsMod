package com.parzivail.util.client.screen.blit;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.util.math.MatrixStack;

public class BlitRectangle
{
	private final IBlittable blittable;

	public int x;
	public int y;
	public int width;
	public int height;

	public int originX;
	public int originY;

	public BlitRectangle(IBlittable blittable, int x, int y, int width, int height)
	{
		this.blittable = blittable;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public BlitRectangle(IBlittable blittable, int x, int y)
	{
		this(blittable, x, y, blittable.width(), blittable.height());
	}

	public boolean contains(int pX, int pY)
	{
		return MathUtil.rectContains(originX + x, originY + y, width, height, pX, pY);
	}

	public void updateMouseState(int mouseX, int mouseY)
	{
		if (!(blittable instanceof IHoverable hoverable))
			return;

		hoverable.setHovering(contains(mouseX, mouseY));
	}

	public void setOrigin(int originX, int originY)
	{
		this.originX = originX;
		this.originY = originY;
	}

	public void blit(MatrixStack matrices)
	{
		blittable.blit(matrices, originX + x, originY + y, width, height);
	}
}
