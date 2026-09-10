package dev.pswg.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class ConnectedTextureBlockGenerator
{
	private static final int QUADRANT_SIZE = 8;
	private static final float UV_SCALE = 0.5f;

	private ConnectedTextureBlockGenerator()
	{
	}

	private record SourceRegion(int x, int y)
	{
	}

	private enum Face
	{
		NORTH, SOUTH, EAST, WEST, UP, DOWN
	}

	private enum TextureQuadrant
	{
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	private record FaceConnections(Direction top, Direction bottom, Direction left, Direction right)
	{
	}

	public static void register(BlockModelGenerators generator, Block block, Identifier texture)
	{
		Map<TextureQuadrant, Map<Integer, Identifier>> models = generateModels(generator, block, texture);

		generator.blockStateOutput.accept(createBlockstate(block, models));
	}

	private static Map<TextureQuadrant, Map<Integer, Identifier>> generateModels(BlockModelGenerators generator, Block block, Identifier texture)
	{
		Map<TextureQuadrant, Map<Integer, Identifier>> result = new EnumMap<>(TextureQuadrant.class);

		for (TextureQuadrant quadrant : TextureQuadrant.values())
		{
			Map<Integer, Identifier> quadrantModels = new HashMap<>();

			for (int state = 0; state < 4; state++)
			{
				Identifier modelId = modelId(block, quadrant, state);
				SourceRegion source = sourceRegion(quadrant, state);

				writeModel(generator, modelId, texture, quadrant, source);

				quadrantModels.put(state, modelId);
			}

			result.put(quadrant, quadrantModels);
		}

		return result;
	}

	private static Identifier modelId(Block block, TextureQuadrant quadrant, int state)
	{
		Identifier blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);

		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "/" + quadrant.name().toLowerCase() + "_" + stateName(state));
	}

	private static String stateName(int state)
	{
		return switch (state)
		{
			case 0 -> "none";
			case 1 -> "vertical";
			case 2 -> "horizontal";
			case 3 -> "all";
			default -> throw new IllegalArgumentException("Invalid connected texture state: " + state);
		};
	}

	private static SourceRegion sourceRegion(TextureQuadrant quadrant, int state)
	{
		boolean vertical = (state & 1) != 0;

		boolean horizontal = (state & 2) != 0;

		return switch (quadrant)
		{
			case TOP_RIGHT ->
			{
				if (vertical && horizontal)
				{
					yield new SourceRegion(16, 16);
				}

				if (vertical)
				{
					yield new SourceRegion(0, 16);
				}

				if (horizontal)
				{
					yield new SourceRegion(16, 0);
				}

				yield new SourceRegion(0, 0);
			}

			case TOP_LEFT ->
			{
				if (vertical && horizontal)
				{
					yield new SourceRegion(24, 16);
				}

				if (vertical)
				{
					yield new SourceRegion(8, 16);
				}

				if (horizontal)
				{
					yield new SourceRegion(24, 0);
				}

				yield new SourceRegion(8, 0);
			}

			case BOTTOM_RIGHT ->
			{
				if (vertical && horizontal)
				{
					yield new SourceRegion(16, 24);
				}

				if (vertical)
				{
					yield new SourceRegion(0, 24);
				}

				if (horizontal)
				{
					yield new SourceRegion(16, 8);
				}

				yield new SourceRegion(0, 8);
			}

			case BOTTOM_LEFT ->
			{
				if (vertical && horizontal)
				{
					yield new SourceRegion(24, 24);
				}

				if (vertical)
				{
					yield new SourceRegion(8, 24);
				}

				if (horizontal)
				{
					yield new SourceRegion(24, 8);
				}

				yield new SourceRegion(8, 8);
			}
		};
	}

	private static void writeModel(BlockModelGenerators generator, Identifier modelId, Identifier texture, TextureQuadrant quadrant, SourceRegion source)
	{
		JsonObject json = new JsonObject();
		json.addProperty("ambientocclusion", true);

		JsonObject textures = new JsonObject();
		textures.addProperty("texture", texture.toString());
		textures.addProperty("particle", texture.toString());
		json.add("textures", textures);

		JsonArray elements = new JsonArray();
		JsonObject element = new JsonObject();

		JsonArray from = new JsonArray();
		JsonArray to = new JsonArray();

		addGeometry(from, to, quadrant);

		element.add("from", from);
		element.add("to", to);

		JsonObject faces = new JsonObject();
		JsonObject defaultFace = new JsonObject();

		defaultFace.add("uv", createUv(source));
		defaultFace.addProperty("texture", "#texture");
		defaultFace.addProperty("cullface", "north");

		faces.add("north", defaultFace);

		element.add("faces", faces);

		elements.add(element);

		json.add("elements", elements);

		generator.modelOutput.accept(modelId, () -> json);
	}

	private static JsonArray createUv(SourceRegion source)
	{
		float u0 = source.x() * UV_SCALE;
		float v0 = source.y() * UV_SCALE;
		float u1 = (source.x() + QUADRANT_SIZE) * UV_SCALE;
		float v1 = (source.y() + QUADRANT_SIZE) * UV_SCALE;

		JsonArray uv = new JsonArray();

		add(uv, u0);
		add(uv, v0);
		add(uv, u1);
		add(uv, v1);

		return uv;
	}

	private static void addGeometry(JsonArray from, JsonArray to, TextureQuadrant quadrant)
	{
		float x0 = quadrantMinX(quadrant);
		float x1 = quadrantMaxX(quadrant);
		float y0 = quadrantMinY(quadrant);
		float y1 = quadrantMaxY(quadrant);

		add(from, x0);
		add(from, y0);
		add(from, 0);

		add(to, x1);
		add(to, y1);
		add(to, 0);
	}

	private static float quadrantMinX(TextureQuadrant quadrant)
	{
		return switch (quadrant)
		{
			case TOP_LEFT, BOTTOM_LEFT -> 0;
			case TOP_RIGHT, BOTTOM_RIGHT -> 8;
		};
	}

	private static float quadrantMaxX(TextureQuadrant quadrant)
	{
		return quadrantMinX(quadrant) + QUADRANT_SIZE;
	}

	private static float quadrantMinY(TextureQuadrant quadrant)
	{
		return switch (quadrant)
		{
			case TOP_LEFT, TOP_RIGHT -> 8;
			case BOTTOM_LEFT, BOTTOM_RIGHT -> 0;
		};
	}

	private static float quadrantMaxY(TextureQuadrant quadrant)
	{
		return quadrantMinY(quadrant) + QUADRANT_SIZE;
	}

	private static void add(JsonArray array, float value)
	{
		array.add(value);
	}

	private static BlockModelDefinitionGenerator createBlockstate(Block block, Map<TextureQuadrant, Map<Integer, Identifier>> models)
	{
		MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

		for (Face face : Face.values())
		{
			addFace(generator, face, models);
		}

		return generator;
	}

	private static void addFace(MultiPartGenerator generator, Face face, Map<TextureQuadrant, Map<Integer, Identifier>> models)
	{
		FaceConnections connections = connections(face);

		addQuadrant(generator, face, models.get(TextureQuadrant.TOP_LEFT), connections.top(), connections.left());
		addQuadrant(generator, face, models.get(TextureQuadrant.TOP_RIGHT), connections.top(), connections.right());
		addQuadrant(generator, face, models.get(TextureQuadrant.BOTTOM_LEFT), connections.bottom(), connections.left());
		addQuadrant(generator, face, models.get(TextureQuadrant.BOTTOM_RIGHT), connections.bottom(), connections.right());
	}

	private static void addQuadrant(MultiPartGenerator generator, Face face, Map<Integer, Identifier> models, Direction verticalDirection, Direction horizontalDirection)
	{
		BooleanProperty verticalProperty = propertyForDirection(verticalDirection);
		BooleanProperty horizontalProperty = propertyForDirection(horizontalDirection);

		generator.with(new ConditionBuilder()
				               .negatedTerm(verticalProperty, true)
				               .negatedTerm(horizontalProperty, true)
				               .build(),
		               variant(models.get(0), face));
		generator.with(new ConditionBuilder()
				               .term(verticalProperty, true)
				               .negatedTerm(horizontalProperty, true)
				               .build(),
		               variant(models.get(1), face));

		generator.with(new ConditionBuilder()
				               .negatedTerm(verticalProperty, true)
				               .term(horizontalProperty, true)
				               .build(),
		               variant(models.get(2), face));

		generator.with(new ConditionBuilder()
				               .term(verticalProperty, true)
				               .term(horizontalProperty, true)
				               .build(),
		               variant(models.get(3), face));
	}

	private static MultiVariant variant(Identifier model, Face face)
	{
		Variant variant = new Variant(model);

		variant = switch (face)
		{
			case NORTH -> variant;
			case SOUTH -> variant.withYRot(Quadrant.R180);
			case EAST -> variant.withYRot(Quadrant.R270);
			case WEST -> variant.withYRot(Quadrant.R90);
			case UP -> variant.withXRot(Quadrant.R90);
			case DOWN -> variant.withXRot(Quadrant.R270);
		};

		return new MultiVariant(WeightedList.of(variant));
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
}