package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class PswgMenuVector extends GuiButton
{
	static boolean firstRun = true;

	public PswgMenuVector(int buttonId, int x, int y, String buttonText)
	{
		super(buttonId, x, y, 1, 1, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (firstRun)
			Client.mc.displayGuiScreen(new GuiScreenTrailer());
		firstRun = false;
	}
}
