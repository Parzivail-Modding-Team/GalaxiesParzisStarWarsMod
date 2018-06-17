package com.parzivail.swg.gui.modern;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
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

	private float contentSize = 1;
	private float windowSize = 1;
	private float windowPosition;

	private boolean isDragging;
	private float dragOffset;

	private final float minScrollbarSize = 2;
	private final float maxScrollbarSize;

	public ModernScrollbar(int id, int x, int y, int h)
	{
		super(id, x, y, 4, h, "");
		this.maxScrollbarSize = h - 2;
	}

	public void setContentSize(float contentSize)
	{
		this.contentSize = contentSize;
	}

	public void setWindowSize(float windowSize)
	{
		this.windowSize = windowSize;
	}

	public void setWindowPosition(float windowPosition)
	{
		this.windowPosition = windowPosition;
	}

	public float getWindowPosition()
	{
		return windowPosition;
	}

	private float getScrollbarPosition()
	{
		return windowPosition / (contentSize - windowSize) * (this.height - 2 - getScrollbarSize());
	}

	private float getScrollbarSize()
	{
		return MathHelper.clamp_float(getViewportRatio() * maxScrollbarSize, minScrollbarSize, maxScrollbarSize);
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

			float scrollY = this.yPosition + scrollbarPosition;
			isDragging = (mouseX >= this.xPosition + 1 && mouseX <= this.xPosition + 2) && (mouseY >= scrollY && mouseY <= scrollY + scrollbarSize);
			dragOffset = mouseY - scrollY;
		}

		return pressed;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
	{
		if (!isDragging)
			return;

		float mousePosition = mouseY - this.yPosition - dragOffset;
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
		if (this.visible)
		{
			this.mouseDragged(mc, mouseX, mouseY);
			float scrollbarSize = getScrollbarSize();
			float scrollbarPosition = getScrollbarPosition();
			float scrollY = this.yPosition + 1 + scrollbarPosition;

			this.hovered = (mouseX >= this.xPosition + 1 && mouseX <= this.xPosition + 2) && (mouseY >= scrollY && mouseY <= scrollY + scrollbarSize);
			boolean hoveredBar = (mouseX >= this.xPosition && mouseX <= this.xPosition + this.width) && (mouseY >= this.yPosition && mouseY <= this.yPosition + this.height);
			int k = this.getHoverState(this.hovered); // 0->disabled, 1->normal, 2->hover

			ScaledResolution sr = Client.resolution;
			float oneOverSr = 1f / sr.getScaleFactor();

			int wheel = hoveredBar ? Mouse.getDWheel() : 0;
			if (wheel > 0)
				this.windowPosition -= 5;
			else if (wheel < 0)
				this.windowPosition += 5;

			this.windowPosition = MathHelper.clamp_float(this.windowPosition, 0, Math.max(0, contentSize - windowSize));

			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Texture2D);
			GL.Enable(EnableCap.LineSmooth);
			GL.Color(GLPalette.WHITE);
			GL11.glLineWidth(2);
			Fx.D2.DrawWireRoundRectangle(this.xPosition, this.yPosition, this.width, this.height, 7 * oneOverSr);

			GL.Color(stateColor[k]);
			Fx.D2.DrawSolidRoundRectangle(this.xPosition + 1, this.yPosition + 1 + scrollbarPosition, 2, scrollbarSize, 4 * oneOverSr);
			GL.PopAttrib();
		}
	}
}
