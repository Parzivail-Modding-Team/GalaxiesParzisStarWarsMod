package com.parzivail.pswg.component;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SwgSpecies;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class SwgPersistentComponents implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int SPECIES_SYNCOP = 1;
	private String species = "";

	public SwgPersistentComponents(PlayerEntity provider)
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

		SwgEntityComponents.PERSISTENT.sync(provider, (buf, recipient) -> writeSyncPacket(buf, recipient, SPECIES_SYNCOP));
	}

	@Override
	public void readFromNbt(NbtCompound tag)
	{
		species = tag.getString("species");
	}

	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putString("species", species);
	}

	/**
	 * This should be used in place of SwgComponents.PERSISTENT.sync(...) if multiple fields get set
	 * in one tick, as per https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components
	 *
	 * If that is the case, calls to SwgComponents.PERSISTENT.sync(...) should be removed from setters, and this
	 * method should be called when all fields have been set in that tick. Otherwise, this method is unnecessary.
	 */
	public void syncAll()
	{
		SwgEntityComponents.PERSISTENT.sync(provider);
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
				NbtCompound tag = buf.readNbt();
				if (tag != null)
					this.readFromNbt(tag);
			}
			case SPECIES_SYNCOP -> species = buf.readString();
		}
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player)
	{
		// If we have any properties that shouldn't be
		// synced to other players, we should just
		// define another component that is local
		// to the player in question. If we leave
		// this as player == this.provider, and
		// use syncOp to send only specific props
		// to clients, they won't get synced on the
		// initial, full sync, so having two different
		// components is the best compromise in
		// my opinion.

		return true;
	}
}
