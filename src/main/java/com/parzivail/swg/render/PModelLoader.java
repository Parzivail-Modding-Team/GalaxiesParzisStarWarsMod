package com.parzivail.swg.render;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import org.apache.commons.io.FilenameUtils;

public class PModelLoader implements ICustomModelLoader
{
	public static final PModelLoader INSTANCE = new PModelLoader();

	private PModelLoader()
	{
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return FilenameUtils.isExtension(modelLocation.getResourcePath(), "pjson");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		return null;
	}
}
