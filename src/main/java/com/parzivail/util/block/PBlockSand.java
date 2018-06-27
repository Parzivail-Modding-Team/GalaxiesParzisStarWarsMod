package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;

public class PBlockSand extends BlockFalling
{
	public final String name;

	public PBlockSand(String name)
	{
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		setTextureName(Resources.modColon(name));
		setHardness(0.5F);
		setStepSound(Block.soundTypeSand);
	}
}