package com.parzivail.util.ui.maui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class NinePatchResource
{
	private final ResourceLocation resourceLocation;
	private final int width;
	private final int height;
	private final int contentCenterX;
	private final int contentCenterY;
	private final int contentCenterWidth;
	private final int contentCenterHeight;
	private final int topEdgeSize;
	private final int bottomEdgeSize;
	private final int leftEdgeSize;
	private final int rightEdgeSize;

	public NinePatchResource(ResourceLocation resourceLocation, int width, int height, int contentCenterX, int contentCenterY, int contentCenterWidth, int contentCenterHeight)
	{
		this.resourceLocation = resourceLocation;
		this.width = width;
		this.height = height;
		this.contentCenterX = contentCenterX;
		this.contentCenterY = contentCenterY;
		this.contentCenterWidth = contentCenterWidth;
		this.contentCenterHeight = contentCenterHeight;

		topEdgeSize = contentCenterY;
		bottomEdgeSize = height - contentCenterHeight - topEdgeSize;
		leftEdgeSize = contentCenterX;
		rightEdgeSize = width - contentCenterWidth - leftEdgeSize;
	}

	public void draw(int width, int height)
	{
		Client.mc.renderEngine.bindTexture(resourceLocation);

		ScaledResolution r = Client.resolution;
		float factor = r.getScaleFactor();

		GL.PushMatrix();
		float xScale = (width * factor - leftEdgeSize - rightEdgeSize) / (float)contentCenterWidth;
		float yScale = (height * factor - topEdgeSize - bottomEdgeSize) / (float)contentCenterHeight;

		GL.Scale(1f / factor);

		/*
		|A|B|C|
		-------
		|D|E|F|
		-------
		|G|H|I|
		 */

		GL.Begin(PrimitiveType.Quads);

		// A
		GL.TexCoord2((0) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(0, 0);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(leftEdgeSize, 0);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize);
		GL.TexCoord2((0) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(0, topEdgeSize);

		// B
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(leftEdgeSize, 0);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, 0);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize);

		// C
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, 0);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (0) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, 0);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, topEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize);

		// D
		GL.TexCoord2((0) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(0, topEdgeSize);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((0) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(0, topEdgeSize + contentCenterHeight * yScale);

		// E
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale);

		// D
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (topEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, topEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale);

		// G
		GL.TexCoord2((0) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(0, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);
		GL.TexCoord2((0) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(0, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);

		// H
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);
		GL.TexCoord2((leftEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);

		// I
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, topEdgeSize + contentCenterHeight * yScale);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth + rightEdgeSize) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale + rightEdgeSize, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);
		GL.TexCoord2((leftEdgeSize + contentCenterWidth) / (float)this.width, (topEdgeSize + contentCenterHeight + bottomEdgeSize) / (float)this.height);
		GL.Vertex2(leftEdgeSize + contentCenterWidth * xScale, topEdgeSize + contentCenterHeight * yScale + bottomEdgeSize);

		GL.End();
		GL.PopMatrix();
	}
}
