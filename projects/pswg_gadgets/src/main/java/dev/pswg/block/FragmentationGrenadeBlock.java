package dev.pswg.block;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.FragmentationGrenadeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FragmentationGrenadeBlock extends GrenadeBlock
{
	public FragmentationGrenadeBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public EntityType<FragmentationGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return GadgetsItems.FRAGMENTATION_GRENADE_ITEM;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return Shapes.or(
				Shapes.box(0.28125, 0.015625, 0.4375, 0.71875, 0.140625, 0.5625)

		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return Shapes.or(
				Shapes.box(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
				Shapes.box(0.281255, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return Shapes.or(
				Shapes.box(0.28125, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625),
				Shapes.box(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
				Shapes.box(0.28125, 0.165625, 0.4375, 0.71875, 0.290625, 0.5625)
		);
	}

	@Override
	public int getMaxClusterSize() {
		return 3;
	}

	@Override
	public IntegerProperty getClusterSizeProperty() {
		return GrenadeBlock.CLUSTER_SIZE_3;
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return grenadeCount / 2 + 2;
	}
}
