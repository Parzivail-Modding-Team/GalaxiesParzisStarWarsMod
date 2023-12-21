package com.parzivail.pswg.network;

import com.parzivail.pswg.client.screen.ScreenHelper;
import com.parzivail.pswg.entity.EntityWithInventory;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;

public class OpenEntityInventoryS2CPacket
{
	private final int syncId;
	private final int slotCount;
	private final int entityId;

	public OpenEntityInventoryS2CPacket(int syncId, int slotCount, int entityId)
	{
		this.syncId = syncId;
		this.slotCount = slotCount;
		this.entityId = entityId;
	}

	public OpenEntityInventoryS2CPacket(PacketByteBuf buf)
	{
		this.syncId = buf.readUnsignedByte();
		this.slotCount = buf.readVarInt();
		this.entityId = buf.readInt();
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.slotCount);
		buf.writeInt(this.entityId);
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
	{
		var packet = new OpenEntityInventoryS2CPacket(buf);
		client.execute(() -> {
			var entity = client.world.getEntityById(packet.getEntityId());
			if (entity instanceof EntityWithInventory inventoryEntity)
			{
				var clientPlayerEntity = client.player;
				var simpleInventory = new SimpleInventory(packet.getSlotCount());
				var handler = inventoryEntity.createScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), simpleInventory);
				clientPlayerEntity.currentScreenHandler = handler;
				client.setScreen(ScreenHelper.createEntityScreen(client, handler, clientPlayerEntity.getInventory(), inventoryEntity));
			}
		});
	}

	public int getSyncId()
	{
		return this.syncId;
	}

	public int getSlotCount()
	{
		return this.slotCount;
	}

	public int getEntityId()
	{
		return this.entityId;
	}
}
