package dev.pswg.rendering.models;

import dev.pswg.utility.math.QuadTransformUtil;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadTransform;
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
import org.joml.Vector2f;
import org.joml.Vector3f;

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
			if(!state.getValue(propertyForDirection(faceToDirection(face))))
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
	private static final QuadTransform FLIP_BACKFACE = quad ->
	{
		Vector3f pos0 = quad.copyPos(0, null);
		Vector3f pos1 = quad.copyPos(1, null);
		Vector3f pos2 = quad.copyPos(2, null);
		Vector3f pos3 = quad.copyPos(3, null);

		Vector3f normal0 = quad.copyNormal(0, null);
		Vector3f normal1 = quad.copyNormal(1, null);
		Vector3f normal2 = quad.copyNormal(2, null);
		Vector3f normal3 = quad.copyNormal(3, null);

		quad.pos(0, pos3);
		quad.pos(1, pos2);
		quad.pos(2, pos1);
		quad.pos(3, pos0);

		if (normal0 != null)
		{
			quad.normal(0, -normal3.x, -normal3.y, -normal3.z);
			quad.normal(1, -normal2.x, -normal2.y, -normal2.z);
			quad.normal(2, -normal1.x, -normal1.y, -normal1.z);
			quad.normal(3, -normal0.x, -normal0.y, -normal0.z);
		}

		quad.cullFace(null);

		return true;
	};
	private static final QuadTransform ROTATE_UV_90 = quad ->
	{
		Vector2f uv0 = quad.copyUv(0, null);
		Vector2f uv1 = quad.copyUv(1, null);
		Vector2f uv2 = quad.copyUv(2, null);
		Vector2f uv3 = quad.copyUv(3, null);

		quad.uv(0, uv1);
		quad.uv(1, uv2);
		quad.uv(2, uv3);
		quad.uv(3, uv0);

		return true;
	};

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



			emitter.pushTransform(QuadTransformUtil.flipFace());
			emitter.pushTransform(QuadTransformUtil.rotateFaceUv180());
			emitter.pushTransform(FLIP_BACKFACE);
			emitter.pushTransform(QuadTransformUtil.moveTowardCenter(faceToDirection(face), 0.001f));
			emitter.cullFace(null);

			model.emitQuads(emitter, direction -> false);

			emitter.popTransform();
			emitter.popTransform();
			emitter.popTransform();
			emitter.popTransform();

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
	private static Direction faceToDirection(Face face){
		return switch (face)
		{
			case NORTH -> Direction.NORTH;
			case SOUTH -> Direction.SOUTH;
			case EAST -> Direction.WEST;
			case WEST -> Direction.EAST;
			case UP -> Direction.DOWN;
			case DOWN -> Direction.UP;
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