package com.parzivail.pswgtk;

import com.formdev.flatlaf.FlatLightLaf;
import com.parzivail.imgui.ImguiTestScreen;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.util.Lumberjack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class ToolkitClient implements PswgClientAddon
{
	//	public static final ExecutorService UI_WORKER = WorkerUtil.createWorker("ui", 1);
	//	public static final ExecutorService WORLDGEN_WORKER = WorkerUtil.createWorker("worldgen", 8);

	public static final String MODID = "aurek";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	public static final String I18N_TOOLKIT = Resources.screen("toolkit");
	public static final Identifier TEX_TOOLKIT = id("textures/gui/toolkit_button.png");
	public static final Identifier TEX_DEBUG = id("textures/debug.png");

	public static Screen createHomeScreen(Screen parent)
	{
		return new ImguiTestScreen(parent);
	}

	public static Identifier id(String path)
	{
		return new Identifier(MODID, path);
	}

	@Override
	public void onPswgClientReady()
	{
		System.setProperty("java.awt.headless", "false");
		FlatLightLaf.setup();
	}
}
