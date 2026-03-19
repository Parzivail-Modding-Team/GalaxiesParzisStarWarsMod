package dev.pswg.formats.nem;

import dev.pswg.data.DataResolution;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.function.Function;

public final class NbtEntityModel
{
	/**
	 * The list of model parts required to successfully render a biped model
	 */
	private static final List<String> REQUIRED_BIPED_PARTS = List.of(
			PartNames.HEAD, PartNames.HAT, PartNames.BODY,
			PartNames.RIGHT_ARM, PartNames.LEFT_ARM, PartNames.RIGHT_LEG,
			PartNames.LEFT_LEG
	);

	/**
	 * Loads a NBT Entity Model (*.nem) file into a {@link LayerDefinition}
	 *
	 * @param nbt                The serialized model data
	 * @param dependencyResolver A function that will resolve dependency identifiers to serialized model data
	 *
	 * @return A loaded {@link LayerDefinition} if all dependencies were resolved, or a list of missing dependencies otherwise
	 */
	public static DataResolution<LayerDefinition> load(CompoundTag nbt, Function<ResourceLocation, CompoundTag> dependencyResolver)
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
	 * @return A processed {@link CompoundTag} if all dependencies were resolved, or a list of missing dependencies otherwise
	 */
	private static DataResolution<CompoundTag> resolveDependencies(CompoundTag nbt, Function<ResourceLocation, CompoundTag> dependencyResolver)
	{
		var partsOpt = nbt.getCompound("parts");

		if (partsOpt.isEmpty())
			return DataResolution.success(nbt);

		var parts = partsOpt.get();

		var base = nbt.getString("base");
		if (base.isPresent())
		{
			var baseId = ResourceLocation.parse(base.get());

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

				for (var entry : overrideParts.keySet())
					if (!parts.contains(entry))
						parts.put(entry, overrideParts.getCompound(entry).orElseThrow());
			}
		}

		if (nbt.getBooleanOr("expand_biped", false))
		{
			for (var part : REQUIRED_BIPED_PARTS)
				if (!parts.contains(part))
					parts.put(part, new CompoundTag());
		}

		return DataResolution.success(nbt);
	}

	/**
	 * Converts a serialized model to a {@link LayerDefinition}
	 *
	 * @param nbt The serialized model data
	 *
	 * @return A textured model
	 */
	private static LayerDefinition buildModel(CompoundTag nbt)
	{
		var modelData = new MeshDefinition();
		var root = modelData.getRoot();

		addChildren(root, nbt.getCompoundOrEmpty("parts"));

		var texTag = nbt.getCompound("tex").orElseThrow();
		return LayerDefinition.create(modelData, texTag.getIntOr("w", 0), texTag.getIntOr("h", 0));
	}

	/**
	 * Recursively adds the children of the serialized model data to the current model part
	 *
	 * @param root  The model part into which the parts will be placed
	 * @param parts The source of the parts to add
	 */
	private static void addChildren(PartDefinition root, CompoundTag parts)
	{
		for (var key : parts.keySet())
			addChild(root, key, parts.getCompound(key).orElseThrow());
	}

	/**
	 * Adds a single child from the serialized model data to the current model part
	 *
	 * @param parent   The model part into which the parts will be placed
	 * @param partName The name of the part to be added
	 * @param part     The serialized value of the part to be added
	 */
	private static void addChild(PartDefinition parent, String partName, CompoundTag part)
	{
		var partBuilder = CubeListBuilder.create();

		if (part.isEmpty())
			parent.addOrReplaceChild(partName, partBuilder, PartPose.ZERO);
		else
		{
			var tex = part.getCompoundOrEmpty("tex");
			var partU = tex.getIntOr("u", 0);
			var partV = tex.getIntOr("v", 0);
			var mirrored = tex.getBooleanOr("mirrored", false);

			var pos = part.getCompoundOrEmpty("pos");
			var x = pos.getFloatOr("x", 0);
			var y = pos.getFloatOr("y", 0);
			var z = pos.getFloatOr("z", 0);

			var rot = part.getCompoundOrEmpty("rot");
			var pitch = rot.getFloatOr("pitch", 0);
			var yaw = rot.getFloatOr("yaw", 0);
			var roll = rot.getFloatOr("roll", 0);

			var transform = PartPose.offsetAndRotation(x, y, z, pitch, yaw, roll);

			var cuboids = part.getListOrEmpty("cuboids");
			for (var i = 0; i < cuboids.size(); i++)
			{
				var cuboid = cuboids.getCompoundOrEmpty(i);

				var cPos = cuboid.getCompoundOrEmpty("pos");
				var cX = cPos.getFloatOr("x", 0);
				var cY = cPos.getFloatOr("y", 0);
				var cZ = cPos.getFloatOr("z", 0);

				var cSize = cuboid.getCompoundOrEmpty("size");
				var cSX = cSize.getIntOr("x", 0);
				var cSY = cSize.getIntOr("y", 0);
				var cSZ = cSize.getIntOr("z", 0);

				var cExpand = cuboid.getCompoundOrEmpty("expand");
				var cEX = cExpand.getFloatOr("x", 0);
				var cEY = cExpand.getFloatOr("y", 0);
				var cEZ = cExpand.getFloatOr("z", 0);

				var cTex = cuboid.getCompoundOrEmpty("tex");
				var cU = cTex.getIntOr("u", 0);
				var cV = cTex.getIntOr("v", 0);
				var cMirrored = cTex.getBooleanOr("mirrored", false);

				partBuilder = partBuilder.mirror(cMirrored ^ mirrored).addBox(
						"",
						cX, cY, cZ,
						cSX, cSY, cSZ,
						new CubeDeformation(cEX, cEY, cEZ),
						partU + cU, partV + cV
				);
			}

			var childPart = parent.addOrReplaceChild(partName, partBuilder, transform);

			if (part.contains("children"))
				addChildren(childPart, part.getCompound("children").orElseThrow());
		}
	}
}
