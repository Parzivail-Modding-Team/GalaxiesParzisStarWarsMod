package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.maui.MauiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenModelBuilder extends GuiScreen
{
	private GuiScreen returnScreen;

	public GuiScreenModelBuilder()
	{
		this.returnScreen = Client.mc.currentScreen;
	}

	public void initGui()
	{
		this.buttonList.add(new MauiButton(10, 10, 10, 30, 5, "Hovered"));
	}

	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			if (button.id == 1)
			{
				this.mc.displayGuiScreen(this.returnScreen);
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		//this.drawBackground(0);
		//this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title", new Object[0]), this.width / 2, 16, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
