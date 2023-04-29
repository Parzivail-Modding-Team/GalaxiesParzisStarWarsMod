package com.parzivail.pswg.features.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;

public class DebugUtil
{
	private static final class DebugPoint
	{
		public final Vec3d position;
		public final float size;
		public final float r;
		public final float g;
		public final float b;

		public int age;

		private DebugPoint(Vec3d position, float size, float r, float g, float b)
		{
			this.position = position;
			this.size = size;
			this.r = r;
			this.g = g;
			this.b = b;
			this.age = 1;
		}
	}

	private static final HashMap<String, DebugPoint> framePoints = new HashMap<>();
	private static final HashMap<String, DebugPoint> tickPoints = new HashMap<>();

	public static void tickPoint(String id, Vec3d position, float r, float g, float b)
	{
		if (RenderSystem.isOnRenderThread())
			tickPoints.put(id, new DebugPoint(position, 0.03f, r, g, b));
		else
			RenderSystem.recordRenderCall(() -> tickPoints.put(id, new DebugPoint(position, 0.03f, r, g, b)));
	}

	public static void framePoint(String id, Vec3d position, float r, float g, float b)
	{
		if (RenderSystem.isOnRenderThread())
			framePoints.put(id, new DebugPoint(position, 0.03f, r, g, b));
		else
			RenderSystem.recordRenderCall(() -> framePoints.put(id, new DebugPoint(position, 0.03f, r, g, b)));
	}

	public static void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ)
	{
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d vec3d = camera.getPos().negate();

		if (camera.isReady())
		{
			for (var point : framePoints.values())
			{
				Box box = new Box(point.position, point.position).offset(vec3d).expand(point.size);
				DebugRenderer.drawBox(matrices, vertexConsumers, box, point.r, point.g, point.b, 1);
			}

			for (var point : tickPoints.values())
			{
				Box box = new Box(point.position, point.position).offset(vec3d).expand(point.size);
				DebugRenderer.drawBox(matrices, vertexConsumers, box, point.r, point.g, point.b, 1);
			}
		}

		ageAndDestroy(framePoints);
	}

	public static void tick(MinecraftClient client)
	{
		if (RenderSystem.isOnRenderThread())
			ageAndDestroy(tickPoints);
		else
			RenderSystem.recordRenderCall(() -> ageAndDestroy(tickPoints));
	}

	private static void ageAndDestroy(HashMap<String, DebugPoint> points)
	{
		var removal = new HashSet<String>();

		// One unit-time grace period to prevent flicker
		for (var entry : points.entrySet())
		{
			entry.getValue().age--;
			if (entry.getValue().age < 0)
				removal.add(entry.getKey());
		}

		for (var key : removal)
			points.remove(key);
	}
}
