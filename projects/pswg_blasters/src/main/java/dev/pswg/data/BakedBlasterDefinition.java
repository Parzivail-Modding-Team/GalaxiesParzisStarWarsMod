package dev.pswg.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.pswg.item.component.StoredCharge;
import dev.pswg.item.crafting.IngredientSnapshots;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.*;

/**
 * Fully resolved blaster data for a level.
 */
public final class BakedBlasterDefinition
{
	/**
	 * Empty client-side baked used before the first server synchronization.
	 */
	public static final BakedBlasterDefinition EMPTY = new BakedBlasterDefinition(BlasterDefinitionData.EMPTY);

	/**
	 * Immutable definition payload represented by this baked.
	 */
	private final BlasterDefinitionData _data;

	/**
	 * Cached resolved attachment options, indexed by blaster ID then local option ID.
	 */
	private final Map<Identifier, Map<Identifier, BlasterAttachmentDefinition>> _resolvedAttachments;

	/**
	 * Bakes the definition data.
	 *
	 * @param data Immutable definition data.
	 */
	public BakedBlasterDefinition(BlasterDefinitionData data)
	{
		_data = Objects.requireNonNull(data, "data");
		_resolvedAttachments = validateAndResolve(data);
	}

	/**
	 * Builds a server candidate after resolving item-tag ingredients from the candidate's native tag baked.
	 * Direct item ingredients need no tag entries; a named ingredient tag must be present and non-empty in
	 * {@code candidateTags}.
	 *
	 * @param lookup        Reload-time registry lookup provider.
	 * @param candidateTags Fully resolved item tags for this reload candidate.
	 *
	 * @return A validated immutable server candidate.
	 */
	public static BakedBlasterDefinition fromLookup(
			HolderLookup.Provider lookup,
			Map<TagKey<Item>, List<Holder<Item>>> candidateTags
	)
	{
		Objects.requireNonNull(lookup, "lookup");
		Objects.requireNonNull(candidateTags, "candidateTags");

		var registryName = BlasterData.BLASTERS.identifier().toString();
		var normalizedBlasters = new HashMap<Identifier, BlasterDatapackDefinition>();

		for (var entry : collectRegistry(lookup, BlasterData.BLASTERS).entrySet())
		{
			validateInRegistry(registryName, entry.getKey(), () ->
			{
				var definition = entry.getValue();
				var stats = definition.stats();
				var ammo = stats.ammo();
				var normalizedIngredient = IngredientSnapshots.resolve(ammo.ingredient(), candidateTags);

				normalizedBlasters.put(
						entry.getKey(),
						definition.withStats(stats.withAmmo(ammo.withIngredient(normalizedIngredient)))
				);
			});
		}

		var data = new BlasterDefinitionData(
				UUID.randomUUID().toString(),
				normalizedBlasters,
				collectRegistry(lookup, BlasterData.ATTACHMENTS),
				collectRegistry(lookup, BlasterData.BEHAVIOR_PROFILES),
				collectRegistry(lookup, BlasterData.STANCE_PROFILES)
		);

		var baked = new BakedBlasterDefinition(data);
		baked.validateNativeReferences(lookup);

		return baked;
	}

