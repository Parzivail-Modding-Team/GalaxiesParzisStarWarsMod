package com.parzivail.pswg.network;

import com.parzivail.pswg.client.screen.ScreenHelper;
import com.parzivail.pswg.entity.MannequinEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class OpenMannequinConfigureS2CPacket
{
	private final int syncId;
	private final int entityId;

	public OpenMannequinConfigureS2CPacket(int syncId, int entityId)
	{
		this.syncId = syncId;
		this.entityId = entityId;
	}

	public OpenMannequinConfigureS2CPacket(PacketByteBuf buf)
	{
		this.syncId = buf.readUnsignedByte();
		this.entityId = buf.readInt();
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeByte(this.syncId);
		buf.writeInt(this.entityId);
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender)
	{
		var packet = new OpenMannequinConfigureS2CPacket(buf);
		client.execute(() -> {
			var entity = client.world.getEntityById(packet.getEntityId());
			if (entity instanceof MannequinEntity mannequinEntity)
			{
				var clientPlayerEntity = client.player;
				var handler = mannequinEntity.createScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory());
				clientPlayerEntity.currentScreenHandler = handler;
				client.setScreen(ScreenHelper.createEntityScreen(client, handler, clientPlayerEntity.getInventory(), mannequinEntity));
			}
		});
	}

	public int getSyncId()
	{
		return this.syncId;
	}

	public int getEntityId()
	{
		return this.entityId;
	}
}
