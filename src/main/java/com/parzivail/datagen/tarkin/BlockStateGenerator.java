package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.data.client.model.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * Heavily based on BlockStateModelGenerator
 */
public class BlockStateGenerator
{
	public static VariantsBlockStateSupplier basic(Block block, Identifier modelId)
	{
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId));
	}

	public static VariantsBlockStateSupplier forBooleanProperty(Block block, BooleanProperty property, Identifier trueModel, Identifier falseModel)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(createBooleanModelMap(property, trueModel, falseModel));
	}

	public static VariantsBlockStateSupplier randomRotation(Block block, Identifier modelId)
	{
		return VariantsBlockStateSupplier.create(block, modelRandomRotation(modelId));
	}

	public static VariantsBlockStateSupplier randomMirror(Block block, Identifier modelId, Identifier mirroredModelId)
	{
		return VariantsBlockStateSupplier.create(block, modelRandomMirror(modelId, mirroredModelId));
	}

	public static VariantsBlockStateSupplier stages(Block block, Identifier modelId, IntProperty ageProperty)
	{
		BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(ageProperty).register((integer) -> BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(modelId, "_stage" + integer)));
		return VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap);
	}

	private static BlockStateVariant[] modelRandomRotation(Identifier modelId)
	{
		return new BlockStateVariant[] {
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R90),
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R180),
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
		};
	}

	private static BlockStateVariant[] modelRandomMirror(Identifier modelId, Identifier mirroredModelId)
	{
		return new BlockStateVariant[] {
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
				BlockStateVariant.create().put(VariantSettings.MODEL, mirroredModelId),
				BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R180),
				BlockStateVariant.create().put(VariantSettings.MODEL, mirroredModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
		};
	}

	public static BlockStateSupplier column(Block block, Identifier modelId)
	{
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId)).coordinate(createAxisRotatedVariantMap());
	}

	private static BlockStateVariantMap createBooleanModelMap(BooleanProperty property, Identifier trueModel, Identifier falseModel)
	{
		return BlockStateVariantMap.create(property)
		                           .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, trueModel))
		                           .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, falseModel));
	}

	private static BlockStateVariantMap createAxisRotatedVariantMap()
	{
		return BlockStateVariantMap.create(Properties.AXIS)
		                           .register(Direction.Axis.Y, BlockStateVariant.create())
		                           .register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
		                           .register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	private static BlockStateVariantMap createUpDefaultFacingVariantMap()
	{
		return BlockStateVariantMap.create(Properties.FACING)
		                           .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
		                           .register(Direction.UP, BlockStateVariant.create())
		                           .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
		                           .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180))
		                           .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
		                           .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	private static BlockStateVariantMap createUpDefaultFacingVariantMap(Identifier wallModel, Identifier floorModel)
	{
		return BlockStateVariantMap.create(Properties.FACING)
		                           .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, floorModel))
		                           .register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, floorModel))
		                           .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, wallModel))
		                           .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, wallModel))
		                           .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, wallModel))
		                           .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, wallModel));
	}

	public static BlockStateSupplier slab(Block block, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.SLAB_TYPE)
		                                                                               .register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
		                                                                               .register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
		                                                                               .register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId)));
	}

	public static BlockStateSupplier stairs(Block block, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)));
	}

	public static BlockStateSupplier tangentRotating(Block block, Identifier model)
	{
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)).coordinate(createUpDefaultFacingVariantMap());
	}

	public static BlockStateSupplier tangentRotating(Block block, Identifier wallModel, Identifier floorModel)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(createUpDefaultFacingVariantMap(wallModel, floorModel));
	}
}
