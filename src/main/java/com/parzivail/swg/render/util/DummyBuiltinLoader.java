package com.parzivail.swg.render.util;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class DummyBuiltinLoader implements ICustomModelLoader
{
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.toString().equals("pswg:builtin#inventory");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		if (!(modelLocation instanceof ModelResourceLocation))
			throw new RuntimeException("Unsupported model format requested pswg:builtin");
		ModelResourceLocation mrl = (ModelResourceLocation)modelLocation;
		return new DummyBuiltinRenderer(mrl.getVariant());
	}
}
