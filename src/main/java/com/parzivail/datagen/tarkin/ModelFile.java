package com.parzivail.datagen.tarkin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;

import java.util.*;

public class ModelFile
{
	private final Identifier filename;
	private final Identifier parent;
	private final HashMap<String, Identifier> textures;

	private ModelFile(Identifier filename, Identifier parent)
	{
		this.filename = filename;
		this.parent = parent;
		this.textures = new HashMap<>();
	}

	public Identifier getId()
	{
		return filename;
	}

	public static ModelFile ofBlock(Block block)
	{
		return new ModelFile(AssetGenerator.getRegistryName(block), AssetGenerator.getTextureName(block));
	}

	public static ModelFile ofBlockDifferentParent(Block block, Identifier parent)
	{
		return new ModelFile(AssetGenerator.getRegistryName(block), parent);
	}

	public static ModelFile ofModel(Identifier filename, Identifier source)
	{
		return new ModelFile(filename, source);
	}

	public static ModelFile cube(Block block)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("block/cube_all"))
				.texture("all", AssetGenerator.getTextureName(block));
	}

	public static ModelFile item(Block block)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("item/generated"))
				.texture("layer0", AssetGenerator.getTextureName(block));
	}

	public static ModelFile item(Block block, Identifier textureName)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("item/generated"))
				.texture("layer0", textureName);
	}

	public static ModelFile item(Item item)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(item), new Identifier("item/generated"))
				.texture("layer0", AssetGenerator.getTextureName(item));
	}

	public static ModelFile empty(Item item)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(item), new Identifier("builtin/generated"));
	}

	public static ModelFile leaves(Block block)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("block/leaves"))
				.texture("all", AssetGenerator.getTextureName(block));
	}

	public static ModelFile column(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("block/cube_column"))
				.texture("end", topTexture)
				.texture("side", sideTexture);
	}

	public static Collection<ModelFile> slab(Block block, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, new Identifier("block/slab"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(IdentifierUtil.concat(id, "_top"), new Identifier("block/slab_top"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> crop(Block block, IntProperty property)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		ArrayList<ModelFile> modelFiles = new ArrayList<>();

		for (int i : property.getValues())
		{
			Identifier localId = IdentifierUtil.concat(id, "_stage" + i);
			modelFiles.add(
					ModelFile
							.ofModel(localId, new Identifier("block/crop"))
							.texture("crop", IdentifierUtil.concat("block/", localId))
			);
		}

		return modelFiles;
	}

	public static Collection<ModelFile> slabUniqueDouble(Block block, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, new Identifier("block/slab"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(IdentifierUtil.concat(id, "_top"), new Identifier("block/slab_top"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(IdentifierUtil.concat(id, "_double"), new Identifier("block/cube_column"))
						.texture("end", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> cubes(Block block, String... suffixes)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		ArrayList<ModelFile> models = new ArrayList<>();

		for (String suffix : suffixes)
		{
			models.add(ModelFile
					           .ofModel(IdentifierUtil.concat(AssetGenerator.getRegistryName(block), suffix), new Identifier("block/cube_all"))
					           .texture("all", IdentifierUtil.concat(AssetGenerator.getTextureName(block), suffix)));
		}

		return models;
	}

	public static Collection<ModelFile> stairs(Block block, Identifier topTexture, Identifier sideTexture)
	{
		Identifier id = AssetGenerator.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, new Identifier("block/stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(IdentifierUtil.concat(id, "_inner"), new Identifier("block/inner_stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(IdentifierUtil.concat(id, "_outer"), new Identifier("block/outer_stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> randomMirror(Block block)
	{
		Identifier mirroredId = IdentifierUtil.concat(AssetGenerator.getRegistryName(block), "_mirrored");
		return Arrays.asList(
				ModelFile.cube(block),
				ModelFile
						.ofModel(mirroredId, new Identifier("block/cube_mirrored_all"))
						.texture("all", AssetGenerator.getTextureName(block))
		);
	}

	public static ModelFile cross(Block block)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("block/cross"))
				.texture("cross", AssetGenerator.getTextureName(block));
	}

	public static ModelFile tintedCross(Block block)
	{
		return ModelFile
				.ofModel(AssetGenerator.getRegistryName(block), new Identifier("block/tinted_cross"))
				.texture("cross", AssetGenerator.getTextureName(block));
	}

	public ModelFile texture(String key, Identifier value)
	{
		textures.put(key, value);
		return this;
	}

	public JsonElement build()
	{
		JsonObject rootElement = new JsonObject();

		rootElement.addProperty("parent", parent.toString());

		if (!textures.isEmpty())
		{
			JsonObject textureElement = new JsonObject();

			for (Map.Entry<String, Identifier> entry : textures.entrySet())
				textureElement.addProperty(entry.getKey(), entry.getValue().toString());

			rootElement.add("textures", textureElement);
		}

		return rootElement;
	}
}
