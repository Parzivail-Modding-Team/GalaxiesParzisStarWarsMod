package com.parzivail.util.ui.maui;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class MauiButton extends GuiButton
{
	public MauiButton(int buttonId, int x, int y, String buttonText)
	{
		super(buttonId, x, y, buttonText);
	}

	public MauiButton(int buttonId, int x, int y, int w, int h, String buttonText)
	{
		super(buttonId, x, y, w, h, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		//GL.Color(GLPalette.GREY);
		//if (Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY))
		//	GL.Color(GLPalette.ELECTRIC_BLUE);
		Fx.D2.RoundRectangle(xPosition, yPosition, width, height, 5, 5, 5, 5, PrimitiveType.TriangleFan);
		GL.PopAttrib();
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}
}
