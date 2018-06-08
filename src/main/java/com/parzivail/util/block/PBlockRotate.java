package com.parzivail.util.block;

import com.parzivail.util.math.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class PBlockRotate extends PBlockContainer
{
	protected int snapAngles;

	public PBlockRotate(String name)
	{
		this(name, Material.ground, 4);
	}

	public PBlockRotate(String name, Material material)
	{
		this(name, material, 4);
	}

	public PBlockRotate(String name, int snapAngles)
	{
		this(name, Material.ground, snapAngles);
	}

	public PBlockRotate(String name, Material material, int snapAngles)
	{
		super(name, material);
		this.snapAngles = snapAngles;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileRotatable)
		{
			TileRotatable vap = (TileRotatable)tile;
			float l = (MathUtil.roundToNearest(player.rotationYaw % 360, 360f / snapAngles) / 90f);
			vap.setFacing(l);
		}
	}
}
