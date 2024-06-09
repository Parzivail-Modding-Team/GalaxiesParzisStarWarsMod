package com.parzivail.datagen.tarkin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parzivail.datagen.AssetUtils;
import com.parzivail.pswg.Resources;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.item.Item;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.*;

public class ModelFile
{
	private final Identifier filename;
	private final Identifier parent;
	private final HashMap<String, Identifier> textures;
	private final ArrayList<ModelOverride> overrides;
	private final ArrayList<ModelFile> dependencies;

	private ModelFile(Identifier filename)
	{
		this(filename, null);
	}

	private ModelFile(Identifier filename, Identifier parent)
	{
		this.filename = filename;
		this.parent = parent;
		this.textures = new HashMap<>();
		this.overrides = new ArrayList<>();
		this.dependencies = new ArrayList<>();
	}

	public Identifier getId()
	{
		return filename;
	}

	public static ModelFile ofBlock(Block block)
	{
		return new ModelFile(AssetUtils.getRegistryName(block), AssetUtils.getTextureName(block));
	}

	public static ModelFile ofAccumulatingBlock(Block block)
	{
		Identifier identifier = AssetUtils.getTextureName(block);
		return new ModelFile(AssetUtils.getRegistryName(block), identifier.withSuffixedPath("_height2"));
	}

	public static ModelFile ofBlockDifferentParent(Block block, Identifier parent)
	{
		return new ModelFile(AssetUtils.getRegistryName(block), parent);
	}

	public static ModelFile noParent(Identifier filename)
	{
		return new ModelFile(filename);
	}

	public static ModelFile ofModel(Identifier filename, Identifier source)
	{
		return new ModelFile(filename, source);
	}

	public static ModelFile cube(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_all"))
				.texture("all", AssetUtils.getTextureName(block));
	}

