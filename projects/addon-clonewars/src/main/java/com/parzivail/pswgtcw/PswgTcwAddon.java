package com.parzivail.pswgtcw;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.registry.RegistryHelper;
import net.minecraft.util.Identifier;

public class PswgTcwAddon implements PswgAddon
{
	public static final String MODID = "pswg_addon_clonewars";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	@Override
	public void onPswgReady()
	{
		RegistryHelper.registerAutoId(MODID, TcwItems.class, Object.class, RegistryHelper::tryRegisterItem);
		SwgSounds.registerIfAbsent(BlasterItem.modelIdToSoundId(id("dc17")));
	}

	public static Identifier id(String path)
	{
		return new Identifier(MODID, path);
	}
}
