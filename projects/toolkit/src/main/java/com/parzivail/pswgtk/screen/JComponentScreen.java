package com.parzivail.pswgtk.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.awt.PostEventAction;
import com.parzivail.pswgtk.awt.TextureBackedContentWrapper;
import jdk.swing.interop.LightweightFrameWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.security.AccessController;
import java.util.function.Supplier;

public class JComponentScreen extends Screen
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private final Screen parent;
	private JComponent root;
	private final Thread swingThread;
	private final LightweightFrameWrapper frame;
	private TextureBackedContentWrapper contentWrapper;

	public JComponentScreen(Screen parent, Supplier<JComponent> root)
	{
		super(Text.translatable(I18N_TOOLKIT_HOME));
		this.parent = parent;

		System.setProperty("java.awt.headless", "false");
		this.root = root.get();

		this.frame = new LightweightFrameWrapper();

		this.swingThread = new Thread(null, this::runSwing, "pswg-toolkit-awt");
	}

	private void runSwing()
	{
		assert this.client != null;

		var window = this.client.getWindow();
		runOnEDT(() -> {
			contentWrapper = new TextureBackedContentWrapper(root);
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

		if (!this.swingThread.isAlive())
			swingThread.start();

		var window = this.client.getWindow();
		runOnEDT(() -> {
			if (contentWrapper != null)
			{
				contentWrapper.getComponent().setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
				frame.setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
			}
		});
	}

	private void dispatchEvent(AWTEvent event)
	{
		@SuppressWarnings("removal")
		var dummy = AccessController.doPrivileged(new PostEventAction(event));
	}

	private static void runOnEDT(final Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		assert this.client != null;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		dispatchEvent(frame.createMouseWheelEvent(frame, 0, x, y, (int)amount));

		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		assert this.client != null;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0,
				x, y, x, y,
				1, false, MouseEvent.BUTTON1 + button
		));

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		assert this.client != null;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0,
				x, y, x, y,
				1, false, MouseEvent.BUTTON1 + button
		));

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		assert this.client != null;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,
				x, y, x, y,
				0, false, MouseEvent.NOBUTTON
		));

		super.mouseMoved(mouseX, mouseY);
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
		//		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFFF0F0F0, 0xFFF0F0F0);
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF000000, 0xFF000000);

		if (contentWrapper != null)
		{
			var widthFraction = contentWrapper.getVisibleWidthFraction();
			var heightFraction = contentWrapper.getVisibleHeightFraction();

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, contentWrapper.getTextureId());
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(0, this.height, 0).texture(0, heightFraction).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(this.width, this.height, 0).texture(widthFraction, heightFraction).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(this.width, 0, 0).texture(widthFraction, 0).color(255, 255, 255, 255).next();
			bufferBuilder.vertex(0, 0, 0).texture(0, 0).color(255, 255, 255, 255).next();
			tessellator.draw();
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
