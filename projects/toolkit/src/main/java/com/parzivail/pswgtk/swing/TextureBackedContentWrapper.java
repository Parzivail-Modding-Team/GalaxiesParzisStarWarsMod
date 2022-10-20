package com.parzivail.pswgtk.swing;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.LightweightContentWrapper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.nio.IntBuffer;

public class TextureBackedContentWrapper extends LightweightContentWrapper
{
	public static final int MASK_COLOR = 0xFFFF00FF;

	private record BufferData(int[] buffer, int x, int y, int width, int height, int lineStride)
	{
	}

	private JComponent component;
	private BufferData pixelBuffer;
	private int textureId = -1;

	private int width = 0;
	private int height = 0;

	public TextureBackedContentWrapper(JComponent component)
	{
		this.component = component;
	}

	private void uploadTexture(boolean destroy, int x, int y, int width, int height)
	{
		if (pixelBuffer == null)
			return;

		if (!RenderSystem.isOnRenderThread())
			RenderSystem.recordRenderCall(() -> uploadTexture(destroy, x, y, width, height));
		else
		{
			// TODO: replace with fragment shader?
			for (int y1 = y; y1 < height; y1++)
				for (int x1 = x; x1 < width; x1++)
				{
					var i = y1 * pixelBuffer.lineStride + x1;
					if (pixelBuffer.buffer[i] == MASK_COLOR)
						pixelBuffer.buffer[i] = 0;
				}

			if (destroy && textureId != -1)
			{
				TextureUtil.releaseTextureId(textureId);
				textureId = -1;
			}

			if (textureId == -1)
			{
				textureId = TextureUtil.generateTextureId();
				RenderSystem.bindTexture(textureId);

				IntBuffer intBuffer = BufferUtils.createIntBuffer(pixelBuffer.buffer.length);
				intBuffer.put(pixelBuffer.buffer);
				intBuffer.flip();

				TextureUtil.initTexture(intBuffer, pixelBuffer.width, pixelBuffer.height);
			}
			else
			{
				RenderSystem.bindTexture(textureId);
				//				GL11.glTexSubImage2D(GlConst.GL_TEXTURE_2D, 0, x, y, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer.buffer);
				GL11.glTexSubImage2D(GlConst.GL_TEXTURE_2D, 0, pixelBuffer.x, pixelBuffer.y, pixelBuffer.width, pixelBuffer.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer.buffer);
			}
		}
	}

	public int getTextureId()
	{
		return textureId;
	}

	public float getVisibleWidthFraction()
	{
		if (pixelBuffer == null)
			return 1;
		return width / (float)pixelBuffer.width;
	}

	public float getVisibleHeightFraction()
	{
		if (pixelBuffer == null)
			return 1;
		return height / (float)pixelBuffer.height;
	}

	private void uploadTexture(boolean destroy)
	{
		uploadTexture(destroy, pixelBuffer.x, pixelBuffer.y, pixelBuffer.width, pixelBuffer.height);
	}

	@Override
	public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride)
	{
		this.width = width;
		this.height = height;

		var destroy = pixelBuffer != null && (width != pixelBuffer.width || height != pixelBuffer.height);
		pixelBuffer = new BufferData(data, x, y, width, height, linestride);
		uploadTexture(destroy);
	}

	@Override
	public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride, double scaleX, double scaleY)
	{
		this.width = width;
		this.height = height;

		var destroy = pixelBuffer != null && (width != pixelBuffer.width || height != pixelBuffer.height);
		pixelBuffer = new BufferData(data, x, y, width, height, linestride);
		uploadTexture(destroy);
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public void paintLock()
	{
	}

	@Override
	public void paintUnlock()
	{
	}

	@Override
	public void imageReshaped(int x, int y, int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	@Override
	public void imageUpdated(int dirtyX, int dirtyY, int dirtyWidth, int dirtyHeight)
	{
		uploadTexture(false, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
	}

	@Override
	public void focusGrabbed()
	{
	}

	@Override
	public void focusUngrabbed()
	{
	}

	@Override
	public void preferredSizeChanged(int width, int height)
	{
	}

	@Override
	public void maximumSizeChanged(int width, int height)
	{
	}

	@Override
	public void minimumSizeChanged(int width, int height)
	{
	}

	@Override
	public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> abstractRecognizerClass, DragSource ds, Component c, int srcActions, DragGestureListener dgl)
	{
		return null;
	}

	@Override
	public DragSourceContextWrapper createDragSourceContext(DragGestureEvent dge) throws InvalidDnDOperationException
	{
		return null;
	}

	@Override
	public void addDropTarget(DropTarget dt)
	{
	}

	@Override
	public void removeDropTarget(DropTarget dt)
	{
	}
}
