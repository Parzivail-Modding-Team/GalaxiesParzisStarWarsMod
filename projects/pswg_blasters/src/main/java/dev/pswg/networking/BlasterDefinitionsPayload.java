package dev.pswg.networking;

import com.google.common.base.Preconditions;
import com.mojang.serialization.JsonOps;
import dev.pswg.data.BakedBlasterDefinition;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.data.BlasterDefinitionData;
import dev.pswg.item.crafting.IngredientSnapshots;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.StrictJsonParser;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Blaster definition payload.
 */
public record BlasterDefinitionsPayload(String json) implements CustomPacketPayload
{
	/**
	 * Serialize
	 */
	public static BlasterDefinitionsPayload prepare(BakedBlasterDefinition snapshot)
	{
		var blasters = new HashMap<Identifier, BlasterDatapackDefinition>();

		for (var entry : snapshot.blasters().entrySet())
		{
			var definition = entry.getValue();
			var stats = definition.stats();
			var ammo = stats.ammo();

			// Resolve ingredients
			var directAmmo = ammo.withIngredient(IngredientSnapshots.resolve(ammo.ingredient(), Map.of()));

			blasters.put(entry.getKey(), definition.withStats(stats.withAmmo(directAmmo)));
		}

		var data = snapshot.data();
		var projection = new BlasterDefinitionData(
				data.generation(),
				blasters,
				data.attachments(),
				data.behaviorProfiles(),
				data.stanceProfiles()
		);

		var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

		var payload = new BlasterDefinitionsPayload(BlasterDefinitionData.CODEC.encodeStart(ops, projection).getOrThrow().toString());
		payload.decodeSnapshot();

		return payload;
	}

	/**
	 * Deserialize
	 */
	public BakedBlasterDefinition decodeSnapshot()
	{
		var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
		var data = BlasterDefinitionData.CODEC.parse(ops, StrictJsonParser.parse(json)).getOrThrow();
		return new BakedBlasterDefinition(data);
	}

	/**
	 * Writes a bounded UTF-8 byte string; the native large-payload transport handles splitting.
	 */
	private void encode(RegistryFriendlyByteBuf buffer)
	{
		buffer.writeByteArray(json.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Reads only the bounded application data before invoking any JSON/definition decoder.
	 */
	private static BlasterDefinitionsPayload decode(RegistryFriendlyByteBuf buffer)
	{
		return new BlasterDefinitionsPayload(new String(buffer.readByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Payload type registered during common module initialization on both physical sides.
	 */
	public static final Type<BlasterDefinitionsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("pswg_blasters", "definitions"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlasterDefinitionsPayload> CODEC = StreamCodec.ofMember(
			BlasterDefinitionsPayload::encode,
			BlasterDefinitionsPayload::decode
	);

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
}
