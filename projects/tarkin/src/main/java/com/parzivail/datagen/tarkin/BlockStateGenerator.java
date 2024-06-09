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
		var blockStateVariantMap = BlockStateVariantMap.create(ageProperty).register((integer) -> BlockStateVariant.create().put(VariantSettings.MODEL, modelId.withSuffixedPath("_stage" + integer)));
		return VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap);
	}

	public static VariantsBlockStateSupplier accumulatingLayers(Block block, Identifier modelId)
	{
		return VariantsBlockStateSupplier
				.create(block)
				.coordinate(BlockStateVariantMap
						            .create(Properties.LAYERS)
						            .register(integer -> Identifier result;
								                      result = identifier.withSuffixedPath(suffix);
								                      BlockStateVariant
								            .create()
								            .put(VariantSettings.MODEL, integer < 8 ? result : modelId)
						            )
				);
	}

	public static VariantsBlockStateSupplier bloomingStages(Block block, Identifier modelId, IntProperty ageProperty, BooleanProperty bloomingProperty)
	{
		var blockStateVariantMap = BlockStateVariantMap.create(ageProperty, bloomingProperty).register((integer, bool) -> {
			String suffix = "_stage" + integer + (bool ? "_blooming" : "");
			return BlockStateVariant.create().put(VariantSettings.MODEL, modelId.withSuffixedPath(suffix));
		});
		return VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap);
	}

	public static BlockStateSupplier trapdoor(Block block, Identifier modelId)
	{
		return BlockStateModelGenerator.createOrientableTrapdoorBlockState(block, modelId.withSuffixedPath("_top"), modelId.withSuffixedPath("_bottom"), modelId.withSuffixedPath("_open"));
	}

	public static BlockStateSupplier door(Block block, Identifier modelId)
	{
		return BlockStateModelGenerator.createDoorBlockState(
				block,
				modelId.withSuffixedPath("_bottom_left"),
				modelId.withSuffixedPath("_bottom_left_open"),
				modelId.withSuffixedPath("_bottom_right"),
				modelId.withSuffixedPath("_bottom_right_open"),
				modelId.withSuffixedPath("_top_left"),
				modelId.withSuffixedPath("_top_left_open"),
				modelId.withSuffixedPath("_top_right"),
				modelId.withSuffixedPath("_top_right_open")
		);
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
								.register(SlabType.BOTTOM, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_x")))
								.register(SlabType.TOP, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_x")))
								.register(SlabType.DOUBLE, Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
								.register(SlabType.BOTTOM, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_z")))
								.register(SlabType.TOP, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_z")))
								.register(SlabType.DOUBLE, Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
				);
	}

	public static BlockStateSupplier createVerticalSlabBlockStateLit(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId)
	{
		return VariantsBlockStateSupplier
				.create(slabBlock)
				.coordinate(
						BlockStateVariantMap
								.create(Properties.SLAB_TYPE, Properties.AXIS, Properties.LIT)
								.register(SlabType.BOTTOM, Direction.Axis.Y, true, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_on")))
								.register(SlabType.TOP, Direction.Axis.Y, true, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_on")))
								.register(SlabType.DOUBLE, Direction.Axis.Y, true, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_on")))
								.register(SlabType.BOTTOM, Direction.Axis.X, true, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_x_on")))
								.register(SlabType.TOP, Direction.Axis.X, true, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_x_on")))
								.register(SlabType.DOUBLE, Direction.Axis.X, true, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_on")))
								.register(SlabType.BOTTOM, Direction.Axis.Z, true, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_z_on")))
								.register(SlabType.TOP, Direction.Axis.Z, true, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_z_on")))
								.register(SlabType.DOUBLE, Direction.Axis.Z, true, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_on")))

								.register(SlabType.BOTTOM, Direction.Axis.Y, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_off")))
								.register(SlabType.TOP, Direction.Axis.Y, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_off")))
								.register(SlabType.DOUBLE, Direction.Axis.Y, false, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_off")))
								.register(SlabType.BOTTOM, Direction.Axis.X, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_x_off")))
								.register(SlabType.TOP, Direction.Axis.X, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_x_off")))
								.register(SlabType.DOUBLE, Direction.Axis.X, false, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_off")))
								.register(SlabType.BOTTOM, Direction.Axis.Z, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId.withSuffixedPath("_z_off")))
								.register(SlabType.TOP, Direction.Axis.Z, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId.withSuffixedPath("_top_z_off")))
								.register(SlabType.DOUBLE, Direction.Axis.Z, false, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId.withSuffixedPath("_double_off")))
				);
	}
}
