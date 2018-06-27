package com.parzivail.util.block;

import com.parzivail.swg.Resources;
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

	public PBlockDynamic(String name, int numVariants)
	{
		this(name, Material.ground, numVariants);
	}

	public PBlockDynamic(String name, Material material, int numVariants)
	{
		super(name, material);
		variants = new String[numVariants];
		for (int i = 0; i < numVariants; i++)
			variants[i] = Resources.modColon(String.format("var/%s/%d", name, i));
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
		double s = 3;
		double n = (noise.eval(x / s, y / s, z / s) + 1) / 2;
		return icons[(int)Math.floor(n * icons.length)];
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return icons[0];
	}
}
