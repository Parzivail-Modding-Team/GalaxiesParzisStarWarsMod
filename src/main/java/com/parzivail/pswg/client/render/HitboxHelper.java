package com.parzivail.pswg.client.render;

import com.parzivail.pswg.entity.collision.CapsuleVolume;
import com.parzivail.pswg.entity.collision.IComplexEntityHitbox;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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

	private static void renderVolume(MatrixStack matrices, VertexConsumer vertices, Vec3d entityPos, Quaternion rotation, CapsuleVolume volume)
	{
		var s = volume.start().subtract(entityPos);
		var e = volume.end().subtract(entityPos);

		var forward = e.subtract(s).normalize().multiply(volume.radius());
		var up = QuatUtil.rotate(new Vec3d(volume.radius(), 0, 0), rotation);
		var left = forward.crossProduct(up).normalize().multiply(volume.radius());

		line(vertices, matrices, s, e);

		line(vertices, matrices, s.add(up), e.add(up));
		line(vertices, matrices, s.add(left), e.add(left));
		line(vertices, matrices, s.subtract(up), e.subtract(up));
		line(vertices, matrices, s.subtract(left), e.subtract(left));

		line(vertices, matrices, s.subtract(forward), s.add(left));
		line(vertices, matrices, s.subtract(forward), s.add(up));
		line(vertices, matrices, s.subtract(forward), s.subtract(left));
		line(vertices, matrices, s.subtract(forward), s.subtract(up));

		line(vertices, matrices, e.add(forward), e.add(left));
		line(vertices, matrices, e.add(forward), e.add(up));
		line(vertices, matrices, e.add(forward), e.subtract(left));
		line(vertices, matrices, e.add(forward), e.subtract(up));
	}

	private static void line(VertexConsumer vertices, MatrixStack ms, Vec3d start, Vec3d end)
	{
		var d = start.subtract(end).normalize();

		var entry = ms.peek();
		vertices.vertex(entry.getModel(), (float)start.x, (float)start.y, (float)start.z).color(255, 255, 0, 255).normal(entry.getNormal(), (float)d.x, (float)d.y, (float)d.z).next();
		vertices.vertex(entry.getModel(), (float)end.x, (float)end.y, (float)end.z).color(255, 255, 0, 255).normal(entry.getNormal(), (float)d.x, (float)d.y, (float)d.z).next();
	}
}
