package com.parzivail.pswg.network;

import com.parzivail.pswg.client.screen.ScreenHelper;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.EntityWithInventory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record OpenEntityInventoryS2CPacket(int syncId, int slotCount, int entityId) implements CustomPayload
{
	public static final PacketCodec<RegistryByteBuf, OpenEntityInventoryS2CPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, OpenEntityInventoryS2CPacket::syncId, PacketCodecs.VAR_INT, OpenEntityInventoryS2CPacket::slotCount, PacketCodecs.INTEGER, OpenEntityInventoryS2CPacket::entityId, OpenEntityInventoryS2CPacket::new);

	public static OpenEntityInventoryS2CPacket create(PacketByteBuf buf)
	{
		return new OpenEntityInventoryS2CPacket(buf.readUnsignedByte(), buf.readVarInt(), buf.readInt());
	}

	@Override
	public Id<OpenEntityInventoryS2CPacket> getId()
	{
		return SwgPackets.S2C.OpenEntityInventory;
	}

	public static void handle(OpenEntityInventoryS2CPacket payload, ClientPlayNetworking.Context context)
	{
		var entity = context.client().world.getEntityById(payload.entityId());
		if (entity instanceof EntityWithInventory inventoryEntity)
		{
			var clientPlayerEntity = context.client().player;
			var simpleInventory = new SimpleInventory(payload.slotCount());
			var handler = inventoryEntity.createScreenHandler(payload.syncId(), clientPlayerEntity.getInventory(), simpleInventory);
			clientPlayerEntity.currentScreenHandler = handler;
			context.client().setScreen(ScreenHelper.createEntityScreen(context.client(), handler, clientPlayerEntity.getInventory(), inventoryEntity));
		}
	}
}
