package com.parzivail.pswg.entity;

import com.parzivail.pswg.mixin.ServerPlayerEntityAccessor;
import com.parzivail.pswg.network.OpenEntityInventoryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
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

		ServerPlayNetworking.send(player, new OpenEntityInventoryS2CPacket(playera.getScreenHandlerSyncId(), inventory.size(), entity.getEntityId()));
		player.currentScreenHandler = entity.createScreenHandler(playera.getScreenHandlerSyncId(), player.getInventory(), inventory);

		playera.invokeOnScreenHandlerOpened(player.currentScreenHandler);
	}

	int getEntityId();

	Inventory getInventory();

	T createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory);
}
