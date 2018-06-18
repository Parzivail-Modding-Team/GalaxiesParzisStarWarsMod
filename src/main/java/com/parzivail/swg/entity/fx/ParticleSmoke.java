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
	private static final ResourceLocation smokeTexture = Resources.location("textures/particle/puff.png");

	public ParticleSmoke(World parWorld, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ)
	{
		super(parWorld, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
		this.particleMaxAge = 20;
	}

	public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_)
	{
		float a = this.particleAge / (float)this.particleMaxAge;
		this.particleScale = (float)(1 / ((1 + Math.pow(2, -(40 * a - 10))) * (1 + Math.pow(2, 40 * a - 32))));
		float f10 = 2f;

		int atlasCol = 4;
		int atlasRow = 8;

		int frame = Math.round(a * atlasCol * atlasRow);

		float oneOverAtlasCol = 1f / atlasCol;
		float oneOverAtlasRow = 1f / atlasRow;

		float x = (frame % atlasCol) * oneOverAtlasCol;
		float y = (float)(Math.floor(frame / (float)atlasCol) * oneOverAtlasRow);

		float f6 = x; // Min U
		float f7 = x + oneOverAtlasCol; // Max U
		float f8 = y; // Min V
		float f9 = y + oneOverAtlasRow; // Max V

		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_70539_2_ - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_70539_2_ - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70539_2_ - interpPosZ);

		Client.mc.renderEngine.bindTexture(smokeTexture);
		GL.Enable(EnableCap.Blend);
		Client.mc.entityRenderer.disableLightmap(0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		p_70539_1_.setColorRGBA(255, 255, 255, (int)(255 * this.particleScale));
		p_70539_1_.addVertexWithUV((double)(f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double)f7, (double)f9);
		p_70539_1_.addVertexWithUV((double)(f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double)f7, (double)f8);
		p_70539_1_.addVertexWithUV((double)(f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double)f6, (double)f8);
		p_70539_1_.addVertexWithUV((double)(f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double)f6, (double)f9);
	}
}