	/**
	 * Validates native registry references.
	 *
	 * @param lookup Reload-time or server registry lookup provider.
	 */
	public void validateNativeReferences(HolderLookup.Provider lookup)
	{
		Objects.requireNonNull(lookup, "lookup");
		var damageTypes = lookup.lookupOrThrow(Registries.DAMAGE_TYPE);
		var items = lookup.lookupOrThrow(Registries.ITEM);

		for (var entry : _data.behaviorProfiles().entrySet())
		{
			validateInRegistry(BlasterData.BEHAVIOR_PROFILES.identifier().toString(), entry.getKey(), () ->
					Preconditions.checkArgument(
							damageTypes.get(ResourceKey.create(Registries.DAMAGE_TYPE, entry.getValue().damageType())).isPresent(),
							"references missing damage type %s", entry.getValue().damageType()
					)
			);
		}

		for (var entry : _data.blasters().entrySet())
		{
			var ammo = entry.getValue().stats().ammo();

			validateInRegistry(BlasterData.BLASTERS.identifier().toString(), entry.getKey(), () ->
			{
				Preconditions.checkArgument(ammo.creativePolicy().equals(BlasterStats.FREE_AMMO_POLICY),
				                            "references unknown creative ammunition policy %s", ammo.creativePolicy());
				validateNativeIngredient(ammo.ingredient(), items);

				if (ammo.consumption() instanceof BlasterStats.ComponentChargeConsumption(
						Identifier component, int unitsPerRound, Optional<BlasterStats.EmptyContainer> container
				))
				{
					var componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(component);
					Preconditions.checkArgument(componentType.isPresent(),
					                            "references missing data component %s", component);
					Preconditions.checkArgument(isNetworkedPersistentChargeComponent(
							                            componentType.orElseThrow(), unitsPerRound),
					                            "data component %s must persist and synchronize StoredCharge values with capacity at least %s",
					                            component, unitsPerRound);

					container.ifPresent(
							emptyContainer ->
							{
								var item = items.get(ResourceKey.create(Registries.ITEM, emptyContainer.item()));
								Preconditions.checkArgument(item.isPresent(), "references missing empty-container item %s", emptyContainer.item());
								Preconditions.checkArgument(emptyContainer.count() <= item.orElseThrow().value().getDefaultMaxStackSize(),
								                            "emptyContainer.count %s exceeds the maximum stack size for %s",
								                            emptyContainer.count(), emptyContainer.item());
							}
					);
				}
			});
		}
	}

	/**
	 * The baked data.
	 */
	public BlasterDefinitionData data()
	{
		return _data;
	}

	/**
	 * Returns all blaster definitions keyed by registry identifier.
	 */
	public Map<Identifier, BlasterDatapackDefinition> blasters()
	{
		return _data.blasters();
	}

	/**
	 * Returns the cached resolved local attachment options for one blaster.
	 *
	 * @param blasterId Blaster registry identifier.
	 *
	 * @return Resolved attachment definitions, or empty when the blaster ID is unavailable in this generation.
	 */
	public Optional<Map<Identifier, BlasterAttachmentDefinition>> resolvedAttachments(Identifier blasterId)
	{
		return Optional.ofNullable(_resolvedAttachments.get(Objects.requireNonNull(blasterId, "blasterId")));
	}

	/**
	 * Returns all shared attachment definitions keyed by registry identifier.
	 */
	public Map<Identifier, BlasterAttachmentDefinition> attachments()
	{
		return _data.attachments();
	}

	/**
	 * Returns all shared behavior profiles keyed by registry identifier.
	 */
	public Map<Identifier, BlasterBehaviorProfile> behaviorProfiles()
	{
		return _data.behaviorProfiles();
	}

	/**
	 * Returns all shared numeric stance profiles keyed by registry identifier.
	 */
	public Map<Identifier, BlasterStanceProfile> stanceProfiles()
	{
		return _data.stanceProfiles();
	}

