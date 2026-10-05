package dev.pswg.networking;

import dev.pswg.Blasters;
import dev.pswg.data.BakedBlasterDefinition;
import dev.pswg.data.BlasterDefinitionData;
import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Standalone ID/value projection encoded with generated and native packet codecs.
 */
public record BlasterDefinitionsPayload(BlasterDefinitionData data) implements CustomPacketPayload
{
	/**
	 * Preflights the exact binary payload against the candidate registry context before it can be published.
	 */
	public static BlasterDefinitionsPayload prepare(BakedBlasterDefinition snapshot, HolderLookup.Provider lookup)
	{
		var registries = lookup.listRegistries().map(
				registry ->
				{
					HolderLookup.RegistryLookup<?> current = registry;
					while (current instanceof HolderLookup.RegistryLookup.Delegate<?> delegate)
						current = delegate.parent();
					return (Registry<?>)current;
				}
		).toList();

		var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), new RegistryAccess.ImmutableRegistryAccess(registries));
		try
		{
			var payload = new BlasterDefinitionsPayload(snapshot.data());
			CODEC.encode(buffer, payload);
			return payload;
		}
		finally
		{
			buffer.release();
		}
	}

	/**
	 * Resolves projection references once; decoding has already produced the typed data.
	 */
	public BakedBlasterDefinition decodeSnapshot()
	{
		return new BakedBlasterDefinition(data);
	}

	/**
	 * Maximum binary definition data, exclusive of its small length prefix.
	 */
	public static final int MAX_PACKET_BYTES = 8 * 1024 * 1024;

	/**
	 * Common payload type.
	 */
	public static final Type<BlasterDefinitionsPayload> TYPE = new Type<>(Blasters.id("definitions"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlasterDefinitionsPayload> CODEC = BlasterDefinitionData.PACKET_CODEC.map(BlasterDefinitionsPayload::new, BlasterDefinitionsPayload::data);

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
