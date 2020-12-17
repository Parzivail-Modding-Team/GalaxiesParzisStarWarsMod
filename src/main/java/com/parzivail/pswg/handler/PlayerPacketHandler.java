package com.parzivail.pswg.handler;

import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.util.item.ILeftClickConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class PlayerPacketHandler
{
	public static void handleLeftClickPacket(PacketContext packetContext, PacketByteBuf packetByteBuf)
	{
		PlayerEntity player = packetContext.getPlayer();
		ItemStack stack = player.getMainHandStack();

		if (stack.getItem() instanceof ILeftClickConsumer)
		{
			((ILeftClickConsumer)stack.getItem()).useLeft(player.world, player, Hand.MAIN_HAND);
		}
	}

	public static void handleLightsaberTogglePacket(PacketContext packetContext, PacketByteBuf packetByteBuf)
	{
		PlayerEntity player = packetContext.getPlayer();
		ItemStack stack = player.getMainHandStack();

		if (stack.getItem() instanceof LightsaberItem)
		{
			LightsaberItem.toggle(player.world, player, stack);
		}
	}
}
