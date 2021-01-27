package com.parzivail.datagen.tarkin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
