package com.parzivail.util.component;

import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class PBlock extends Block
{
	protected String name;
	private boolean opaque = true;

	public PBlock(String name, Material material)
	{
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
	}

	public void registerItemModel(Item itemBlock)
	{
		StarWarsGalaxy.proxy.registerItemRenderer(itemBlock, name);
	}

	public Item createItemBlock()
	{
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}

	public PBlock setNotFullBlock()
	{
		this.opaque = false;
		return this;
	}

	public boolean isOpaqueCube(IBlockState state)
	{
		return opaque;
	}
}
