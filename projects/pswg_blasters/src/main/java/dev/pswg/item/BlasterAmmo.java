package dev.pswg.item;

import dev.pswg.data.BlasterStats;
import dev.pswg.item.component.StoredCharge;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Blaster ammo transactions.
 */
public final class BlasterAmmo
{
	/**
	 * A simulated inventory debit.
	 */
	public record Transfer(int loaded, List<ItemStack> before, List<ItemStack> after)
	{
		/**
		 * Rechecks the captured inventory before committing changed source/remainder slots.
		 */
		public boolean matches(Inventory inventory)
		{
			for (var slot = 0; slot < before.size(); slot++)
				if (!ItemStack.matches(before.get(slot), inventory.getItem(slot)))
					return false;

			return true;
		}

		/**
		 * Commits only changed slots.
		 */
		public void apply(Inventory inventory)
		{
			for (var slot = 0; slot < before.size(); slot++)
				if (!ItemStack.matches(before.get(slot), after.get(slot)))
					inventory.setItem(slot, after.get(slot));

			inventory.setChanged();
		}
	}

	/**
	 * Gets the charge type.
	 */
	@SuppressWarnings("unchecked")
	private static DataComponentType<StoredCharge> chargeType(BlasterStats.ComponentChargeConsumption consumption)
	{
		var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(consumption.component());
		return type != null && type.codec() == StoredCharge.CODEC ? (DataComponentType<StoredCharge>)type : null;
	}

	/**
	 * Inserts a remainder into native compatible stacks/empty storage slots without dropping or deleting it.
	 */
	private static boolean insert(List<ItemStack> inventory, ItemStack remainder)
	{
		for (var slot = 0; slot < Inventory.INVENTORY_SIZE && !remainder.isEmpty(); slot++)
		{
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

		for (var slot = 0; slot < Inventory.INVENTORY_SIZE && !remainder.isEmpty(); slot++)
			if (inventory.get(slot).isEmpty())
				inventory.set(slot, remainder.split(Math.min(remainder.getCount(), remainder.getMaxStackSize())));

		return remainder.isEmpty();
	}

	/**
	 * Simulates a bounded requested quantity, supporting item counts and arbitrarily component-bearing charged packs.
	 */
	public static Optional<Transfer> prepare(Inventory inventory, ItemStack weapon, BlasterStats.Ammo ammo, int requested)
	{
		if (requested <= 0)
			return Optional.empty();

		var before = new ArrayList<ItemStack>();
		var after = new ArrayList<ItemStack>();
		for (var slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			before.add(inventory.getItem(slot).copy());
			after.add(inventory.getItem(slot).copy());
		}

		var charge = ammo.consumption() instanceof BlasterStats.ComponentChargeConsumption value ? value : null;
		var component = charge == null ? null : chargeType(charge);
		if (charge != null && component == null)
			return Optional.empty();

		var quantum = ammo.feed() instanceof BlasterStats.ChargeStoreFeed ? 1
		                                                                  : charge == null ? ((BlasterStats.ItemCountConsumption)ammo.consumption()).itemsPerRound() : charge.unitsPerRound();
		var limit = (long)requested * quantum;
		var available = 0L;
		for (var slot = 0; slot < before.size(); slot++)
		{
			if (slot >= Inventory.INVENTORY_SIZE && slot != Inventory.SLOT_OFFHAND)
				continue;

			var source = inventory.getItem(slot);
			if (source == weapon || !ammo.ingredient().test(source))
				continue;

			var units = component == null ? source.getCount()
			                              : (long)source.getOrDefault(component, new StoredCharge(0, 1)).current() * source.getCount();
			available += Math.min(units, limit - available);
		}

		var loaded = (int)Math.min(requested, available / quantum);
		if (loaded == 0)
			return Optional.empty();

		var remaining = (long)loaded * quantum;
		for (var slot = 0; slot < after.size() && remaining > 0; slot++)
		{
			if (slot >= Inventory.INVENTORY_SIZE && slot != Inventory.SLOT_OFFHAND)
				continue;

			var source = after.get(slot);
			if (inventory.getItem(slot) == weapon || !ammo.ingredient().test(source))
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

				if (taken == stored.current() && charge.emptyContainer().isPresent())
					remainder = charge.emptyContainer().orElseThrow().create();
				else
					remainder.set(component, new StoredCharge(stored.current() - taken, stored.capacity()));

				if (source.isEmpty())
					after.set(slot, remainder);
				else if (!insert(after, remainder))
					return Optional.empty();

				remaining -= taken;
			}
		}

		return remaining == 0 ? Optional.of(new Transfer(loaded, List.copyOf(before), List.copyOf(after))) : Optional.empty();
	}

	/**
	 * Debits one accepted shot; creative free-ammo policy bypasses all source and loaded-store mutations.
	 */
	public static boolean consumeShot(ServerLevel level, Player player, ItemStack stack, BlasterStats.Ammo ammo)
	{
		if (player.isCreative())
			return true;

		return switch (ammo.feed())
		{
			case BlasterStats.MagazineFeed ignored -> BlasterItem.getLoadedRounds(stack) > 0
			                                          && BlasterItem.setLoadedRounds(level, stack, BlasterItem.getLoadedRounds(stack) - 1);
			case BlasterStats.ChargeStoreFeed ignored ->
			{
				var units = ((BlasterStats.ComponentChargeConsumption)ammo.consumption()).unitsPerRound();
				var current = BlasterItem.getLoadedCharge(stack).map(StoredCharge::current).orElse(0);

				yield current >= units && BlasterItem.setLoadedCharge(level, stack, current - units);
			}
			case BlasterStats.PerShotFeed ignored ->
			{
				var transfer = prepare(player.getInventory(), stack, ammo, 1);
				if (transfer.isEmpty() || !transfer.orElseThrow().matches(player.getInventory()))
					yield false;

				transfer.orElseThrow().apply(player.getInventory());
				yield true;
			}
		};
	}

	/**
	 * Tops up the current feed at reload completion, committing inventory debit only after the weapon accepts the quantity.
	 */
	public static boolean reload(ServerLevel level, Player player, ItemStack stack, BlasterStats.Ammo ammo)
	{
		var current = BlasterItem.getLoadedAmmo(stack, ammo);
		var room = BlasterItem.getAmmoCapacity(ammo) - current;
		if (room <= 0)
			return false;

		var transfer = player.isCreative() ? Optional.<Transfer>empty() : prepare(player.getInventory(), stack, ammo, room);
		if (!player.isCreative() && (transfer.isEmpty() || !transfer.orElseThrow().matches(player.getInventory())))
			return false;

		var loaded = current + (player.isCreative() ? room : transfer.orElseThrow().loaded());
		var accepted = ammo.feed() instanceof BlasterStats.MagazineFeed
		               ? BlasterItem.setLoadedRounds(level, stack, loaded) : BlasterItem.setLoadedCharge(level, stack, loaded);

		if (accepted && transfer.isPresent())
			transfer.orElseThrow().apply(player.getInventory());

		return accepted;
	}

	/**
	 * Utility class.
	 */
	private BlasterAmmo()
	{
	}
}
