package com.parzivail.util.component;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PBlockFacing extends PBlock
{
	public static final PropertyEnum<EnumFacing> FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	private final HashMap<EnumFacing, List<AxisAlignedBB>> boundingBoxes;
	private final boolean hasComplexBounds;

	public PBlockFacing(String name, Material material)
	{
		super(name, material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));

		boundingBoxes = new HashMap<>();
		List<AxisAlignedBB> boundingBox = createBoundingBox();
		if (boundingBox != null)
		{
			hasComplexBounds = true;
			addBoundingBoxRotation(EnumFacing.NORTH, boundingBox);
			addBoundingBoxRotation(EnumFacing.SOUTH, boundingBox);
			addBoundingBoxRotation(EnumFacing.EAST, boundingBox);
			addBoundingBoxRotation(EnumFacing.WEST, boundingBox);
		}
		else
		{
			hasComplexBounds = false;
		}
	}

	private void addBoundingBoxRotation(EnumFacing facing, List<AxisAlignedBB> boundingBox)
	{
		List<AxisAlignedBB> boxes = new ArrayList<>();
		for (AxisAlignedBB box : boundingBox)
			boxes.add(rotateAabb(facing, box));
		boundingBoxes.put(facing, boxes);
	}

	public List<AxisAlignedBB> createBoundingBox()
	{
		return null;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 3);
	}

	private AxisAlignedBB rotateAabb(EnumFacing face, AxisAlignedBB aabb)
	{
		double length = aabb.maxX - aabb.minX;
		double width = aabb.maxZ - aabb.minZ;

		switch (face)
		{
			case NORTH:
				break; // Default AABB state should always be north
			case SOUTH:
				aabb = new AxisAlignedBB(1 - aabb.maxX, aabb.minY, 1 - aabb.maxZ, 1 - aabb.maxX + length, aabb.maxY, 1 - aabb.maxZ + width);
				break;
			case WEST:
				aabb = new AxisAlignedBB(aabb.maxZ, aabb.minY, 1 - aabb.maxX, aabb.minZ, aabb.maxY, 1 - aabb.maxX + length);
				break;
			case EAST:
				aabb = new AxisAlignedBB(1 - aabb.maxZ, aabb.minY, 1 - aabb.maxX, 1 - aabb.maxZ + width, aabb.maxY, 1 - aabb.maxX + length);
				break;
		}

		return aabb;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		if (!hasComplexBounds)
			return super.getBlockLayer();
		return BlockRenderLayer.CUTOUT;
	}

	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		if (!hasComplexBounds)
			return super.isPassable(worldIn, pos);
		return true;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (!hasComplexBounds)
			return super.getBoundingBox(state, source, pos);
		return FULL_BLOCK_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		if (!hasComplexBounds)
			return super.isOpaqueCube(state);
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		if (!hasComplexBounds)
			return super.isOpaqueCube(state);
		return false;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
	{
		if (!hasComplexBounds)
			super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
		else
		{
			EnumFacing facing = state.getValue(FACING);

			List<AxisAlignedBB> boxes = boundingBoxes.get(facing);
			for (AxisAlignedBB box : boxes)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
		}
	}
}
