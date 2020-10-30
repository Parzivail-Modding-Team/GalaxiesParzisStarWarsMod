package com.parzivail.pswg.component;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class SwgVolatileComponents implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int CREDITS_SYNCOP = 1;
	private int credits;

	public SwgVolatileComponents(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public int getCredits()
	{
		return credits;
	}

	public void setCredits(int credits)
	{
		this.credits = credits;
		SwgEntityComponents.VOLATILE.sync(provider, CREDITS_SYNCOP);
	}

	@Override
	public void readFromNbt(CompoundTag tag)
	{
		credits = tag.getInt("credits");
	}

	@Override
	public void writeToNbt(CompoundTag tag)
	{
		tag.putInt("credits", credits);
	}

	/**
	 * See comment on {@link SwgPersistentComponents#syncAll}
	 */
	public void syncAll()
	{
		SwgEntityComponents.VOLATILE.sync(provider);
	}

	@Override
	public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int syncOp)
	{
		buf.writeInt(syncOp);

		switch (syncOp)
		{
			case FULL_SYNC:
				writeToPacket(buf, recipient);
				break;
			case CREDITS_SYNCOP:
				buf.writeInt(credits);
				break;
		}
	}

	@Override
	public void readFromPacket(PacketByteBuf buf)
	{
		int syncOp = buf.readInt();

		// It seems like a bad idea to read arbitrary fields
		// on command from a packet, but this packet will only
		// be used from server->client, so the server will
		// always have the correct data. The client should
		// never do anything other than visual changes based
		// on this data

		switch (syncOp)
		{
			case FULL_SYNC:
				CompoundTag tag = buf.readCompoundTag();
				if (tag != null)
					this.readFromNbt(tag);
				break;
			case CREDITS_SYNCOP:
				credits = buf.readInt();
				break;
		}
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player, int syncOp)
	{
		// If we have any properties that should* be
		// synced to other players, we should just
		// define another component that is local
		// to the player in question. If we leave
		// this as player == this.provider, and
		// use syncOp to send only specific props
		// to clients, they won't get synced on the
		// initial, full sync, so having two different
		// components is the best compromise in
		// my opinion.

		// TODO: * which we will, the current force power etc.

		return player == provider;
	}
}
