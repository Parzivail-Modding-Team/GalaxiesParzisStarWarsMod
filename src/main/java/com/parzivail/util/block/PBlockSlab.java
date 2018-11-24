package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.BlockSlab;
import net.minecraft.util.IIcon;

public class PBlockSlab extends BlockSlab implements INameProvider
{
	private final String name;
	private final PBlock host;
	private final int metadata;

	public PBlockSlab(String name, PBlock host, int metadata, boolean doubleSlab)
	{
		super(doubleSlab, host.getMaterial());
		this.name = name;
		this.host = host;
		this.metadata = metadata;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		setHarvestLevel("pickaxe", host.getHarvestLevel(metadata));
		setTextureName(Resources.modColon(textureName));
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return host.getIcon(side, metadata);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getFullSlabName(int p_150002_1_)
	{
		return Resources.modDot(name);
	}
}
