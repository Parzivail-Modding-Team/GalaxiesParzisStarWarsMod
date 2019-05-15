package com.parzivail.swg.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiShipInput extends GuiScreen
{
	@Override
	public void updateScreen()
	{
		super.updateScreen();

		//		Mouse.setCursorPosition(SwgClientProxy.mc.displayWidth / 2, SwgClientProxy.mc.displayHeight / 2);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		//		Mouse.setGrabbed();
	}
}
