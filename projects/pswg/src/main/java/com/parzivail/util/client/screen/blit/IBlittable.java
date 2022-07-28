package com.parzivail.util.client.screen.blit;

import net.minecraft.client.util.math.MatrixStack;

public interface IBlittable
{
	int width();
	int height();

	void blit(MatrixStack matrices, int destX, int destY);
	void blit(MatrixStack matrices, int destX, int destY, int destWidth, int destHeight);
}
