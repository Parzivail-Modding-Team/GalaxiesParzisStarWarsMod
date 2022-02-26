package com.parzivail.util.entity;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.mixin.ServerPlayerEntityAccessor;
import com.parzivail.util.network.OpenEntityInventoryS2CPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EntityWithInventory<T extends ScreenHandler>
{
	static <T extends ScreenHandler> void openScreen(ServerPlayerEntity player, EntityWithInventory<T> entity)
	{
		if (player.currentScreenHandler != player.playerScreenHandler)
			player.closeHandledScreen();

		var playera = (ServerPlayerEntityAccessor)player;

		playera.invokeIncrementScreenHandlerSyncId();

		var inventory = entity.getInventory();

		var buf = new PacketByteBuf(Unpooled.buffer());
		new OpenEntityInventoryS2CPacket(playera.getScreenHandlerSyncId(), inventory.size(), entity.getEntityId()).write(buf);
		ServerPlayNetworking.send(player, SwgPackets.S2C.OpenEntityInventory, buf);
		player.currentScreenHandler = entity.createScreenHandler(playera.getScreenHandlerSyncId(), player.getInventory(), inventory);

		playera.invokeOnScreenHandlerOpened(player.currentScreenHandler);
	}

	int getEntityId();

	Inventory getInventory();

	Screen createScreen(T handler, PlayerInventory playerInventory);

	T createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory);
}
