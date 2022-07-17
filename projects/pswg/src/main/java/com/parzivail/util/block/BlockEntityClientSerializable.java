package com.parzivail.util.block;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface BlockEntityClientSerializable
{
	void fromClientTag(NbtCompound tag);

	NbtCompound toClientTag(NbtCompound tag);

	Identifier getSyncPacketId();

	default void sync()
	{
		if (this instanceof BlockEntity entity)
		{
			var level = entity.getWorld();
			if (level != null && !level.isClient)
			{
				var passedData = new PacketByteBuf(Unpooled.buffer());

				passedData.writeBlockPos(entity.getPos());
				passedData.writeIdentifier(level.getRegistryKey().getValue());
				passedData.writeNbt(toClientTag(new NbtCompound()));

				for (var trackingPlayer : PlayerLookup.tracking((ServerWorld)level, entity.getPos()))
					ServerPlayNetworking.send(trackingPlayer, getSyncPacketId(), passedData);
			}
		}
	}

	static void handle(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender)
	{
		var targetPos = buf.readBlockPos();
		var targetWorldId = buf.readIdentifier();
		var tag = buf.readNbt();

		minecraftClient.execute(() -> {
			var world = minecraftClient.world;
			var targetWorld = RegistryKey.of(Registry.WORLD_KEY, targetWorldId);

			if (world == null || !world.getRegistryKey().equals(targetWorld) || !(world.getBlockEntity(targetPos) instanceof BlockEntityClientSerializable tile))
				return;

			tile.fromClientTag(tag);
		});
	}
}
