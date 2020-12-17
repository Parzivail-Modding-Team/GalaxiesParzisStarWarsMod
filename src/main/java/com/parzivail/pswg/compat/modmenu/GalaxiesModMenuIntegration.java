package com.parzivail.pswg.compat.modmenu;

import com.parzivail.pswg.Config;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class GalaxiesModMenuIntegration implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory()
	{
		return parent -> AutoConfig.getConfigScreen(Config.class, parent).get();
	}
}
