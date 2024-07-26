package com.parzivail.util.network;

import com.parzivail.pswg.component.PlayerData;
import com.parzivail.pswg.network.PlayerItemLeftClickC2SPacket;
import com.parzivail.pswg.network.UnitC2SPacket;
import com.parzivail.util.ParziUtil;
import com.parzivail.util.item.IItemActionListener;
import com.parzivail.util.item.ILeftClickConsumer;
import com.parzivail.util.item.ItemAction;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class PlayerPacketHandler
{
	public static void handleLeftClickPacket(PlayerItemLeftClickC2SPacket packet, ServerPlayNetworking.Context context)
	{
		if (context.player().isSpectator())
			return;

		var stack = context.player().getStackInHand(packet.hand());

		if (stack.getItem() instanceof ILeftClickConsumer)
			((ILeftClickConsumer)stack.getItem()).useLeft(context.player().getWorld(), context.player(), packet.hand(), packet.isRepeatEvent());
	}

	public static void handleItemAction(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var action = buf.readInt();

		var actions = ItemAction.values();
		if (action < 0 || action >= actions.length)
		{
			ParziUtil.LOG.warn("Player %s attempted to use invalid item action ordinal %s", player, action);
			return;
		}

		server.execute(() -> {
			var stack = getStackInHand(player, false);
			if (stack.getItem() instanceof IItemActionListener)
				((IItemActionListener)stack.getItem()).onItemAction(player.getWorld(), player, stack, actions[action]);
			else
			{
				stack = getStackInHand(player, true);
				if (stack.getItem() instanceof IItemActionListener)
					((IItemActionListener)stack.getItem()).onItemAction(player.getWorld(), player, stack, actions[action]);
			}
		});
	}

	private static ItemStack getStackInHand(PlayerEntity player, boolean altHand)
	{
		// TODO: separate keybind?
		altHand ^= player.isSneaking();

		if (altHand)
			return player.getOffHandStack();
		return player.getMainHandStack();
	}

	public static void handleTogglePatrolPosture(UnitC2SPacket packet, ServerPlayNetworking.Context context)
	{
		var data = PlayerData.getVolatilePublic(context.player());

		data.setPatrolPosture(!data.isPatrolPosture());
		data.syncAll();
	}
}
