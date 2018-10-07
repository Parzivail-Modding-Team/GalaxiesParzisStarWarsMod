package com.parzivail.swg.gui.modern;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

public class ModernButton extends GuiButton
{
	private static final int[] stateColor = { GLPalette.GREY, GLPalette.WHITE, GLPalette.SW_YELLOW };

	public ModernButton(int id, int x, int y, int w, int h, String text)
	{
		super(id, x, y, w, h, text);
	}

	public void setText(String text)
	{
		displayString = text;
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

			float oneOverSr = 1f / 4;

			//			int textColor = GLPalette.WHITE;
			//			if (!this.enabled)
			//				textColor = GLPalette.MEDIUM_GREY;
			//			else if (this.hovered)
			//				textColor = GLPalette.BUTTER_YELLOW;

			boolean inverted = false;

			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Texture2D);
			GL.Disable(EnableCap.Lighting);
			GL.Enable(EnableCap.LineSmooth);
			GL11.glLineWidth(1);
			if (inverted)
			{
				GL.Color(stateColor[k]);
				D2.DrawSolidRoundRectangle(xPosition, yPosition, width, height, 7 * oneOverSr);
			}
			else
			{
				GL.Color(stateColor[k] & 0x00FFFFFF | 0x22000000);
				D2.DrawSolidRoundRectangle(xPosition, yPosition, width, height, 7 * oneOverSr);

				GL.Color(stateColor[k]);
				D2.DrawWireRoundRectangle(xPosition, yPosition, width, height, 7 * oneOverSr);
			}

			GL.Enable(EnableCap.Texture2D);
			GL.PushMatrix();
			FontRenderer fontrenderer = Client.mc.fontRendererObj;
			GL.Translate((int)(xPosition + width / 2f - fontrenderer.getStringWidth(displayString) / 4f), (int)(yPosition + height / 2f - fontrenderer.FONT_HEIGHT / 4f) + 1, 0);
			GL.Scale(0.5f);
			if (!inverted)
				fontrenderer.drawString(displayString, 1, 1, GLPalette.BLACK);
			fontrenderer.drawString(displayString, 0, 0, inverted ? GLPalette.ALMOST_BLACK : GLPalette.WHITE);
			GL.PopMatrix();
			GL.PopAttrib();
		}
	}
}