	/**
	 * Resolves and validates cross-data references
	 */
	private static Map<Identifier, Map<Identifier, BlasterAttachmentDefinition>> validateAndResolve(BlasterDefinitionData data)
	{
		var stanceProfiles = data.stanceProfiles();
		var behaviorProfiles = data.behaviorProfiles();
		var knownArchetypes = collectProfileArchetypes(stanceProfiles);

		for (var entry : stanceProfiles.entrySet())
		{
			validateInRegistry(BlasterData.STANCE_PROFILES.identifier().toString(), entry.getKey(), () ->
			{
				var codecResult = BlasterStanceProfile.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
				Preconditions.checkArgument(codecResult.error().isEmpty(), "invalid stance profile: %s", codecResult);
			});
		}

		for (var entry : behaviorProfiles.entrySet())
		{
			validateInRegistry(BlasterData.BEHAVIOR_PROFILES.identifier().toString(), entry.getKey(), () ->
			{
				var profile = entry.getValue();
				for (var effect : profile.effects())
				{
					Preconditions.checkArgument(effect.when() != null,
					                            "effect %s must declare an application phase", effect.type());
				}
				var codecResult = BlasterBehaviorProfile.CODEC.encodeStart(JsonOps.INSTANCE, profile);
				Preconditions.checkArgument(codecResult.error().isEmpty(), "invalid behavior profile: %s", codecResult);
			});
		}

		for (var entry : data.attachments().entrySet())
		{
			validateInRegistry(BlasterData.ATTACHMENTS.identifier().toString(), entry.getKey(), () ->
					validateAttachmentReferences(entry.getValue(), entry.getKey().toString(), stanceProfiles, knownArchetypes, behaviorProfiles)
			);
		}

		var resolvedByBlaster = new HashMap<Identifier, Map<Identifier, BlasterAttachmentDefinition>>();
		for (var entry : data.blasters().entrySet())
		{
			validateInRegistry(BlasterData.BLASTERS.identifier().toString(), entry.getKey(), () ->
			{
				var definition = entry.getValue();
				var resolved = definition.attachments().resolve(data.attachments());
				validateWeaponStanceProfile(entry.getKey(), definition.stats(), stanceProfiles);
				var availableModes = collectAvailableModes(entry.getKey(), definition.stats(), resolved, behaviorProfiles);

				for (var attachmentEntry : resolved.entrySet())
				{
					var attachment = attachmentEntry.getValue();
					validateAttachmentReferences(
							attachment,
							"option " + attachmentEntry.getKey(),
							stanceProfiles,
							knownArchetypes,
							behaviorProfiles
					);

					validateAttachmentContextReferences(attachment, attachmentEntry.getKey(), availableModes, knownArchetypes);
					attachment.stanceProfile().ifPresent(
							profileId ->
									validateStanceArchetype(profileId, definition.stats().configuration().archetype(), stanceProfiles,
									                        "option " + attachmentEntry.getKey() + ".stanceProfile")
					);
				}

				validateConversionConditions(entry.getKey(), definition.stats(), resolved, availableModes);
				resolvedByBlaster.put(entry.getKey(), resolved);
			});
		}

		validateConversionTargets(data);
		return resolvedByBlaster;
	}

	/**
	 * Reads one registry into an immutable identifier-to-value map.
	 */
	private static <T> Map<Identifier, T> collectRegistry(
			HolderLookup.Provider lookup,
			ResourceKey<? extends Registry<? extends T>> registryKey
	)
	{
		var values = new HashMap<Identifier, T>();

		for (var holder : lookup.lookupOrThrow(registryKey).listElements().toList())
		{
			var id = holder.key().identifier();
			var previous = values.putIfAbsent(id, holder.value());
			Preconditions.checkArgument(previous == null, "%s contains a duplicate definition ID %s", registryKey.identifier(), id);
		}

		return values;
	}

	/**
	 * Prefixes a validation failure with its registry and definition identifiers.
	 */
	private static void validateInRegistry(String registry, Identifier id, Runnable validation)
	{
		try
		{
			validation.run();
		}
		catch (IllegalArgumentException exception)
		{
			throw new IllegalArgumentException(
					registry + "/" + id + ": " + Objects.requireNonNullElse(exception.getMessage(), "invalid definition"),
					exception
			);
		}
	}

	/**
	 * Returns the set of archetype IDs declared by the numeric stance profiles.
	 */
	private static Set<Identifier> collectProfileArchetypes(Map<Identifier, BlasterStanceProfile> stanceProfiles)
	{
		var archetypes = new HashSet<Identifier>();

		for (var profile : stanceProfiles.values())
			archetypes.add(profile.archetype());

		return archetypes;
	}

