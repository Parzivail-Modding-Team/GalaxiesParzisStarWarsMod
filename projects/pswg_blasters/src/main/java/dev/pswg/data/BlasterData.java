package dev.pswg.data;

import dev.pswg.networking.BlasterDefinitionsPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ClientboundPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.fabric.api.resource.v1.DataResourceStore;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;

/**
 * Blaster data registry
 */
public final class BlasterData
{
	/**
	 * Prepared blaster data
	 */
	public record Prepared(BakedBlasterDefinition baked, BlasterDefinitionsPayload payload)
	{
	}

	private static final class Reloader extends SimpleReloadListener<Prepared>
	{
		@Override
		protected Prepared prepare(SharedState state)
		{
			var lookup = state.get(ResourceLoader.REGISTRY_LOOKUP_KEY);
			var items = lookup.lookupOrThrow(Registries.ITEM);

			var candidateTags = TagLoader.loadTagsForRegistry(
					state.resourceManager(),
					Registries.ITEM,
					(id, required) -> items.get(ResourceKey.create(Registries.ITEM, id))
			);

			var snapshot = BakedBlasterDefinition.fromLookup(lookup, candidateTags);
			return new Prepared(snapshot, BlasterDefinitionsPayload.prepare(snapshot));
		}

		@Override
		protected void apply(Prepared prepared, SharedState state)
		{
			state.get(DataResourceLoader.DATA_RESOURCE_STORE_KEY).put(SNAPSHOT, prepared);
		}
	}

	/**
	 * Register the data registry, packets, and reload listener.
	 */
	public static void register()
	{
		DynamicRegistries.registerReloadable(BLASTERS, BlasterDatapackDefinition.CODEC);
		DynamicRegistries.registerReloadable(ATTACHMENTS, BlasterAttachmentDefinition.CODEC);
		DynamicRegistries.registerReloadable(BEHAVIOR_PROFILES, BlasterBehaviorProfile.CODEC);
		DynamicRegistries.registerReloadable(STANCE_PROFILES, BlasterStanceProfile.CODEC);

		DataResourceLoader.get().registerReloadListener(id("definitions"), new Reloader());

		PayloadTypeRegistry.clientboundPlay().registerLarge(
				BlasterDefinitionsPayload.TYPE,
				BlasterDefinitionsPayload.CODEC,
				BlasterDefinitionsPayload.MAX_JSON_BYTES + 1024
		);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> synchronize(player));

		ClientboundPlayChannelEvents.REGISTER.register((listener, sender, server, channels) -> {
			// Connection-local receivers can be advertised after the initial join callback.
			if (channels.contains(BlasterDefinitionsPayload.TYPE.id()))
			{
				synchronize(listener.player);
			}
		});
	}

	/**
	 * Freezes all registered types.
	 */
	public static void freezeTypes()
	{
		BlasterStats.freezeTypes();
		BlasterBehaviorProfile.freezeTypes();
		BlasterStatFunctions.freezeTypes();
	}

	/**
	 * Gets the blaster definition baked for the given level.
	 */
	public static BakedBlasterDefinition get(Level level)
	{
		if (level instanceof ServerLevel serverLevel)
		{
			return serverLevel.getServer().getOrThrow(SNAPSHOT).baked();
		}

		return level != null && level.isClientSide() ? _clientLookup.apply(level) : BakedBlasterDefinition.EMPTY;
	}

	/**
	 * Installs client presentation lookup behavior; no server data is stored in this bridge.
	 */
	public static void setClientLookup(Function<Level, BakedBlasterDefinition> lookup)
	{
		_clientLookup = Objects.requireNonNull(lookup);
	}

	/**
	 * Sends an already validated/encoded projection of the installed server generation.
	 */
	private static void synchronize(ServerPlayer player)
	{
		if (ServerPlayNetworking.canSend(player, BlasterDefinitionsPayload.TYPE))
		{
			var prepared = ((DataResourceStore)player.level().getServer()).getOrThrow(SNAPSHOT);
			ServerPlayNetworking.send(player, prepared.payload());
		}
	}

	/**
	 * Creates registry/resource identifiers without initializing item registration.
	 */
	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath("pswg_blasters", path);
	}

	/**
	 * Native reloadable weapon definitions.
	 */
	public static final ResourceKey<Registry<BlasterDatapackDefinition>> BLASTERS = ResourceKey.createRegistryKey(id("blasters"));

	/**
	 * Native reloadable shared attachment definitions.
	 */
	public static final ResourceKey<Registry<BlasterAttachmentDefinition>> ATTACHMENTS = ResourceKey.createRegistryKey(id("attachments"));

	/**
	 * Native reloadable behavior definitions.
	 */
	public static final ResourceKey<Registry<BlasterBehaviorProfile>> BEHAVIOR_PROFILES = ResourceKey.createRegistryKey(id("behavior_profiles"));

	/**
	 * Native reloadable numeric pose definitions.
	 */
	public static final ResourceKey<Registry<BlasterStanceProfile>> STANCE_PROFILES = ResourceKey.createRegistryKey(id("stance_profiles"));

	/**
	 * Per-resource-generation store key, shared across dimensions of one server instance.
	 */
	public static final DataResourceStore.Key<Prepared> SNAPSHOT = new DataResourceStore.Key<>();

	/**
	 * Client-only implementation supplied during client module initialization.
	 */
	private static Function<Level, BakedBlasterDefinition> _clientLookup = level -> BakedBlasterDefinition.EMPTY;

	/**
	 * Utility class.
	 */
	private BlasterData()
	{
	}
}
