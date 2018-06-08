package com.parzivail.swg.tile.atmosphere;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileAtmoSound;
import net.minecraft.util.ResourceLocation;

public class TileSoundHothTelemetry extends TileAtmoSound
{
	private static final ResourceLocation sound = Resources.location("swg.atmo.hothTelemetry");

	@Override
	public ResourceLocation getSound()
	{
		return sound;
	}
}
