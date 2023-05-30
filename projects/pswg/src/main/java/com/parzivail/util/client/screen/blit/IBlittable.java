package com.parzivail.util.client.screen.blit;

import net.minecraft.client.gui.DrawContext;

public interface IBlittable
{
	int width();

	int height();

	void blit(DrawContext context, int destX, int destY);

	void blit(DrawContext context, int destX, int destY, int destWidth, int destHeight);
}
