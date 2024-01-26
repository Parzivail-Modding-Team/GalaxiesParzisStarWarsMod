package com.parzivail.nem;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.render.ModelAngleAnimator;
import com.parzivail.util.client.render.MutableAnimatedModel;
import com.parzivail.util.client.render.armor.BipedEntityArmorModel;
import com.parzivail.util.data.DataResolution;
import com.parzivail.util.data.KeyedReloadableLoader;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NemManager extends KeyedReloadableLoader<NbtCompound>
{
	private static final Identifier HUMANOID_BASE_ID = Resources.id("models/species/humanoid_base.nem");
	private static final List<String> REQUIRED_BIPED_PARTS = List.of(
			EntityModelPartNames.HEAD, EntityModelPartNames.HAT, EntityModelPartNames.BODY,
			EntityModelPartNames.RIGHT_ARM, EntityModelPartNames.LEFT_ARM, EntityModelPartNames.RIGHT_LEG,
			EntityModelPartNames.LEFT_LEG
	);

	private final Identifier id;
	private final Map<Identifier, TexturedModelData> modelData;
	private final ArrayList<Pair<Identifier, Consumer<ModelPart>>> models;
	private final HashMap<Identifier, PlayerEntityModel<AbstractClientPlayerEntity>> playerModels;
	private final HashMap<Identifier, BipedEntityModel<LivingEntity>> bipedModels;

	private NbtCompound humanoidBaseData;

	public NemManager(Identifier id)
	{
		super("models", "nem");
		this.id = id;
		modelData = new HashMap<>();
		models = new ArrayList<>();
		playerModels = new HashMap<>();
		bipedModels = new HashMap<>();
	}

	@Override
	public DataResolution<NbtCompound> readResource(ResourceManager resourceManager, Profiler profiler, Map<Identifier, NbtCompound> loadedResources, Identifier id, InputStream stream) throws IOException
	{
		var nbt = NbtIo.readCompound(new DataInputStream(stream));

		var parts = nbt.getCompound("parts");

		var base = nbt.getString("base");
		if (!base.isEmpty())
		{
			var baseId = new Identifier(base);
			var overrideNbt = loadedResources.get(baseId);
			if (overrideNbt == null)
				return DataResolution.missingDependency(List.of(baseId));

			var overrideParts = overrideNbt.getCompound("parts");

			for (var entry : overrideParts.getKeys())
				if (!parts.contains(entry))
					parts.put(entry, overrideParts.getCompound(entry));
		}

		if (nbt.getBoolean("expand_biped"))
		{
			for (var part : REQUIRED_BIPED_PARTS)
				if (!parts.contains(part))
					parts.put(part, new NbtCompound());
		}

		return DataResolution.success(nbt);
	}

	@NotNull
	public static TexturedModelData buildModel(NbtCompound nbt)
	{
		var modelData = new ModelData();
		var root = modelData.getRoot();

		addChildren(root, nbt.getCompound("parts"));

		var texTag = nbt.getCompound("tex");
		return TexturedModelData.of(modelData, texTag.getInt("w"), texTag.getInt("h"));
	}

	private static void addChildren(ModelPartData root, NbtCompound parts)
	{
		for (var key : parts.getKeys())
			addChild(root, key, parts.getCompound(key));
	}

	private static void addChild(ModelPartData parent, String partName, NbtCompound part)
	{
		var partBuilder = ModelPartBuilder.create();

		if (part.isEmpty())
			parent.addChild(partName, partBuilder, ModelTransform.NONE);
		else
		{
			var tex = part.getCompound("tex");
			var partU = tex.getInt("u");
			var partV = tex.getInt("v");
			var mirrored = tex.getBoolean("mirrored");

			var pos = part.getCompound("pos");
			var x = pos.getFloat("x");
			var y = pos.getFloat("y");
			var z = pos.getFloat("z");

			var rot = part.getCompound("rot");
			var pitch = rot.getFloat("pitch");
			var yaw = rot.getFloat("yaw");
			var roll = rot.getFloat("roll");

			var transform = ModelTransform.of(x, y, z, pitch, yaw, roll);

			var cuboids = part.getList("cuboids", NbtElement.COMPOUND_TYPE);
			for (var i = 0; i < cuboids.size(); i++)
			{
				var cuboid = cuboids.getCompound(i);

				var cPos = cuboid.getCompound("pos");
				var cX = cPos.getFloat("x");
				var cY = cPos.getFloat("y");
				var cZ = cPos.getFloat("z");

				var cSize = cuboid.getCompound("size");
				var cSX = cSize.getInt("x");
				var cSY = cSize.getInt("y");
				var cSZ = cSize.getInt("z");

				var cExpand = cuboid.getCompound("expand");
				var cEX = cExpand.getFloat("x");
				var cEY = cExpand.getFloat("y");
				var cEZ = cExpand.getFloat("z");

				var cTex = cuboid.getCompound("tex");
				var cU = cTex.getInt("u");
				var cV = cTex.getInt("v");
				var cMirrored = cTex.getBoolean("mirrored");

				partBuilder = partBuilder.mirrored(cMirrored ^ mirrored).cuboid(
						"",
						cX, cY, cZ,
						cSX, cSY, cSZ,
						new Dilation(cEX, cEY, cEZ),
						partU + cU, partV + cV
				);
			}

			var childPart = parent.addChild(partName, partBuilder, transform);

			if (part.contains("children"))
				addChildren(childPart, part.getCompound("children"));
		}
	}

	@Override
	protected void apply(Map<Identifier, NbtCompound> prepared, ResourceManager manager, Profiler profiler)
	{
		modelData.clear();
		prepared.forEach((key, value) -> modelData.put(key, buildModel(value)));

		// The model loading sites return before the models are loaded from disk and before
		// they're actually rendered, so we use a callback system to set the model part
		// when the part becomes available
		for (var modelInfo : models)
		{
			var modelId = modelInfo.getLeft();
			var model = modelInfo.getRight();

			if (modelData.containsKey(modelId))
				model.accept(modelData.get(modelId).createModel());
		}
	}

	public <T extends Entity> MutableAnimatedModel<T> getModel(Identifier modelId, ModelAngleAnimator<T> angleAnimator)
	{
		var model = new MutableAnimatedModel<>(angleAnimator);
		models.add(new Pair<>(modelId, model::setRoot));
		return model;
	}

	public Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> getPlayerModel(Identifier modelId, boolean thinArms)
	{
		models.add(new Pair<>(modelId, modelPart -> playerModels.put(modelId, new PlayerEntityModel<>(modelPart, thinArms))));
		return () -> playerModels.get(modelId);
	}

	public Supplier<BipedEntityModel<LivingEntity>> getBipedModel(Identifier modelId)
	{
		models.add(new Pair<>(modelId, modelPart -> bipedModels.put(modelId, new BipedEntityModel<>(modelPart))));
		return () -> bipedModels.get(modelId);
	}

	public Supplier<BipedEntityModel<LivingEntity>> getPlayerBipedModel(Identifier overrideModelId, boolean thinArms)
	{
		models.add(new Pair<>(overrideModelId, modelPart -> bipedModels.put(overrideModelId, new PlayerEntityModel<>(modelPart, thinArms))));
		return () -> bipedModels.get(overrideModelId);
	}

	public Supplier<BipedEntityArmorModel<LivingEntity>> getBipedArmorModel(Identifier modelId)
	{
		models.add(new Pair<>(modelId, modelPart -> bipedModels.put(modelId, new BipedEntityArmorModel<>(modelPart))));
		return () -> (BipedEntityArmorModel<LivingEntity>)bipedModels.get(modelId);
	}

	@Override
	public Identifier getFabricId()
	{
		return this.id;
	}
}
