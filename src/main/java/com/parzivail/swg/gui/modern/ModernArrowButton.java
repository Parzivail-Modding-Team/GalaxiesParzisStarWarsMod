package com.parzivail.swg.gui.modern;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

public class ModernArrowButton extends GuiButton
{
	private static final int[] stateColor = { GLPalette.GREY, GLPalette.WHITE, GLPalette.SW_YELLOW };

	public ModernArrowButton(int id, int x, int y)
	{
		super(id, x, y, 5, 5, "");
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
			GL.Enable(EnableCap.LineSmooth);
			GL.Color(stateColor[k]);
			GL11.glLineWidth(1);
			GL.PushMatrix();

			GL.Translate(xPosition + width / 2f + Fx.Util.Hz(1) * 0.5f + 1, yPosition + height / 2f, 0);
			GL.Rotate(90, 0, 0, 1);

			GL.Color(0xFF000000);
			Fx.D2.DrawSolidTriangle(0.5f, -0.5f, width);

			GL.Color(stateColor[k]);
			Fx.D2.DrawSolidTriangle(0, 0, width);

			GL.PopMatrix();
			GL.PopAttrib();
		}
	}
}
