package com.parzivail.pm3d;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Pm3dModelLoader implements ICustomModelLoader
{
	private IResourceManager manager;
	private final Map<ResourceLocation, Pm3dModel> cache = new HashMap<>();
	private final Map<ResourceLocation, Exception> errors = new HashMap<>();

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		this.manager = resourceManager;
		cache.clear();
		errors.clear();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.getResourcePath().endsWith(".pm3d");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath());
		if (!cache.containsKey(file))
		{
			IResource resource = null;
			try
			{
				try
				{
					resource = manager.getResource(file);
				}
				catch (FileNotFoundException e)
				{
					if (modelLocation.getResourcePath().startsWith("models/block/"))
						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/item/" + file.getResourcePath().substring("models/block/".length())));
					else if (modelLocation.getResourcePath().startsWith("models/item/"))
						resource = manager.getResource(new ResourceLocation(file.getResourceDomain(), "models/block/" + file.getResourcePath().substring("models/item/".length())));
					else
						throw e;
				}
				Pm3dLoader loader = new Pm3dLoader(resource, manager);
				Pm3dModel model = null;
				try
				{
					model = loader.load();
				}
				catch (Exception e)
				{
					errors.put(modelLocation, e);
				}
				finally
				{
					cache.put(modelLocation, model);
				}
			}
			finally
			{
				IOUtils.closeQuietly(resource);
			}
		}
		Pm3dModel model = cache.get(file);
		if (model == null)
			throw new ModelLoaderRegistry.LoaderException("Error loading model previously: " + file, errors.get(modelLocation));
		return model;
	}
}
