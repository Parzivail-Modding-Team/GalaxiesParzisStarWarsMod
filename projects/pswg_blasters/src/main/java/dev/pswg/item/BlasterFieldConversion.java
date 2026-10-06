package dev.pswg.item;

import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.data.BakedBlasterDefinition;
import dev.pswg.data.BlasterAttachmentDefinition;
import dev.pswg.data.BlasterStats;
import dev.pswg.generated.codecs.IBlasterFieldConversionCodec;
import dev.pswg.item.component.StoredCharge;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

/**
 * Persistent field-conversion selection. The base weapon ID and mode preference remain separate components.
 *
 * @param activeOptionId    Option in the base definition's conversion list.
 * @param startedAtGameTime Server game-time clock used by timed conversions.
 */
@GenerateCodec(strict = true)
public record BlasterFieldConversion(
		Identifier activeOptionId,
		@CodecRange(min = 0) long startedAtGameTime
) implements IBlasterFieldConversionCodec
{
	/**
	 * Checks if the given field-conversion attachments can fit on this blaster.
	 */
	public static boolean canAttachmentsFit(Map<Identifier, BlasterAttachmentDefinition> options, Map<Identifier, Identifier> applied)
	{
		for (var entry : applied.entrySet())
		{
			var option = options.get(entry.getValue());
			if (option == null || !option.slots().contains(entry.getKey()))
				return false;
		}
		return true;
	}

	/**
	 * Checks if the blaster's ammo can be converted to the given ammo.
	 */
	public static boolean isAmmoCompatible(BlasterStats.Ammo source, BlasterStats.Ammo target, int rounds, int charge)
	{
		return (rounds == 0 && charge == 0)
		       || (source.feed().type() == target.feed().type()
		           && source.ingredient().equals(target.ingredient())
		           && source.consumption().equals(target.consumption()));
	}

	/**
	 * Tests source-loadout conditions.
	 */
	private static boolean matches(BlasterStats.FieldConversionCondition condition, BlasterLoadout source, Map<Identifier, Identifier> applied, int charge)
	{
		return switch (condition)
		{
			case BlasterStats.ModeIsCondition mode -> source.selectedMode().id().equals(mode.modeId());
			case BlasterStats.HasAttachmentCondition attachment ->
					attachment.attachmentId().equals(applied.get(attachment.attachmentSlot()))
					&& source.activeAttachments().containsKey(attachment.attachmentSlot());
			case BlasterStats.LoadedChargeMinCondition minimum -> charge >= minimum.units();
		};
	}

	/**
	 * Resolves a terminal target from one snapshot. Invalid, missing or expired selections resolve inactive.
	 * Capacity reductions are intentionally not a resolution failure: surplus ammo survives reload/reversion.
	 */
	public Optional<BlasterLoadout> resolve(
			BakedBlasterDefinition snapshot,
			BlasterLoadout source,
			Map<Identifier, Identifier> applied,
			Optional<Identifier> preferredMode,
			long gameTime,
			Optional<StoredCharge> loadedCharge
	)
	{
		// Ignore empty or pending conversions
		var conversions = source.definition().stats().configuration().fieldConversion();
		if (conversions.isEmpty() || gameTime < startedAtGameTime)
			return Optional.empty();

		// Ignore conversions that don't match the active one
		var option = conversions.orElseThrow().options().stream().filter(value -> value.id().equals(activeOptionId)).findFirst();
		if (option.isEmpty())
			return Optional.empty();

		// Ignore expired conversions
		var selected = option.orElseThrow();
		if (selected.durationTicks() > 0 && gameTime - startedAtGameTime >= selected.durationTicks())
			return Optional.empty();

		var charge = loadedCharge.map(StoredCharge::current).orElse(0);

		// Ignore conversions that don't match the conversion condition criteria
		for (var condition : selected.conditions())
			if (!matches(condition, source, applied, charge))
				return Optional.empty();

		var target = snapshot.blasters().get(selected.targetBlasterId());
		var options = snapshot.resolvedAttachments(selected.targetBlasterId());

		// Ignore conversions when the attachments don't fit
		if (target == null || options.isEmpty() || !canAttachmentsFit(options.orElseThrow(), applied))
			return Optional.empty();

		var targetLoadout = BlasterLoadout.resolve(target, options.orElseThrow(), applied, preferredMode);

		return Optional.of(new BlasterLoadout(
				targetLoadout.definition(),
				targetLoadout.activeAttachments(),
				targetLoadout.activeOptions(),
				targetLoadout.availableModes(),
				targetLoadout.selectedMode(),
				Optional.of(activeOptionId)
		));
	}
}
