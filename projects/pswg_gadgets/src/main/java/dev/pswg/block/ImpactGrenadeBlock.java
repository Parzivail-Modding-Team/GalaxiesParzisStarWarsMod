package dev.pswg.block;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.ImpactGrenadeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ImpactGrenadeBlock extends GrenadeBlock
{
	public ImpactGrenadeBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public EntityType<ImpactGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.IMPACT_GRENADE_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return GadgetsItems.IMPACT_GRENADE_ITEM;
	}

	@Override
	public IntegerProperty getClusterSizeProperty()
	{
		return GrenadeBlock.CLUSTER_SIZE_4;
	}

	@Override
	public int getMaxClusterSize()
	{
		return 4;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return Shapes.or(
				Shapes.box(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375)
		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.40625, 0.484375, 0.1875, 0.59375),
				Shapes.box(0.515625, 0, 0.40625, 0.703125, 0.1875, 0.59375)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				Shapes.box(0.40625, 0, 0.515625, 0.59375, 0.1875, 0.703125)
		);
	}

	@Override
	public VoxelShape getQuadrupleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				Shapes.box(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
				Shapes.box(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125)
		);
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return (grenadeCount + 4) / 2;
	}
}
