package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.BlockLog;

public class PBlockPillar extends BlockLog
{
	public final String name;

	public PBlockPillar(String name)
	{
		this.name = name;
		this.setCreativeTab(StarWarsGalaxy.tab);
		this.setUnlocalizedName(Resources.modDot(this.name));
	}
}