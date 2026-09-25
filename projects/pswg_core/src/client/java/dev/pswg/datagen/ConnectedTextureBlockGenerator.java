package dev.pswg.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.pswg.Galaxies;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

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

	private enum TextureQuadrant
	{
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	public static void register(BlockModelGenerators generator, Block block, Identifier borderTexture, Identifier centerTexture)
	{
		generateBlockModels(generator, block, borderTexture, centerTexture);

		Identifier connectingModel = connectingModelId(block);
		generator.blockStateOutput.accept(createBlockstate(block, connectingModel));

		TextureMapping mapping = TextureMapping.cube(new Material(borderTexture));
		Identifier itemModel = new ModelTemplate(Optional.of(Galaxies.id("block/template_connected_block_item")), Optional.empty(), TextureSlot.ALL).create(block, mapping, generator.modelOutput);
		generator.registerSimpleItemModel(block, itemModel);
	}

	private static void generateBlockModels(BlockModelGenerators generator, Block block, Identifier borderTexture, Identifier centerTexture)
	{
		for (TextureQuadrant quadrant : TextureQuadrant.values())
		{
			writeModel(generator, normalModelId(block, quadrant, "none"), borderTexture, quadrant, sourceRegion(quadrant, 0));
			writeModel(generator, normalModelId(block, quadrant, "vertical"), borderTexture, quadrant, sourceRegion(quadrant, 1));
			writeModel(generator, normalModelId(block, quadrant, "horizontal"), borderTexture, quadrant, sourceRegion(quadrant, 2));
			writeModel(generator, cornerModelId(block, quadrant), borderTexture, quadrant, sourceRegion(quadrant, 3));
			writeCenterModel(generator, centerModelId(block, quadrant), centerTexture, quadrant);
		}

		writeConnectingModel(generator, connectingModelId(block), borderTexture);
	}

	private static Identifier normalModelId(Block block, TextureQuadrant quadrant, String state)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_" + state);
	}

	private static Identifier cornerModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_corner");
	}

	private static Identifier centerModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_center");
	}

	private static Identifier connectingModelId(Block block)
	{
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey =  blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "/" + blockKey +"_connecting_model");
	}
	private static Identifier getBaseIdentifier(Block block){
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey =  blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "/" + blockKey + "_");
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
					yield new SourceRegion(16, 16);

				if (vertical)
					yield new SourceRegion(0, 16);

				if (horizontal)
					yield new SourceRegion(16, 0);

				yield new SourceRegion(0, 0);
			}

			case TOP_LEFT ->
			{
				if (vertical && horizontal)
					yield new SourceRegion(24, 16);

				if (vertical)
					yield new SourceRegion(8, 16);

				if (horizontal)
					yield new SourceRegion(24, 0);

				yield new SourceRegion(8, 0);
			}

			case BOTTOM_RIGHT ->
			{
				if (vertical && horizontal)
					yield new SourceRegion(16, 24);

				if (vertical)
					yield new SourceRegion(0, 24);

				if (horizontal)
					yield new SourceRegion(16, 8);

				yield new SourceRegion(0, 8);
			}

			case BOTTOM_LEFT ->
			{
				if (vertical && horizontal)
					yield new SourceRegion(24, 24);

				if (vertical)
					yield new SourceRegion(8, 24);

				if (horizontal)
					yield new SourceRegion(24, 8);

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
		JsonObject northFace = new JsonObject();

		northFace.add("uv", createUv(source));

		northFace.addProperty("texture", "#texture");
		northFace.addProperty("cullface", "north");

		faces.add("north", northFace);

		element.add("faces", faces);

		elements.add(element);

		json.add("elements", elements);

		generator.modelOutput.accept(modelId, () -> json);
	}

	private static void writeCenterModel(BlockModelGenerators generator, Identifier modelId, Identifier texture, TextureQuadrant quadrant)
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
		JsonObject northFace = new JsonObject();

		northFace.add("uv", createCenterUv(quadrant));

		northFace.addProperty("texture", "#texture");
		northFace.addProperty("cullface", "north");

		faces.add("north", northFace);
		element.add("faces", faces);

		elements.add(element);

		json.add("elements", elements);

		generator.modelOutput.accept(modelId, () -> json);
	}

	private static void writeConnectingModel(BlockModelGenerators generator, Identifier modelId, Identifier texture)
	{
		JsonObject json = new JsonObject();
		JsonObject textures = new JsonObject();

		textures.addProperty("particle", texture.toString());
		textures.addProperty("texture", texture.toString());

		json.add("textures", textures);

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

	private static JsonArray createCenterUv(TextureQuadrant quadrant)
	{
		float u0 = quadrantMinX(quadrant);
		float v0 = 16 - quadrantMaxY(quadrant);
		float u1 = quadrantMaxX(quadrant);
		float v1 = 16 - quadrantMinY(quadrant);

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

	private static BlockModelDefinitionGenerator createBlockstate(Block block, Identifier connectingModel)
	{
		return new BlockModelDefinitionGenerator()
		{
			@Override
			public Block block()
			{
				return block;
			}

			@Override
			public BlockStateModelDispatcher create()
			{
				return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(Map.of("", new net.minecraft.client.renderer.block.dispatch.SingleVariant.Unbaked(new net.minecraft.client.renderer.block.dispatch.Variant(connectingModel))))), Optional.empty());
			}
		};
	}
}