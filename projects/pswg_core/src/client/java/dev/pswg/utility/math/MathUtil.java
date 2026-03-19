package dev.pswg.utility.math;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.lang.Math;

public class MathUtil
{
	public static final int TICKS_PER_SECOND = 20;

	public static final float fPI = (float)Math.PI;
	public static final float SQRT2DIV2 = (float)(Math.sqrt(2) / 2);
	public static final double ONE_OVER_GOLDEN_RATIO = 0.61803398875;

	public static final float SPEED_OF_SOUND = 275f / TICKS_PER_SECOND; // m/tick

	public static final Vec3 V3D_POS_X = new Vec3(1, 0, 0);
	public static final Vec3 V3D_NEG_X = new Vec3(-1, 0, 0);
	public static final Vec3 V3D_POS_Y = new Vec3(0, 1, 0);
	public static final Vec3 V3D_NEG_Y = new Vec3(0, -1, 0);
	public static final Vec3 V3D_POS_Z = new Vec3(0, 0, 1);
	public static final Vec3 V3D_NEG_Z = new Vec3(0, 0, -1);

	public static final Vector3f V3F_POS_X = new Vector3f(1, 0, 0);
	public static final Vector3f V3F_NEG_X = new Vector3f(-1, 0, 0);
	public static final Vector3f V3F_POS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f V3F_NEG_Y = new Vector3f(0, -1, 0);
	public static final Vector3f V3F_POS_Z = new Vector3f(0, 0, 1);
	public static final Vector3f V3F_NEG_Z = new Vector3f(0, 0, -1);

	public static final Matrix4f MAT4_IDENTITY = new Matrix4f();
	public static final Matrix4f MAT4_SCALE_10_16THS = new Matrix4f().scale(10 / 16f, 10 / 16f, 10 / 16f);

	public static float fract(double d)
	{
		return (float)(d - Math.floor(d));
	}

	public static Vec2 fract(Vec2 v)
	{
		return new Vec2(fract(v.x), fract(v.y));
	}

	public static Vec3 fract(Vec3 v)
	{
		return new Vec3(fract(v.x), fract(v.y), fract(v.z));
	}

	public static double seed(double d, long seed)
	{
		return Double.longBitsToDouble(Double.doubleToLongBits(d) ^ seed);
	}

	public static Vec2 floor(Vec2 v)
	{
		return new Vec2((float)Math.floor(v.x), (float)Math.floor(v.y));
	}

	public static Vec3 floor(Vec3 v)
	{
		return new Vec3(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
	}

	public static Vec3i floorInt(Vec3 v) {
		return new Vec3i(Mth.floor(v.x), Mth.floor(v.y), Mth.floor(v.z));
	}

	public static Vec3 lerp(float tickDelta, Vec3 a, Vec3 b)
	{
		return new Vec3(Mth.lerp(tickDelta, a.x, b.x), Mth.lerp(tickDelta, a.y, b.y), Mth.lerp(tickDelta, a.z, b.z));
	}

	public static Vec2 add(Vec2 a, Vec2 b)
	{
		return new Vec2(a.x + b.x, a.y + b.y);
	}

	public static Vec2 sub(Vec2 a, Vec2 b)
	{
		return new Vec2(a.x - b.x, a.y - b.y);
	}

	public static double length(Vec2 v)
	{
		return Math.sqrt(v.x * v.x + v.y * v.y);
	}

	public static int clamp(int i, int min, int max)
	{
		return Math.max(min, Math.min(i, max));
	}

	public static float remap(float x, float iMin, float iMax, float oMin, float oMax)
	{
		return (x - iMin) / (iMax - iMin) * (oMax - oMin) + oMin;
	}

	public static float calculateDopplerShift(Entity a, Entity b)
	{
		// TODO: move doppler handling to OpenAL through SoundSystem's updateListenerPosition call?
		var velA = a.position().subtract(a.xo, a.yo, a.zo);
		var velB = b.position().subtract(b.xo, b.yo, b.zo);

		var posA = a.getEyePosition();
		var posB = b.getEyePosition();

		var relativeSpeed = posA.distanceTo(posB) - posA.add(velA).distanceTo(posB.add(velB));

		return Mth.clamp((float)(relativeSpeed / SPEED_OF_SOUND), -1, 1);
	}

	public static Vec3 project(Vec3 v, Vec3 onto)
	{
		return onto.scale(v.dot(onto) / onto.dot(onto));
	}

	public static int lerpLight(float delta, int packedA, int packedB)
	{
		return LightTexture.pack(
				Mth.lerpInt(delta,
				                LightTexture.block(packedA),
				                LightTexture.block(packedB)
				),
				Mth.lerpInt(delta,
				                LightTexture.sky(packedA),
				                LightTexture.sky(packedB)
				)
		);
	}

	@NotNull
	public static Vec3 anglesToLook(float pitch, float yaw)
	{
		var x = -Mth.sin(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);
		var y = -Mth.sin(pitch * Mth.DEG_TO_RAD);
		var z = Mth.cos(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);

		return new Vec3(x, y, z).normalize();
	}

	@NotNull
	public static Rotations lookToAngles(Vec3 forward)
	{
		forward = forward.normalize();

		var yaw = -(float)Math.atan2(forward.x, forward.z);
		var pitch = -(float)Math.asin(forward.y);

		return new Rotations(pitch * Mth.RAD_TO_DEG, yaw * Mth.RAD_TO_DEG, 0);
	}

	public static boolean rectContains(int l, int t, int w, int h, double x, double y)
	{
		return x >= l && x < l + w && y >= t && y < t + h;
	}

	public static float toRadians(float degrees)
	{
		return degrees * Mth.DEG_TO_RAD;
	}

	public static Vec3 transform(Vec3 v, Matrix4f transform)
	{
		var vec3d = new Vector3d(v.x, v.y, v.z).mulPosition(transform);
		return new Vec3(vec3d.x, vec3d.y, vec3d.z);
	}

	public static void scalePos(PoseStack stack, float x, float y, float z)
	{
		var entry = stack.last();
		entry.pose().scale(x, y, z);
	}

	public static Quaternionf getEastRotation(Direction direction)
	{
		return switch (direction)
		{
			case DOWN -> new Quaternionf().rotationXYZ(0, 0, (float)(Math.PI / -2));
			case UP -> new Quaternionf().rotationXYZ(0, 0, (float)(Math.PI / 2));
			case NORTH -> new Quaternionf().rotationXYZ(0, (float)(Math.PI / 2), 0);
			case SOUTH -> new Quaternionf().rotationXYZ(0, (float)(Math.PI / -2), 0);
			case WEST -> new Quaternionf().rotationXYZ(0, (float)Math.PI, 0);
			case EAST -> new Quaternionf().rotationXYZ(0, 0, 0);
		};
	}

	public static Quaternionf getNorthRotation(Direction direction)
	{
		return switch (direction)
		{
			case NORTH -> Axis.YP.rotationDegrees(0);
			case EAST -> Axis.YP.rotationDegrees(270);
			case SOUTH -> Axis.YP.rotationDegrees(180);
			case WEST -> Axis.YP.rotationDegrees(90);
			case UP -> Axis.XP.rotationDegrees(90);
			case DOWN -> Axis.XP.rotationDegrees(270);
		};
	}

	public static Vector4f vec3to4(Vector3f in, Vector4f out)
	{
		return out.set(in.x(), in.y(), in.z(), 1);
	}

	public static Vec3 reflect(Vec3 incident, Vec3 normal)
	{
		var reflection = normal.scale(2 * normal.dot(incident)).subtract(incident);
		return reflection.scale(-1);
	}
}
