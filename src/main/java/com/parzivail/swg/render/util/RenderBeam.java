package com.parzivail.swg.render.util;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import org.lwjgl.opengl.GL11;

public class RenderBeam
{
	/**
	 * Draws a beam. Lightsabers use 0.005 localInstability, (1.025 - openPercent) * 0.016f globalInstability, 19 layers, 0.037 taper, 1.275 layerDeltaOpacity
	 */
	public static void render(float length, float taper, int layers, float layerDeltaOpacity, boolean hasCore, int coreColor, boolean hasGlow, int glowColor, float globalInstability, float localInstability)
	{
		if (length == 0)
			return;

		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Texture2D);
		GL.Disable(EnableCap.AlphaTest);
		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.CullFace);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		SwgClientProxy.mc.entityRenderer.disableLightmap();

		if (globalInstability != 0)
		{
			double dX = Resources.RANDOM.nextGaussian() * globalInstability;
			double dY = Resources.RANDOM.nextGaussian() * globalInstability;
			GL.Translate(dX, 0, dY);
		}

		if (hasGlow)
		{
			// draw glow
			GL11.glDepthMask(false);
			float fLayers = layers;
			int layersPlusOne = layers + 1;
			for (int layer = layers; layer >= 0; layer--)
			{
				GL.Color(glowColor, (int)(layerDeltaOpacity * layer));
				Fx.D3.DrawSolidBoxSkewTaper(0.12 - 0.0058f * layer, 0.16 - 0.0058f * layer, 0, length - 0.13f + 0.2f * Math.sqrt(1 - Math.pow(1 - layer / fLayers, 2)), 0, 0, -(layersPlusOne - layer) * 0.005f, 0);
			}
			GL11.glDepthMask(true);
		}

		if (hasCore)
		{
			// draw core
			GL.Color(coreColor);

			boolean isBladeUnstable = localInstability != 0;

			int segments = isBladeUnstable ? 15 : 1;
			float dSegments = 1f / segments;
			float dLength = length / segments;
			float bottomThickness = 0.035f;
			float topThickness = bottomThickness * (1 - taper); //0.022f;
			double offset = Resources.RANDOM.nextGaussian();

			double dTRoundBottom = isBladeUnstable ? Resources.NOISE.eval(offset, dLength * (segments + 1)) * localInstability : 0;
			Fx.D3.DrawSolidBoxSkewTaper(0.01f, topThickness + dTRoundBottom, 0, length + 0.02f, 0, 0, length, 0);

			for (int i = 0; i < segments; i++)
			{
				float topThicknessLerp = (float)Fx.Util.Lerp(bottomThickness, topThickness, dSegments * (i + 1));
				float bottomThicknessLerp = (float)Fx.Util.Lerp(bottomThickness, topThickness, dSegments * i);

				double dTTop = isBladeUnstable ? Resources.NOISE.eval(offset, dLength * (i + 1)) * localInstability : 0;
				double dTBottom = isBladeUnstable ? Resources.NOISE.eval(offset, dLength * i) * localInstability : 0;

				Fx.D3.DrawSolidBoxSkewTaper(topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
			}
		}

		SwgClientProxy.mc.entityRenderer.enableLightmap();
		GL.PopAttrib();
		GL11.glPopMatrix();
	}
}