	public static ModelFile cube(Block block, Identifier texture)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_all"))
				.texture("all", texture);
	}

	public static ModelFile cube_no_cull(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Resources.id("block/template/double_cull_cube_all"))
				.texture("all", AssetUtils.getTextureName(block));
	}

	public static ModelFile item(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("item/generated"))
				.texture("layer0", AssetUtils.getTextureName(block));
	}

	public static ModelFile blockSeparateItem(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("item/generated"))
				.texture("layer0", AssetUtils.getTextureName(block.asItem()));
	}

	public static ModelFile item(Block block, Identifier textureName)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("item/generated"))
				.texture("layer0", textureName);
	}

	public static ModelFile particle(Block block, Identifier particle)
	{
		return ModelFile
				.noParent(AssetUtils.getRegistryName(block))
				.texture("particle", particle);
	}

	public static ModelFile item(Item item)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(item), Identifier.ofVanilla("item/generated"))
				.texture("layer0", AssetUtils.getTextureName(item));
	}

	public static ModelFile itemSprite(Identifier id)
	{
		return ModelFile
				.ofModel(id, Identifier.ofVanilla("item/generated"))
				.texture("layer0", id.withPrefixedPath("item/"));
	}

	public static ModelFile handheld_item(Item item)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(item), Identifier.ofVanilla("item/handheld"))
				.texture("layer0", AssetUtils.getTextureName(item));
	}

	public static ModelFile spawn_egg(Item item)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(item), Identifier.ofVanilla("item/template_spawn_egg"));
	}

	public static ModelFile empty(Item item)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(item), Identifier.ofVanilla("builtin/generated"));
	}

	public static ModelFile leaves(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/leaves"))
				.texture("all", AssetUtils.getTextureName(block));
	}

	public static ModelFile column(Block block, Identifier topTexture, Identifier sideTexture)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_column"))
				.texture("end", topTexture)
				.texture("side", sideTexture);
	}

	public static ModelFile columnTop(Block block)
	{
		var tex = AssetUtils.getTextureName(block);
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_column"))
				.texture("end", tex.withSuffixedPath("_top"))
				.texture("side", tex);
	}

	public static ModelFile columnTopBottom(Block block, Identifier topTexture, Identifier sideTexture, Identifier bottomTexture)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_bottom_top"))
				.texture("top", topTexture)
				.texture("bottom", bottomTexture)
				.texture("side", sideTexture);
	}

	public static ModelFile columnTopBottom(Block block)
	{
		var tex = AssetUtils.getTextureName(block);
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cube_bottom_top"))
				.texture("top", tex.withSuffixedPath("_top"))
				.texture("bottom", tex.withSuffixedPath("_bottom"))
				.texture("side", tex);
	}

	public static Collection<ModelFile> verticalSlab(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/slab"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top"), Identifier.ofVanilla("block/slab_top"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_x"), Resources.id("block/template/slab_x"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_x"), Resources.id("block/template/slab_top_x"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_z"), Resources.id("block/template/slab_z"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_z"), Resources.id("block/template/slab_top_z"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> verticalSlabs(Block block, Identifier topTexture, Identifier sideTexture, String... suffixes)
	{
		var id = AssetUtils.getRegistryName(block);
		var models = new ArrayList<ModelFile>();
		for (var suffix : suffixes)
		{
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath(suffix), Identifier.ofVanilla("block/slab"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath("_top" + suffix), Identifier.ofVanilla("block/slab_top"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath("_x" + suffix), Resources.id("block/template/slab_x"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath("_top_x" + suffix), Resources.id("block/template/slab_top_x"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath("_z" + suffix), Resources.id("block/template/slab_z"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.add(ModelFile
					           .ofModel(id.withSuffixedPath("_top_z" + suffix), Resources.id("block/template/slab_top_z"))
					           .texture("bottom", topTexture)
					           .texture("top", topTexture)
					           .texture("side", sideTexture.withSuffixedPath(suffix)));
			models.addAll(ModelFile
					              .columns(block, topTexture, "_double" + suffix));
		}
		return models;
	}

	public static Collection<ModelFile> trapdoor(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id.withSuffixedPath("_bottom"), Identifier.ofVanilla("block/template_orientable_trapdoor_bottom"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_open"), Identifier.ofVanilla("block/template_orientable_trapdoor_open"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top"), Identifier.ofVanilla("block/template_orientable_trapdoor_top"))
						.texture("texture", texture)
		);
	}

	public static Collection<ModelFile> door(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);
		var textureTop = texture.withSuffixedPath("_top");
		var textureBottom = texture.withSuffixedPath("_bottom");
		return Arrays.asList(
				ModelFile
						.ofModel(id.withSuffixedPath("_bottom_left"), Identifier.ofVanilla("block/door_bottom_left"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_bottom_left_open"), Identifier.ofVanilla("block/door_bottom_left_open"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_bottom_right"), Identifier.ofVanilla("block/door_bottom_right"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_bottom_right_open"), Identifier.ofVanilla("block/door_bottom_right_open"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_left"), Identifier.ofVanilla("block/door_top_left"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_left_open"), Identifier.ofVanilla("block/door_top_left_open"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_right"), Identifier.ofVanilla("block/door_top_right"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_right_open"), Identifier.ofVanilla("block/door_top_right_open"))
						.texture("top", textureTop)
						.texture("bottom", textureBottom)
		);
	}

	public static Collection<ModelFile> verticalSlabUniqueDouble(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/slab"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top"), Identifier.ofVanilla("block/slab_top"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_double"), Identifier.ofVanilla("block/cube_column"))
						.texture("end", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_x"), Resources.id("block/template/slab_x"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_x"), Resources.id("block/template/slab_top_x"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_double_x"), Identifier.ofVanilla("block/cube_column"))
						.texture("end", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_z"), Resources.id("block/template/slab_z"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top_z"), Resources.id("block/template/slab_top_z"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_double_z"), Identifier.ofVanilla("block/cube_column"))
						.texture("end", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static ModelFile wallInventory(Block block, Identifier texture)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/wall_inventory"))
				.texture("wall", texture);
	}

	public static Collection<ModelFile> wall(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id.withSuffixedPath("_post"), Identifier.ofVanilla("block/template_wall_post"))
						.texture("wall", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_side"), Identifier.ofVanilla("block/template_wall_side"))
						.texture("wall", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_side_tall"), Identifier.ofVanilla("block/template_wall_side_tall"))
						.texture("wall", texture)
		);
	}

	public static Collection<ModelFile> cropStages(Block block, IntProperty property)
	{
		var id = AssetUtils.getRegistryName(block);
		var modelFiles = new ArrayList<ModelFile>();

		for (int i : property.getValues())
		{
			var localId = id.withSuffixedPath("_stage" + i);
			modelFiles.add(
					ModelFile
							.ofModel(localId, Identifier.ofVanilla("block/crop"))
							.texture("crop", localId.withPrefixedPath("block/"))
			);
		}

		return modelFiles;
	}

	public static Collection<ModelFile> accumulatingLayers(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);

		var modelFiles = new ArrayList<ModelFile>();

		for (int i : Properties.LAYERS.getValues())
		{
			if (i >= 8)
				continue;

			var localId = id.withSuffixedPath("_height" + i * 2);
			// TODO: remove dependency on block/snow_heightN?
			modelFiles.add(
					ModelFile
							.ofModel(localId, Identifier.ofVanilla("block/snow_height" + i * 2))
							.texture("particle", texture)
							.texture("texture", texture)
			);
		}

		modelFiles.add(ModelFile.cube(block, texture));

		return modelFiles;
	}

	public static Collection<ModelFile> accumulatingLayers(Block block)
	{
		var id = AssetUtils.getRegistryName(block);
		return accumulatingLayers(block, id.withPrefixedPath("block/"));
	}

	public static Collection<ModelFile> bushStages(Block block, IntProperty property)
	{
		var id = AssetUtils.getRegistryName(block);
		var modelFiles = new ArrayList<ModelFile>();

		for (int i : property.getValues())
		{
			var localId = id.withSuffixedPath("_stage" + i);
			modelFiles.add(
					ModelFile
							.ofModel(localId, Identifier.ofVanilla("block/cross"))
							.texture("cross", localId.withPrefixedPath("block/"))
			);
		}

		return modelFiles;
	}

	public static Collection<ModelFile> bloomingBushStages(Block block, IntProperty property)
	{
		var id = AssetUtils.getRegistryName(block);
		var modelFiles = new ArrayList<ModelFile>();

		for (int i : property.getValues())
		{
			var localId = id.withSuffixedPath("_stage" + i);
			modelFiles.add(
					ModelFile
							.ofModel(localId, Identifier.ofVanilla("block/cross"))
							.texture("cross", localId.withPrefixedPath("block/"))
			);

			localId = id.withSuffixedPath("_stage" + i + "_blooming");
			modelFiles.add(
					ModelFile
							.ofModel(localId, Identifier.ofVanilla("block/cross"))
							.texture("cross", localId.withPrefixedPath("block/"))
			);
		}

		return modelFiles;
	}

	public static Collection<ModelFile> slabUniqueDouble(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/slab"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_top"), Identifier.ofVanilla("block/slab_top"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_double"), Identifier.ofVanilla("block/cube_column"))
						.texture("end", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> columns(Block block, Identifier topTexture, String... suffixes)
	{
		var id = AssetUtils.getRegistryName(block);
		var models = new ArrayList<ModelFile>();
		for (var suffix : suffixes)
		{
			Identifier identifier = AssetUtils.getTextureName(block);
			Identifier identifier1 = AssetUtils.getRegistryName(block);
			models.add(ModelFile
					           .ofModel(identifier1.withSuffixedPath(suffix), Identifier.ofVanilla("block/cube_column"))
					           .texture("side", identifier.withSuffixedPath(suffix))
					           .texture("end", topTexture));
		}

		return models;
	}

	public static Collection<ModelFile> cubes(Block block, String... suffixes)
	{
		var id = AssetUtils.getRegistryName(block);
		var models = new ArrayList<ModelFile>();

		for (var suffix : suffixes)
		{
			Identifier identifier = AssetUtils.getTextureName(block);
			Identifier identifier1 = AssetUtils.getRegistryName(block);
			models.add(ModelFile
					           .ofModel(identifier1.withSuffixedPath(suffix), Identifier.ofVanilla("block/cube_all"))
					           .texture("all", identifier.withSuffixedPath(suffix)));
		}

		return models;
	}

	public static Collection<ModelFile> stairs(Block block, Identifier topTexture, Identifier sideTexture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_inner"), Identifier.ofVanilla("block/inner_stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture),
				ModelFile
						.ofModel(id.withSuffixedPath("_outer"), Identifier.ofVanilla("block/outer_stairs"))
						.texture("bottom", topTexture)
						.texture("top", topTexture)
						.texture("side", sideTexture)
		);
	}

	public static Collection<ModelFile> fence(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id.withSuffixedPath("_post"), Identifier.ofVanilla("block/fence_post"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_side"), Identifier.ofVanilla("block/fence_side"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/fence_inventory"))
						.texture("texture", texture)
		);
	}

	public static Collection<ModelFile> fenceGate(Block block, Identifier texture)
	{
		var id = AssetUtils.getRegistryName(block);
		return Arrays.asList(
				ModelFile
						.ofModel(id, Identifier.ofVanilla("block/template_fence_gate"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_open"), Identifier.ofVanilla("block/template_fence_gate_open"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_wall"), Identifier.ofVanilla("block/template_fence_gate_wall"))
						.texture("texture", texture),
				ModelFile
						.ofModel(id.withSuffixedPath("_wall_open"), Identifier.ofVanilla("block/template_fence_gate_wall_open"))
						.texture("texture", texture)
		);
	}

	public static Collection<ModelFile> randomMirror(Block block)
	{
		Identifier identifier = AssetUtils.getRegistryName(block);
		var mirroredId = identifier.withSuffixedPath("_mirrored");
		return Arrays.asList(
				ModelFile.cube(block),
				ModelFile
						.ofModel(mirroredId, Identifier.ofVanilla("block/cube_mirrored_all"))
						.texture("all", AssetUtils.getTextureName(block))
		);
	}

	public static Collection<ModelFile> fans(Block block)
	{
		Identifier identifier = AssetUtils.getRegistryName(block);
		var wallId = identifier.withSuffixedPath("_wall");
		return Arrays.asList(
				fan(block),
				ModelFile
						.ofModel(wallId, Identifier.ofVanilla("block/coral_wall_fan"))
						.texture("fan", AssetUtils.getTextureName(block))
		);
	}

	public static ModelFile fan(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/coral_fan"))
				.texture("fan", AssetUtils.getTextureName(block));
	}

	public static ModelFile wallFan(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/coral_wall_fan"))
				.texture("fan", AssetUtils.getTextureName(block));
	}

	public static ModelFile cross(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/cross"))
				.texture("cross", AssetUtils.getTextureName(block));
	}

	public static ModelFile tintedCross(Block block)
	{
		return ModelFile
				.ofModel(AssetUtils.getRegistryName(block), Identifier.ofVanilla("block/tinted_cross"))
				.texture("cross", AssetUtils.getTextureName(block));
	}

	public ModelFile texture(String key, Identifier value)
	{
		textures.put(key, value);
		return this;
	}

	public ModelFile predicate(Identifier conditionType, float threshold, Identifier modelId)
	{
		this.overrides.add(new ModelOverride(modelId, List.of(new ModelOverride.Condition(conditionType, threshold))));
		return this;
	}

	public ModelFile predicate(Identifier conditionType, float threshold, ModelFile other)
	{
		this.overrides.add(new ModelOverride(other.filename.withPrefixedPath("item/"), List.of(new ModelOverride.Condition(conditionType, threshold))));
		this.dependencies.add(other);
		return this;
	}

	public JsonElement build()
	{
		var rootElement = new JsonObject();

		if (parent != null)
			rootElement.addProperty("parent", parent.toString());

		if (!textures.isEmpty())
		{
			var textureElement = new JsonObject();

			for (var entry : textures.entrySet())
				textureElement.addProperty(entry.getKey(), entry.getValue().toString());

			rootElement.add("textures", textureElement);
		}

		if (!overrides.isEmpty())
		{
			var overrideElement = new JsonArray();

			for (var override : overrides)
			{
				var overrideEntry = new JsonObject();
				overrideEntry.addProperty("model", override.getModelId().toString());

				var predicateElement = new JsonObject();
				override.streamConditions().forEach(condition -> predicateElement.addProperty(condition.getType().toString(), condition.getThreshold()));

				overrideEntry.add("predicate", predicateElement);

				overrideElement.add(overrideEntry);
			}

			rootElement.add("overrides", overrideElement);
		}

		return rootElement;
	}

	public ArrayList<ModelFile> getDependencies()
	{
		return dependencies;
	}
}
