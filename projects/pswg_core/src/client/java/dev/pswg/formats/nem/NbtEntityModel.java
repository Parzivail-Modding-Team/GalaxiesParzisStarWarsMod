package dev.pswg.formats.nem;

import dev.pswg.data.DataResolution;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public final class NbtEntityModel
{
	/**
	 * The list of model parts required to successfully render a biped model
	 */
	private static final List<String> REQUIRED_BIPED_PARTS = List.of(
			EntityModelPartNames.HEAD, EntityModelPartNames.HAT, EntityModelPartNames.BODY,
			EntityModelPartNames.RIGHT_ARM, EntityModelPartNames.LEFT_ARM, EntityModelPartNames.RIGHT_LEG,
			EntityModelPartNames.LEFT_LEG
	);

	/**
	 * Loads a NBT Entity Model (*.nem) file into a {@link TexturedModelData}
	 *
	 * @param nbt                The serialized model data
	 * @param dependencyResolver A function that will resolve dependency identifiers to serialized model data
	 *
	 * @return A loaded {@link TexturedModelData} if all dependencies were resolved, or a list of missing dependencies otherwise
	 */
	public static DataResolution<TexturedModelData> load(NbtCompound nbt, Function<Identifier, NbtCompound> dependencyResolver)
	{
		var resolution = resolveDependencies(nbt, dependencyResolver);

		if (resolution.getDependencies().isPresent())
			// bubble up missing dependencies
			return DataResolution.missingDependency(resolution.getDependencies().get());

		return DataResolution.success(buildModel(nbt));
	}

	/**
	 * Resolves the dependencies of a model and expands the missing parts of the model as requested
	 *
	 * @param nbt                The serialized model data
	 * @param dependencyResolver A function that will resolve dependency identifiers to serialized model data
	 *
	 * @return A processed {@link NbtCompound} if all dependencies were resolved, or a list of missing dependencies otherwise
	 */
	private static DataResolution<NbtCompound> resolveDependencies(NbtCompound nbt, Function<Identifier, NbtCompound> dependencyResolver)
	{
		var partsOpt = nbt.getCompound("parts");

		if (partsOpt.isEmpty())
			return DataResolution.success(nbt);

		var parts = partsOpt.get();

		var base = nbt.getString("base");
		if (base.isPresent())
		{
			var baseId = Identifier.of(base.get());

			// resolve the serialized data of the dependency
			var overrideNbt = dependencyResolver.apply(baseId);
			if (overrideNbt == null)
				// if the dep itself wasn't found, call it missing
				return DataResolution.missingDependency(List.of(baseId));

			// Process the dep's dependencies
			var overrideNbtResult = resolveDependencies(overrideNbt, dependencyResolver);
			if (overrideNbtResult.getDependencies().isPresent())
				// if it had missing deps of its own, bubble them up
				return DataResolution.missingDependency(overrideNbtResult.getDependencies().get());

			// consider the dep resolved
			overrideNbt = overrideNbtResult.getValue();

			var overridePartsOpt = overrideNbt.getCompound("parts");
			if (overridePartsOpt.isPresent())
			{
				var overrideParts = overridePartsOpt.get();

				for (var entry : overrideParts.getKeys())
					if (!parts.contains(entry))
						parts.put(entry, overrideParts.getCompound(entry).orElseThrow());
			}
		}

		if (nbt.getBoolean("expand_biped", false))
		{
			for (var part : REQUIRED_BIPED_PARTS)
				if (!parts.contains(part))
					parts.put(part, new NbtCompound());
		}

		return DataResolution.success(nbt);
	}

	/**
	 * Converts a serialized model to a {@link TexturedModelData}
	 *
	 * @param nbt The serialized model data
	 *
	 * @return A textured model
	 */
	private static TexturedModelData buildModel(NbtCompound nbt)
	{
		var modelData = new ModelData();
		var root = modelData.getRoot();

		addChildren(root, nbt.getCompoundOrEmpty("parts"));

		var texTag = nbt.getCompound("tex").orElseThrow();
		return TexturedModelData.of(modelData, texTag.getInt("w", 0), texTag.getInt("h", 0));
	}

	/**
	 * Recursively adds the children of the serialized model data to the current model part
	 *
	 * @param root  The model part into which the parts will be placed
	 * @param parts The source of the parts to add
	 */
	private static void addChildren(ModelPartData root, NbtCompound parts)
	{
		for (var key : parts.getKeys())
			addChild(root, key, parts.getCompound(key).orElseThrow());
	}

	/**
	 * Adds a single child from the serialized model data to the current model part
	 *
	 * @param parent   The model part into which the parts will be placed
	 * @param partName The name of the part to be added
	 * @param part     The serialized value of the part to be added
	 */
	private static void addChild(ModelPartData parent, String partName, NbtCompound part)
	{
		var partBuilder = ModelPartBuilder.create();

		if (part.isEmpty())
			parent.addChild(partName, partBuilder, ModelTransform.NONE);
		else
		{
			var tex = part.getCompoundOrEmpty("tex");
			var partU = tex.getInt("u", 0);
			var partV = tex.getInt("v", 0);
			var mirrored = tex.getBoolean("mirrored", false);

			var pos = part.getCompoundOrEmpty("pos");
			var x = pos.getFloat("x", 0);
			var y = pos.getFloat("y", 0);
			var z = pos.getFloat("z", 0);

			var rot = part.getCompoundOrEmpty("rot");
			var pitch = rot.getFloat("pitch", 0);
			var yaw = rot.getFloat("yaw", 0);
			var roll = rot.getFloat("roll", 0);

			var transform = ModelTransform.of(x, y, z, pitch, yaw, roll);

			var cuboids = part.getListOrEmpty("cuboids");
			for (var i = 0; i < cuboids.size(); i++)
			{
				var cuboid = cuboids.getCompoundOrEmpty(i);

				var cPos = cuboid.getCompoundOrEmpty("pos");
				var cX = cPos.getFloat("x", 0);
				var cY = cPos.getFloat("y", 0);
				var cZ = cPos.getFloat("z", 0);

				var cSize = cuboid.getCompoundOrEmpty("size");
				var cSX = cSize.getInt("x", 0);
				var cSY = cSize.getInt("y", 0);
				var cSZ = cSize.getInt("z", 0);

				var cExpand = cuboid.getCompoundOrEmpty("expand");
				var cEX = cExpand.getFloat("x", 0);
				var cEY = cExpand.getFloat("y", 0);
				var cEZ = cExpand.getFloat("z", 0);

				var cTex = cuboid.getCompoundOrEmpty("tex");
				var cU = cTex.getInt("u", 0);
				var cV = cTex.getInt("v", 0);
				var cMirrored = cTex.getBoolean("mirrored", false);

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
				addChildren(childPart, part.getCompound("children").orElseThrow());
		}
	}
}
