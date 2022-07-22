package com.parzivail.util.network;

import com.parzivail.util.ParziUtil;
import com.parzivail.util.item.IItemActionListener;
import com.parzivail.util.item.ILeftClickConsumer;
import com.parzivail.util.item.ItemAction;
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
		server.execute(() -> {
			var stack = player.getMainHandStack();

			if (stack.getItem() instanceof ILeftClickConsumer)
			{
				((ILeftClickConsumer)stack.getItem()).useLeft(player.world, player, Hand.MAIN_HAND);
			}
		});
	}

	public static void handleItemAction(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var action = buf.readInt();

		server.execute(() -> {
			var stack = player.getMainHandStack();

			var actions = ItemAction.values();
			if (action < 0 || action >= actions.length)
			{
				ParziUtil.LOG.warn("Player %s attempted to use invalid item action ordinal %s", player, action);
				return;
			}

			if (stack.getItem() instanceof IItemActionListener)
				((IItemActionListener)stack.getItem()).onItemAction(player.world, player, stack, actions[action]);
		});
	}
}
