package com.parzivail.pswg.component;

import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PersistentPublicPlayerData implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int SPECIES_SYNCOP = 1;
	private String species = "";

	public PersistentPublicPlayerData(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public SwgSpecies getSpecies()
	{
		if (species.isEmpty())
			return null;

		return SwgSpeciesRegistry.deserialize(species);
	}

	public void setSpecies(SwgSpecies species)
	{
		this.species = "";

		if (species != null)
			this.species = species.serialize();

		PlayerData.PERSISTENT_PUBLIC.sync(provider, (buf, recipient) -> writeSyncPacket(buf, recipient, SPECIES_SYNCOP));
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		species = tag.getString("species");
		onSpeciesChange();
	}

	private void onSpeciesChange()
	{
		provider.calculateDimensions();
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putString("species", species);
	}

	/**
	 * This should be used in place of SwgComponents.PERSISTENT_PUBLIC.sync(...) if multiple fields get set
	 * in one tick, as per https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components
	 *
	 * If that is the case, calls to SwgComponents.PERSISTENT_PUBLIC.sync(...) should be removed from setters, and this
	 * method should be called when all fields have been set in that tick. Otherwise, this method is unnecessary.
	 */
	public void syncAll()
	{
		PlayerData.PERSISTENT_PUBLIC.sync(provider);
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
			case 0 -> { // Full sync
				var tag = new NbtCompound();
				writeToNbt(tag);
				buf.writeNbt(tag);
			}
			case SPECIES_SYNCOP -> buf.writeString(species);
		}
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf)
	{
		var syncOp = buf.readInt();

		// It seems like a bad idea to read arbitrary fields
		// on command from a packet, but this packet will only
		// be used from server->client, so the server will
		// always have the correct data. The client should
		// never do anything other than visual changes based
		// on this data

		switch (syncOp)
		{
			case 0 -> { // Full sync
				var tag = buf.readNbt();
				if (tag != null)
					this.readFromNbt(tag);
			}
			case SPECIES_SYNCOP ->
			{
				species = buf.readString();
				onSpeciesChange();
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
