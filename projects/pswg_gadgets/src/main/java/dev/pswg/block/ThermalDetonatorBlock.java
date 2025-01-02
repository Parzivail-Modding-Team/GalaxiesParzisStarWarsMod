package dev.pswg.block;

import dev.pswg.Gadgets;
import dev.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ThermalDetonatorBlock extends GrenadeBlock
{
	public ThermalDetonatorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public EntityType<ThermalDetonatorEntity> getEntity()
	{
		return Gadgets.THERMAL_DETONATOR_ENTITY;
	}

	@Override
	public Item getItem()
	{
		return Gadgets.THERMAL_DETONATOR_ITEM;
	}

	@Override
	public VoxelShape getSingleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
				VoxelShapes.cuboid(0.46875, 0.15625, 0.453125, 0.53125, 0.21875, 0.578125)
		);
	}

	@Override
	public VoxelShape getDoubleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.296875, 0, 0.40625, 0.484375, 0.1875, 0.59375),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.453125, 0.421875, 0.21875, 0.578125),

				VoxelShapes.cuboid(0.515625, 0, 0.40625, 0.703125, 0.1875, 0.59375),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.453125, 0.640625, 0.21875, 0.578125)
		);
	}

	@Override
	public VoxelShape getTripleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.40625, 0, 0.515625, 0.59375, 0.1875, 0.703125),
				VoxelShapes.cuboid(0.46875, 0.15625, 0.5625, 0.53125, 0.21875, 0.6875)
		);
	}

	@Override
	public VoxelShape getQuadrupleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

				VoxelShapes.cuboid(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875)
		);
	}

	@Override
	public VoxelShape getQuintupleShape()
	{
		return VoxelShapes.union(
				VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

				VoxelShapes.cuboid(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
				VoxelShapes.cuboid(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

				VoxelShapes.cuboid(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
				VoxelShapes.cuboid(0.46875, 0.15625, 0.421875, 0.53125, 0.21875, 0.546875),

				VoxelShapes.cuboid(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
				VoxelShapes.cuboid(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875),

				VoxelShapes.cuboid(0.40625, 0.1875, 0.40625, 0.59375, 0.375, 0.59375),
				VoxelShapes.cuboid(0.46875, 0.34375, 0.453125, 0.53125, 0.40625, 0.578125)
		);
	}

	@Override
	public int calculatePower(int grenadeCount)
	{
		return grenadeCount + 4;
	}
}
