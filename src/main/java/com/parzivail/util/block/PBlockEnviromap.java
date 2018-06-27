package com.parzivail.util.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PBlockEnviromap extends PBlock
{
	private final String[][] variants;
	private final int width;
	private final int height;
	private IIcon[][] icons;

	public PBlockEnviromap(String name, String texturePrefix, int width, int height)
	{
		this(name, Material.ground, texturePrefix, width, height);
	}

	public PBlockEnviromap(String name, Material material, String texturePrefix, int width, int height)
	{
		super(name, material);
		variants = new String[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				variants[i][j] = String.format("%s_%s_%s", texturePrefix, i, j);
		this.width = width;
		this.height = height;
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		icons = new IIcon[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				icons[i][j] = reg.registerIcon(variants[i][j]);
	}

	@Override
	public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
	{
		switch (side)
		{
			case 0:
			case 1:
				return icons[width - 1 - Math.abs(x) % width][Math.abs(z) % height];
			case 2:
				return icons[Math.abs(x) % width][height - 1 - Math.abs(z + y) % height];
			case 3:
				return icons[width - 1 - Math.abs(x) % width][height - 1 - Math.abs(z + y) % height];
			case 4:
				return icons[Math.abs(x + z) % width][height - 1 - Math.abs(y) % height];
			case 5:
				return icons[width - 1 - Math.abs(x + z) % width][height - 1 - Math.abs(y) % height];
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
