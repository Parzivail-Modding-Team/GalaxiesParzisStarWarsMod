package com.parzivail.swg.gui;

import com.parzivail.util.ui.Fx;
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
		if (this.visible)
		{
			this.mouseDragged(mc, mouseX, mouseY);

			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int k = this.getHoverState(this.hovered); // 0->disabled, 1->normal, 2->hover

			int textColor = 0xe0e0e0;
			if (!this.enabled)
				textColor = 0xa0a0a0;
			else if (this.hovered)
				textColor = 0xffffa0;

			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Texture2D);
			GLPalette.glColorI(stateColor[k], 192);
			Fx.D2.DrawSolidRectangle(this.xPosition, this.yPosition, this.width, this.height);
			//GLPalette.glColorI(stateColor[k], 192);
			Fx.D2.DrawWireRectangle(this.xPosition, this.yPosition, this.width, this.height);
			GL.Enable(EnableCap.Texture2D);

			FontRenderer fontrenderer = mc.fontRendererObj;
			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
			GL.PopAttrib();
		}
	}
}
