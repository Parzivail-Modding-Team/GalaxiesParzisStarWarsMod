package com.parzivail.pswgtcw;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.util.Lumberjack;
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
				new BlasterDescriptor(
						id("dc17"),
						id("dc17"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, Falloff.cliff(0.3),
						188, 2, 0.62f, 1, 1,
						50, 2, 4, 3, 8,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 150, 8, 20, 30, 100, 60),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.45f, 0.05f),
						new BlasterAttachmentBuilder().build()
				)
		);
	}

	public static Identifier id(String path)
	{
		return new Identifier(MODID, path);
	}
}
