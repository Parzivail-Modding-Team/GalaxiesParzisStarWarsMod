package dev.pswg.block;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.InfernoGrenadeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InfernoGrenadeBlock extends GrenadeBlock
{
	public InfernoGrenadeBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public EntityType<InfernoGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.INFERNO_GRENADE_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return GadgetsItems.INFERNO_GRENADE_ITEM;
	}

	@Override
	public IntegerProperty getClusterSizeProperty()
	{
		return GrenadeBlock.CLUSTER_SIZE_3;
	}

	@Override
	public int getMaxClusterSize()
	{
		return 3;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return Shapes.or(
				Shapes.box(0.40625, 0, 0.40625, 0.59375, 0.375, 0.59375)
		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.40625, 0.484375, 0.375, 0.59375),
				Shapes.box(0.515625, 0, 0.40625, 0.703125, 0.375, 0.59375)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.375, 0.484375),
				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.375, 0.484375),
				Shapes.box(0.40625, 0, 0.515625, 0.59375, 0.375, 0.703125)
		);
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return (grenadeCount + 4) / 2;
	}
}
