package com.parzivail.pswg.component;

import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class PersistentPublicPlayerData implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int CHARACTER_SYNCOP = 1;
	private String character = "";

	public PersistentPublicPlayerData(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public SwgSpecies getCharacter()
	{
		if (character.isEmpty())
			return null;

		return SwgSpeciesRegistry.deserialize(character);
	}

	public void setCharacter(SwgSpecies character)
	{
		this.character = "";

		if (character != null)
			this.character = character.serialize();

		PlayerData.PERSISTENT_PUBLIC.sync(provider, (buf, recipient) -> writeSyncPacket(buf, recipient, CHARACTER_SYNCOP));
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		character = tag.getString("character");
		onCharacterChange();
	}

	private void onCharacterChange()
	{
		provider.calculateDimensions();
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		tag.putString("character", character);
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
			case CHARACTER_SYNCOP -> buf.writeString(character);
		}
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf)
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
			case 0 ->
			{ // Full sync
				var tag = buf.readNbt();
				if (tag != null)
					this.readFromNbt(tag, buf.getRegistryManager());
			}
			case CHARACTER_SYNCOP ->
			{
				character = buf.readString();
				onCharacterChange();
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
