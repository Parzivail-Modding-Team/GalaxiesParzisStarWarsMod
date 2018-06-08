package com.parzivail.swg.tile.machine;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.util.ResourceLocation;

public class TileMV extends TileRotatable
{
	private static final ResourceLocation sound = Resources.location("swg.mech.vaporator");

	public int frame;

	public TileMV()
	{
		this.frame = 0;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		this.frame++;
	}

	@Override
	public ResourceLocation getSound()
	{
		return sound;
	}
}
