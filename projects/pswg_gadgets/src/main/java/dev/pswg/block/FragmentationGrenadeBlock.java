package dev.pswg.block;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.FragmentationGrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class FragmentationGrenadeBlock extends GrenadeBlock
{
	public FragmentationGrenadeBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public EntityType<FragmentationGrenadeEntity> getEntityType()
	{
		return Gadgets.FRAGMENTATION_GRENADE_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return Gadgets.FRAGMENTATION_GRENADE_ITEM;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.28125, 0.015625, 0.4375, 0.71875, 0.140625, 0.5625)

		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
				VoxelShapes.cuboid(0.281255, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.28125, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625),
				VoxelShapes.cuboid(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
				VoxelShapes.cuboid(0.28125, 0.165625, 0.4375, 0.71875, 0.290625, 0.5625)
		);
	}

	@Override
	public VoxelShape getQuadrupleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.28125, 0.015625, 0.3625, 0.71875, 0.140625, 0.4875),
				VoxelShapes.cuboid(0.28125, 0.165625, 0.3625, 0.71875, 0.290625, 0.4875),
				VoxelShapes.cuboid(0.28125, 0.015625, 0.5125, 0.71875, 0.140625, 0.6375),
				VoxelShapes.cuboid(0.28125, 0.165625, 0.5125, 0.71875, 0.290625, 0.6375)
		);
	}

	@Override
	public VoxelShape getQuintupleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.28125, 0.015625, 0.3625, 0.71875, 0.140625, 0.4875),
				VoxelShapes.cuboid(0.28125, 0.165625, 0.3625, 0.71875, 0.290625, 0.4875),
				VoxelShapes.cuboid(0.28125, 0.0156258, 0.5125, 0.71875, 0.140625, 0.6375),
				VoxelShapes.cuboid(0.28125, 0.165625, 0.5125, 0.71875, 0.290625, 0.6375),
				VoxelShapes.cuboid(0.28125, 0.315625, 0.4375, 0.71875, 0.440625, 0.5625)
		);
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return grenadeCount / 2 + 2;
	}
}
