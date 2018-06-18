package com.parzivail.swg.entity.fx;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ParticleSmoke extends EntityFX
{
	private static final ResourceLocation smokeTexture = Resources.location("textures/particle/smoke.png");

	public ParticleSmoke(World parWorld, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ)
	{
		super(parWorld, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
		this.particleMaxAge = 20;
	}

	public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_)
	{
		float x = this.particleAge / (float)this.particleMaxAge;
		this.particleScale = (float)(1 / ((1 + Math.pow(2, -(40 * x - 10))) * (1 + Math.pow(2, 40 * x - 32))));
		float f10 = 1.5f;

		int id = this.getEntityId() % 4;

		float f6 = 0.5f * (id / 2); // Min U
		float f7 = 0.5f + 0.5f * (id / 2); // Max U
		float f8 = 0.5f * (id % 2); // Min V
		float f9 = 0.5f + 0.5f * (id % 2); // Max V

		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_70539_2_ - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_70539_2_ - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70539_2_ - interpPosZ);

		Client.mc.renderEngine.bindTexture(smokeTexture);
		GL.Enable(EnableCap.Blend);
		GL.Disable(EnableCap.Lighting);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		p_70539_1_.setColorRGBA(255, 255, 255, (int)(255 * this.particleScale));
		p_70539_1_.addVertexWithUV((double)(f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double)f7, (double)f9);
		p_70539_1_.addVertexWithUV((double)(f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double)f7, (double)f8);
		p_70539_1_.addVertexWithUV((double)(f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double)f6, (double)f8);
		p_70539_1_.addVertexWithUV((double)(f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double)f6, (double)f9);
	}
}
