package com.parzivail.pswgtk.render;

import net.minecraft.client.gl.Framebuffer;

public class TextureFramebuffer extends Framebuffer
{
	public TextureFramebuffer(boolean useDepth)
	{
		super(useDepth);
	}

	@Override
	public int getColorAttachment()
	{
		return super.getColorAttachment();
	}

	public void resizeIfRequired(int targetWidth, int targetHeight)
	{
		if (this.textureWidth == targetWidth && this.textureHeight == targetHeight)
			return;

		this.resize(targetWidth, targetHeight, false);
	}
}
