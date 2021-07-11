package com.parzivail.pswg.compat.modmenu;

import com.parzivail.pswg.Config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class GalaxiesModMenuIntegration implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory()
	{
		return parent -> AutoConfig.getConfigScreen(Config.class, parent).get();
	}
}
