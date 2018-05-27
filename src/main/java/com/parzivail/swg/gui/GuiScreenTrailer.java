package com.parzivail.swg.gui;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenTrailer extends GuiScreen
{
	public GuiScreenTrailer()
	{
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
		if (keyCode == 1)
		{
			this.mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		GL.Color(0f, 0f, 0f);
		Fx.D2.DrawSolidRectangle(0, 0, width, height);

		this.fontRendererObj.drawString("Hello, World!", 10, 10, 0xFFFFFF);
	}
}
