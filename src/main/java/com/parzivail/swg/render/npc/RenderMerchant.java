package com.parzivail.swg.render.npc;

import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.npc.NpcProfession;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMerchant extends RenderBiped<NpcMerchant>
{
	protected ResourceLocation getEntityTexture(NpcMerchant p_110775_1_)
	{
		return NpcMerchant.professionSkins.get(NpcProfession.fromIndex(p_110775_1_.getProfession()))[p_110775_1_.getSkin()];
	}

	protected void preRenderCallback(NpcMerchant p_77041_1_, float p_77041_2_)
	{
		float f1 = 0.9375F;
		float p = (p_77041_1_.getHeight()) / 4f;
		f1 -= p / 10;
		GL11.glScalef(f1, f1, f1);
	}
}
