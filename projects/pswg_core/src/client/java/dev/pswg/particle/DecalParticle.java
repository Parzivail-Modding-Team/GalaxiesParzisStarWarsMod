package dev.pswg.particle;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import dev.pswg.utility.math.MathUtil;
import dev.pswg.utility.QuatUtil;

@Environment(EnvType.CLIENT)
public class DecalParticle extends SimpleAnimatedParticle
{
	protected DecalParticle(ClientLevel clientWorld, double x, double y, double z, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider, 0.0F);
	}

	@Override
	public void tick()
	{
		if (this.age++ >= this.lifetime)
			this.remove();

		this.setSpriteFromAge(this.sprites);
		if (this.age > this.lifetime / 2)
			this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);

		var normal = new Vec3(xd, yd, zd).normalize();
		var pos = new Vec3(this.x, this.y, this.z);

		var hostBlockPos = new BlockPos(MathUtil.floorInt(pos.subtract(normal.scale(0.1f))));
		if (level.isEmptyBlock(hostBlockPos))
			this.remove();
	}

	@Override
	protected void extractRotatedQuad(QuadParticleRenderState submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		var vec3d = camera.getPosition();
		var f = (float)(Mth.lerp(tickProgress, this.xo, this.x) - vec3d.x());
		var g = (float)(Mth.lerp(tickProgress, this.yo, this.y) - vec3d.y());
		var h = (float)(Mth.lerp(tickProgress, this.zo, this.z) - vec3d.z());

		// We're abusing the velocity component as a normal vector
		var normal = new Vec3(xd, yd, zd).normalize();

		Quaternionf rot = QuatUtil.lookAt(Vec3.ZERO, normal);
		rot.rotateZ(rot.angle());

		var a = (1 - this.age / (float)this.lifetime) * 0.005f;

		var corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, a),
				new Vector3f(-1.0F, 1.0F, a),
				new Vector3f(1.0F, 1.0F, a),
				new Vector3f(1.0F, -1.0F, a)
		};

		var j = this.getQuadSize(tickProgress);

		var l = this.getU0();
		var m = this.getU1();
		var n = this.getV0();
		var o = this.getV1();
		var p = this.getLightColor(tickProgress);

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

		submittable.add(this.getLayer(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getQuadSize(tickProgress), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightColor(tickProgress));
		/*vertexConsumer.vertex(corners[3].x, corners[3].y, corners[3].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[2].x, corners[2].y, corners[2].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[1].x, corners[1].y, corners[1].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p);
		vertexConsumer.vertex(corners[0].x, corners[0].y, corners[0].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p);*/
		//super.render(submittable, camera, rotation, tickProgress);
	}
}
