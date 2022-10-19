package com.parzivail.pswgtk.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.awt.TextureBackedContentWrapper;
import jdk.swing.interop.LightweightFrameWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.*;

public class ToolkitHomeScreen extends Screen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private final Screen parent;
	private final Thread swingThread;
	private final LightweightFrameWrapper frame;
	private TextureBackedContentWrapper contentWrapper;

	public ToolkitHomeScreen(Screen parent)
	{
		super(Text.translatable(I18N_TOOLKIT_HOME));
		this.parent = parent;

		System.setProperty("java.awt.headless", "false");
		this.frame = new LightweightFrameWrapper();

		swingThread = new Thread(null, this::runSwing, "pswg-toolkit-awt");
		swingThread.start();
	}

	private void runSwing()
	{
		assert this.client != null;

		var panel = new JPanel();
		panel.setBackground(Color.MAGENTA);
		var b = new JButton("Button");
		panel.add(b);

		var window = this.client.getWindow();
		runOnEDT(() -> {
			contentWrapper = new TextureBackedContentWrapper(panel);
			frame.setContent(contentWrapper);
			frame.setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		});

		frame.setVisible(true);
	}

	@Override
	protected void init()
	{
		assert this.client != null;

		super.init();

		var window = this.client.getWindow();
		runOnEDT(() -> {
			if (contentWrapper != null)
				frame.setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		});
	}

	private static void runOnEDT(final Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
	}

	@Override
	public void close()
	{
		assert this.client != null;
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFFF0F0F0, 0xFFF0F0F0);

		if (contentWrapper != null)
		{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, contentWrapper.getTextureId());
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(0, this.height, 0).texture(0, 1).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(this.width, this.height, 0).texture(1, 1).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(this.width, 0, 0).texture(1, 0).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(0, 0, 0).texture(0, 0).color(255, 255, 255, 255).next();
			tessellator.draw();
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
