package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.BlockFalling;

public class PBlockSand extends BlockFalling
{
	public final String name;

	public PBlockSand(String name)
	{
		this.name = name;
		this.setCreativeTab(StarWarsGalaxy.tab);
		this.setUnlocalizedName(Resources.modDot(this.name));
		this.setTextureName(Resources.modColon(name));
		this.setHardness(0.5F);
		this.setStepSound(soundTypeSand);
	}
}