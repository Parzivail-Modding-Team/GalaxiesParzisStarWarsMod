package com.parzivail.pswg.client.render.cable;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.MutableBox;
import com.parzivail.util.math.Pose3f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Consumer;

/*
	Based on: https://github.com/FoundationGames/Phonos
 */
public class CableRenderer
{
	public static double cableLODNearDetail = 1;
	public static double cableLODFarDetail = 0.25;

	private static final MutableBox bounds = new MutableBox();

	private static final Quaternionf rotationCache = new Quaternionf();
	private static final BlockPos.Mutable lightPos = new BlockPos.Mutable();

	private static final Vector3f cableStPt = new Vector3f();
	private static final Vector3f cableEnPt = new Vector3f();
	private static final Vector3f cableRotAxis = new Vector3f();

	private static final Vector4f[] cableStart = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };
	private static final Vector4f[] cableEnd = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };

	private static final Vector4f[] currCableStart = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };
	private static final Vector4f[] currCableEnd = new Vector4f[] { new Vector4f(), new Vector4f(), new Vector4f(), new Vector4f() };

	private static final Vector3f[] cableNormal = new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };

	private static void loadCableEnd(Vector4f[] end)
	{
		end[0].set(-0.0442, 0, 0, 1);
		end[1].set(0, -0.0442, 0, 1);
		end[2].set(0.0442, 0, 0, 1);
		end[3].set(0, 0.0442, 0, 1);
	}

	private static void transformCableEnd(Vector4f[] end, Consumer<Vector4f> tfm)
	{
		tfm.accept(end[0]);
		tfm.accept(end[1]);
		tfm.accept(end[2]);
		tfm.accept(end[3]);
	}

	private static void transformCableNormal(Vector3f[] nml, Consumer<Vector3f> tfm)
	{
		tfm.accept(nml[0]);
		tfm.accept(nml[1]);
		tfm.accept(nml[2]);
		tfm.accept(nml[3]);
	}

	private static void lerpCableEnd(Vector4f[] out, Vector4f[] begin, Vector4f[] end, float delta)
	{
		for (int i = 0; i < 4; i++)
		{
			out[i].set(MathHelper.lerp(delta, begin[i].x, end[i].x),
			           MathHelper.lerp(delta, begin[i].y, end[i].y),
			           MathHelper.lerp(delta, begin[i].z, end[i].z), 1);
		}
	}

	public static void renderConnection(long seed, World world, CableSocket start, CableSocket end, Frustum frustum, MatrixStack matrices, VertexConsumer immediate, int overlay, float tickDelta)
	{
		int startLight, endLight;

		// Connection plug points are always rendered immediate
		matrices.push();
		transformConnPoint(world, start, matrices, cableStPt, tickDelta);
		lightPos.set(cableStPt.x, cableStPt.y, cableStPt.z);
		startLight = WorldRenderer.getLightmapCoordinates(world, lightPos);
		matrices.pop();

		matrices.push();
		transformConnPoint(world, end, matrices, cableEnPt, tickDelta);
		lightPos.set(cableEnPt.x, cableEnPt.y, cableEnPt.z);
		endLight = WorldRenderer.getLightmapCoordinates(world, lightPos);
		matrices.pop();
		// ---

		float length = cableStPt.distance(cableEnPt);
		double detail = cableLODNearDetail;
		int segments = Math.max((int)Math.ceil(4 * length * detail), 1);

		bounds.fitTwo(cableStPt.x, cableStPt.y, cableStPt.z, cableEnPt.x, cableEnPt.y, cableEnPt.z);
		if (!bounds.visible(frustum))
			return;

		float cx = (cableStPt.x + cableEnPt.x) * 0.5f;
		float cy = (cableStPt.y + cableEnPt.y) * 0.5f;
		float cz = (cableStPt.z + cableEnPt.z) * 0.5f;

		double sqDist = MinecraftClient.getInstance().gameRenderer.getCamera().getPos()
		                                                          .squaredDistanceTo(cx, cy, cz);
		double delta = MathHelper.clamp(sqDist / (length * length * 12), 0, 1);
		detail = MathHelper.lerp(delta, cableLODNearDetail, cableLODFarDetail);

		segments = Math.max((int)Math.ceil(4 * length * detail), Math.min(3, segments));

		buildCableGeometry(seed, world, matrices, immediate, segments, length, detail, startLight, endLight, overlay, tickDelta);
	}

	private static void buildCableGeometry(long seed, World world, MatrixStack matrices, VertexConsumer buffer, int segments, float length, double detail, int startLight, int endLight, int overlay, float tickDelta)
	{
		matrices.push();

		float vOffset = 0;
		float r = 0.2f;
		float g = 0.2f;
		float b = 0.2f;

		final float texUWid = (float)(0.25 / detail);

		cableRotAxis.set(cableEnPt.z - cableStPt.z, 0, cableEnPt.x - cableStPt.x);

		float cablePitch = (float)Math.atan2(cableEnPt.y - cableStPt.y, cableRotAxis.length());
		float cableYaw = (float)Math.atan2(cableRotAxis.z, cableRotAxis.x);
		cableRotAxis.normalize();

		loadCableEnd(cableStart);
		loadCableEnd(cableEnd);

		cableNormal[0].set(-MathUtil.SQRT2DIV2, -MathUtil.SQRT2DIV2, 0);
		cableNormal[1].set(MathUtil.SQRT2DIV2, -MathUtil.SQRT2DIV2, 0);
		cableNormal[2].set(MathUtil.SQRT2DIV2, MathUtil.SQRT2DIV2, 0);
		cableNormal[3].set(-MathUtil.SQRT2DIV2, MathUtil.SQRT2DIV2, 0);

		rotationCache.setAngleAxis(cablePitch, 1, 0, 0);
		transformCableEnd(cableStart, vec -> vec.rotate(rotationCache));
		transformCableEnd(cableEnd, vec -> vec.rotate(rotationCache));
		transformCableNormal(cableNormal, vec -> vec.rotate(rotationCache));

		rotationCache.setAngleAxis(Math.PI + cableYaw, 0, 1, 0);
		transformCableEnd(cableStart, vec -> vec.rotate(rotationCache));
		transformCableEnd(cableEnd, vec -> vec.rotate(rotationCache));
		transformCableNormal(cableNormal, vec -> vec.rotate(rotationCache));

		transformCableEnd(cableStart, vec -> vec.set(vec.x + cableStPt.x, vec.y + cableStPt.y, vec.z + cableStPt.z, 1));
		transformCableEnd(cableEnd, vec -> vec.set(vec.x + cableEnPt.x, vec.y + cableEnPt.y, vec.z + cableEnPt.z, 1));

		transformCableNormal(cableNormal, matrices.peek().getNormalMatrix()::transform);

		var time = world.getTime();
		time += seed;
		time %= 100;
		var timeRads = (time + tickDelta) / 50f * MathHelper.PI;

		for (int s = 0; s < segments; s++)
		{
			float startDelta = (float)s / segments;
			float endDelta = (float)(s + 1) / segments;
			float startYOffset = length * 0.35f * (0.25f - (float)Math.pow(startDelta - 0.5, 2));
			float endYOffset = length * 0.35f * (0.25f - (float)Math.pow(endDelta - 0.5, 2));

			var startXOffset = 0.2f * MathHelper.sin(timeRads) * (0.25f - (float)Math.pow(startDelta - 0.5, 2));
			var endXOffset = 0.2f * MathHelper.sin(timeRads) * (0.25f - (float)Math.pow(endDelta - 0.5, 2));

			int segStartLight = MathUtil.lerpLight(startDelta, startLight, endLight);
			int segEndLight = MathUtil.lerpLight(endDelta, startLight, endLight);

			lerpCableEnd(currCableStart, cableStart, cableEnd, startDelta);
			lerpCableEnd(currCableEnd, cableStart, cableEnd, endDelta);

			transformCableEnd(currCableStart, vec -> vec.add(-cableRotAxis.x * startXOffset, -startYOffset, cableRotAxis.z * startXOffset, 0));
			transformCableEnd(currCableEnd, vec -> vec.add(-cableRotAxis.x * endXOffset, -endYOffset, cableRotAxis.z * endXOffset, 0));

			transformCableEnd(currCableStart, matrices.peek().getPositionMatrix()::transform);
			transformCableEnd(currCableEnd, matrices.peek().getPositionMatrix()::transform);

			for (int i = 0; i < 4; i++)
			{
				float vOffset2 = i % 2 == 0 ? vOffset + 0.0625f : vOffset;
				int next = (i + 1) % 4;
				var nml = cableNormal[i];

				buffer.vertex(currCableStart[i].x, currCableStart[i].y, currCableStart[i].z).color(r, g, b, 1)
				      .texture(texUWid, 0.4375f + vOffset2).overlay(overlay).light(segStartLight).normal(nml.x, nml.y, nml.z).next();
				buffer.vertex(currCableEnd[i].x, currCableEnd[i].y, currCableEnd[i].z).color(r, g, b, 1)
				      .texture(0, 0.4375f + vOffset2).overlay(overlay).light(segEndLight).normal(nml.x, nml.y, nml.z).next();
				buffer.vertex(currCableEnd[next].x, currCableEnd[next].y, currCableEnd[next].z).color(r, g, b, 1)
				      .texture(0, 0.5f + vOffset2).overlay(overlay).light(segEndLight).normal(nml.x, nml.y, nml.z).next();
				buffer.vertex(currCableStart[next].x, currCableStart[next].y, currCableStart[next].z).color(r, g, b, 1)
				      .texture(texUWid, 0.5f + vOffset2).overlay(overlay).light(segStartLight).normal(nml.x, nml.y, nml.z).next();
			}
		}

		matrices.pop();
	}

	private static final Pose3f tcp_originPose = new Pose3f(new Vector3f(), new Quaternionf());
	private static final Pose3f tcp_endPose = new Pose3f(new Vector3f(), new Quaternionf());
	private static final Quaternionf tcp_rot = new Quaternionf();
	private static final Vector4f tcp_vec4a = new Vector4f();

	private static void transformConnPoint(World world, CableSocket point, MatrixStack matrices, Vector3f connPos, float tickDelta)
	{
		connPos.set(0, 0, 0);

		point.writeOriginPose(world, tickDelta, tcp_originPose);
		point.writePlugPose(world, tickDelta, tcp_endPose);

		matrices.translate(tcp_originPose.pos().x(), tcp_originPose.pos().y(), tcp_originPose.pos().z());
		connPos.add(tcp_originPose.pos());
		matrices.multiply(tcp_originPose.rotation());

		matrices.translate(tcp_endPose.pos().x(), tcp_endPose.pos().y(), tcp_endPose.pos().z());
		tcp_vec4a.set(tcp_endPose.pos().x, tcp_endPose.pos().y, tcp_endPose.pos().z, 1);
		matrices.multiply(tcp_endPose.rotation());

		tcp_vec4a.rotate(tcp_originPose.rotation());
		connPos.add(tcp_vec4a.x, tcp_vec4a.y, tcp_vec4a.z);

		tcp_vec4a.set(0, 0, 0, 1);
		tcp_rot.set(tcp_originPose.rotation()).mul(tcp_endPose.rotation());
		tcp_vec4a.rotate(tcp_rot);
		connPos.add(tcp_vec4a.x, tcp_vec4a.y, tcp_vec4a.z);
	}
}
