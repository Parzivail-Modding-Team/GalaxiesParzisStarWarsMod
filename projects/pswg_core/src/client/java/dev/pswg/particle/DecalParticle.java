package dev.pswg.particle;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import dev.pswg.utility.math.MathUtil;
import dev.pswg.utility.QuatUtil;

@Environment(EnvType.CLIENT)
public class DecalParticle extends AnimatedParticle
{
	protected DecalParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider, 0.0F);
	}

	@Override
	public void tick()
	{
		if (this.age++ >= this.maxAge)
			this.markDead();

		this.updateSprite(this.spriteProvider);
		if (this.age > this.maxAge / 2)
			this.setAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);

		var normal = new Vec3d(velocityX, velocityY, velocityZ).normalize();
		var pos = new Vec3d(this.x, this.y, this.z);

		var hostBlockPos = new BlockPos(MathUtil.floorInt(pos.subtract(normal.multiply(0.1f))));
		if (world.isAir(hostBlockPos))
			this.markDead();
	}

	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		var vec3d = camera.getPos();
		var f = (float)(MathHelper.lerp(tickProgress, this.lastX, this.x) - vec3d.getX());
		var g = (float)(MathHelper.lerp(tickProgress, this.lastY, this.y) - vec3d.getY());
		var h = (float)(MathHelper.lerp(tickProgress, this.lastZ, this.z) - vec3d.getZ());

		// We're abusing the velocity component as a normal vector
		var normal = new Vec3d(velocityX, velocityY, velocityZ).normalize();

		Quaternionf rot = QuatUtil.lookAt(Vec3d.ZERO, normal);
		rot.rotateZ(rot.angle());

		var z = (1 - this.age / (float)this.maxAge) * 0.005f;

		var corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, z),
				new Vector3f(-1.0F, 1.0F, z),
				new Vector3f(1.0F, 1.0F, z),
				new Vector3f(1.0F, -1.0F, z)
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

		submittable.render(this.getRenderType(), corners[0].x, corners[0].y, corners[0].z, rotation.x, rotation.y, rotation.z, rotation.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));
		/*vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);*/
		//super.render(submittable, camera, rotation, tickProgress);
	}
}
