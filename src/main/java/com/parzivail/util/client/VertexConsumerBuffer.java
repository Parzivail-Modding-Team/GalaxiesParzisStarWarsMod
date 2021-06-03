package com.parzivail.util.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public enum VertexConsumerBuffer
{
	Instance;

	private VertexConsumer vertexConsumer;
	private MatrixStack.Entry matrices;
	private float r;
	private float g;
	private float b;
	private float a;
	private int overlay;
	private int light;

	public void init(VertexConsumer vertexConsumer, MatrixStack.Entry matrices, float r, float g, float b, float a, int overlay, int light)
	{
		this.vertexConsumer = vertexConsumer;
		this.matrices = matrices;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.overlay = overlay;
		this.light = light;
	}

	public void setVertexConsumer(VertexConsumer vertexConsumer)
	{
		this.vertexConsumer = vertexConsumer;
	}

	public void setMatrices(MatrixStack.Entry matrices)
	{
		this.matrices = matrices;
	}

	public void setColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setColor(int argb)
	{
		this.a = ((argb >> 24) & 0xFF) / 255f;
		this.r = ((argb >> 16) & 0xFF) / 255f;
		this.g = ((argb >> 8) & 0xFF) / 255f;
		this.b = (argb & 0xFF) / 255f;
	}

	public void setColor(int baseRgb, int a)
	{
		this.a = (a & 0xFF) / 255f;
		this.r = ((baseRgb >> 16) & 0xFF) / 255f;
		this.g = ((baseRgb >> 8) & 0xFF) / 255f;
		this.b = (baseRgb & 0xFF) / 255f;
	}

	public void setOverlay(int overlay)
	{
		this.overlay = overlay;
	}

	public void setLight(int light)
	{
		this.light = light;
	}

	public void vertex(Vec3f pos, Vec3f normal, float u, float v)
	{
		Vector4f pos4 = new Vector4f(pos);
		normal = normal.copy();

		pos4.transform(matrices.getModel());
		normal.transform(matrices.getNormal());

		vertexConsumer.vertex(pos4.getX(), pos4.getY(), pos4.getZ(), r, g, b, a, u, v, overlay, light, normal.getX(), normal.getY(), normal.getZ());
	}

	public void vertex(float x, float y, float z, float nx, float ny, float nz, float u, float v)
	{
		vertex(new Vec3f(x, y, z), new Vec3f(nx, ny, nz), u, v);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		Vec3f start = new Vec3f(x1, y1, z1);
		Vec3f end = new Vec3f(x2, y2, z2);
		Vec3f normal = new Vec3f(x2, y2, z2);
		normal.subtract(start);
		normal.normalize();

		vertex(start, normal,0, 0);
		vertex(end, normal,0, 0);
	}

	public void line(Vec3d start, Vec3d end)
	{
		Vec3d normal = new Vec3d(end.x, end.y, end.z).subtract(start).normalize();

		vertex((float)start.x, (float)start.y, (float)start.z, (float)normal.x, (float)normal.y, (float)normal.z,0, 0);
		vertex((float)end.x, (float)end.y, (float)end.z, (float)normal.x, (float)normal.y, (float)normal.z,0, 0);
	}
}
