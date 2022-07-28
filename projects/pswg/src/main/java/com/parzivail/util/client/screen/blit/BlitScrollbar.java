package com.parzivail.util.client.screen.blit;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class BlitScrollbar
{
	private int originX;
	private int originY;

	private final int trackX;
	private final int trackY;
	private final int trackHeight;
	private final IBlittable thumb;

	private boolean scrolling;
	private float scroll;
	private float scrollInputFactor = 1;

	public BlitScrollbar(IBlittable thumb, int trackX, int trackY, int trackHeight)
	{
		this.thumb = thumb;
		this.trackX = trackX;
		this.trackY = trackY;
		this.trackHeight = trackHeight;
	}

	public void blit(MatrixStack matrices)
	{
		thumb.blit(matrices, this.originX + trackX, this.originY + trackY + (int)((trackHeight - thumb.height()) * scroll));
	}

	public void setOrigin(int originX, int originY)
	{
		this.originX = originX;
		this.originY = originY;
	}

	public boolean isScrolling()
	{
		return scrolling;
	}

	public void setScrolling(boolean scrolling)
	{
		this.scrolling = scrolling;
	}

	public float getScroll()
	{
		return scroll;
	}

	public void setScroll(float scroll)
	{
		this.scroll = scroll;
	}

	public void setScrollInputFactor(float scrollInputFactor)
	{
		this.scrollInputFactor = Math.max(scrollInputFactor, 1);
	}

	public boolean contains(int mouseX, int mouseY)
	{
		return MathUtil.rectContains(this.originX + trackX, this.originY + trackY, thumb.width(), trackHeight, mouseX, mouseY);
	}

	public void updateMouseState(int mouseX, int mouseY)
	{
		if (thumb instanceof IHoverable h)
			h.setHovering(scrolling || MathUtil.rectContains(this.originX + trackX, this.originY + trackY + (int)((trackHeight - thumb.height()) * scroll), thumb.width(), thumb.height(), mouseX, mouseY));

		var halfHeight = thumb.height() / 2;
		if (scrolling)
			scroll = MathHelper.clamp((mouseY - (this.originY + trackY) - halfHeight) / (float)(trackHeight - thumb.height()), 0, 1);
	}

	public void inputScroll(double amount)
	{
		scroll = (float)MathHelper.clamp(scroll - amount / scrollInputFactor, 0, 1);
	}
}
