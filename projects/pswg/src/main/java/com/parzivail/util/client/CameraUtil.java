package com.parzivail.util.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CameraUtil
{
	public static PositionNormal3f unproject(MatrixStack matrices, Vector3f normal)
	{
		var worldPos = matrices.peek().getPositionMatrix().transform(new Vector4f(0, 0, 0, 1));
		var worldNormal = matrices.peek().getNormalMatrix().transform(normal);
		worldPos.mul(1, -1, 1, 1);
		worldNormal.mul(1, -1, 1);

		var cam = MinecraftClient.getInstance().gameRenderer.getCamera();

		var camPos = cam.getPos();
		var pitch = new Quaternionf().rotateX(cam.getPitch() * MathHelper.RADIANS_PER_DEGREE);
		var yaw = new Quaternionf().rotateY(-cam.getYaw() * MathHelper.RADIANS_PER_DEGREE);

		pitch.transform(worldPos);
		yaw.transform(worldPos);

		pitch.transform(worldNormal);
		yaw.transform(worldNormal);

		return new PositionNormal3f(new Vector3f(camPos.toVector3f()).sub(worldPos.x, worldPos.y, worldPos.z), worldNormal);
	}
}