	/**
	 * Validates the references an attachment can express without knowing a containing blaster.
	 */
	private static void validateAttachmentReferences(
			BlasterAttachmentDefinition attachment,
			String field,
			Map<Identifier, BlasterStanceProfile> stanceProfiles,
			Set<Identifier> knownArchetypes,
			Map<Identifier, BlasterBehaviorProfile> behaviorProfiles
	)
	{
		attachment.stanceProfile().ifPresent(
				profileId ->
						Preconditions.checkArgument(stanceProfiles.containsKey(profileId),
						                            "%s.stanceProfile references missing stance profile %s", field, profileId)
		);
		attachment.grantedModes().ifPresent(
				modes -> modes.forEach(mode ->
						                       Preconditions.checkArgument(behaviorProfiles.containsKey(mode.behaviorProfile()),
						                                                   "%s.grantedModes[%s] references missing behavior profile %s",
						                                                   field, mode.id(), mode.behaviorProfile()))
		);

		for (var modifier : attachment.effectiveModifiers())
		{
			for (var archetype : modifier.modifierCondition().archetype())
			{
				Preconditions.checkArgument(knownArchetypes.contains(archetype),
				                            "%s modifier context references archetype %s without a stance profile", field, archetype);
			}
		}
	}

	/**
	 * Validates the selected weapon stance profile and all option stance-profile archetype pairings.
	 */
	private static void validateWeaponStanceProfile(
			Identifier blasterId,
			BlasterStats stats,
			Map<Identifier, BlasterStanceProfile> stanceProfiles
	)
	{
		var configuration = stats.configuration();
		var selectedProfileId = configuration.stanceProfile().orElse(configuration.archetype());
		var selectedProfile = stanceProfiles.get(selectedProfileId);

		Preconditions.checkArgument(selectedProfile != null,
		                            "blaster %s resolves to missing stance profile %s for archetype %s",
		                            blasterId, selectedProfileId, configuration.archetype());
		Preconditions.checkArgument(selectedProfile.archetype().equals(configuration.archetype()),
		                            "blaster %s stance profile %s declares archetype %s, expected %s",
		                            blasterId, selectedProfileId, selectedProfile.archetype(), configuration.archetype());
	}

	/**
	 * Requires a named stance profile to describe the containing weapon's archetype.
	 */
	private static void validateStanceArchetype(
			Identifier profileId,
			Identifier archetype,
			Map<Identifier, BlasterStanceProfile> stanceProfiles,
			String field
	)
	{
		var profile = stanceProfiles.get(profileId);
		Preconditions.checkArgument(profile != null, "%s references missing stance profile %s", field, profileId);
		Preconditions.checkArgument(profile.archetype().equals(archetype),
		                            "%s profile %s declares archetype %s, expected %s", field, profileId, profile.archetype(), archetype);
	}

	/**
	 * Builds the available mode union and validates profile references, conflicts, and its total size.
	 */
	private static Map<Identifier, BlasterStats.Mode> collectAvailableModes(
			Identifier blasterId,
			BlasterStats stats,
			Map<Identifier, BlasterAttachmentDefinition> attachments,
			Map<Identifier, BlasterBehaviorProfile> behaviorProfiles
	)
	{
		var modes = new HashMap<Identifier, BlasterStats.Mode>();

		for (var mode : stats.modes().options())
			addAvailableMode(modes, mode, "base modes of " + blasterId);

		for (var attachmentEntry : attachments.entrySet())
		{
			attachmentEntry.getValue().grantedModes().ifPresent(
					grants -> grants.forEach(mode ->
							                         addAvailableMode(modes, mode, "attachment option " + attachmentEntry.getKey() + " of " + blasterId))
			);
		}

		for (var entry : modes.entrySet())
		{
			var mode = entry.getValue();
			var behaviorProfile = behaviorProfiles.get(mode.behaviorProfile());
			Preconditions.checkArgument(behaviorProfile != null,
			                            "mode %s references missing behavior profile %s", mode.id(), mode.behaviorProfile());
			validateChargedShot(blasterId, mode, behaviorProfile, stats.ammo());
		}

		return modes;
	}

	/**
	 * Adds a mode unless another source gives the same ID a different definition.
	 */
	private static void addAvailableMode(Map<Identifier, BlasterStats.Mode> modes, BlasterStats.Mode mode, String source)
	{
		var previous = modes.putIfAbsent(mode.id(), mode);
		Preconditions.checkArgument(previous == null || previous.equals(mode),
		                            "mode %s from %s conflicts with another base or granted mode definition", mode.id(), source);
	}

