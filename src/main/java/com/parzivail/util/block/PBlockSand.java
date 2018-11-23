package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;

public class PBlockSand extends BlockFalling implements INameProvider
{
	public final String name;

	public PBlockSand(String name)
	{
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		setTextureName(Resources.modColon(name));
		setHardness(0.5F);
		setHarvestLevel("shovel", HarvestLevel.WOOD);
		setStepSound(Block.soundTypeSand);
	}

	@Override
	public String getName()
	{
		return name;
	}
}
