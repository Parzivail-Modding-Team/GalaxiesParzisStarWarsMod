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
		var parts = nbt.getCompound("parts");

		var base = nbt.getString("base");
		if (!base.isEmpty())
		{
			var baseId = Identifier.of(base);

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

		addChildren(root, nbt.getCompound("parts"));

		var texTag = nbt.getCompound("tex");
		return TexturedModelData.of(modelData, texTag.getInt("w"), texTag.getInt("h"));
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
			addChild(root, key, parts.getCompound(key));
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
}