	/**
	 * Validates charged-shot source and consumption against the mode trigger and ammunition feed.
	 */
	private static void validateChargedShot(
			Identifier blasterId,
			BlasterStats.Mode mode,
			BlasterBehaviorProfile profile,
			BlasterStats.Ammo ammo
	)
	{
		if (profile.chargedShot().isEmpty())
			return;

		var chargedShot = profile.chargedShot().orElseThrow();
		if (chargedShot.source().equals("held_duration"))
		{
			Preconditions.checkArgument(mode.trigger() instanceof BlasterStats.ChargeTrigger,
			                            "mode %s of %s uses held_duration charged damage but does not have a charge trigger", mode.id(), blasterId);
		}
		else
		{
			Preconditions.checkArgument(ammo.feed().equals(BlasterStats.FEED_CHARGE_STORE)
			                            && ammo.consumption() instanceof BlasterStats.ComponentChargeConsumption,
			                            "mode %s of %s uses loaded_component_charge without a component-charge ammunition feed", mode.id(), blasterId);
		}

		if (chargedShot.consume().equals("all_remaining_component_charge"))
		{
			Preconditions.checkArgument(chargedShot.source().equals("loaded_component_charge")
			                            && ammo.feed().equals(BlasterStats.FEED_CHARGE_STORE),
			                            "mode %s of %s consumes all component charge without a loaded component-charge source", mode.id(), blasterId);
		}
	}

	/**
	 * Validates attachment modifier mode-context references against one blaster's available mode union.
	 */
	private static void validateAttachmentContextReferences(
			BlasterAttachmentDefinition attachment,
			Identifier optionId,
			Map<Identifier, BlasterStats.Mode> availableModes,
			Set<Identifier> knownArchetypes
	)
	{
		for (var modifier : attachment.effectiveModifiers())
		{
			var context = modifier.modifierCondition();
			for (var modeId : context.mode())
			{
				Preconditions.checkArgument(availableModes.containsKey(modeId),
				                            "attachment option %s modifier context references unavailable mode %s", optionId, modeId);
			}
			for (var archetype : context.archetype())
			{
				Preconditions.checkArgument(knownArchetypes.contains(archetype),
				                            "attachment option %s modifier context references archetype %s without a stance profile",
				                            optionId, archetype);
			}
		}
	}

	/**
	 * Validates all conversion conditions against the containing blaster's modes, attachments, and charge feed.
	 */
	private static void validateConversionConditions(
			Identifier blasterId,
			BlasterStats stats,
			Map<Identifier, BlasterAttachmentDefinition> attachments,
			Map<Identifier, BlasterStats.Mode> availableModes
	)
	{
		var fieldConversion = stats.configuration().fieldConversion();
		if (fieldConversion.isPresent())
		{
			for (var option : fieldConversion.orElseThrow().options())
			{
				for (var condition : option.conditions())
				{
					if (condition instanceof BlasterStats.ModeIsCondition(Identifier modeId))
					{
						Preconditions.checkArgument(availableModes.containsKey(modeId),
						                            "fieldConversion option %s references unavailable mode %s",
						                            option.id(), modeId);
					}
					else if (condition instanceof BlasterStats.HasAttachmentCondition(
							Identifier attachmentSlot, Identifier attachmentId
					))
					{
						var attachment = attachments.get(attachmentId);
						Preconditions.checkArgument(attachment != null,
						                            "fieldConversion option %s references unavailable attachment %s",
						                            option.id(), attachmentId);
						Preconditions.checkArgument(attachment.slots().contains(attachmentSlot),
						                            "fieldConversion option %s attachment %s is not compatible with slot %s",
						                            option.id(), attachmentId, attachmentSlot);
					}
					else if (condition instanceof BlasterStats.LoadedChargeMinCondition(int units))
					{
						Preconditions.checkArgument(stats.ammo().feed().equals(BlasterStats.FEED_CHARGE_STORE),
						                            "fieldConversion option %s requires a charge-store feed for loaded_charge_min",
						                            option.id());
						var capacity = stats.ammo().chargeCapacityUnits().orElseThrow();
						Preconditions.checkArgument(units <= capacity,
						                            "fieldConversion option %s loaded_charge_min.units %s exceeds charge capacity %s",
						                            option.id(), units, capacity);
					}
				}
			}
		}

		if (stats.ammo().feed().equals(BlasterStats.FEED_CHARGE_STORE)
		    && stats.ammo().consumption() instanceof BlasterStats.ComponentChargeConsumption consumption)
		{
			var capacity = stats.ammo().chargeCapacityUnits().orElseThrow();
			Preconditions.checkArgument(consumption.unitsPerRound() <= capacity,
			                            "component-charge unitsPerRound %s exceeds charge capacity %s on %s",
			                            consumption.unitsPerRound(), capacity, blasterId);
		}
	}

