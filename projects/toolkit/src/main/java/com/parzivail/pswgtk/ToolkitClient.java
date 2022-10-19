package com.parzivail.pswgtk;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswgtk.screen.ToolkitHomeScreen;
import com.parzivail.util.Lumberjack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class ToolkitClient implements PswgClientAddon
{
	public static final String MODID = "pswg-toolkit";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	public static final String I18N_TOOLKIT = Resources.screen("toolkit");
	public static final Identifier TEX_TOOLKIT = Resources.id("textures/gui/toolkit_button.png");

	public static Screen createScreen(Screen parent)
	{
		return new ToolkitHomeScreen(parent);
	}

	@Override
	public void onPswgClientReady()
	{
	}
}
