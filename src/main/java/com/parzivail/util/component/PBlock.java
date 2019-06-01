package com.parzivail.util.component;

import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PBlock extends Block implements IBlockWithItem
{
	protected String name;
	private boolean hasCustomBounds;
	private BlockRenderLayer renderLayer = BlockRenderLayer.CUTOUT;

	private final List<AxisAlignedBB> boundingBoxes;

	public PBlock(String name, Material material)
	{
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);

		List<AxisAlignedBB> boundingBox = createBoundingBox();
		if (boundingBox != null)
		{
			hasCustomBounds = true;
			boundingBoxes = boundingBox;
		}
		else
		{
			boundingBoxes = new ArrayList<>();
			boundingBoxes.add(FULL_BLOCK_AABB);
		}
	}

	public List<AxisAlignedBB> createBoundingBox()
	{
		return null;
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
		boundingBoxes.clear();
		boundingBoxes.add(new AxisAlignedBB(minX / 16f, minY / 16f, minZ / 16f, (minX + width) / 16f, (minY + height) / 16f, (minZ + length) / 16f));
		setLightOpacity(0);
		return this;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (hasCustomBounds)
			return boundingBoxes.get(0);
		return super.getBoundingBox(state, source, pos);
	}

	public PBlock withTranslucent()
	{
		renderLayer = BlockRenderLayer.TRANSLUCENT;
		return this;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
	{
		if (!hasCustomBounds)
			super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
		else
		{
			for (AxisAlignedBB box : boundingBoxes)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
		}
	}
}
