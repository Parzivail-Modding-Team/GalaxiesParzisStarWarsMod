package com.parzivail.pswgtk.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;
import com.parzivail.pswgtk.util.GlfwKeyUtil;
import jdk.swing.interop.LightweightFrameWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class JComponentScreen extends Screen
{
	private final Screen parent;
	private final Thread swingThread;
	private final LightweightFrameWrapper frame;

	private TextureBackedContentWrapper contentWrapper;
	private Rectangle hostRectangle;
	private boolean isContentReady;

	private int mouseButtonMask = 0;

	public JComponentScreen(Screen parent, Text title)
	{
		super(title);
		this.parent = parent;

		this.frame = new LightweightFrameWrapper();
		this.swingThread = new Thread(null, this::runSwing, "pswg-toolkit-awt");
	}

	private void runSwing()
	{
		assert this.client != null;

		var window = this.client.getWindow();
		runOnEDT(() -> {
			contentWrapper = new TextureBackedContentWrapper(getRootComponent());
			frame.setContent(contentWrapper);
			frame.setBounds(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
			isContentReady = true;
		});

		frame.setVisible(true);

		dispatchEvent(new FocusEvent(getFrame(), FocusEvent.FOCUS_GAINED));
	}

	protected abstract JComponent getRootComponent();

	protected Frame getFrame()
	{
		var dummyEvent = frame.createUngrabEvent(frame);
		return (Frame)dummyEvent.getSource();
	}

	@Override
	protected void init()
	{
		assert this.client != null;

		super.init();

		if (this.contentWrapper == null && !swingThread.isAlive())
			swingThread.start();
	}

	private void dispatchEvent(AWTEvent event)
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}

	private static void runOnEDT(final Runnable r)
	{
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
	}

	private void onMouseScrolled(MouseWheelEvent event)
	{
	}

	private void onMousePressed(MouseEvent event)
	{
	}

	private void onMouseReleased(MouseEvent releaseEvent)
	{
	}

	private void onMouseClicked(MouseEvent clickEvent)
	{
	}

	private void onMouseMoved(MouseEvent event)
	{
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (this.client == null)
			return false;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		var event = frame.createMouseWheelEvent(frame, 0, x, y, -(int)amount);
		dispatchEvent(event);
		onMouseScrolled(event);

		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (this.client == null)
			return false;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		this.mouseButtonMask |= glfwToSwingMouseButtonMask(button);
		var event = frame.createMouseEvent(
				frame, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
		);
		dispatchEvent(event);
		onMousePressed(event);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (this.client == null)
			return false;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		this.mouseButtonMask &= ~glfwToSwingMouseButtonMask(button);
		var releaseEvent = frame.createMouseEvent(
				frame, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
		);
		dispatchEvent(releaseEvent);
		onMouseReleased(releaseEvent);
		var clickEvent = frame.createMouseEvent(
				frame, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				1, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT, glfwToSwingMouseButton(button)
		);
		dispatchEvent(clickEvent);
		onMouseClicked(clickEvent);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		if (this.client == null)
			return;

		var x = (int)this.client.mouse.getX();
		var y = (int)this.client.mouse.getY();
		var event = frame.createMouseEvent(
				frame, this.mouseButtonMask != 0 ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), this.mouseButtonMask,
				x, y, x, y,
				0, false, MouseEvent.NOBUTTON
		);
		dispatchEvent(event);
		onMouseMoved(event);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		var vk = glfwToSwingKeyCode(keyCode);
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), vk, (char)vk
		));

		if (this.client == null)
			return false;

		if (keyCode == GLFW.GLFW_KEY_ESCAPE)
			this.client.setScreen(this.parent);

		return true;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		var vk = glfwToSwingKeyCode(keyCode);
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), vk, (char)vk
		));
		return true;
	}

	@Override
	public boolean charTyped(char chr, int modifiers)
	{
		dispatchEvent(frame.createKeyEvent(
				frame, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
				glfwToSwingKeyModifiers(modifiers), KeyEvent.VK_UNDEFINED, chr
		));
		return true;
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
	public void tick()
	{
		if (!isContentReady)
			return;

		var window = this.client.getWindow();
		var currentRect = new Rectangle(window.getX(), window.getY(), window.getFramebufferWidth(), window.getFramebufferHeight());
		if (!currentRect.equals(hostRectangle))
		{
			this.hostRectangle = currentRect;
			runOnEDT(() -> {
				this.frame.setBounds(currentRect.x, currentRect.y, currentRect.width, currentRect.height);
				this.contentWrapper.getComponent().setBounds(0, 0, currentRect.width, currentRect.height);
			});
		}
	}

	@Override
	public void close()
	{
	}

	@Override
	public void removed()
	{
		if (swingThread != null)
			swingThread.interrupt();
	}

	protected Vec2f transformSwingToScreen(Vec2f point)
	{
		return new Vec2f(
				(int)(this.width * point.x / this.client.getWindow().getFramebufferWidth()),
				(int)(this.height * point.y / this.client.getWindow().getFramebufferHeight())
		);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.fillGradient(matrices, 0, 0, this.width, this.height, 0xFF000000, 0xFF000000);

		renderContent(matrices);
		renderInterface(matrices);
	}

	protected void renderContent(MatrixStack matrices)
	{
	}

	private void renderInterface(MatrixStack matrices)
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
