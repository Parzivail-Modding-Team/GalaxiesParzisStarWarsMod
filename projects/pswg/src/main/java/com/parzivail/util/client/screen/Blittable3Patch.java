package com.parzivail.util.client.screen;

import net.minecraft.client.util.math.MatrixStack;

public record Blittable3Patch(BlittableAsset first, BlittableAsset center, BlittableAsset second, int leftBleed, int rightBleed)
{
	public void blitHorizontal(MatrixStack matrices, int x, int y, int width)
	{
		var leftInnerWidth = first.width() - leftBleed;
		var rightInnerWidth = second.width() - rightBleed;
		var centerWidth = width - leftInnerWidth - rightInnerWidth;

		first.blit(matrices, x - leftBleed, y);
		center.blit(matrices, x + leftInnerWidth, y, centerWidth, center.height());
		second.blit(matrices, x + leftInnerWidth + centerWidth, y);
	}

	public void blitVertical(MatrixStack matrices, int x, int y, int width)
	{
		var leftInnerHeight = first.width() - leftBleed;
		var rightInnerHight = second.width() - rightBleed;
		var centerHeight = width - leftInnerHeight - rightInnerHight;

		first.blit(matrices,  x, y - leftBleed);
		center.blit(matrices, x, y + leftInnerHeight, center.width(), centerHeight);
		second.blit(matrices, x, y + rightInnerHight + centerHeight);
	}
}
