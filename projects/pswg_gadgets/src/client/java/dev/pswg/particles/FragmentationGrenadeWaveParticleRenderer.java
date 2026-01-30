package dev.pswg.particles;

import dev.pswg.GalaxiesRenderLayers;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class FragmentationGrenadeWaveParticleRenderer extends ParticleRenderer<FragmentationGrenadeWaveParticle>
{
	public FragmentationGrenadeWaveParticleRenderer(ParticleManager particleManager)
	{
		super(particleManager);
	}

	@Override
	public Submittable render(Frustum frustum, Camera camera, float tickProgress)
	{
		return new FragmentationGrenadeWaveParticleRenderer.Result(
				this.particles.stream().map(particle -> FragmentationGrenadeWaveParticleRenderer.State.create(particle, camera, tickProgress)).toList()
		);
	}

	record State(MatrixStack matrices, Sprite sprite, float xScale, float yScale, float scale, float alpha)
	{
		public static FragmentationGrenadeWaveParticleRenderer.State create(FragmentationGrenadeWaveParticle particle, Camera camera, float tickProgress)
		{
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.translate(particle.getPos().subtract(camera.getPos()));
			matrixStack.multiply(camera.getRotation());

			return new FragmentationGrenadeWaveParticleRenderer.State(matrixStack, particle.getSprite(), particle.getScaleX(), particle.getScaleY(), particle.getSize(tickProgress), particle.getAlpha());
		}
	}

	record Result(List<FragmentationGrenadeWaveParticleRenderer.State> states) implements Submittable
	{
		@Override
		public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState)
		{
			for (FragmentationGrenadeWaveParticleRenderer.State state : this.states)
			{
				var matrix = state.matrices;
				orderedRenderCommandQueue.submitCustom(matrix, GalaxiesRenderLayers.GALAXIES_TRANSLUCENT, (matricesEntry, vertexConsumer) -> {
					float xSize = state.xScale * state.scale;
					float ySize = state.yScale * state.scale;
					int alpha = (int)(state.alpha * 255);
					vertex(vertexConsumer, matricesEntry, 255, alpha, -0.5F * xSize, -0.5F * ySize, state.sprite.getMinU(), state.sprite.getMaxV());
					vertex(vertexConsumer, matricesEntry, 255, alpha, 0.5F * xSize, -0.5F * ySize, state.sprite.getMaxU(), state.sprite.getMaxV());
					vertex(vertexConsumer, matricesEntry, 255, alpha, 0.5F * xSize, 0.5F * ySize, state.sprite.getMaxU(), state.sprite.getMinV());
					vertex(vertexConsumer, matricesEntry, 255, alpha, -0.5F * xSize, 0.5F * ySize, state.sprite.getMinU(), state.sprite.getMinV());
				});
			}
		}

		private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, int alpha, float x, float y, float u, float v)
		{
			buffer.vertex(matrix, x, y, 0.0F)
			      .color(ColorHelper.withAlpha(alpha, Colors.WHITE))
			      .texture(u, v)
			      .overlay(OverlayTexture.DEFAULT_UV)
			      .light(light)
			      .normal(matrix, 0.0F, 1.0F, 0.0F);
		}
	}
}
