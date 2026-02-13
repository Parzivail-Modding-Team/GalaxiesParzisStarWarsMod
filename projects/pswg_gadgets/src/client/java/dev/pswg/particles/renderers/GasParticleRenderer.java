package dev.pswg.particles.renderers;

import dev.pswg.Gadgets;
import dev.pswg.GalaxiesRenderLayers;
import dev.pswg.particles.GasParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class GasParticleRenderer extends ParticleRenderer<GasParticle>
{
	public GasParticleRenderer(ParticleManager particleManager)
	{
		super(particleManager);
	}

	@Override
	public Submittable render(Frustum frustum, Camera camera, float tickProgress)
	{
		return new Result(
				this.particles.stream().map(particle -> State.create(particle, camera, tickProgress)).toList()
		);
	}

	record State(MatrixStack matrices, Sprite sprite, float billowing, float alpha, float scale, int color, int light)
	{
		public static State create(GasParticle particle, Camera camera, float tickProgress)
		{
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.translate(particle.getPos().subtract(camera.getPos()));
			matrixStack.multiply(camera.getRotation().rotateZ((float)Math.toRadians(particle.getBillowing())));

			return new State(matrixStack, particle.getSprite(), particle.getBillowing(), particle.getAlpha(), particle.getSize(tickProgress), particle.getColor(), particle.getBrightness(tickProgress));
		}
	}

	record Result(List<State> states) implements Submittable
	{
		@Override
		public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState)
		{
			for (State state : this.states)
			{
				var matrix = state.matrices;
				orderedRenderCommandQueue.submitCustom(matrix, GalaxiesRenderLayers.GALAXIES_TRANSLUCENT, (matricesEntry, vertexConsumer) -> {
					float scale = state.scale;
					int alpha = (int)(state.alpha * 255);
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, -1F * scale, -1F * scale, state.sprite.getMinU(), state.sprite.getMaxV());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, 1F * scale, -1F * scale, state.sprite.getMaxU(), state.sprite.getMaxV());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, 1F * scale, 1F * scale, state.sprite.getMaxU(), state.sprite.getMinV());
					vertex(vertexConsumer, matricesEntry, state.light, alpha, state.color, -1F * scale, 1F * scale, state.sprite.getMinU(), state.sprite.getMinV());
				});
			}
		}

		private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, int alpha, int color, float x, float y, float u, float v)
		{
			buffer.vertex(matrix, x, y, 0.0F)
			      .color(ColorHelper.withAlpha(alpha, color))
			      .texture(u, v)
			      .overlay(OverlayTexture.DEFAULT_UV)
			      .light(light)
			      .normal(matrix, 0.0F, 1.0F, 0.0F);
		}
	}
}
