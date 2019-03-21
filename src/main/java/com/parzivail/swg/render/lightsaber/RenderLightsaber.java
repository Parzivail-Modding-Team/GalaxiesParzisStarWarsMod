package com.parzivail.swg.render.lightsaber;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.item.lightsaber.LightsaberDescriptor;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.worldext.RenderExtLightsaberTrail;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.render.pipeline.JsonItemRenderer;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLightsaber extends JsonItemRenderer
{
	public RenderLightsaber(ResourceLocation modelLocation)
	{
		super(modelLocation);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return type == ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		LightsaberData bd = new LightsaberData(item);
		LightsaberDescriptor d = bd.descriptor;

		if (d == null)
			d = LightsaberDescriptor.BLANK;

		boolean renderBlade = true;

		GL.PushAttrib(AttribMask.EnableBit);
		switch (type)
		{
			case ENTITY:
				break;
			case EQUIPPED:
				GL.Rotate(13, 0, 0, 1);
				GL.Rotate(15.5f, 1, 0, 0);
				GL.Rotate(10f, 0, 1, 0);
				GL.Translate(0.425f, 0.2f, 0);
				break;
			case EQUIPPED_FIRST_PERSON:
				GL.Rotate(20, 0, 0, 1);
				GL.Translate(0.6f, 0.6f, 0);
				break;
			case INVENTORY:
				GL.Scale(15);
				GL.Rotate(-135, 0, 0, 1);
				GL.Translate(-0.75f, 0.5f, 0);
				GL.Rotate(135, 0, 1, 0);
				renderBlade = false;
				GL.Disable(EnableCap.CullFace);
				break;
		}

		EntityPlayer player = null;
		if (data.length >= 2 && data[1] instanceof EntityPlayer)
			player = (EntityPlayer)data[1];

		if (player != null)
		{
			if (player.getItemInUse() == item && player.getItemInUseDuration() > 0 && type == ItemRenderType.EQUIPPED)
			{
				GL.Translate(0.3f, -0.2f, 0);
				GL.Rotate(-75, 0, 0, 1);
			}
		}

		GL.PushMatrix();
		GL.Translate(-0.5f, 0, -0.5f);

		Client.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		super.renderItem(type, item, data);
		GL.PopMatrix();

		if (renderBlade)
		{
			float length = d.bladeLength * MathHelper.clamp_float((bd.openAnimation + Client.renderPartialTicks * bd.openingState - bd.openingState) / 4f, 0, 1);

			if (player != null && (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON))
			{
				GL.PushMatrix();
				Vector3f pBase = Client.getLocalToWorldPos();
				float bladeGlowYOffset = 0.06f;
				GL.Translate(0, length + bladeGlowYOffset, 0);
				Vector3f pEnd = Client.getLocalToWorldPos();
				RenderExtLightsaberTrail.addTrailComponent(player, d.bladeColor, d.coreColor, d.bladeLength, pBase, pEnd);
				GL.PopMatrix();
			}

			renderBlade(length, (4.1f - bd.openAnimation) * 0.004f, d.bladeColor, d.coreColor, d.unstable);
		}
		GL.PopAttrib();
	}

	public static void renderBlade(float bladeLength, float shake, int bladeColor, int coreColor, boolean unstable)
	{
		if (bladeLength == 0)
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Texture2D);
		GL.Disable(EnableCap.AlphaTest);
		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.CullFace);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Client.mc.entityRenderer.disableLightmap(0);

		double dX = StarWarsGalaxy.random.nextGaussian() * shake;
		double dY = StarWarsGalaxy.random.nextGaussian() * shake;
		GL.Translate(dX, 0, dY);

		// draw glow
		GL11.glDepthMask(false);
		for (int layer = 19; layer >= 0; layer--)
		{
			GL.Color(bladeColor, (int)(1.275f * layer));
			Fx.D3.DrawSolidBoxSkewTaper(0.12 - 0.0058f * layer, 0.16 - 0.0058f * layer, 0, bladeLength - 0.13f + 0.2f * Math.sqrt(1 - Math.pow(1 - layer / 19f, 2)), 0, 0, -(20 - layer) * 0.005f, 0);
		}
		GL11.glDepthMask(true);

		// draw core
		GL.Color(coreColor);

		int segments = unstable ? 15 : 1;
		float dSegments = 1f / segments;
		float dLength = bladeLength / segments;
		float topThickness = 0.022f;
		float bottomThickness = 0.035f;
		double offset = StarWarsGalaxy.random.nextGaussian();

		double dTRoundBottom = unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * (segments + 1)) * 0.005f : 0;
		Fx.D3.DrawSolidBoxSkewTaper(0.01f, 0.022f + dTRoundBottom, 0, bladeLength + 0.02f, 0, 0, bladeLength, 0);

		for (int i = 0; i < segments; i++)
		{
			float topThicknessLerp = (float)Fx.Util.Lerp(bottomThickness, topThickness, dSegments * (i + 1));
			float bottomThicknessLerp = (float)Fx.Util.Lerp(bottomThickness, topThickness, dSegments * i);

			double dTTop = unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * (i + 1)) * 0.005f : 0;
			double dTBottom = unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * i) * 0.005f : 0;

			Fx.D3.DrawSolidBoxSkewTaper(topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
		}

		Client.mc.entityRenderer.enableLightmap(0);
		GL.PopAttrib();
		GL11.glPopMatrix();
	}
}
