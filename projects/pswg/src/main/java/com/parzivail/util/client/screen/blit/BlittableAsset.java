package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public record BlittableAsset(Identifier source, int u, int v, int width, int height, int sourceWidth, int sourceHeight) implements IBlittable
{
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
}
