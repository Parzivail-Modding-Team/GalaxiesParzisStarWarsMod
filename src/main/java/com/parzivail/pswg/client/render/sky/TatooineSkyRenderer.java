package com.parzivail.pswg.client.render.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.client.render.ICustomSkyRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TatooineSkyRenderer implements ICustomSkyRenderer
{
	private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier SUN = new Identifier("textures/environment/sun.png");

	@Override
	public void render(MinecraftClient client, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Runnable fogApplier, CallbackInfo ci)
	{
		RenderSystem.disableTexture();
		Vec3d vec3d = client.world.method_23777(client.gameRenderer.getCamera().getPos(), tickDelta);
		float g = (float)vec3d.x;
		float h = (float)vec3d.y;
		float i = (float)vec3d.z;
		BackgroundRenderer.setFogBlack();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.depthMask(false);
		RenderSystem.setShaderColor(g, h, i, 1.0F);
		Shader shader = RenderSystem.getShader();
		lightSkyBuffer.setShader(matrices.peek().getModel(), projectionMatrix, shader);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		float[] fs = client.world.getSkyProperties().getFogColorOverride(client.world.getSkyAngle(tickDelta), tickDelta);
		float s;
		float t;
		float p;
		float q;
		float r;
		if (fs != null)
		{
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.disableTexture();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.push();
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			s = MathHelper.sin(client.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(s));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			float k = fs[0];
			t = fs[1];
			float m = fs[2];
			Matrix4f matrix4f2 = matrices.peek().getModel();
			bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix4f2, 0.0F, 100.0F, 0.0F).color(k, t, m, fs[3]).next();

			for (int o = 0; o <= 16; ++o)
			{
				p = (float)o * 6.2831855F / 16.0F;
				q = MathHelper.sin(p);
				r = MathHelper.cos(p);
				bufferBuilder.vertex(matrix4f2, q * 120.0F, r * 120.0F, -r * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
			}

			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			matrices.pop();
		}

		RenderSystem.enableTexture();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		matrices.push();
		s = 1.0F - client.world.getRainGradient(tickDelta);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(client.world.getSkyAngle(tickDelta) * 360.0F));

		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(5));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(5));
		Matrix4f matrix4f3 = matrices.peek().getModel();
		t = 23;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SUN);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, 100.0F, -t).texture(0.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f3, t, 100.0F, -t).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f3, t, 100.0F, t).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(matrix4f3, -t, 100.0F, t).texture(0.0F, 1.0F).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		t = 30;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-5));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-5));
		Matrix4f offsetMat = matrices.peek().getModel();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SUN);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(offsetMat, -t, 100.0F, -t).texture(0.0F, 0.0F).next();
		bufferBuilder.vertex(offsetMat, t, 100.0F, -t).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(offsetMat, t, 100.0F, t).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(offsetMat, -t, 100.0F, t).texture(0.0F, 1.0F).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		t = 40;
		RenderSystem.setShaderTexture(0, MOON_PHASES);
		int u = client.world.getMoonPhase();
		int v = u % 4;
		int w = u / 4 % 2;
		float x = (float)(v + 0) / 4.0F;
		p = (float)(w + 0) / 2.0F;
		q = (float)(v + 1) / 4.0F;
		r = (float)(w + 1) / 2.0F;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-7));
		matrix4f3 = matrices.peek().getModel();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, t).texture(q, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, t).texture(x, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, -t).texture(x, p).next();
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, -t).texture(q, p).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		t = 14;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-7));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(8));
		matrix4f3 = matrices.peek().getModel();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, t).texture(q, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, t).texture(x, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, -t).texture(x, p).next();
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, -t).texture(q, p).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		// TODO: have these moons rotate independently

		t = 8;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-9));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(13));
		matrix4f3 = matrices.peek().getModel();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, t).texture(q, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, t).texture(x, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, -t).texture(x, p).next();
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, -t).texture(q, p).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		RenderSystem.disableTexture();
		float ab = client.world.method_23787(tickDelta) * s;
		if (ab > 0.0F)
		{
			RenderSystem.setShaderColor(ab, ab, ab, ab);
			BackgroundRenderer.method_23792();
			starsBuffer.setShader(matrices.peek().getModel(), projectionMatrix, GameRenderer.getPositionShader());
			fogApplier.run();
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		matrices.pop();
		RenderSystem.disableTexture();
		RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
		double d = client.player.getCameraPosVec(tickDelta).y - client.world.getLevelProperties().getSkyDarknessHeight(client.world);
		if (d < 0.0D)
		{
			matrices.push();
			matrices.translate(0.0D, 12.0D, 0.0D);
			darkSkyBuffer.setShader(matrices.peek().getModel(), projectionMatrix, shader);
			matrices.pop();
		}

		if (client.world.getSkyProperties().isAlternateSkyColor())
		{
			RenderSystem.setShaderColor(g * 0.2F + 0.04F, h * 0.2F + 0.04F, i * 0.6F + 0.1F, 1.0F);
		}
		else
		{
			RenderSystem.setShaderColor(g, h, i, 1.0F);
		}

		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
	}
}
