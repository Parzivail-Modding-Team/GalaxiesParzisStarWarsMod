package com.parzivail.swg.render.npc;

import com.parzivail.swg.Resources;
import com.parzivail.swg.npc.NpcJawa;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderJawa extends RenderBiped<NpcJawa>
{
	private static final ResourceLocation texture = Resources.location("textures/npc/jawa.png");

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(NpcJawa p_110775_1_)
	{
		return texture;
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(NpcJawa p_77041_1_, float p_77041_2_)
	{
		float f1 = 0.5f;
		switch (p_77041_1_.getHeight())
		{
			case 0:
				f1 = 0.7f;
				break;
			case 1:
				f1 = 0.6f;
				break;
			case 2:
				f1 = 0.5f;
				break;
		}
		GL11.glScalef(f1 * 1.1f, f1, f1 * 1.1f);
	}
}
