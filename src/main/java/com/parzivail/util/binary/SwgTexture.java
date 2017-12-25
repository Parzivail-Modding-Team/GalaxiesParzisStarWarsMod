package com.parzivail.util.binary;

import com.parzivail.swg.Resources;
import net.minecraft.util.ResourceLocation;

/**
 * Created by colby on 12/25/2017.
 */
public class SwgTexture
{
	public String textureName;
	public ResourceLocation texture;

	public SwgTexture(String name)
	{
		this.textureName = name;
		this.texture = new ResourceLocation(Resources.MODID, "textures/model/" + textureName);
	}
}
