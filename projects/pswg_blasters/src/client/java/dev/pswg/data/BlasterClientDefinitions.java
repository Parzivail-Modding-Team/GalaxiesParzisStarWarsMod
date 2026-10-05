package dev.pswg.data;

import dev.pswg.Blasters;
import dev.pswg.item.BlasterItem;
import dev.pswg.networking.BlasterDefinitionsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;

/**
 * Logical-client definitions tied to the active play connection.
 */
public final class BlasterClientDefinitions
{
	/**
	 * Registers connection lifetime and presentation hooks through the client module lifecycle.
	 */
	public static void register()
	{
		BlasterData.setClientLookup(BlasterClientDefinitions::get);

		ClientPlayConnectionEvents.INIT.register((listener, client) -> {
			_owner = listener;
			_snapshot = BakedBlasterDefinition.EMPTY;
			ClientPlayNetworking.registerReceiver(BlasterDefinitionsPayload.TYPE, (payload, context) -> {
				if (_owner != listener || context.client().getConnection() != listener)
				{
					return;
				}
				try
				{
					var candidate = payload.decodeSnapshot();
					_snapshot = candidate;
					invalidateCreativeContents(context.client());
					Blasters.LOGGER.info("Received blaster definitions {} ({} weapons)", candidate.data().generation(), candidate.blasters().size());
				}
				catch (RuntimeException exception)
				{
					Blasters.LOGGER.error("Invalid blaster definition projection", exception);
					listener.getConnection().disconnect(Component.literal("Invalid blaster definitions: " + exception.getMessage()));
				}
			});
		});

		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
			if (_owner == listener)
			{
				_owner = null;
				_snapshot = BakedBlasterDefinition.EMPTY;
			}
		});

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
			current().blasters().entrySet().stream().sorted(
					java.util.Map.Entry.comparingByKey()).forEach(entry -> output.accept(BlasterItem.createStack(entry.getKey(), entry.getValue()))
			);
		});
	}

	/**
	 * Gets the current blaster definitions.
	 */
	public static BakedBlasterDefinition current()
	{
		return Minecraft.getInstance().getConnection() == _owner && _owner != null ? _snapshot : BakedBlasterDefinition.EMPTY;
	}

	/**
	 * Gets the blaster definitions for the given level.
	 */
	private static BakedBlasterDefinition get(Level level)
	{
		return Minecraft.getInstance().level == level ? current() : BakedBlasterDefinition.EMPTY;
	}

	/**
	 * Forces a native rebuild with a fresh lookup identity; an open creative screen refreshes on its next tick.
	 */
	private static void invalidateCreativeContents(Minecraft client)
	{
		if (client.level == null || client.player == null)
		{
			return;
		}
		var lookup = HolderLookup.Provider.create(client.level.registryAccess().listRegistries());
		CreativeModeTabs.tryRebuildTabContents(_owner.enabledFeatures(), false, lookup);
	}

	/**
	 * Connection owning the installed client projection.
	 */
	private static ClientPacketListener _owner;

	/**
	 * Atomically replaced client view; all entries belong to the same generation.
	 */
	private static BakedBlasterDefinition _snapshot = BakedBlasterDefinition.EMPTY;

	/**
	 * Utility class.
	 */
	private BlasterClientDefinitions()
	{
	}
}
