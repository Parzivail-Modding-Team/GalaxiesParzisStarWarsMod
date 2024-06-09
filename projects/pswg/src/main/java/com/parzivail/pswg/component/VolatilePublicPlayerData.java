package com.parzivail.pswg.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class VolatilePublicPlayerData implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int PATROL_POSTURE_SYNCOP = 1;
	private boolean patrolPosture = false;

	public VolatilePublicPlayerData(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public boolean isPatrolPosture()
	{
		return patrolPosture;
	}

	public void setPatrolPosture(boolean patrolPosture)
	{
		this.patrolPosture = patrolPosture;
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		patrolPosture = tag.getBoolean("patrol_posture");
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
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
	public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient)
	{
		writeSyncPacket(buf, recipient, 0);
	}

	private void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient, int syncOp)
	{
		buf.writeInt(syncOp);

		switch (syncOp)
		{
			case 0 ->
			{ // Full sync
				var tag = new NbtCompound();
				writeToNbt(tag, buf.getRegistryManager());
				buf.writeNbt(tag);
			}
			case PATROL_POSTURE_SYNCOP -> buf.writeBoolean(patrolPosture);
		}
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf)
	{
		var syncOp = buf.readInt();

		switch (syncOp)
		{
			case 0 ->
			{ // Full sync
				var tag = buf.readNbt();
				if (tag != null)
					this.readFromNbt(tag, buf.getRegistryManager());
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
