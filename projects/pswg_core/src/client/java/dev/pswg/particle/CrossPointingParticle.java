package dev.pswg.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dev.pswg.utility.QuatUtil;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
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
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		var vec3d = camera.getPos();
		var f = (float)(MathHelper.lerp(tickProgress, this.lastX, this.x) - vec3d.x);
		var g = (float)(MathHelper.lerp(tickProgress, this.lastY, this.y) - vec3d.y);
		var h = (float)(MathHelper.lerp(tickProgress, this.lastZ, this.z) - vec3d.z);

		Quaternionf rot = QuatUtil.lookAt(Vec3d.ZERO, new Vec3d(velocityX, velocityY, velocityZ));
		rot.mul(QuatUtil.ROT_Y_POS90);
		rot.mul(QuatUtil.ROT_X_POS45);

		var corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F),
				new Vector3f(-1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, -1.0F, 0.0F)
		};

		var j = this.getSize(tickProgress);

		var l = this.getMinU();
		var m = this.getMaxU();
		var n = this.getMinV();
		var o = this.getMaxV();
		var p = this.getBrightness(tickProgress);

		for (var k = 0; k < 4; ++k)
		{
			var vec3f2 = corners[k];
			vec3f2.rotate(rot);
			vec3f2.mul(j);
			vec3f2.add(f, g, h);
		}
		float x = (corners[0].x + corners[1].x + corners[2].x + corners[3].x) / 4f;
		float y = (corners[0].y + corners[1].y + corners[2].y + corners[3].y) / 4f;
		float z = (corners[0].z + corners[1].z + corners[2].z + corners[3].z) / 4f;
		submittable.render(this.getRenderType(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));
		rot = rot.rotateX((float)(Math.PI));
		submittable.render(this.getRenderType(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));

		corners = new Vector3f[] {
				new Vector3f(-1.0F, 0.0F, 1.0F),
				new Vector3f(-1.0F, 0.0F, -1.0F),
				new Vector3f(1.0F, 0.0F, -1.0F),
				new Vector3f(1.0F, 0.0F, 1.0F)
		};

		for (var k = 0; k < 4; ++k)
		{
			var vec3f2 = corners[k];
			vec3f2.rotate(rot);
			vec3f2.mul(j);
			vec3f2.add(f, g, h);
		}
		x = (corners[0].x + corners[1].x + corners[2].x + corners[3].x) / 4f;
		y = (corners[0].y + corners[1].y + corners[2].y + corners[3].y) / 4f;
		z = (corners[0].z + corners[1].z + corners[2].z + corners[3].z) / 4f;
		rot = rot.rotateX((float)(Math.PI / 2f));
		submittable.render(this.getRenderType(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));
		rot = rot.rotateX((float)(Math.PI));
		submittable.render(this.getRenderType(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));
	}
}
