package dev.pswg.particles.renderers;

import dev.pswg.particles.FragmentationGrenadeWaveParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.pswg.GalaxiesRenderLayers;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import java.util.List;

public class FragmentationGrenadeWaveParticleRenderer extends ParticleGroup<FragmentationGrenadeWaveParticle>
{
	public FragmentationGrenadeWaveParticleRenderer(ParticleEngine particleManager)
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

	record State(PoseStack matrices, TextureAtlasSprite sprite, float xScale, float yScale, float scale, float alpha)
	{
		public static State create(FragmentationGrenadeWaveParticle particle, Camera camera, float tickProgress)
		{
			PoseStack matrixStack = new PoseStack();
			matrixStack.pushPose();
			matrixStack.translate(particle.getPos().subtract(camera.getPosition()));
			matrixStack.mulPose(camera.rotation());

			return new State(matrixStack, particle.getSprite(), particle.getScaleX(), particle.getScaleY(), particle.getQuadSize(tickProgress), particle.getAlpha());
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
					float xSize = state.xScale * state.scale;
					float ySize = state.yScale * state.scale;
					int alpha = (int)(state.alpha * 255);
					vertex(vertexConsumer, matricesEntry, 255, alpha, -0.5F * xSize, -0.5F * ySize, state.sprite.getU0(), state.sprite.getV1());
					vertex(vertexConsumer, matricesEntry, 255, alpha, 0.5F * xSize, -0.5F * ySize, state.sprite.getU1(), state.sprite.getV1());
					vertex(vertexConsumer, matricesEntry, 255, alpha, 0.5F * xSize, 0.5F * ySize, state.sprite.getU1(), state.sprite.getV0());
					vertex(vertexConsumer, matricesEntry, 255, alpha, -0.5F * xSize, 0.5F * ySize, state.sprite.getU0(), state.sprite.getV0());
				});
			}
		}

		private static void vertex(VertexConsumer buffer, PoseStack.Pose matrix, int light, int alpha, float x, float y, float u, float v)
		{
			buffer.addVertex(matrix, x, y, 0.0F)
			      .setColor(ARGB.color(alpha, CommonColors.WHITE))
			      .setUv(u, v)
			      .setOverlay(OverlayTexture.NO_OVERLAY)
			      .setLight(light)
			      .setNormal(matrix, 0.0F, 1.0F, 0.0F);
		}
	}
}
