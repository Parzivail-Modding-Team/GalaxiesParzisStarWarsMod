package com.parzivail.pswg.client.pm3d;

import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PM3DLoader implements ModelResourceProvider, Function<ResourceManager, ModelResourceProvider>
{
	public static final PM3DLoader INSTANCE = new PM3DLoader();
	private Set<String> domains = new HashSet<>();

	public void registerDomain(String modid)
	{
		if (!domains.contains(modid))
		{
			domains.add(modid);
		}
		else
		{
			Lumberjack.warn("Duplicate registry of PM3D handler, source: " + modid);
		}
	}

	public boolean isRegistered(String modid)
	{
		return domains.contains(modid);
	}

	public PM3DContainer loadModel(InputStream reader, Identifier identifier)
	{
		PM3DContainer container;
		try
		{
			container = PM3DContainer.load(reader, identifier);
		}
		catch (IOException e)
		{
			Lumberjack.error("Could not read PM3D model:", e);
			return null;
		}

		return container;
	}

	@Override
	public UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext)
	{
		return loadModelResource(identifier, modelProviderContext, ModelTransformation.NONE);
	}

	protected UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext, ModelTransformation transform)
	{
		if (isRegistered(identifier.getNamespace()) && identifier.getPath().endsWith(".pm3d"))
		{
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

			try (InputStream reader = resourceManager.getResource(new Identifier(identifier.getNamespace(), "models/" + identifier.getPath())).getInputStream())
			{
				PM3DContainer model = loadModel(reader, identifier);
				Mesh mesh = PM3DBuilder.build(model);
				return new PM3DUnbakedModel(model, mesh, transform);
			}
			catch (IOException e)
			{
				Lumberjack.error("Unable to load PM3D model, source: " + identifier.toString(), e);
			}
		}

		return null;
	}

	@Override
	public ModelResourceProvider apply(ResourceManager manager)
	{
		return this;
	}
}
