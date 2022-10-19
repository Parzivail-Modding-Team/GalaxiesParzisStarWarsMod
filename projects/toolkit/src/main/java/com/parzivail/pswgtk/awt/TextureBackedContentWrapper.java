package com.parzivail.pswgtk.awt;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswgtk.ToolkitClient;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.LightweightContentWrapper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.nio.IntBuffer;

public class TextureBackedContentWrapper extends LightweightContentWrapper
{
	private record BufferData(int[] buffer, int x, int y, int width, int height, int lineStride)
	{
	}

	private JComponent component;
	private BufferData pixelBuffer;
	private int textureId = -1;

	public TextureBackedContentWrapper(JComponent component)
	{
		this.component = component;
	}

	private void uploadTexture(int x, int y, int width, int height)
	{
		if (pixelBuffer == null)
			return;

		if (!RenderSystem.isOnRenderThread())
			RenderSystem.recordRenderCall(() -> uploadTexture(x, y, width, height));
		else
		{
			if (textureId == -1)
			{
				textureId = TextureUtil.generateTextureId();
				RenderSystem.bindTexture(textureId);
				TextureUtil.initTexture(IntBuffer.wrap(pixelBuffer.buffer), pixelBuffer.width, pixelBuffer.height);
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

	private void uploadTexture()
	{
		uploadTexture(pixelBuffer.x, pixelBuffer.y, pixelBuffer.width, pixelBuffer.height);
	}

	@Override
	public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride)
	{
		ToolkitClient.LOG.info("imageBufferReset");
		pixelBuffer = new BufferData(data, x, y, width, height, linestride);
		uploadTexture();
	}

	@Override
	public void imageBufferReset(int[] data, int x, int y, int width, int height, int linestride, double scaleX, double scaleY)
	{
		ToolkitClient.LOG.info("imageBufferReset[scale]");
		pixelBuffer = new BufferData(data, x, y, width, height, linestride);
		uploadTexture();
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public void paintLock()
	{
		ToolkitClient.LOG.info("paintLock");
	}

	@Override
	public void paintUnlock()
	{
		ToolkitClient.LOG.info("paintUnlock");
	}

	@Override
	public void imageReshaped(int x, int y, int width, int height)
	{
		ToolkitClient.LOG.info("imageReshaped");
	}

	@Override
	public void imageUpdated(int dirtyX, int dirtyY, int dirtyWidth, int dirtyHeight)
	{
		ToolkitClient.LOG.info("imageUpdated");
		uploadTexture(dirtyX, dirtyY, dirtyWidth, dirtyHeight);
	}

	@Override
	public void focusGrabbed()
	{
		ToolkitClient.LOG.info("focusGrabbed");
	}

	@Override
	public void focusUngrabbed()
	{
		ToolkitClient.LOG.info("focusUngrabbed");
	}

	@Override
	public void preferredSizeChanged(int width, int height)
	{
		ToolkitClient.LOG.info("preferredSizeChanged");
	}

	@Override
	public void maximumSizeChanged(int width, int height)
	{
		ToolkitClient.LOG.info("maximumSizeChanged");
	}

	@Override
	public void minimumSizeChanged(int width, int height)
	{
		ToolkitClient.LOG.info("minimumSizeChanged");
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
		ToolkitClient.LOG.info("addDropTarget");
	}

	@Override
	public void removeDropTarget(DropTarget dt)
	{
		ToolkitClient.LOG.info("removeDropTarget");
	}
}
