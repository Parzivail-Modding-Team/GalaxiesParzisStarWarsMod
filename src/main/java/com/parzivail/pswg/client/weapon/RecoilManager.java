package com.parzivail.pswg.client.weapon;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class RecoilManager
{
	private static final float recoilCompAccel = 1.25f;

	private static float recoilVelocity = 0;

	public static void tick(MinecraftClient mc)
	{
		if (recoilVelocity + recoilCompAccel < 0)
		{
			recoilVelocity += recoilCompAccel;

			if (mc.player != null)
			{
				var p = mc.player.getPitch(0);

				mc.player.prevPitch = p;
				mc.player.setPitch(p + recoilVelocity);
			}
		}
		else
		{
			recoilVelocity = 0;
		}
	}

	public static float getRecoilMovement(float tickDelta)
	{
		if (recoilVelocity + recoilCompAccel >= 0)
			return 0;

		return (recoilVelocity + recoilCompAccel) * tickDelta;
	}

	public static void setRecoil(float amount)
	{
		recoilVelocity = -amount;
	}

	public static void handleAccumulateRecoil(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var recoil = buf.readFloat();
		setRecoil(recoil);
	}
}
