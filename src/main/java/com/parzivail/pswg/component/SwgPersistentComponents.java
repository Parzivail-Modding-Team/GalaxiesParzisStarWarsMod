package com.parzivail.pswg.component;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class SwgPersistentComponents implements ComponentV3, AutoSyncedComponent
{
	private final PlayerEntity provider;

	private static final int TEST_SYNCOP = 1;
	private int test;

	public SwgPersistentComponents(PlayerEntity provider)
	{
		this.provider = provider;
	}

	public int getTest()
	{
		return test;
	}

	public void setTest(int test)
	{
		this.test = test;
		SwgEntityComponents.PERSISTENT.sync(provider, TEST_SYNCOP);
	}

	@Override
	public void readFromNbt(CompoundTag tag)
	{
		test = tag.getInt("test");
	}

	@Override
	public void writeToNbt(CompoundTag tag)
	{
		tag.putInt("test", test);
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
	public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int syncOp)
	{
		buf.writeInt(syncOp);

		switch (syncOp)
		{
			case FULL_SYNC:
				writeToPacket(buf, recipient);
				break;
			case TEST_SYNCOP:
				buf.writeInt(test);
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
			case TEST_SYNCOP:
				test = buf.readInt();
				break;
		}
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player, int syncOp)
	{
		// We should only sync data with tehe client associated with
		// the player containing the data. This won't be the case,
		// obviously, when we need to show visual effects on all
		// clients, as with force powers, etc. That data will be
		// sent on the correct syncOp

		return player == provider;
	}
}
