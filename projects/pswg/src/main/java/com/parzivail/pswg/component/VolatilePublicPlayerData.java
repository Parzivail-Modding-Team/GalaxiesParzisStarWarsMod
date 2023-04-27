package com.parzivail.pswg.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class VolatilePublicPlayerData implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int PATROL_POSTURE_SYNCOP = 1;
	private boolean patrolPosture = false;

	public VolatilePublicPlayerData(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public boolean getPatrolPosture()
	{
		return patrolPosture;
	}

	public void setPatrolPosture(boolean patrolPosture)
	{
		this.patrolPosture = patrolPosture;
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		patrolPosture = tag.getBoolean("patrol_posture");
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("patrol_posture", patrolPosture);
	}

	/**
	 * See comment on {@link PersistentPublicPlayerData#syncAll}
	 */
	public void syncAll()
	{
		PlayerData.VOLATILE_PUBLIC.sync(provider);
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient)
	{
		writeSyncPacket(buf, recipient, 0);
	}

	private void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int syncOp)
	{
		buf.writeInt(syncOp);

		switch (syncOp)
		{
			case 0 ->
			{ // Full sync
				var tag = new NbtCompound();
				writeToNbt(tag);
				buf.writeNbt(tag);
			}
			case PATROL_POSTURE_SYNCOP -> buf.writeBoolean(patrolPosture);
		}
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf)
	{
		var syncOp = buf.readInt();

		switch (syncOp)
		{
			case 0 ->
			{ // Full sync
				var tag = buf.readNbt();
				if (tag != null)
					this.readFromNbt(tag);
			}
			case PATROL_POSTURE_SYNCOP ->
			{
				patrolPosture = buf.readBoolean();
			}
		}
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player)
	{
		// Sync to all players
		return true;
	}
}
