package dev.pswg.block;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThermalDetonatorBlock extends GrenadeBlock
{
	public ThermalDetonatorBlock(Properties settings)
	{
		super(settings);
	}

	@Override
	public EntityType<ThermalDetonatorEntity> getEntityType()
	{
		return GadgetsEntities.THERMAL_DETONATOR_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return GadgetsItems.THERMAL_DETONATOR_ITEM;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return Shapes.or(
				Shapes.box(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
				Shapes.box(0.46875, 0.15625, 0.453125, 0.53125, 0.21875, 0.578125)
		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.40625, 0.484375, 0.1875, 0.59375),
				Shapes.box(0.359375, 0.15625, 0.453125, 0.421875, 0.21875, 0.578125),

				Shapes.box(0.515625, 0, 0.40625, 0.703125, 0.1875, 0.59375),
				Shapes.box(0.578125, 0.15625, 0.453125, 0.640625, 0.21875, 0.578125)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				Shapes.box(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				Shapes.box(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				Shapes.box(0.40625, 0, 0.515625, 0.59375, 0.1875, 0.703125),
				Shapes.box(0.46875, 0.15625, 0.5625, 0.53125, 0.21875, 0.6875)
		);
	}

	@Override
	public VoxelShape getQuadrupleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				Shapes.box(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				Shapes.box(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				Shapes.box(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
				Shapes.box(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

				Shapes.box(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
				Shapes.box(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875)
		);
	}

	@Override
	public VoxelShape getQuintupleShape()
	{
		return Shapes.or(
				Shapes.box(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				Shapes.box(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				Shapes.box(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				Shapes.box(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				Shapes.box(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
				Shapes.box(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

				Shapes.box(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
				Shapes.box(0.46875, 0.15625, 0.421875, 0.53125, 0.21875, 0.546875),

				Shapes.box(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
				Shapes.box(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875),

				Shapes.box(0.40625, 0.1875, 0.40625, 0.59375, 0.375, 0.59375),
				Shapes.box(0.46875, 0.34375, 0.453125, 0.53125, 0.40625, 0.578125)
		);
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return grenadeCount + 4;
	}
}
