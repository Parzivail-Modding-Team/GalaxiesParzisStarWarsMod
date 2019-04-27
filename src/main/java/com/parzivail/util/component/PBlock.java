package com.parzivail.util.component;

import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PBlock extends Block implements IBlockWithItem
{
	private AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
	protected String name;
	private boolean hasCustomBounds;
	private BlockRenderLayer renderLayer = BlockRenderLayer.CUTOUT;

	public PBlock(String name, Material material)
	{
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
	}

	public PBlock(String name)
	{
		this(name, Material.GROUND);
	}

	@Override
	public void registerItemModel(Item itemBlock)
	{
		StarWarsGalaxy.proxy.registerItemRenderer(itemBlock, name);
	}

	@Override
	public Block getBlock()
	{
		return this;
	}

	@Override
	public Item createItemBlock()
	{
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}

	@Override
	public String getName()
	{
		return name;
	}

	public boolean isOpaqueCube(IBlockState state)
	{
		return !hasCustomBounds;
	}

	public boolean isFullCube(IBlockState state)
	{
		return !hasCustomBounds;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return hasCustomBounds ? renderLayer : super.getBlockLayer();
	}

	public PBlock withBoundingBox(int minX, int minY, int minZ, int width, int height, int length)
	{
		hasCustomBounds = true;
		BOUNDING_BOX = new AxisAlignedBB(minX / 16f, minY / 16f, minZ / 16f, (minX + width) / 16f, (minY + height) / 16f, (minZ + length) / 16f);
		setLightOpacity(0);
		return this;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (hasCustomBounds)
			return BOUNDING_BOX;
		return super.getBoundingBox(state, source, pos);
	}

	public PBlock withTranslucent()
	{
		renderLayer = BlockRenderLayer.TRANSLUCENT;
		return this;
	}
}
