package dev.pswg.data;

import com.mojang.serialization.Codec;
import dev.pswg.codecgenerator.*;
import dev.pswg.generated.codecs.IBlasterDefinitionDataCodec;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Contains all definition registries used by the common blaster API.
 *
 * @param generation       Identifier for this data generation.
 * @param blasters         Blaster definitions keyed by their registry identifiers.
 * @param attachments      Shared attachment definitions keyed by their registry identifiers.
 * @param behaviorProfiles Shared behavior profiles keyed by their registry identifiers.
 * @param stanceProfiles   Shared numeric stance profiles keyed by their registry identifiers.
 */
@GenerateCodec(strict = true)
public record BlasterDefinitionData(
		@UseCodec(customCodec = @CodecSource(source = BlasterDefinitionData.class, member = "GENERATION_CODEC"))
		String generation,
		@SelfCodec
		Map<Identifier, BlasterDatapackDefinition> blasters,
		@SelfCodec
		Map<Identifier, BlasterAttachmentDefinition> attachments,
		@SelfCodec
		Map<Identifier, BlasterBehaviorProfile> behaviorProfiles,
		@SelfCodec
		Map<Identifier, BlasterStanceProfile> stanceProfiles
) implements IBlasterDefinitionDataCodec
{
	/**
	 * Bounded native string codec for a generation label.
	 */
	public static final Codec<String> GENERATION_CODEC = Codec.sizeLimitedString(256);

	/**
	 * Valid empty client-side value used before the first server synchronization.
	 */
	public static final BlasterDefinitionData EMPTY = new BlasterDefinitionData(
			"empty",
			Map.of(),
			Map.of(),
			Map.of(),
			Map.of()
	);
}
