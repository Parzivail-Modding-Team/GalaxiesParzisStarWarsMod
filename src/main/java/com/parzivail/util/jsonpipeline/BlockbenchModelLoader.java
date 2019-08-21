package com.parzivail.util.jsonpipeline;

import com.parzivail.util.common.Lumberjack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public class BlockbenchModelLoader implements ICustomModelLoader
{
	private final Predicate<ResourceLocation> handleCondition;

	public BlockbenchModelLoader(Predicate<ResourceLocation> handleCondition)
	{
		this.handleCondition = handleCondition;
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return handleCondition.test(modelLocation);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		// Load vanilla model
		BlockbenchGeometry vanillaModel;
		ResourceLocation vanillaModelLocation = new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath());
		try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(vanillaModelLocation); Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
		{
			vanillaModel = BlockbenchGeometry.deserialize(reader);
			vanillaModel.name = modelLocation.toString();
		}

		// Load armature animation (currently disabled for efficiency, see MixinModelBlockAnimation)
		String modelPath = modelLocation.getResourcePath();
		if (modelPath.startsWith("models/"))
		{
			modelPath = modelPath.substring("models/".length());
		}
		ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getResourceDomain(), "armatures/" + modelPath);
		ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(Minecraft.getMinecraft().getResourceManager(), armatureLocation);

		Lumberjack.log(modelLocation);
		// Return the vanilla model weapped in a BlockbenchModel
		return new BlockbenchModel(modelLocation, vanillaModel, false, animation);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
	}

	@Override
	public String toString()
	{
		return "BlockbenchModelLoader";
	}
}
