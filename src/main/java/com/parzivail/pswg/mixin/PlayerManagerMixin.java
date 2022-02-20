package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.data.SwgLightsaberManager;
import com.parzivail.pswg.data.SwgSpeciesManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
	@Shadow
	@Final
	private List<ServerPlayerEntity> players;

	// TODO: check if this can be replaced with net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN
	@Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.BEFORE))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
	{
		ServerPlayNetworking.send(player, SwgPackets.S2C.PacketSyncBlasters, SwgBlasterManager.INSTANCE.createPacket());
		ServerPlayNetworking.send(player, SwgPackets.S2C.PacketSyncLightsabers, SwgLightsaberManager.INSTANCE.createPacket());
		ServerPlayNetworking.send(player, SwgPackets.S2C.PacketSyncSpecies, SwgSpeciesManager.INSTANCE.createPacket());
	}

	@Inject(method = "onDataPacksReloaded()V", at = @At("TAIL"))
	public void onDataPacksReloaded(CallbackInfo ci)
	{
		for (var serverPlayerEntity : this.players)
		{
			ServerPlayNetworking.send(serverPlayerEntity, SwgPackets.S2C.PacketSyncBlasters, SwgBlasterManager.INSTANCE.createPacket());
			ServerPlayNetworking.send(serverPlayerEntity, SwgPackets.S2C.PacketSyncLightsabers, SwgLightsaberManager.INSTANCE.createPacket());
			ServerPlayNetworking.send(serverPlayerEntity, SwgPackets.S2C.PacketSyncSpecies, SwgSpeciesManager.INSTANCE.createPacket());
		}
	}
}
