package com.parzivail.swg.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

/**
 * Created by colby on 1/6/2018.
 */
public class PEntityRenderer extends EntityRenderer
{
	public PEntityRenderer(Minecraft minecraft, IResourceManager resourceManager)
	{
		super(minecraft, resourceManager);
	}
}
