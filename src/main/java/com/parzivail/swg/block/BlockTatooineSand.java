package com.parzivail.swg.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockTatooineSand extends Block
{
	public BlockTatooineSand()
	{
		super(Material.SAND, MapColor.SAND);
		setRegistryName("sand_tatooine");
	}
}
