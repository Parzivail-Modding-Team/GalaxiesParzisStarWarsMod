package com.parzivail.datagen.tarkin;

import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
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
	public static VariantsBlockStateSupplier forBooleanProperty(Block block, BooleanProperty property, Identifier trueModel, Identifier falseModel)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(createBooleanModelMap(property, trueModel, falseModel));
	}

	public static VariantsBlockStateSupplier stages(Block block, Identifier modelId, IntProperty ageProperty)
	{
		var blockStateVariantMap = BlockStateVariantMap.create(ageProperty).register((integer) -> BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(modelId, "_stage" + integer)));
		return VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap);
	}

	public static VariantsBlockStateSupplier accumulatingLayers(Block block, Identifier modelId)
	{
		return VariantsBlockStateSupplier
				.create(block)
				.coordinate(BlockStateVariantMap
						            .create(Properties.LAYERS)
						            .register(integer -> BlockStateVariant
								            .create()
								            .put(VariantSettings.MODEL, integer < 8 ? IdentifierUtil.concat(modelId, "_height" + integer * 2) : modelId)
						            )
				);
	}

	public static VariantsBlockStateSupplier bloomingStages(Block block, Identifier modelId, IntProperty ageProperty, BooleanProperty bloomingProperty)
	{
		var blockStateVariantMap = BlockStateVariantMap.create(ageProperty, bloomingProperty).register((integer, bool) -> BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(modelId, "_stage" + integer + (bool ? "_blooming" : ""))));
		return VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap);
	}

	public static BlockStateSupplier trapdoor(Block block, Identifier modelId)
	{
		return BlockStateModelGenerator.createOrientableTrapdoorBlockState(block, IdentifierUtil.concat(modelId, "_top"), IdentifierUtil.concat(modelId, "_bottom"), IdentifierUtil.concat(modelId, "_open"));
	}

	public static BlockStateSupplier door(Block block, Identifier modelId)
	{
		return BlockStateModelGenerator.createDoorBlockState(block, IdentifierUtil.concat(modelId, "_bottom"), IdentifierUtil.concat(modelId, "_bottom_hinge"), IdentifierUtil.concat(modelId, "_top"), IdentifierUtil.concat(modelId, "_top_hinge"));
	}

	private static BlockStateVariantMap createBooleanModelMap(BooleanProperty property, Identifier trueModel, Identifier falseModel)
	{
		return BlockStateVariantMap
				.create(property)
				.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, trueModel))
				.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, falseModel));
	}

	private static BlockStateVariantMap createUpDefaultFacingVariantMap()
	{
		return BlockStateVariantMap
				.create(Properties.FACING)
				.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
				.register(Direction.UP, BlockStateVariant.create())
				.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
				.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	private static BlockStateVariantMap createUpDefaultFacingVariantMap(Identifier wallModel, Identifier floorModel)
	{
		return BlockStateVariantMap
				.create(Properties.FACING)
				.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, floorModel))
				.register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, floorModel))
				.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, wallModel))
				.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, wallModel))
				.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, wallModel))
				.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, wallModel));
	}

	public static BlockStateSupplier tangentRotating(Block block, Identifier model)
	{
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)).coordinate(createUpDefaultFacingVariantMap());
	}

	public static BlockStateSupplier tangentRotating(Block block, Identifier wallModel, Identifier floorModel)
	{
		return VariantsBlockStateSupplier.create(block).coordinate(createUpDefaultFacingVariantMap(wallModel, floorModel));
	}

	public static BlockStateSupplier createVerticalSlabBlockState(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId)
	{
		return VariantsBlockStateSupplier
				.create(slabBlock)
				.coordinate(
						BlockStateVariantMap
								.create(Properties.SLAB_TYPE, Properties.AXIS)
								.register(SlabType.BOTTOM, Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
								.register(SlabType.TOP, Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
								.register(SlabType.DOUBLE, Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
								.register(SlabType.BOTTOM, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(bottomModelId, "_x")))
								.register(SlabType.TOP, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(topModelId, "_x")))
								.register(SlabType.DOUBLE, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
								.register(SlabType.BOTTOM, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(bottomModelId, "_z")))
								.register(SlabType.TOP, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, IdentifierUtil.concat(topModelId, "_z")))
								.register(SlabType.DOUBLE, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
				);
	}
}
