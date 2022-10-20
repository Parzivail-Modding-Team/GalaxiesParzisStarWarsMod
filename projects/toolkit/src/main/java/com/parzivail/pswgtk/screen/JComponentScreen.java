package com.parzivail.pswgtk.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswgtk.swing.PostEventAction;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.util.GlfwKeyUtil;
import jdk.swing.interop.LightweightFrameWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.security.AccessController;

public abstract class JComponentScreen extends Screen
{
	private final Screen parent;
	private final Thread swingThread;
	private final LightweightFrameWrapper frame;
	private TextureBackedContentWrapper contentWrapper;

	private int mouseButtonMask = 0;

	public JComponentScreen(Screen parent, Text title)
	{
		super(title);
		this.parent = parent;

		this.frame = new LightweightFrameWrapper();
		this.swingThread = new Thread(null, this::runSwing, "pswg-toolkit-awt");
		swingThread.start();
	}

	private void runSwing()
	{
		assert this.client != null;

		var window = this.client.getWindow();
		runOnEDT(() -> {
			contentWrapper = new TextureBackedContentWrapper(buildInterface());
			frame.setContent(contentWrapper);
			frame.setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
		});

		frame.setVisible(true);

		var dummyEvent = frame.createUngrabEvent(frame);
		dispatchEvent(new FocusEvent((Component)dummyEvent.getSource(), FocusEvent.FOCUS_GAINED));
	}

	protected abstract JComponent buildInterface();

	@Override
	protected void init()
	{
		assert this.client != null;

		super.init();

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
		this.mouseButtonMask |= glfwToSwingMouseButtonMask(button);
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
		));

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		assert this.client != null;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		this.mouseButtonMask &= ~glfwToSwingMouseButtonMask(button);
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
		));
		dispatchEvent(frame.createMouseEvent(
				frame, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
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
				frame, this.mouseButtonMask != 0 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				0, false, MouseEvent.NOBUTTON
		));

		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		var vk = glfwToSwingKeyCode(keyCode);
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), vk, (char)vk
		));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		var vk = glfwToSwingKeyCode(keyCode);
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), vk, (char)vk
		));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers)
	{
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), KeyEvent.VK_UNDEFINED, chr
		));
		return super.charTyped(chr, modifiers);
	}

	private int glfwToSwingKeyModifiers(int glfwModifiers)
	{
		int mods = this.mouseButtonMask;

		if ((glfwModifiers & GLFW.GLFW_MOD_CONTROL) != 0)
			mods |= InputEvent.CTRL_DOWN_MASK;
		if ((glfwModifiers & GLFW.GLFW_MOD_ALT) != 0)
			mods |= InputEvent.ALT_DOWN_MASK;
		if ((glfwModifiers & GLFW.GLFW_MOD_SHIFT) != 0)
			mods |= InputEvent.SHIFT_DOWN_MASK;
		if ((glfwModifiers & GLFW.GLFW_MOD_SUPER) != 0)
			mods |= InputEvent.META_DOWN_MASK;

		return mods;
	}

	private static int glfwToSwingMouseButtonMask(int glfwButton)
	{
		if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			return MouseEvent.BUTTON1_DOWN_MASK;
		else if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
			return MouseEvent.BUTTON2_DOWN_MASK;
		else if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
			return MouseEvent.BUTTON3_DOWN_MASK;

		return 0;
	}

	private static int glfwToSwingKeyCode(int glfwScanCode)
	{
		return GlfwKeyUtil.getAwtGet(glfwScanCode);
	}

	private static int glfwToSwingMouseButton(int glfwButton)
	{
		if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			return MouseEvent.BUTTON1;
		else if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
			return MouseEvent.BUTTON2;
		else if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
			return MouseEvent.BUTTON3;
		return MouseEvent.NOBUTTON;
	}

	@Override
	public void close()
	{
		assert this.client != null;

		this.client.setScreen(this.parent);

		if (swingThread != null)
			swingThread.interrupt();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF000000, 0xFF000000);

		renderContent();
		renderInterface();
	}

	protected abstract void renderContent();

	private void renderInterface()
	{
		if (contentWrapper != null)
		{
			var widthFraction = contentWrapper.getVisibleWidthFraction();
			var heightFraction = contentWrapper.getVisibleHeightFraction();

			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

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
	}
}
