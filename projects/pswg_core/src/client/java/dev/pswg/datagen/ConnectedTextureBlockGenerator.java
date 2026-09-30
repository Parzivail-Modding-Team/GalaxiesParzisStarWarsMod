package dev.pswg.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.pswg.Galaxies;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
	/**
	 * Registers a block with connected textures on all sides
	 * @param block The block that is receiving the model
	 * @param borderTexture Identifier of the location of the texture used for borders
	 * @param centerTexture Identifier of the location of the texture used for the center
	 * **/
	public static void registerCubic(BlockModelGenerators generator, Block block, Identifier borderTexture, Identifier centerTexture)
	{
		generateBlockModels(generator, block, borderTexture, centerTexture, false);

		Identifier connectingModel = connectingModelId(block, false);
		generator.blockStateOutput.accept(createBlockstate(block, connectingModel));

		TextureMapping mapping = TextureMapping.cube(new Material(borderTexture));
		ModelTemplate template = new ModelTemplate(Optional.of(Galaxies.id("block/template_connected_block_item")), Optional.empty(), TextureSlot.ALL);
		Identifier itemModel = template.create(block, mapping, generator.modelOutput);
		generator.registerSimpleItemModel(block, itemModel);
	}

	/**
	 * Registers a block that only connects vertically
	 * @param block The block that is receiving the model
	 * @param sideTexture Identifier of the location of the texture used for the side faces of the block
	 * @param topTexture Identifier of the location of the texture used for the up and down faces of the block
	 * **/
	public static void registerPillar(BlockModelGenerators generator, Block block, Identifier sideTexture, Identifier topTexture)
	{
		generateBlockModels(generator, block, sideTexture, topTexture, true);

		Identifier connectingModel = connectingModelId(block, true);
		generator.blockStateOutput.accept(createBlockstate(block, connectingModel));

		TextureMapping mapping = TextureMapping.column(new Material(sideTexture), new Material(topTexture));
		ModelTemplate template = new ModelTemplate(Optional.of(Galaxies.id("block/template_connected_pillar_block_item")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
		Identifier itemModel = template.create(block, mapping, generator.modelOutput);
		generator.registerSimpleItemModel(block, itemModel);
	}

	/**
	 * Registers a block that only connects vertically that can changes between a lit and unlit state
	 * @param block The block that is receiving the model
	 * @param sideTexture Identifier of the location of the texture used for the side faces of the block
	 * @param topTexture Identifier of the location of the texture used for the up and down faces of the block
	 * **/
	public static void registerPillarLightingPanel(BlockModelGenerators generator, Block block, Identifier sideTexture, Identifier topTexture)
	{
		generateBlockModels(generator, block, sideTexture, topTexture, true);
		generateBlockLitModels(generator, block, sideTexture.withSuffix("_on"), topTexture, true);

		Identifier connectingModelUnlit = connectingModelId(block, true);
		Identifier connectingModelLit = connectingModelLitId(block, true);

		generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, createWeightedVariant(connectingModelLit), createWeightedVariant(connectingModelUnlit))));

		TextureMapping mapping = TextureMapping.column(new Material(sideTexture), new Material(topTexture));
		ModelTemplate template = new ModelTemplate(Optional.of(Galaxies.id("block/template_connected_pillar_block_item")), Optional.empty(), TextureSlot.END, TextureSlot.SIDE);
		Identifier itemModel = template.create(block, mapping, generator.modelOutput);
		generator.registerSimpleItemModel(block, itemModel);
	}

	/**
	 * Generates the individual models used for connecting parts
	 * @param block The block for which the models are being generated
	 * @param borderTexture The texture used for borders if it's a cube or sides if it's a pillar
	 * @param centerTexture The texture used for center if it's a cube or top/bottom if it's a pillar
	 * @param pillar If the block is a pillar or not
	 * **/
	private static void generateBlockModels(BlockModelGenerators generator, Block block, Identifier borderTexture, Identifier centerTexture, boolean pillar)
	{
		for (TextureQuadrant quadrant : TextureQuadrant.values())
		{
			writeModel(generator, normalModelId(block, quadrant, "none"), borderTexture, quadrant, sourceRegion(quadrant, 0));
			writeModel(generator, normalModelId(block, quadrant, "vertical"), borderTexture, quadrant, sourceRegion(quadrant, 1));
			if (!pillar)
			{
				writeModel(generator, normalModelId(block, quadrant, "horizontal"), borderTexture, quadrant, sourceRegion(quadrant, 2));
			}

			writeCenterModel(generator, centerModelId(block, quadrant), centerTexture, quadrant);

			writeModel(generator, cornerModelId(block, quadrant), borderTexture, quadrant, sourceRegion(quadrant, 3));
		}

		writeConnectingModel(generator, connectingModelId(block, pillar), borderTexture);
	}

	/**
	 * Generates the individual models used for connecting parts of a lit model
	 * @param block The block for which the models are being generated
	 * @param borderTexture The texture used for borders if it's a cube or sides if it's a pillar
	 * @param centerTexture The texture used for center if it's a cube or top/bottom if it's a pillar
	 * @param pillar If the block is a pillar or not
	 * **/
	private static void generateBlockLitModels(BlockModelGenerators generator, Block block, Identifier borderTexture, Identifier centerTexture, boolean pillar)
	{
		for (TextureQuadrant quadrant : TextureQuadrant.values())
		{
			writeModel(generator, normalLitModelId(block, quadrant, "none"), borderTexture, quadrant, sourceRegion(quadrant, 0));
			writeModel(generator, normalLitModelId(block, quadrant, "vertical"), borderTexture, quadrant, sourceRegion(quadrant, 1));
			if (!pillar)
			{
				writeModel(generator, normalLitModelId(block, quadrant, "horizontal"), borderTexture, quadrant, sourceRegion(quadrant, 2));
			}

			writeCenterModel(generator, centerLitModelId(block, quadrant), centerTexture, quadrant);

			writeModel(generator, cornerLitModelId(block, quadrant), borderTexture, quadrant, sourceRegion(quadrant, 3));
		}

		writeConnectingModel(generator, connectingModelLitId(block, pillar), borderTexture);
	}

	/**
	 * @return The identifier of a model of the given quadrant and state
	 **/
	private static Identifier normalModelId(Block block, TextureQuadrant quadrant, String state)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_" + state);
	}

	/**
	 * @return The identifier of a lit model of the given quadrant and state
	 **/
	private static Identifier normalLitModelId(Block block, TextureQuadrant quadrant, String state)
	{
		return getBaseLitIdentifier(block).withSuffix("lit" + "_" + quadrant.name().toLowerCase() + "_" + state);
	}

	/**
	 * @return The identifier of a corner model of the given quadrant
	 **/
	private static Identifier cornerModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_corner");
	}

	/**
	 * @return The identifier of a lit corner model of the given quadrant
	 **/
	private static Identifier cornerLitModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseLitIdentifier(block).withSuffix("lit" + "_" + quadrant.name().toLowerCase() + "_corner");
	}

	/**
	 * @return The identifier of a center model of the given quadrant
	 **/
	private static Identifier centerModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseIdentifier(block).withSuffix(quadrant.name().toLowerCase() + "_center");
	}

	/**
	 * @return The identifier of a lit center model of the given quadrant
	 **/
	private static Identifier centerLitModelId(Block block, TextureQuadrant quadrant)
	{
		return getBaseLitIdentifier(block).withSuffix("lit" + "_" + quadrant.name().toLowerCase() + "_center");
	}

	/**
	 * @return The identifier of the connecting model to which the block is assosciated to during loading
	 **/
	private static Identifier connectingModelId(Block block, boolean pillar)
	{
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey = blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		String suffix = pillar ? "_connecting_pillar_model" : "_connecting_model";
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "/" + blockKey + suffix);
	}

	/**
	 * @return The identifier of the lit connecting model to which the block is assosciated to during loading
	 **/
	private static Identifier connectingModelLitId(Block block, boolean pillar)
	{
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey = blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		String suffix = pillar ? "_connecting_pillar_model" : "_connecting_model";
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "_lit" + "/" + blockKey + "_lit" + suffix);
	}

	private static Identifier getBaseIdentifier(Block block)
	{
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey = blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "/" + blockKey + "_");
	}

	private static Identifier getBaseLitIdentifier(Block block)
	{
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
		String blockKey = blockId.getPath().substring(blockId.getPath().lastIndexOf('/') + 1, blockId.getPath().length());
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/connected/" + blockId.getPath() + "_lit" + "/" + blockKey + "_");
	}

	/**
	 * @return The region in which a quadrant should be found
	 **/
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

	/**
	 * Writes the model json for borders
	 * @param modelId The id the model should have
	 * @param texture The path of the texture the model uses
	 * @param quadrant The model's quadrant
	 * @param source The model's region
	 **/
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

	/**
	 * Writes the center model json
	 * @param modelId The id the model should have
	 * @param texture The path of the texture the model uses
	 * @param quadrant The model's quadrant
	 **/
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

	/**
	 * Writes the json model for the model assosciated directly with the block
	 * @param modelId The id the model should have
	 * @param texture The path of the texture the model uses
	 **/
	private static void writeConnectingModel(BlockModelGenerators generator, Identifier modelId, Identifier texture)
	{
		JsonObject json = new JsonObject();
		JsonObject textures = new JsonObject();

		textures.addProperty("particle", texture.toString());
		textures.addProperty("texture", texture.toString());

		json.add("textures", textures);

		generator.modelOutput.accept(modelId, () -> json);
	}

	/**
	 * @return returns an array of the locations on the UV of a region of the texture based on the SourceRegion
	 **/
	private static JsonArray createUv(SourceRegion source)
	{
		float u0 = source.x() * UV_SCALE + 0.025f;
		float v0 = source.y() * UV_SCALE + 0.025f;
		float u1 = (source.x() + QUADRANT_SIZE) * UV_SCALE - 0.025f;
		float v1 = (source.y() + QUADRANT_SIZE) * UV_SCALE - 0.025f;

		JsonArray uv = new JsonArray();

		add(uv, u0);
		add(uv, v0);
		add(uv, u1);
		add(uv, v1);

		return uv;
	}

	/**
	 * @return returns an array of the locations on the UV of a center quadrant
	 **/
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

	/**
	 * @return a BlockStateModelDispatcher for the given block
	 **/
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
				return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(Map.of("", new SingleVariant.Unbaked(new Variant(connectingModel))))), Optional.empty());
			}
		};
	}

	public static MultiVariant createWeightedVariant(Identifier id)
	{
		return new MultiVariant(WeightedList.of(new Variant(id)));
	}
}