package com.parzivail.pswg.client.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.item.data.LightsaberTag;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SimpleItemRender
{
	private static final Lazy<PM3DFile> lightsaber_luke_rotj = new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/lightsaber_luke_rotj.pm3d")));

	private static final RenderLayer LAYER_LIGHTSABER_CORE = RenderLayer.of("lightsaber_core", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().build(true));
	private static final RenderLayer LAYER_LIGHTSABER_GLOW_THIRDPERSON = RenderLayer.of("lightsaber_glow_fp", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).target(new RenderPhase.Target("translucent_target", () -> {
		MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer().beginWrite(false);
	}, () -> {
		MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
	})).build(true));
	private static final RenderLayer LAYER_LIGHTSABER_GLOW_FIRSTPERSON = RenderLayer.of("lightsaber_glow_fp", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).build(true));

	public static void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci)
	{
		if (!stack.isEmpty() && stack.getItem() == SwgItems.Lightsaber)
		{
			matrices.push();

			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

			matrices.translate(-0.02f, 0.16f, 0.04f);

			matrices.push();
			matrices.scale(0.04f, 0.04f, 0.04f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("minecraft", "textures/block/stone.png")));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);

			MinecraftClient mc = MinecraftClient.getInstance();
			LightsaberTag lt = new LightsaberTag(stack.getOrCreateTag());

			float lengthCoefficient = lt.getSize(mc.getTickDelta());
			float baseLength = 1.6f;

			lightsaber_luke_rotj.get().render(VertexConsumerBuffer.Instance);
			matrices.pop();

			matrices.translate(0.02f, 0, 0.02f);

			vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_CORE);

			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			renderBlade(VertexConsumerBuffer.Instance, baseLength * lengthCoefficient, 0, 0xFF00FF00, 0xFFFFFFFF, false, true, false);

			if (renderMode != ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND && renderMode != ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND && renderMode != ModelTransformation.Mode.GUI)
				vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_GLOW_THIRDPERSON);
			else
				vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_GLOW_FIRSTPERSON);

			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			renderBlade(VertexConsumerBuffer.Instance, baseLength * lengthCoefficient, 0, 0xFF0040FF, 0xFFFFFFFF, false, false, true);

			matrices.pop();

			ci.cancel();
		}
	}

	public static void renderBlade(VertexConsumerBuffer vcb, float bladeLength, float shake, int bladeColor, int coreColor, boolean unstable, boolean blade, boolean glow)
	{
		if (bladeLength == 0)
			return;

		//		double dX = StarWarsGalaxy.random.nextGaussian() * shake;
		//		double dY = StarWarsGalaxy.random.nextGaussian() * shake;
		//		GL.Translate(dX, 0, dY);

		int segments = unstable ? 15 : 1;
		float dSegments = 1f / segments;
		float dLength = bladeLength / segments;
		float topThickness = 0.022f;
		float bottomThickness = 0.035f;
		float offset = 0; //StarWarsGalaxy.random.nextGaussian();

		if (glow)
		{
			// draw glow
			//			for (int layer = 10; layer <= 16; layer++)
			//			{
			//				vcb.setColor(bladeColor, (int)(1.275f * layer));
			//				RenderShapes.drawSolidBoxSkewTaper(vcb, 0.14f - 0.0058f * layer, 0.16f - 0.0058f * layer, 0, bladeLength - 0.14f + 0.2f * (float)Math.sqrt(1 - Math.pow(1 - layer / 19f, 2)), 0, 0, -(20 - layer) * 0.005f, 0);
			//			}

			int layer = 0;

			vcb.setColor(bladeColor, 0x40);
			layer = 16;
			RenderShapes.drawSolidBoxSkewTaper(vcb, 0.14f - 0.0058f * layer, 0.16f - 0.0058f * layer, 0, bladeLength + 0.18f, 0, 0, -(20 - layer) * 0.005f, 0);

			vcb.setColor(0xFF0000, 0x40);
			layer = 10;
			RenderShapes.drawSolidBoxSkewTaper(vcb, 0.14f - 0.0058f * layer, 0.16f - 0.0058f * layer, 0, bladeLength + 0.2f, 0, 0, -(20 - layer) * 0.005f, 0);
		}

		if (blade)
		{
			// draw core
			vcb.setColor(coreColor);
			float dTRoundBottom = 0; //unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * (segments + 1)) * 0.005f : 0;
			RenderShapes.drawSolidBoxSkewTaper(vcb, 0.01f, 0.022f + dTRoundBottom, 0, bladeLength + 0.02f, 0, 0, bladeLength, 0);

			for (int i = 0; i < segments; i++)
			{
				float topThicknessLerp = MathHelper.lerp(dSegments * (i + 1), bottomThickness, topThickness);
				float bottomThicknessLerp = MathHelper.lerp(dSegments * i, bottomThickness, topThickness);

				float dTTop = 0; //unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * (i + 1)) * 0.005f : 0;
				float dTBottom = 0; //unstable ? StarWarsGalaxy.simplexNoise.eval(offset, dLength * i) * 0.005f : 0;

				RenderShapes.drawSolidBoxSkewTaper(vcb, topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
			}
		}
	}
}
