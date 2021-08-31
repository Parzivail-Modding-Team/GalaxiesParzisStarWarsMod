package com.parzivail.util.client.particle;

import com.parzivail.pswg.util.QuatUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class CrossPointingParticle extends AnimatedParticle
{
	protected CrossPointingParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider, 0.0F);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		var vec3d = camera.getPos();
		var f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
		var g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
		var h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

		Quaternion rotation = QuatUtil.lookAt(Vec3d.ZERO, new Vec3d(velocityX, velocityY, velocityZ));
		rotation.hamiltonProduct(new Quaternion(Vec3f.POSITIVE_Y, 90, true));
		rotation.hamiltonProduct(new Quaternion(Vec3f.POSITIVE_X, 45, true));

		var corners = new Vec3f[] {
				new Vec3f(-1.0F, -1.0F, 0.0F),
				new Vec3f(-1.0F, 1.0F, 0.0F),
				new Vec3f(1.0F, 1.0F, 0.0F),
				new Vec3f(1.0F, -1.0F, 0.0F)
		};

		var j = this.getSize(tickDelta);

		var l = this.getMinU();
		var m = this.getMaxU();
		var n = this.getMinV();
		var o = this.getMaxV();
		var p = this.getBrightness(tickDelta);

		for (var k = 0; k < 4; ++k)
		{
			var vec3f2 = corners[k];
			vec3f2.rotate(rotation);
			vec3f2.scale(j);
			vec3f2.add(f, g, h);
		}

		vertexConsumer.vertex(corners[0].getX(), corners[0].getY(), corners[0].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[1].getX(), corners[1].getY(), corners[1].getZ()).texture(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[2].getX(), corners[2].getY(), corners[2].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[3].getX(), corners[3].getY(), corners[3].getZ()).texture(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();

		vertexConsumer.vertex(corners[3].getX(), corners[3].getY(), corners[3].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[2].getX(), corners[2].getY(), corners[2].getZ()).texture(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[1].getX(), corners[1].getY(), corners[1].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[0].getX(), corners[0].getY(), corners[0].getZ()).texture(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();

		corners = new Vec3f[] {
				new Vec3f(-1.0F, 0.0F, 1.0F),
				new Vec3f(-1.0F, 0.0F, -1.0F),
				new Vec3f(1.0F, 0.0F, -1.0F),
				new Vec3f(1.0F, 0.0F, 1.0F)
		};

		for (var k = 0; k < 4; ++k)
		{
			var vec3f2 = corners[k];
			vec3f2.rotate(rotation);
			vec3f2.scale(j);
			vec3f2.add(f, g, h);
		}

		vertexConsumer.vertex(corners[0].getX(), corners[0].getY(), corners[0].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[1].getX(), corners[1].getY(), corners[1].getZ()).texture(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[2].getX(), corners[2].getY(), corners[2].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[3].getX(), corners[3].getY(), corners[3].getZ()).texture(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();

		vertexConsumer.vertex(corners[3].getX(), corners[3].getY(), corners[3].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[2].getX(), corners[2].getY(), corners[2].getZ()).texture(m, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[1].getX(), corners[1].getY(), corners[1].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
		vertexConsumer.vertex(corners[0].getX(), corners[0].getY(), corners[0].getZ()).texture(l, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(p).next();
	}
}
