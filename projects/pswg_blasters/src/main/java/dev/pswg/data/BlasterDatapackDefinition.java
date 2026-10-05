package dev.pswg.data;

import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.generated.codecs.IBlasterDatapackDefinitionCodec;
import dev.pswg.generated.recordbuilders.IBlasterDatapackDefinitionBuilder;
import dev.pswg.mutablerecord.MutableRecord;

/**
 * Defines the common data format for one blaster datapack entry.
 *
 * @param stats Stats, ammunition, modes, and configuration for this blaster.
 * @param attachments HUD, defaults, and attachment options for this blaster.
 */
@GenerateCodec(strict = true)
@MutableRecord
public record BlasterDatapackDefinition(
		@SelfCodec BlasterStats stats,
		@SelfCodec BlasterAttachmentSet attachments
) implements IBlasterDatapackDefinitionCodec, IBlasterDatapackDefinitionBuilder
{
}
