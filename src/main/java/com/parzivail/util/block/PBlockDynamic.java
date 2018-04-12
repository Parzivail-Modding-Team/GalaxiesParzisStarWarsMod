package com.parzivail.util.block;

import com.parzivail.util.common.OpenSimplexNoise;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PBlockDynamic extends PBlock
{
	private final OpenSimplexNoise noise = new OpenSimplexNoise();
	private final String[] variants;
	private IIcon[] icons;

	public PBlockDynamic(String name, String[] variants)
	{
		this(name, Material.ground, variants);
	}

	public PBlockDynamic(String name, Material material, String[] variants)
	{
		super(name, material);
		this.variants = variants;
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		icons = new IIcon[variants.length];
		for (int i = 0; i < variants.length; i++)
			icons[i] = reg.registerIcon(variants[i]);
	}

	@Override
	public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
	{
		final double s = 3;
		double n = (noise.eval(x / s, y / s, z / s) + 1) / 2;
		return icons[(int)Math.floor(n * icons.length)];
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return icons[0];
	}
}
