package dev.pswg.particles.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.pswg.Gadgets;
import dev.pswg.GalaxiesRenderLayers;
import dev.pswg.particles.GasParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import java.util.List;

public class GasParticleRenderer extends ParticleGroup<GasParticle>
{
	public GasParticleRenderer(ParticleEngine particleManager)
	{
		super(particleManager);
	}

	@Override
	public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float tickProgress)
	{
		return new Result(
				this.particles.stream().map(particle -> State.create(particle, camera, tickProgress)).toList()
		);
	}

	record State(PoseStack matrices, TextureAtlasSprite sprite, float billowing, float alpha, float scale, int color, int light)
	{
		public static State create(GasParticle particle, Camera camera, float tickProgress)
		{
			PoseStack matrixStack = new PoseStack();
			matrixStack.pushPose();
			matrixStack.translate(particle.getPos().subtract(camera.position()));
			matrixStack.mulPose(camera.rotation().rotateZ((float)Math.toRadians(particle.getBillowing())));

			return new State(
					matrixStack,
					particle.getSprite(),
					particle.getBillowing(),
					particle.getAlpha(),
					particle.getQuadSize(tickProgress),
					particle.getColor(),
					particle.getLightCoords(tickProgress)
			);
		}
	}

	record Result(List<State> states) implements ParticleGroupRenderState
	{
		@Override
		public void submit(SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState)
		{
			for (State state : this.states)
			{
				var matrix = state.matrices;
				orderedRenderCommandQueue.submitCustomGeometry(matrix, GalaxiesRenderLayers.GALAXIES_TRANSLUCENT, (matricesEntry, vertexConsumer) -> {
					float scale = state.scale;
					int alpha = (int)(state.alpha * 255);
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, -1F * scale, -1F * scale, state.sprite.getU0(), state.sprite.getV1());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, 1F * scale, -1F * scale, state.sprite.getU1(), state.sprite.getV1());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, 1F * scale, 1F * scale, state.sprite.getU1(), state.sprite.getV0());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, -1F * scale, 1F * scale, state.sprite.getU0(), state.sprite.getV0());
				});
			}
		}

		private static void vertex(VertexConsumer buffer, PoseStack.Pose matrix, int light, int alpha, int color, float x, float y, float u, float v)
		{
			buffer.addVertex(matrix, x, y, 0.0F)
			      .setColor(ARGB.color(alpha, color))
			      .setUv(u, v)
			      .setOverlay(OverlayTexture.NO_OVERLAY)
			      .setLight(light)
			      .setNormal(matrix, 0.0F, 1.0F, 0.0F);
		}
	}
}
