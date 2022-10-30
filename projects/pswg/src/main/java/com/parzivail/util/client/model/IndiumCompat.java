package com.parzivail.util.client.model;

import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import java.util.Optional;

public class IndiumCompat
{
	@SuppressWarnings("unchecked")
	private static <T> Optional<T> tryInit(String className)
	{
		try
		{
			var clazz = Class.forName(className, false, IndiumCompat.class.getClassLoader());
			return Optional.of((T)clazz.getDeclaredConstructor().newInstance());
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	public static Optional<MaterialFinder> getMaterialFinder()
	{
		return tryInit("link.infra.indium.renderer.RenderMaterialImpl$Finder");
	}

	public static Optional<MeshBuilder> getMeshBuilder()
	{
		return tryInit("link.infra.indium.renderer.mesh.MeshBuilderImpl");
	}
}
