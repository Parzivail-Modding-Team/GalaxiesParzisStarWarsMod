package com.parzivail.util.ui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class FxMC
{
	public static void drawTexturedModalRect(int x, int y, int z, int textureX, int textureY, int width, int height)
	{
		float f = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, z).tex((float)(textureX) * f, (float)(textureY + height) * f).endVertex();
		bufferbuilder.pos(x + width, y + height, z).tex((float)(textureX + width) * f, (float)(textureY + height) * f).endVertex();
		bufferbuilder.pos(x + width, y, z).tex((float)(textureX + width) * f, (float)(textureY) * f).endVertex();
		bufferbuilder.pos(x, y, z).tex((float)(textureX) * f, (float)(textureY) * f).endVertex();
		tessellator.draw();
	}
}
