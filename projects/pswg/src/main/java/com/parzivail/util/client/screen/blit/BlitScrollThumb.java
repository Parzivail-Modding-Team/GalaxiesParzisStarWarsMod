package com.parzivail.util.client.screen.blit;

import com.parzivail.util.math.MathUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class BlitScrollThumb
{
	private int originX;
	private int originY;

	private final int trackX;
	private final int trackY;
	private final int trackSize;
	private final IBlittable thumb;

	private boolean visible;

	private boolean scrolling;
	private float scroll;
	private float scrollInputFactor = 1;

	public BlitScrollThumb(IBlittable thumb, int trackX, int trackY, int trackSize)
	{
		this.thumb = thumb;
		this.trackX = trackX;
		this.trackY = trackY;
		this.trackSize = trackSize;
	}

	public void blitVertical(MatrixStack matrices)
	{
		if (!visible)
			return;

		thumb.blit(matrices, this.originX + trackX, this.originY + trackY + (int)((trackSize - thumb.height()) * scroll));
	}

	public void blitHorizontal(MatrixStack matrices)
	{
		if (!visible)
			return;

		thumb.blit(matrices, this.originX + trackX + (int)((trackSize - thumb.width()) * scroll), this.originY + trackY);
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

	public void setVisible(boolean visible)
	{
		this.visible = visible;
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

	public boolean containsVertical(int mouseX, int mouseY)
	{
		if (!visible)
			return false;

		return MathUtil.rectContains(this.originX + trackX, this.originY + trackY, thumb.width(), trackSize, mouseX, mouseY);
	}

	public boolean containsHorizontal(int mouseX, int mouseY)
	{
		if (!visible)
			return false;

		return MathUtil.rectContains(this.originX + trackX, this.originY + trackY, trackSize, thumb.height(), mouseX, mouseY);
	}

	public void updateMouseStateHorizontal(int mouseX, int mouseY)
	{
		if (!visible)
			return;

		if (thumb instanceof IHoverable h)
			h.setHovering(scrolling || MathUtil.rectContains(this.originX + trackX + (int)((trackSize - thumb.width()) * scroll), this.originY + trackY, thumb.width(), thumb.height(), mouseX, mouseY));

		var halfWidth = thumb.width() / 2;
		if (scrolling)
			scroll = MathHelper.clamp((mouseX - (this.originX + trackX) - halfWidth) / (float)(trackSize - thumb.width()), 0, 1);
	}

	public void updateMouseStateVertical(int mouseX, int mouseY)
	{
		if (!visible)
			return;

		if (thumb instanceof IHoverable h)
			h.setHovering(scrolling || MathUtil.rectContains(this.originX + trackX, this.originY + trackY + (int)((trackSize - thumb.height()) * scroll), thumb.width(), thumb.height(), mouseX, mouseY));

		var halfHeight = thumb.height() / 2;
		if (scrolling)
			scroll = MathHelper.clamp((mouseY - (this.originY + trackY) - halfHeight) / (float)(trackSize - thumb.height()), 0, 1);
	}

	public void inputScroll(double amount)
	{
		if (!visible)
			return;

		scroll = (float)MathHelper.clamp(scroll - amount / scrollInputFactor, 0, 1);
	}
}
