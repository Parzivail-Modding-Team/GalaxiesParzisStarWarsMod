package dev.pswg.item;

import dev.pswg.item.component.StoredCharge;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simulates conserved item-count or stored-charge withdrawals from native containers.
 */
public final class InventoryResourceTransfer
{
	/**
	 * A simulated inventory debit.
	 */
	public record Transfer(int quantity, long debitedUnits, List<ItemStack> before, List<ItemStack> after)
	{
		/**
		 * Rechecks the captured inventory before committing changed source/remainder slots.
		 */
		public boolean matches(Container inventory)
		{
			for (var slot = 0; slot < before.size(); slot++)
				if (!ItemStack.matches(before.get(slot), inventory.getItem(slot)))
					return false;

			return true;
		}

		/**
		 * Commits only changed slots.
		 */
		public void apply(Container inventory)
		{
			for (var slot = 0; slot < before.size(); slot++)
				if (!ItemStack.matches(before.get(slot), after.get(slot)))
					inventory.setItem(slot, after.get(slot));

			inventory.setChanged();
		}
	}

	/**
	 * Inserts a remainder into native compatible stacks/empty storage slots without dropping or deleting it.
	 */
	private static boolean insert(List<ItemStack> inventory, List<Integer> remainderSlots, ItemStack remainder)
	{
		for (var slot : remainderSlots)
		{
			if (remainder.isEmpty())
				break;
			var current = inventory.get(slot);
			if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, remainder))
			{
				var count = Math.min(remainder.getCount(), current.getMaxStackSize() - current.getCount());
				if (count > 0)
				{
					current.grow(count);
					remainder.shrink(count);
				}
			}
		}

		for (var slot : remainderSlots)
		{
			if (remainder.isEmpty())
				break;
			if (inventory.get(slot).isEmpty())
				inventory.set(slot, remainder.split(Math.min(remainder.getCount(), remainder.getMaxStackSize())));
		}

		return remainder.isEmpty();
	}

	/**
	 * Simulates a requested number of quanta without mutating the container. A null component selects item counts;
	 * otherwise the registered StoredCharge representation supplies units. Slot lists are caller-owned access policy.
	 */
	public static Optional<Transfer> prepare(
			Container inventory,
			List<Integer> sourceSlots,
			List<Integer> remainderSlots,
			ItemStack excluded,
			Ingredient ingredient,
			@Nullable DataComponentType<StoredCharge> component,
			int unitsPerQuantity,
			Optional<ItemStackTemplate> emptyContainer,
			int requested
	)
	{
		if (requested <= 0 || unitsPerQuantity <= 0 || (component != null && component.codec() != StoredCharge.CODEC))
			return Optional.empty();

		var before = new ArrayList<ItemStack>();
		var after = new ArrayList<ItemStack>();
		for (var slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			before.add(inventory.getItem(slot).copy());
			after.add(inventory.getItem(slot).copy());
		}

		var quantum = unitsPerQuantity;
		var limit = (long)requested * quantum;
		var available = 0L;
		for (var slot : sourceSlots)
		{
			var source = inventory.getItem(slot);
			if (source == excluded || !ingredient.test(source))
				continue;

			var units = component == null ? source.getCount()
			                              : (long)source.getOrDefault(component, new StoredCharge(0, 1)).current() * source.getCount();
			available += Math.min(units, limit - available);
		}

		var loaded = (int)Math.min(requested, available / quantum);
		if (loaded == 0)
			return Optional.empty();

		var remaining = (long)loaded * quantum;
		for (var slot : sourceSlots)
		{
			if (remaining == 0)
				break;

			var source = after.get(slot);
			if (inventory.getItem(slot) == excluded || !ingredient.test(source))
				continue;

			if (component == null)
			{
				var taken = (int)Math.min(remaining, source.getCount());
				source.shrink(taken);
				remaining -= taken;

				continue;
			}

			var stored = source.get(component);
			if (stored == null || stored.current() == 0)
				continue;

			var prototype = source.copyWithCount(1);
			var count = source.getCount();
			for (var index = 0; index < count && remaining > 0; index++)
			{
				var taken = (int)Math.min(remaining, stored.current());
				source.shrink(1);
				var remainder = prototype.copy();

				if (taken == stored.current() && emptyContainer.isPresent())
					remainder = emptyContainer.orElseThrow().create();
				else
					remainder.set(component, new StoredCharge(stored.current() - taken, stored.capacity()));

				if (source.isEmpty())
					after.set(slot, remainder);
				else if (!insert(after, remainderSlots, remainder))
					return Optional.empty();

				remaining -= taken;
			}
		}

		return remaining == 0 ? Optional.of(new Transfer(loaded, (long)loaded * quantum, List.copyOf(before), List.copyOf(after))) : Optional.empty();
	}

	/**
	 * Utility class.
	 */
	private InventoryResourceTransfer()
	{
	}
}
