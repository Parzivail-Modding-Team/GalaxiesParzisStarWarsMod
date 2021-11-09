package com.parzivail.pswg.client.render;

import com.parzivail.pswg.entity.collision.CapsuleVolume;
import com.parzivail.pswg.entity.collision.ICollisionVolume;
import com.parzivail.pswg.entity.collision.IComplexEntityHitbox;
import com.parzivail.pswg.entity.collision.SweptTriangleVolume;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class HitboxHelper
{
	public static void render(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta)
	{
		var pos = entity.getPos();
		if (entity instanceof IComplexEntityHitbox iceh)
		{

			var hitbox = iceh.getCollision();

			var rotation = iceh.getRotation();

			for (var volume : hitbox)
			{
				renderVolume(matrices, vertices, pos, rotation, volume);
			}
		}
		else
		{
			var volume = CapsuleVolume.of(entity.getBoundingBox());
			renderVolume(matrices, vertices, pos, Quaternion.IDENTITY, volume);
		}
	}

	private static void renderVolume(MatrixStack matrices, VertexConsumer vertices, Vec3d entityPos, Quaternion rotation, ICollisionVolume volume)
	{
		if (volume instanceof CapsuleVolume capsule)
			renderVolume(matrices, vertices, entityPos, rotation, capsule);
		else if (volume instanceof SweptTriangleVolume triangle)
			renderVolume(matrices, vertices, entityPos, rotation, triangle);
	}

	private static void renderVolume(MatrixStack matrices, VertexConsumer vertices, Vec3d entityPos, Quaternion rotation, SweptTriangleVolume volume)
	{
		var a = volume.a().subtract(entityPos);
		var b = volume.b().subtract(entityPos);
		var c = volume.c().subtract(entityPos);

		line(vertices, matrices, a, b);
		line(vertices, matrices, a, c);
		line(vertices, matrices, b, c);
	}

	private static void renderVolume(MatrixStack matrices, VertexConsumer vertices, Vec3d entityPos, Quaternion rotation, CapsuleVolume volume)
	{
		var s = volume.start().subtract(entityPos);
		var e = volume.end().subtract(entityPos);

		var forward = e.subtract(s).normalize().multiply(volume.radius());
		var up = QuatUtil.rotate(new Vec3d(volume.radius(), 0, 0), rotation);
		var left = forward.crossProduct(up).normalize().multiply(volume.radius());

		var back = forward.multiply(-1);
		var down = up.multiply(-1);
		var right = left.multiply(-1);

		line(vertices, matrices, s, e);

		line(vertices, matrices, s.add(up), e.add(up));
		line(vertices, matrices, s.add(down), e.add(down));

		line(vertices, matrices, s.add(left), e.add(left));
		line(vertices, matrices, s.add(right), e.add(right));

		dome(matrices, vertices, e, forward, up, left, down, right);
		dome(matrices, vertices, s, back, up, left, down, right);
	}

	private static void dome(MatrixStack matrices, VertexConsumer vertices, Vec3d origin, Vec3d normal, Vec3d up, Vec3d left, Vec3d down, Vec3d right)
	{
		quarterCircle(vertices, matrices, origin, normal, up);
		quarterCircle(vertices, matrices, origin, normal, down);
		quarterCircle(vertices, matrices, origin, normal, left);
		quarterCircle(vertices, matrices, origin, normal, right);

		quarterCircle(vertices, matrices, origin, left, up);
		quarterCircle(vertices, matrices, origin, left, down);

		quarterCircle(vertices, matrices, origin, right, up);
		quarterCircle(vertices, matrices, origin, right, down);
	}

	private static void quarterCircle(VertexConsumer vertices, MatrixStack ms, Vec3d origin, Vec3d r1, Vec3d r2)
	{
		var center = origin.add(r1.add(r2).multiply(MathHelper.SQUARE_ROOT_OF_TWO / 2));
		line(vertices, ms, origin.add(r1), center);
		line(vertices, ms, center, origin.add(r2));
	}

	private static void line(VertexConsumer vertices, MatrixStack ms, Vec3d start, Vec3d end)
	{
		var d = start.subtract(end).normalize();

		var entry = ms.peek();
		vertices.vertex(entry.getModel(), (float)start.x, (float)start.y, (float)start.z).color(255, 255, 0, 255).normal(entry.getNormal(), (float)d.x, (float)d.y, (float)d.z).next();
		vertices.vertex(entry.getModel(), (float)end.x, (float)end.y, (float)end.z).color(255, 255, 0, 255).normal(entry.getNormal(), (float)d.x, (float)d.y, (float)d.z).next();
	}
}
