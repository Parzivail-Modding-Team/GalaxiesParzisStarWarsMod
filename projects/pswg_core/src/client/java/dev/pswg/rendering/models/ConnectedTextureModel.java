package dev.pswg.rendering.models;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Map;
import java.util.function.Predicate;

public final class ConnectedTextureModel extends WrapperBlockStateModel
{
	private final Block block;

	private final Map<Face, Map<TextureQuadrant, BlockStateModelPart>> noneModels;
	private final Map<Face, Map<TextureQuadrant, BlockStateModelPart>> verticalModels;
	private final Map<Face, Map<TextureQuadrant, BlockStateModelPart>> horizontalModels;
	private final Map<Face, Map<TextureQuadrant, BlockStateModelPart>> cornerModels;
	private final Map<Face, Map<TextureQuadrant, BlockStateModelPart>> centerModels;

	public ConnectedTextureModel(Block block,
			BlockStateModel fallback,
			Map<Face, Map<TextureQuadrant, BlockStateModelPart>> noneModels,
			Map<Face, Map<TextureQuadrant, BlockStateModelPart>> verticalModels,
			Map<Face, Map<TextureQuadrant, BlockStateModelPart>> horizontalModels,
			Map<Face, Map<TextureQuadrant, BlockStateModelPart>> cornerModels,
			Map<Face, Map<TextureQuadrant, BlockStateModelPart>> centerModels
	)
	{
		super(fallback);

		this.block = block;
		this.noneModels = noneModels;
		this.verticalModels = verticalModels;
		this.horizontalModels = horizontalModels;
		this.cornerModels = cornerModels;
		this.centerModels = centerModels;
	}

	@Override
	public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<Direction> cullTest)
	{
		for (Face face : Face.values())
		{
			emitFace(emitter, level, pos, state, face, cullTest);
		}
	}

	private void emitFace(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, Face face, Predicate<Direction> cullTest)
	{
		FaceConnections connections = connections(face);

		emitQuadrant(emitter, level, pos, state, face, TextureQuadrant.TOP_LEFT, connections.top(), connections.left(), cullTest);
		emitQuadrant(emitter, level, pos, state, face, TextureQuadrant.TOP_RIGHT, connections.top(), connections.right(), cullTest);
		emitQuadrant(emitter, level, pos, state, face, TextureQuadrant.BOTTOM_LEFT, connections.bottom(), connections.left(), cullTest);
		emitQuadrant(emitter, level, pos, state, face, TextureQuadrant.BOTTOM_RIGHT, connections.bottom(), connections.right(), cullTest);
	}

	private void emitQuadrant(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, Face face, TextureQuadrant quadrant, Direction verticalDirection, Direction horizontalDirection, Predicate<Direction> cullTest)
	{
		BooleanProperty verticalProperty = propertyForDirection(verticalDirection);

		BooleanProperty horizontalProperty = propertyForDirection(horizontalDirection);

		boolean vertical = state.getValue(verticalProperty);
		boolean horizontal = state.getValue(horizontalProperty);

		BlockStateModelPart model = null;

		if (!vertical && !horizontal)
		{
			model = noneModels.get(face).get(quadrant);
		}
		else if (vertical && !horizontal)
		{
			model = verticalModels.get(face).get(quadrant);
		}
		else if (!vertical && horizontal)
		{
			model = horizontalModels.get(face).get(quadrant);
		}
		else
		{

			BlockPos diagonal = pos.relative(verticalDirection).relative(horizontalDirection);

			if (level.getBlockState(diagonal).is(block))
				model = centerModels.get(face).get(quadrant);
			else
				model = cornerModels.get(face).get(quadrant);
		}

		if (model != null)
		{
			model.emitQuads(emitter, cullTest);
		}
	}

	@Override
	public Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random)
	{
		long mask = 0;
		int bit = 0;

		for (Face face : Face.values())
		{
			FaceConnections connections = connections(face);

			mask |= connectionBit(state, connections.top()) << bit++;
			mask |= connectionBit(state, connections.bottom()) << bit++;
			mask |= connectionBit(state, connections.left()) << bit++;
			mask |= connectionBit(state, connections.right()) << bit++;

			mask |= diagonalBit(level, pos, connections.top(), connections.left()) << bit++;
			mask |= diagonalBit(level, pos, connections.top(), connections.right()) << bit++;
			mask |= diagonalBit(level, pos, connections.bottom(), connections.left()) << bit++;
			mask |= diagonalBit(level, pos, connections.bottom(), connections.right()) << bit++;
		}

		return mask;
	}

	private long diagonalBit(BlockAndTintGetter level, BlockPos pos, Direction vertical, Direction horizontal)
	{
		boolean connected = level.getBlockState(pos.relative(vertical).relative(horizontal)).is(block);

		return connected ? 1L : 0L;
	}

	private static long connectionBit(BlockState state, Direction direction)
	{
		return state.getValue(propertyForDirection(direction)) ? 1L : 0L;
	}

	private static BooleanProperty propertyForDirection(Direction direction)
	{
		return switch (direction)
		{
			case DOWN -> PipeBlock.DOWN;
			case UP -> PipeBlock.UP;
			case NORTH -> PipeBlock.NORTH;
			case SOUTH -> PipeBlock.SOUTH;
			case EAST -> PipeBlock.EAST;
			case WEST -> PipeBlock.WEST;
		};
	}

	private static FaceConnections connections(Face face)
	{
		return switch (face)
		{
			case NORTH -> new FaceConnections(Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST);
			case SOUTH -> new FaceConnections(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
			case EAST -> new FaceConnections(Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH);
			case WEST -> new FaceConnections(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
			case UP -> new FaceConnections(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
			case DOWN -> new FaceConnections(Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST);
		};
	}

	private record FaceConnections(Direction top, Direction bottom, Direction left, Direction right)
	{
	}

	public enum Face
	{
		NORTH,
		SOUTH,
		EAST,
		WEST,
		UP,
		DOWN
	}

	public enum TextureQuadrant
	{
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
}