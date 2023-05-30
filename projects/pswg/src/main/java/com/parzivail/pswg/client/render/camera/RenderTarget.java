package com.parzivail.pswg.client.render.camera;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class RenderTarget
{
	public static final SimpleFramebuffer FRAMEBUFFER = new SimpleFramebuffer(512, 512, true, MinecraftClient.IS_SYSTEM_MAC);
	public static final Camera CAMERA = new Camera();
	public static final MutableCameraEntity CAMERA_ENTITY = new MutableCameraEntity();

	public static void capture(float tickDelta, long startTime, boolean tick)
	{
		if (!FabricLoader.getInstance().isDevelopmentEnvironment())
			return;

		var client = MinecraftClient.getInstance();

		if (client.player == null || client.player.age < 10)
			return;

		int windowFboWidth = client.getWindow().getFramebufferWidth();
		int windowFboHeight = client.getWindow().getFramebufferHeight();

		client.getWindow().setFramebufferWidth(FRAMEBUFFER.textureWidth);
		client.getWindow().setFramebufferHeight(FRAMEBUFFER.textureHeight);

		var mode = client.options.getGraphicsMode().getValue();
		var perspective = client.options.getPerspective();
		client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
		client.options.setPerspective(Perspective.FIRST_PERSON);

		FRAMEBUFFER.beginWrite(true);

		RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

		CAMERA.update(client.world, CAMERA_ENTITY.with(client.world, new Vec3d(-153, 67, 122), new Quaternionf(), true), false, false, tickDelta);
		CAMERA.updateEyeHeight();

		var matrices = new MatrixStack();

		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0)); // pitch
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(client.player.age + tickDelta)); // yaw

		var projMat = client.gameRenderer.getBasicProjectionMatrix(90);
		RenderSystem.setProjectionMatrix(projMat, VertexSorter.BY_Z);

		client.worldRenderer.setupFrustum(matrices, CAMERA.getPos(), projMat);
		client.worldRenderer.render(matrices, tickDelta, 0, false, CAMERA, client.gameRenderer, client.gameRenderer.getLightmapTextureManager(), projMat);

		client.options.setPerspective(perspective);
		client.options.getGraphicsMode().setValue(mode);
		client.getWindow().setFramebufferWidth(windowFboWidth);
		client.getWindow().setFramebufferHeight(windowFboHeight);
		client.getFramebuffer().beginWrite(true);
	}
}
