package dev.pswg.item.crafting;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Resolves native item ingredients from tags.
 */
public final class IngredientSnapshots
{
	/**
	 * Prevents instances of this static ingredient-snapshot utility.
	 */
	private IngredientSnapshots()
	{
	}

	/**
	 * Resolves an ingredient's named item tag from the candidate snapshot and freezes its holders as direct items.
	 * Native ingredients expose their holder set through the public slot display. Custom predicate ingredients
	 * cannot be frozen as an item-only set without changing their semantics, so they are rejected.
	 *
	 * @param ingredient    Native ingredient with direct items or an item tag.
	 * @param candidateTags Fully resolved item-tag holders for one candidate reload.
	 *
	 * @return An ingredient backed by direct built-in item holders, with no dependency on pending tag binding.
	 *
	 * @throws IllegalArgumentException if a referenced tag is missing or empty, or if a resolved ingredient is empty/air.
	 */
	public static Ingredient resolve(Ingredient ingredient, Map<TagKey<Item>, List<Holder<Item>>> candidateTags)
	{
		Objects.requireNonNull(ingredient, "ingredient");
		Objects.requireNonNull(candidateTags, "candidateTags");

		Preconditions.checkArgument(!((Object)ingredient instanceof FabricIngredient extension)
		                            || extension.getCustomIngredient() == null, "Custom predicate ingredients cannot be snapshotted as item membership");
		Preconditions.checkArgument(ingredient.display() instanceof SlotDisplay.TagSlotDisplay,
		                            "Ingredient must expose a native item-list/tag display");

		var display = (SlotDisplay.TagSlotDisplay)ingredient.display();
		var holders = display.tag().unwrap().map(
				tag -> {
					var tagHolders = candidateTags.getOrDefault(tag, List.of());
					Preconditions.checkArgument(tagHolders != null && !tagHolders.isEmpty(),
					                            "Ingredient references missing or empty item tag %s", tag);
					return tagHolders;
				},
				directHolders -> directHolders
		);

		Preconditions.checkArgument(!holders.isEmpty(), "Ingredient resolves to an empty item set");
		for (var holder : holders)
			Preconditions.checkArgument(holder.value() != Items.AIR, "Ingredient must not include air");

		return Ingredient.of(holders.stream().map(Holder::value));
	}
}
