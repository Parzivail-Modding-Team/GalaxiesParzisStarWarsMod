package com.parzivail.swg.gui.modern;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.math.Ease;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

public class ModernScrollbar extends GuiButton
{
	private static final int[] stateColor = { GLPalette.GREY, GLPalette.WHITE, GLPalette.SW_YELLOW };
	private final AnimatedValue windowRenderPosition = new AnimatedValue(0, 50);
	private final float maxScrollbarSize;
	private float contentSize = 1;
	private float windowSize = 1;
	private float windowPosition;
	private boolean isDragging;
	private float dragOffset;

	public ModernScrollbar(int id, int x, int y, int h)
	{
		super(id, x, y, 4, h, "");
		maxScrollbarSize = h - 2;
	}

	public void setContentSize(float contentSize)
	{
		this.contentSize = contentSize;
	}

	public void setWindowSize(float windowSize)
	{
		this.windowSize = windowSize;
	}

	public float getWindowPosition()
	{
		return windowRenderPosition.animateTo(windowPosition, Ease::outQuad);
	}

	public void setWindowPosition(float windowPosition)
	{
		this.windowPosition = windowPosition;
	}

	private float getScrollbarPosition()
	{
		return getWindowPosition() / (contentSize - windowSize) * (height - 2 - getScrollbarSize());
	}

	private float getScrollbarSize()
	{
		return MathHelper.clamp_float(getViewportRatio() * maxScrollbarSize, 2, maxScrollbarSize);
	}

	private float getViewportRatio()
	{
		return windowSize / contentSize;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
	{
		boolean pressed = super.mousePressed(mc, mouseX, mouseY);

		if (pressed)
		{
			float scrollbarSize = getScrollbarSize();
			float scrollbarPosition = getScrollbarPosition();

			float scrollY = yPosition + scrollbarPosition;
			isDragging = (mouseX >= xPosition + 1 && mouseX <= xPosition + 2) && (mouseY >= scrollY && mouseY <= scrollY + scrollbarSize);
			dragOffset = mouseY - scrollY;
		}

		return pressed;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
	{
		if (!isDragging)
			return;

		float mousePosition = mouseY - yPosition - dragOffset;
		float lerp = MathHelper.clamp_float(mousePosition / (maxScrollbarSize - getScrollbarSize()), 0, 1);
		windowPosition = lerp * (contentSize - windowSize);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY)
	{
		if (!isDragging)
			return;

		isDragging = false;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			mouseDragged(mc, mouseX, mouseY);
			float scrollbarSize = getScrollbarSize();
			float scrollbarPosition = getScrollbarPosition();
			float scrollY = yPosition + 1 + scrollbarPosition;

			hovered = (mouseX >= xPosition + 1 && mouseX <= xPosition + 2) && (mouseY >= scrollY && mouseY <= scrollY + scrollbarSize);
			boolean hoveredBar = (mouseX >= xPosition && mouseX <= xPosition + width) && (mouseY >= yPosition && mouseY <= yPosition + height);
			int k = getHoverState(hovered || isDragging); // 0->disabled, 1->normal, 2->hover

			ScaledResolution sr = Client.resolution;
			float oneOverSr = 1f / sr.getScaleFactor();

			int wheel = hoveredBar ? Mouse.getDWheel() : 0;
			if (wheel > 0)
				windowPosition -= 5;
			else if (wheel < 0)
				windowPosition += 5;

			windowPosition = MathHelper.clamp_float(windowPosition, 0, Math.max(0, contentSize - windowSize));

			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Texture2D);
			GL.Enable(EnableCap.LineSmooth);
			GL.Color(GLPalette.WHITE);
			GL11.glLineWidth(2);
			D2.DrawWireRoundRectangle(xPosition, yPosition, width, height, 7 * oneOverSr);

			GL.Color(stateColor[k]);
			D2.DrawSolidRoundRectangle(xPosition + 1, yPosition + 1 + scrollbarPosition, 2, scrollbarSize, 4 * oneOverSr);
			GL.PopAttrib();
		}
	}
}
