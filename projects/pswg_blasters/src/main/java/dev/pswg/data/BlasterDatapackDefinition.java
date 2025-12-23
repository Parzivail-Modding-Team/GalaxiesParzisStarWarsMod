package dev.pswg.data;

import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.generated.codecs.IBlasterDatapackDefinitionCodec;
import dev.pswg.item.BlasterItem;

/**
 * Defines the format for a blaster datapack JSON entry
 *
 * @param stats       The stats the blaster preset will use
 * @param attachments The attachments the blaster preset will use
 */
@GenerateCodec
public record BlasterDatapackDefinition(
		@SelfCodec BlasterItem.StatsComponent stats,
		@SelfCodec BlasterItem.AvailableAttachmentsComponent attachments
) implements IBlasterDatapackDefinitionCodec
{
}
