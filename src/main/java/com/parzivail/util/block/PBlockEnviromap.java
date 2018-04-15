package com.parzivail.util.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PBlockEnviromap extends PBlock
{
	private final String[][] variants;
	private IIcon[][] icons;

	public PBlockEnviromap(String name, String[][] variants)
	{
		this(name, Material.ground, variants);
	}

	public PBlockEnviromap(String name, Material material, String[][] variants)
	{
		super(name, material);
		this.variants = variants;
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		icons = new IIcon[variants.length][variants[0].length];
		for (int i = 0; i < variants.length; i++)
			for (int j = 0; j < variants[0].length; j++)
				icons[i][j] = reg.registerIcon(variants[i][j]);
	}

	@Override
	public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
	{
		switch (side)
		{
			case 0:
			case 1:
				return icons[Math.abs(x) % variants.length][Math.abs(z) % variants[0].length];
			case 2:
			case 3:
				return icons[Math.abs(x + 1) % variants.length][Math.abs(z + y + 1) % variants[0].length];
			case 4:
			case 5:
				return icons[Math.abs(x + z) % variants.length][Math.abs(y + 1) % variants[0].length];
			default:
				return Blocks.dirt.getIcon(worldIn, x, y, z, side);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return icons[0][0];
	}
}
