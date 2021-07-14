package com.parzivail.pswg.handler;

import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.util.item.ILeftClickConsumer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class PlayerPacketHandler
{
	public static void handleLeftClickPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var stack = player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
		{
			((ILeftClickConsumer)stack.getItem()).useLeft(player.world, player, Hand.MAIN_HAND);
		}
	}

	public static void handleLightsaberTogglePacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var stack = player.getMainHandStack();

		if (stack.getItem() instanceof LightsaberItem)
		{
			LightsaberItem.toggle(player.world, player, stack);
		}
	}
}
