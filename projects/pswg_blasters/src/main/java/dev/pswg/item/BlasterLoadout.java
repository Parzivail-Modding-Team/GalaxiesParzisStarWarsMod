package dev.pswg.item;

import dev.pswg.data.BlasterAttachmentDefinition;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.data.BlasterStats;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Resolved, read-only loadout for one definition generation and physical stack's selections.
 * This derived view is not stored or synchronized independently of its source components.
 *
 * @param activeAttachments Slot-compatible installed attachments, keyed by slot.
 * @param availableModes    Base modes in authored order, followed by installed attachment grants.
 * @param selectedMode      Available preferred mode, or the definition's default when unavailable.
 */
public record BlasterLoadout(
		Map<Identifier, BlasterAttachmentDefinition> activeAttachments,
		List<BlasterStats.Mode> availableModes,
		BlasterStats.Mode selectedMode
)
{
	/**
	 * Resolves already validated definitions without mutating component selections.
	 * Grants follow attachment option ID order, retain their authored order, and occur once per mode ID.
	 * An option installed in several compatible slots contributes its grants once.
	 *
	 * @param definition    Current weapon definition.
	 * @param options       Current resolved inline/shared attachment options.
	 * @param applied       Stored slot-to-option selections, including any stale selections.
	 * @param preferredMode Stored preference; absence or unavailability resolves to the base default.
	 *
	 * @return Immutable active attachments, available modes and resolved selection.
	 */
	public static BlasterLoadout resolve(
			BlasterDatapackDefinition definition,
			Map<Identifier, BlasterAttachmentDefinition> options,
			Map<Identifier, Identifier> applied,
			Optional<Identifier> preferredMode
	)
	{
		var activeSlots = new HashMap<Identifier, BlasterAttachmentDefinition>();
		var activeOptions = new TreeMap<Identifier, BlasterAttachmentDefinition>();

		for (var entry : applied.entrySet())
		{
			var option = options.get(entry.getValue());
			if (option != null && option.slots().contains(entry.getKey()))
			{
				activeSlots.put(entry.getKey(), option);
				activeOptions.put(entry.getValue(), option);
			}
		}

		var modes = new LinkedHashMap<Identifier, BlasterStats.Mode>();
		for (var mode : definition.stats().modes().options())
			modes.put(mode.id(), mode);

		for (var option : activeOptions.values())
			option.grantedModes().ifPresent(grants -> grants.forEach(mode -> modes.putIfAbsent(mode.id(), mode)));

		var selected = preferredMode.map(modes::get)
		                            .orElseGet(() -> modes.get(definition.stats().modes().defaultMode()));

		return new BlasterLoadout(Map.copyOf(activeSlots), List.copyOf(modes.values()), selected);
	}

	/**
	 * Finds an available mode for selection validation or a mode-selection UI.
	 */
	public Optional<BlasterStats.Mode> findMode(Identifier modeId)
	{
		return availableModes.stream().filter(mode -> mode.id().equals(modeId)).findFirst();
	}
}
