package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import dev.pswg.codecgenerator.CodecDefault;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.generated.codecs.IBlasterAttachmentSetCodec;
import dev.pswg.generated.codecs.IReferenceFieldsCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Blaster attachment options and defaults.
 *
 * @param hud      HUD renderer ID; defaults to {@code pswg_blasters:default}.
 * @param defaults Slot-ID to attachment-ID selections applied to new blasters.
 * @param options  Attachment-ID to inline or shared-reference option map.
 */
@GenerateCodec(strict = true)
public record BlasterAttachmentSet(
		@CodecDefault("dev.pswg.data.BlasterAttachmentSet.DEFAULT_HUD")
		Identifier hud,
		@SelfCodec
		@CodecDefault("java.util.Map.of()")
		Map<Identifier, Identifier> defaults,
		@SelfCodec
		@CodecDefault("java.util.Map.of()")
		Map<Identifier, Option> options
) implements IBlasterAttachmentSetCodec
{
	/**
	 * Inline or shared-reference option value accepted by the option-map codec.
	 */
	public sealed interface Option permits Inline, Reference
	{
		/**
		 * Codec that tries a strict inline definition, then a strict reference-only object.
		 */
		Codec<Option> CODEC = Codec.either(BlasterAttachmentDefinition.CODEC, ReferenceFields.CODEC)
		                           .xmap(
				                           value -> value.map(Inline::new, fields -> new Reference(fields.reference())),
				                           option -> {
					                           if (option instanceof Inline(BlasterAttachmentDefinition definition))
						                           return Either.left(definition);
					                           var reference = (Reference)option;
					                           return Either.right(new ReferenceFields(reference.referenceId()));
				                           }
		                           );
		/**
		 * Binary option form; the boolean discriminates inline from shared reference.
		 */
		StreamCodec<RegistryFriendlyByteBuf, Option> PACKET_CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().dispatch(
				option -> option instanceof Inline,
				inline -> inline
				          ? BlasterAttachmentDefinition.PACKET_CODEC.map(Inline::new, Inline::definition)
				          : Identifier.STREAM_CODEC.map(Reference::new, Reference::referenceId)
		);
	}

	/**
	 * Option containing a complete inline attachment definition.
	 */
	public record Inline(BlasterAttachmentDefinition definition) implements Option
	{
	}

	/**
	 * Option naming a shared attachment definition by its resource ID.
	 */
	public record Reference(Identifier referenceId) implements Option
	{
	}

	/**
	 * Strict authoring shape for a shared reference, containing no inline-definition fields.
	 */
	@GenerateCodec(packetCodec = false, strict = true)
	public record ReferenceFields(Identifier reference) implements IReferenceFieldsCodec
	{
	}

	/**
	 * Default attachment HUD ID.
	 */
	public static final Identifier DEFAULT_HUD = Blasters.id("default");

	/**
	 * Resolves local options against the shared attachment table and validates defaults and cross-option conflicts.
	 *
	 * @param attachmentDefinitions Shared attachment definitions keyed by resource ID.
	 *
	 * @return Immutable attachment definitions keyed by this set's local option IDs.
	 */
	public Map<Identifier, BlasterAttachmentDefinition> resolve(Map<Identifier, BlasterAttachmentDefinition> attachmentDefinitions)
	{
		var resolvedAttachments = new HashMap<Identifier, BlasterAttachmentDefinition>();

		for (var entry : options.entrySet())
		{
			var optionId = entry.getKey();
			var option = entry.getValue();

			if (option instanceof Inline(BlasterAttachmentDefinition inlineDefinition))
			{
				resolvedAttachments.put(optionId, inlineDefinition);
				continue;
			}

			var reference = (Reference)option;
			Preconditions.checkArgument(
					optionId.equals(reference.referenceId()),
					"attachments.options[%s].reference must match its option key (was %s)", optionId, reference.referenceId()
			);

			var definition = attachmentDefinitions.get(reference.referenceId());
			Preconditions.checkArgument(
					definition != null,
					"attachments.options[%s] references missing shared attachment %s", optionId, reference.referenceId()
			);

			resolvedAttachments.put(optionId, definition);
		}

		for (var entry : defaults.entrySet())
		{
			var attachment = resolvedAttachments.get(entry.getValue());
			Preconditions.checkArgument(attachment != null && attachment.slots().contains(entry.getKey()),
			                            "Default attachment %s does not resolve in slot %s", entry.getValue(), entry.getKey());
		}

		return resolvedAttachments;
	}
}
