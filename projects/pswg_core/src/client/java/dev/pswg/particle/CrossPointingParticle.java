package dev.pswg.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import dev.pswg.utility.QuatUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CrossPointingParticle extends SimpleAnimatedParticle
{
	protected CrossPointingParticle(ClientLevel clientWorld, double x, double y, double z, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider, 0.0F);
	}

	@Override
	protected void extractRotatedQuad(QuadParticleRenderState submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		var vec3d = camera.getPosition();
		var f = (float)(Mth.lerp(tickProgress, this.xo, this.x) - vec3d.x);
		var g = (float)(Mth.lerp(tickProgress, this.yo, this.y) - vec3d.y);
		var h = (float)(Mth.lerp(tickProgress, this.zo, this.z) - vec3d.z);

		Quaternionf rot = QuatUtil.lookAt(Vec3.ZERO, new Vec3(xd, yd, zd));
		rot.mul(QuatUtil.ROT_Y_POS90);
		rot.mul(QuatUtil.ROT_X_POS45);

		var corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F),
				new Vector3f(-1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, -1.0F, 0.0F)
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
		rot = rot.rotateX((float)(Math.PI));
		submittable.add(this.getLayer(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getQuadSize(tickProgress), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightColor(tickProgress));

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
		submittable.add(this.getLayer(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getQuadSize(tickProgress), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightColor(tickProgress));
		rot = rot.rotateX((float)(Math.PI));
		submittable.add(this.getLayer(), x, y, z, rot.x, rot.y, rot.z, rot.w, this.getQuadSize(tickProgress), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightColor(tickProgress));
	}
}
