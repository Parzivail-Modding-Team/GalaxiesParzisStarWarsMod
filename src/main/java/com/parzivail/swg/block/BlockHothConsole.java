package com.parzivail.swg.block;

import com.parzivail.util.component.PBlockFacing;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Arrays;
import java.util.List;

public class BlockHothConsole extends PBlockFacing
{
	public BlockHothConsole(String name, Material material)
	{
		super(name, material);
	}

	@Override
	public List<AxisAlignedBB> createBoundingBox()
	{
		AxisAlignedBB stand = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75, 0.4);
		AxisAlignedBB keyboard = new AxisAlignedBB(0.0D, 0.75, 0.0D, 1.0D, 1, 0.9);
		AxisAlignedBB screen = new AxisAlignedBB(0.0D, 1, 0.0D, 1.0D, 2, 0.4);
		AxisAlignedBB buttons = new AxisAlignedBB(0.0D, 2, 0.0D, 1.0D, 2.5, 0.6);
		return Arrays.asList(stand, keyboard, screen, buttons);
	}
}
