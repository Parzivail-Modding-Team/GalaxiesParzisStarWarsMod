package com.parzivail.pswg.client.weapon;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class RecoilManager
{
	private static final float recoilCompAccel = 1.25f;

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
		verticalVelocity = -vertical;
		horizontalVelocity = horizontal;
	}

	public static void handleAccumulateRecoil(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var horiz = buf.readFloat();
		var vert = buf.readFloat();
		setRecoil(vert, horiz);
	}
}
