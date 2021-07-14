package com.parzivail.pswg.client.loader;

import com.parzivail.pswg.client.model.ModelAngleAnimator;
import com.parzivail.pswg.client.model.MutableAnimatedModel;
import com.parzivail.util.binary.KeyedReloadableLoader;
import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;
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

public class NemManager extends KeyedReloadableLoader<TexturedModelData>
{
	private final Map<Identifier, TexturedModelData> modelData;
	private final ArrayList<Pair<Identifier, MutableAnimatedModel<? extends Entity>>> models;

	public NemManager()
	{
		super("models", "nem");
		modelData = new HashMap<>();
		models = new ArrayList<>();
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

		// The model instances get pulled into their renderers before the models are loaded
		// from disk but before they're actually rendered, so we can set the model root here
		for (var modelInfo : models)
		{
			var modelId = modelInfo.getLeft();
			var model = modelInfo.getRight();

			model.setRoot(modelData.get(modelId).createModel());
		}
	}

	public <T extends Entity> MutableAnimatedModel<T> getModel(Identifier modelId, ModelAngleAnimator<T> angleAnimator)
	{
		var model = new MutableAnimatedModel<>(angleAnimator);
		models.add(new Pair<>(modelId, model));
		return model;
	}
}
