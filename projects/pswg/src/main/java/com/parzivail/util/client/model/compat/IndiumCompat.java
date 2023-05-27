package com.parzivail.util.client.model.compat;

import com.parzivail.util.runtime.ClassLoadingHelper;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import java.util.Optional;

public class IndiumCompat
{
	public static Optional<MaterialFinder> getMaterialFinder()
	{
		return ClassLoadingHelper.tryInit("link.infra.indium.renderer.RenderMaterialImpl$Finder");
	}

	public static Optional<MeshBuilder> getMeshBuilder()
	{
		return ClassLoadingHelper.tryInit("link.infra.indium.renderer.mesh.MeshBuilderImpl");
	}
}
