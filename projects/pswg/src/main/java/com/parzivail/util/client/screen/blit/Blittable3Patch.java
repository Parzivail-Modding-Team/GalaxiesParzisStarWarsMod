package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawContext;

public record Blittable3Patch(IBlittable first, IBlittable center, IBlittable second, int leftBleed, int rightBleed)
{
	public void blitHorizontal(DrawContext context, int x, int y, int width)
	{
		var leftInnerWidth = first.width() - leftBleed;
		var rightInnerWidth = second.width() - rightBleed;
		var centerWidth = width - leftInnerWidth - rightInnerWidth;

		first.blit(context, x - leftBleed, y);
		center.blit(context, x + leftInnerWidth, y, centerWidth, center.height());
		second.blit(context, x + leftInnerWidth + centerWidth, y);
	}

	public void blitVertical(DrawContext context, int x, int y, int height)
	{
		var leftInnerHeight = first.width() - leftBleed;
		var rightInnerHight = second.width() - rightBleed;
		var centerHeight = height - leftInnerHeight - rightInnerHight;

		first.blit(context, x, y - leftBleed);
		center.blit(context, x, y + leftInnerHeight, center.width(), centerHeight);
		second.blit(context, x, y + rightInnerHight + centerHeight);
	}
}
