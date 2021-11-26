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
		var vec3d = client.world.getSkyColor(client.gameRenderer.getCamera().getPos(), tickDelta);
		var g = (float)vec3d.x;
		var h = (float)vec3d.y;
		var i = (float)vec3d.z;
		BackgroundRenderer.setFogBlack();
		var bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.depthMask(false);
		RenderSystem.setShaderColor(g, h, i, 1.0F);
		var shader = RenderSystem.getShader();
		lightSkyBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		var fs = client.world.getDimensionEffects().getFogColorOverride(client.world.getSkyAngle(tickDelta), tickDelta);
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
			var k = fs[0];
			t = fs[1];
			var m = fs[2];
			var matrix4f2 = matrices.peek().getPositionMatrix();
			bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix4f2, 0.0F, 100.0F, 0.0F).color(k, t, m, fs[3]).next();

			for (var o = 0; o <= 16; ++o)
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

		var fractionalDay = client.world.getLevelProperties().getTimeOfDay() / 24000f;
		var fractionalWeek = fractionalDay / 7;

		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(5 * MathHelper.sin(fractionalWeek)));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(8 * MathHelper.cos(fractionalWeek)));
		var matrix4f3 = matrices.peek().getPositionMatrix();

		t = 23 + 10 * MathHelper.sin(fractionalWeek);
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

		t = 30 + 10 * MathHelper.sin((float)(fractionalWeek + Math.PI));
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(5 * MathHelper.sin((float)(fractionalWeek + Math.PI))));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(8 * MathHelper.cos((float)(fractionalWeek + Math.PI))));
		var offsetMat = matrices.peek().getPositionMatrix();
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
		var u = client.world.getMoonPhase();
		var v = u % 4;
		var w = u / 4 % 2;
		var x = (float)(v) / 4.0F;
		p = (float)(w) / 2.0F;
		q = (float)(v + 1) / 4.0F;
		r = (float)(w + 1) / 2.0F;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-7));
		matrix4f3 = matrices.peek().getPositionMatrix();
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
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(fractionalDay * 0.1f * 360.0F));
		matrix4f3 = matrices.peek().getPositionMatrix();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, t).texture(q, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, t).texture(x, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, -t).texture(x, p).next();
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, -t).texture(q, p).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		t = 8;
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-9));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(13));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(fractionalDay * 0.2f * 360.0F));
		matrix4f3 = matrices.peek().getPositionMatrix();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, t).texture(q, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, t).texture(x, r).next();
		bufferBuilder.vertex(matrix4f3, t, -100.0F, -t).texture(x, p).next();
		bufferBuilder.vertex(matrix4f3, -t, -100.0F, -t).texture(q, p).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		matrices.pop();

		RenderSystem.disableTexture();
		var ab = client.world.method_23787(tickDelta) * s;
		if (ab > 0.0F)
		{
			RenderSystem.setShaderColor(ab, ab, ab, ab);
			BackgroundRenderer.clearFog();
			starsBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionShader());
			fogApplier.run();
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		matrices.pop();
		RenderSystem.disableTexture();
		RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
		var d = client.player.getCameraPosVec(tickDelta).y - client.world.getLevelProperties().getSkyDarknessHeight(client.world);
		if (d < 0.0D)
		{
			matrices.push();
			matrices.translate(0.0D, 12.0D, 0.0D);
			darkSkyBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
			matrices.pop();
		}

		if (client.world.getDimensionEffects().isAlternateSkyColor())
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
