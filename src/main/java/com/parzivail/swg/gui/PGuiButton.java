package com.parzivail.swg.gui;

import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import java.util.EnumSet;

public class PGuiButton extends GuiButton
{
	private static final int[] stateColor = { GLPalette.GREY, GLPalette.ALMOST_BLACK, GLPalette.ELECTRIC_BLUE };

	public PGuiButton(int id, int x, int y, int w, int h, String text)
	{
		super(id, x, y, w, h, text);
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			mouseDragged(mc, mouseX, mouseY);

			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int k = getHoverState(hovered); // 0->disabled, 1->normal, 2->hover

			int textColor = GLPalette.WHITE;
			if (!enabled)
				textColor = GLPalette.MEDIUM_GREY;
			else if (hovered)
				textColor = GLPalette.BUTTER_YELLOW;

			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Texture2D);
			GLPalette.glColorI(stateColor[k], 192);
			D2.DrawSolidRectangle(xPosition, yPosition, width, height);
			//GLPalette.glColorI(stateColor[k], 192);
			D2.DrawWireRectangle(xPosition, yPosition, width, height);
			GL.Enable(EnableCap.Texture2D);

			GL.PushMatrix();
			GL.Translate(0, 0.5f, 0); // This is fine if you don't play on Small gui scale. I'm OK with that tradeoff.
			FontRenderer fontrenderer = mc.fontRendererObj;
			fontrenderer.drawString(displayString, xPosition + width / 2 - fontrenderer.getStringWidth(displayString) / 2, yPosition + height / 2 - fontrenderer.FONT_HEIGHT / 2, textColor);
			GL.PopMatrix();
			GL.PopAttrib();
		}
	}
}