	/**
	 * Validates field-conversion targets and requires each target to be a terminal definition.
	 */
	private static void validateConversionTargets(BlasterDefinitionData data)
	{
		for (var entry : data.blasters().entrySet())
		{
			var options = entry.getValue().stats().configuration().fieldConversion();
			if (options.isEmpty())
				continue;

			for (var option : options.orElseThrow().options())
			{
				validateInRegistry(BlasterData.BLASTERS.identifier().toString(), entry.getKey(), () ->
				{
					Preconditions.checkArgument(!option.targetBlasterId().equals(entry.getKey()),
					                            "fieldConversion option %s targets its source blaster", option.id());
					var target = data.blasters().get(option.targetBlasterId());
					Preconditions.checkArgument(target != null,
					                            "fieldConversion option %s references missing target blaster %s", option.id(), option.targetBlasterId());
					Preconditions.checkArgument(target.stats().configuration().fieldConversion().isEmpty(),
					                            "fieldConversion option %s targets blaster %s which declares another conversion",
					                            option.id(), option.targetBlasterId());
				});
			}
		}
	}

	/**
	 * Validates that a native ingredient resolves to bounded, registered, non-air concrete items.
	 */
	private static void validateNativeIngredient(Ingredient ingredient, HolderLookup.RegistryLookup<Item> items)
	{
		var ingredientHolders = ((SlotDisplay.TagSlotDisplay)ingredient.display()).tag().unwrap().map(
				tag -> {
					throw new IllegalArgumentException("ammunition ingredient tag " + tag + " was not resolved from candidate tags");
				},
				directItems -> directItems
		);
		Preconditions.checkArgument(!ingredientHolders.isEmpty(), "ammunition ingredient resolves to an empty item set");
		var concreteItems = ingredientHolders.stream().distinct().toList();
		Preconditions.checkArgument(!concreteItems.isEmpty(), "ammunition ingredient resolves to an empty item set");
		for (var item : concreteItems)
		{
			var key = item.unwrapKey();
			Preconditions.checkArgument(item.value() != Items.AIR,
			                            "ammunition ingredient must not include air (%s)", key.map(ResourceKey::identifier).orElse(Items.AIR.builtInRegistryHolder().key().identifier()));
			Preconditions.checkArgument(key.isPresent(), "ammunition ingredient contains an unkeyed item %s", item.value());
			var itemKey = key.orElseThrow();
			Preconditions.checkArgument(items.get(itemKey).isPresent(),
			                            "ammunition ingredient references item %s not registered in the server lookup", itemKey.identifier());
		}
	}

	/**
	 * Requires a component type with a persistent {@link StoredCharge} codec and a network stream codec.
	 */
	private static boolean isNetworkedPersistentChargeComponent(DataComponentType<?> componentType, int minimumCapacity)
	{
		var codec = componentType.codec();
		if (codec == null)
			return false;

		try
		{
			var chargeValue = new JsonObject();
			chargeValue.addProperty("current", 0);
			chargeValue.addProperty("capacity", minimumCapacity);
			var parseResult = codec.parse(JsonOps.INSTANCE, chargeValue);
			return parseResult.error().isEmpty() && parseResult.result().filter(StoredCharge.class::isInstance).isPresent();
		}
		catch (RuntimeException exception)
		{
			return false;
		}
	}
}
