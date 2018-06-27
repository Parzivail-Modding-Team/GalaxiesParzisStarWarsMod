package com.parzivail.util.binary.Swg3;

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
		textureName = name;
		texture = new ResourceLocation(Resources.MODID, "textures/model/" + textureName);
	}
}
