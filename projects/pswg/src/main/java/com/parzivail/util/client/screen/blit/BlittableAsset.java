package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public record BlittableAsset(int u, int v, int width, int height, int sourceWidth, int sourceHeight) implements IBlittable
{
	public void blit(MatrixStack matrices, int destX, int destY)
	{
		blit(matrices, destX, destY, width, height);
	}

	public void blit(MatrixStack matrices, int destX, int destY, int destWidth, int destHeight)
	{
		DrawableHelper.drawTexture(matrices, destX, destY, destWidth, destHeight, u, v, width, height, sourceWidth, sourceHeight);
	}
}
