package com.parzivail.pswg.client.loader;

import com.google.common.collect.ImmutableList;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.mixin.ModelPartAccessor;
import com.parzivail.util.client.render.ModelAngleAnimator;
import com.parzivail.util.client.render.MutableAnimatedModel;
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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NemManager extends KeyedReloadableLoader<TexturedModelData>
{
	public static final Identifier ID = Resources.id("nem_manager");
	public static final NemManager INSTANCE = new NemManager();

	private final Map<Identifier, TexturedModelData> modelData;
	private final ArrayList<Pair<Identifier, Consumer<ModelPart>>> models;
	private final HashMap<Identifier, PlayerEntityModel<AbstractClientPlayerEntity>> playerModels;
	private final HashMap<Identifier, BipedEntityModel<LivingEntity>> bipedModels;

	private NemManager()
	{
		super("models", "nem");
		modelData = new HashMap<>();
		models = new ArrayList<>();
		playerModels = new HashMap<>();
		bipedModels = new HashMap<>();
	}

	@Override
	public TexturedModelData readResource(ResourceManager resourceManager, Profiler profiler, InputStream stream) throws IOException
	{
		DataInput d = new DataInputStream(stream);

		var nbt = NbtIo.read(d);

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

		var partBuilder = ModelPartBuilder.create().mirrored(mirrored);

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

			partBuilder = partBuilder.cuboid(
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

	@Override
	protected void apply(Map<Identifier, TexturedModelData> prepared, ResourceManager manager, Profiler profiler)
	{
		modelData.clear();
		modelData.putAll(prepared);

		// The model loading sites return before the models are loaded from disk and before
		// they're actually rendered, so we use a callback system to set the model part
		// when the part becomes available
		for (var modelInfo : models)
		{
			var modelId = modelInfo.getLeft();
			var model = modelInfo.getRight();

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
		models.add(new Pair<>(modelId, modelPart -> bipedModels.put(modelId, new BipedEntityModel<>(ensureBipedParts(modelPart)))));
		return () -> bipedModels.get(modelId);
	}

	private static ModelPart ensureBipedParts(ModelPart modelPart)
	{
		var mpa = (ModelPartAccessor)(Object)modelPart;

		if (!modelPart.hasChild(EntityModelPartNames.HEAD))
			mpa.getChildren().put(EntityModelPartNames.HEAD, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.HAT))
			mpa.getChildren().put(EntityModelPartNames.HAT, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.BODY))
			mpa.getChildren().put(EntityModelPartNames.BODY, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.RIGHT_ARM))
			mpa.getChildren().put(EntityModelPartNames.RIGHT_ARM, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.LEFT_ARM))
			mpa.getChildren().put(EntityModelPartNames.LEFT_ARM, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.RIGHT_LEG))
			mpa.getChildren().put(EntityModelPartNames.RIGHT_LEG, createEmptyModelPart());
		if (!modelPart.hasChild(EntityModelPartNames.LEFT_LEG))
			mpa.getChildren().put(EntityModelPartNames.LEFT_LEG, createEmptyModelPart());

		return modelPart;
	}

	private static ModelPart createEmptyModelPart()
	{
		return new ModelPart(ImmutableList.of(), Map.of());
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}
}
