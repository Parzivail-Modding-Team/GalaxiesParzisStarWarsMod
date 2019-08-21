package com.parzivail.pm3d;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Pm3dLoader implements ICustomModelLoader
{
	private IResourceManager manager;
	private final Set<String> enabledDomains = new HashSet<>();
	private final Map<ResourceLocation, OBJModel> cache = new HashMap<>();
	private final Map<ResourceLocation, Exception> errors = new HashMap<>();

	public Pm3dLoader(String domain)
	{
		enabledDomains.add(domain.toLowerCase());
	}

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
		return enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".pm3d");
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
				OBJModel.Parser parser = new OBJModel.Parser(resource, manager);
				OBJModel model = null;
				try
				{
					model = parser.parse();
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
		OBJModel model = cache.get(file);
		if (model == null)
			throw new ModelLoaderRegistry.LoaderException("Error loading model previously: " + file, errors.get(modelLocation));
		return model;
	}
}
