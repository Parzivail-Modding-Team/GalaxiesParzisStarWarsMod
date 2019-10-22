package com.parzivail.util.component;

import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class PBlockFalling extends BlockFalling implements IBlockWithItem
{
	protected String name;

	public PBlockFalling(String name, Material material)
	{
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);

		setHardness(5);
		setResistance(10);
	}

	@Override
	public void registerItemModel(Item itemBlock)
	{
		StarWarsGalaxy.proxy.registerItemRenderer(itemBlock, name);
	}

	@Override
	public Item createItemBlock()
	{
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}

	@Override
	public Block getBlock()
	{
		return this;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
