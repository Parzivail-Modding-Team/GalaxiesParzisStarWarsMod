package com.parzivail.swg.gui;

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

		this.fontRendererObj.drawString("Hello, World!", 10, 10, 0xFFFFFF);
	}
}
