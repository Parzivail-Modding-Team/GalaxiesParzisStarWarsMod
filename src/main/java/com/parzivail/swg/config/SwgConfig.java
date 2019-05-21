package com.parzivail.swg.config;

import com.parzivail.swg.Resources;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Resources.MODID)
@Config.LangKey("pswg.config.category.general")
public class SwgConfig
{
	@Config(modid = Resources.MODID, category = "client")
	@Config.LangKey("pswg.config.category.client")
	public static class SwgConfigClient
	{
		@Config.RangeDouble(min = 0.1, max = 1)
		@Config.LangKey("pswg.config.entry.cameraStiffness")
		public static float cameraStiffness = 0.4f;
	}

	@Mod.EventBusSubscriber
	private static class EventHandler
	{
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.getModID().equals(Resources.MODID))
			{
				ConfigManager.sync(Resources.MODID, Config.Type.INSTANCE);
			}
		}
	}
}
