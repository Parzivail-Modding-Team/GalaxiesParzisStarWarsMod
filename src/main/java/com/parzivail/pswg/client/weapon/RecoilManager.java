package com.parzivail.pswg.client.weapon;

import com.parzivail.pswg.Resources;
import com.parzivail.util.math.Ease;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

public class RecoilManager
{
	private static final float recoilCompAccel = 1.25f;

	private static float impulse = 0;
	private static float verticalVelocity = 0;
	private static float horizontalVelocity = 0;

	public static void tick(MinecraftClient mc)
	{
		if (verticalVelocity + recoilCompAccel < 0)
		{
			verticalVelocity += recoilCompAccel;

			if (mc.player != null)
			{
				var p = mc.player.getPitch(0);

				mc.player.prevPitch = p;
				mc.player.setPitch(p + verticalVelocity);
			}
		}
		else
			verticalVelocity = 0;

		var horizVelSign = Math.signum(horizontalVelocity);
		var hComp = recoilCompAccel * -horizVelSign;
		if (horizVelSign == Math.signum(horizontalVelocity + hComp))
		{
			horizontalVelocity += hComp;

			if (mc.player != null)
			{
				var y = mc.player.getYaw();

				mc.player.prevYaw = y;
				mc.player.setYaw(y + horizontalVelocity);
			}
		}
		else
			horizontalVelocity = 0;

		impulse--;
		impulse = Math.max(impulse, 0);
	}

	public static float getVerticalRecoilMovement(float tickDelta)
	{
		if (verticalVelocity + recoilCompAccel >= 0)
			return 0;

		return (verticalVelocity + recoilCompAccel) * tickDelta;
	}

	public static float getHorizontalRecoilMovement(float tickDelta)
	{
		var horizVelSign = Math.signum(horizontalVelocity);
		var comp = recoilCompAccel * -horizVelSign;

		if (horizVelSign != Math.signum(horizontalVelocity + comp))
			return 0;

		return (horizontalVelocity + comp) * tickDelta;
	}

	public static void setRecoil(float vertical, float horizontal)
	{
		impulse = 10;
		verticalVelocity = -vertical;
		horizontalVelocity = horizontal;
	}

	public static void handleAccumulateRecoil(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var horiz = buf.readFloat();
		var vert = buf.readFloat();
		setRecoil(vert, horiz);
	}

	public static void applyCameraShake(MinecraftClient mc, MatrixStack matrix, Camera camera, float tickDelta, double fov)
	{
		if (mc.player == null || !Resources.CONFIG.get().view.enableScreenShake)
			return;

		var t = (mc.player.age + tickDelta) * 0.65f;
		var scale = 0.5f * Ease.inCubic(Math.max(impulse - tickDelta, 0) / 10) * (float)((fov - 13) / 60 + 1);
		var x = scale * (horizontalVelocity + 1) * (float)Resources.SIMPLEX_0.noise2(0, t);
		var y = scale * (verticalVelocity + 1) * (float)Resources.SIMPLEX_0.noise2(t, 0);
		var z = scale * 1.1f * (float)Resources.SIMPLEX_0.noise2(t, t);

		matrix.multiply(new Quaternion(0, 0, z, true));
		matrix.multiply(new Quaternion(y, 0, 0, true));
		matrix.multiply(new Quaternion(0, x, 0, true));
	}
}
