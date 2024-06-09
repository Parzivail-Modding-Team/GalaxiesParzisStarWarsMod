package com.parzivail.pswgtcw;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.data.*;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Falloff;
import com.parzivail.util.registry.RegistryHelper;
import net.minecraft.util.Identifier;

import java.util.List;

public class PswgTcwAddon implements PswgAddon
{
	public static final String MODID = "pswg_addon_clonewars";
	public static final Lumberjack LOG = new Lumberjack(MODID);

	@Override
	public void onPswgReady()
	{
		RegistryHelper.registerAutoId(MODID, TcwItems.class, Object.class, SwgItems::tryRegisterItem);
		SwgSounds.registerIfAbsent(BlasterItem.modelIdToSoundId(id("dc17")));

		PswgContent.registerBlasterPreset(
				new BlasterDescriptor(id("dc17"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(2, -0.5f, 8, 50)
						.damage(3.06f, 188, Falloff.cliff(0.3))
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 150, 8, 20, 30, 100, 60))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.45f, 0.05f))
		);
	}

	public static Identifier id(String path)
	{
		return Identifier.of(MODID, path);
	}
}
