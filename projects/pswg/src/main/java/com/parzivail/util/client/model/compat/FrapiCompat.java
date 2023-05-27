package com.parzivail.util.client.model.compat;

import com.parzivail.util.runtime.ClassLoadingHelper;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import java.util.Optional;

public class FrapiCompat
{
	public static Optional<MaterialFinder> getMaterialFinder82()
	{
		// FAPI >=0.82.0+1.19.4
		return ClassLoadingHelper.tryInit("net.fabricmc.fabric.impl.client.indigo.renderer.material.MaterialFinderImpl");
	}

	public static Optional<MaterialFinder> getMaterialFinderLegacy()
	{
		// FAPI <0.82.0+1.19.4
		return ClassLoadingHelper.tryInit("net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl$Finder");
	}

	public static Optional<MeshBuilder> getMeshBuilder()
	{
		return ClassLoadingHelper.tryInit("net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshBuilderImpl");
	}
}
