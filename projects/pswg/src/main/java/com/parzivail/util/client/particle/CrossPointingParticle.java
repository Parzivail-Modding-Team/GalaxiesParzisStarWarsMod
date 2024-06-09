package com.parzivail.util.client.particle;

import com.parzivail.util.math.QuatUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
		var f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.x);
		var g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.y);
		var h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.z);

		Quaternionf rotation = QuatUtil.lookAt(Vec3d.ZERO, new Vec3d(velocityX, velocityY, velocityZ));
		rotation.mul(QuatUtil.ROT_Y_POS90);
		rotation.mul(QuatUtil.ROT_X_POS45);

		var corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F),
				new Vector3f(-1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, -1.0F, 0.0F)
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
			vec3f2.mul(j);
			vec3f2.add(f, g, h);
		}

		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);

		vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);

		corners = new Vector3f[] {
				new Vector3f(-1.0F, 0.0F, 1.0F),
				new Vector3f(-1.0F, 0.0F, -1.0F),
				new Vector3f(1.0F, 0.0F, -1.0F),
				new Vector3f(1.0F, 0.0F, 1.0F)
		};

		for (var k = 0; k < 4; ++k)
		{
			var vec3f2 = corners[k];
			vec3f2.rotate(rotation);
			vec3f2.mul(j);
			vec3f2.add(f, g, h);
		}

		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);

		vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);
	}
}
