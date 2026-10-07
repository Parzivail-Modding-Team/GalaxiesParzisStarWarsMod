package dev.pswg.item;

import dev.pswg.data.BlasterStats;
import dev.pswg.item.component.StoredCharge;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Blaster ammo transactions.
 */
public final class BlasterAmmo
{
	/**
	 * Source-unit cost of one stored round (or one raw charge-store unit).
	 */
	public static int unitsPerLoadedQuantity(BlasterStats.Ammo ammo)
	{
		if (ammo.feed() instanceof BlasterStats.ChargeStoreFeed)
			return 1;
		return switch (ammo.consumption())
		{
			case BlasterStats.ItemCountConsumption count -> count.itemsPerRound();
			case BlasterStats.ComponentChargeConsumption charge -> charge.unitsPerRound();
		};
	}

	/**
	 * Plans inventory consumption using only main inventory/offhand sources and main-inventory remainder space.
	 */
	@SuppressWarnings("unchecked")
	public static Optional<InventoryResourceTransfer.Transfer> prepare(Inventory inventory, ItemStack weapon, BlasterStats.Ammo ammo, int requested)
	{
		DataComponentType<StoredCharge> component = null;
		if (ammo.consumption() instanceof BlasterStats.ComponentChargeConsumption charge)
		{
			var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(charge.component());
			if (type == null || type.codec() != StoredCharge.CODEC)
				return Optional.empty();

			component = (DataComponentType<StoredCharge>)type;
		}
		var container = ammo.consumption() instanceof BlasterStats.ComponentChargeConsumption charge
		                ? charge.emptyContainer() : Optional.<net.minecraft.world.item.ItemStackTemplate>empty();
		return InventoryResourceTransfer.prepare(
				inventory,
				_sourceSlots,
				_remainderSlots,
				weapon,
				ammo.ingredient(),
				component,
				unitsPerLoadedQuantity(ammo),
				container,
				requested
		);
	}

	/**
	 * Debits one shot.
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
	 * Commits a debit, returns the amount loaded.
	 */
	public static int reload(ServerLevel level, Player player, ItemStack stack, BlasterStats.Ammo ammo)
	{
		var current = BlasterItem.getLoadedAmmo(stack, ammo);
		var room = BlasterItem.getAmmoCapacity(ammo) - current;
		if (room <= 0)
			return 0;

		var transfer = player.isCreative() ? Optional.<InventoryResourceTransfer.Transfer>empty() : prepare(player.getInventory(), stack, ammo, room);
		if (!player.isCreative() && (transfer.isEmpty() || !transfer.orElseThrow().matches(player.getInventory())))
			return 0;

		var amount = player.isCreative() ? room : transfer.orElseThrow().quantity();
		var accepted = ammo.feed() instanceof BlasterStats.MagazineFeed
		               ? BlasterItem.setLoadedRounds(level, stack, current + amount) : BlasterItem.setLoadedCharge(level, stack, current + amount);

		if (!accepted)
			return 0;

		transfer.ifPresent(value -> value.apply(player.getInventory()));

		return amount;
	}

	/**
	 * The slots where the remainder can go.
	 */
	private static final List<Integer> _remainderSlots = IntStream.range(0, Inventory.INVENTORY_SIZE).boxed().toList();

	/**
	 * The source slots.
	 */
	private static final List<Integer> _sourceSlots = IntStream.rangeClosed(0, Inventory.INVENTORY_SIZE)
	                                                           .map(slot -> slot == Inventory.INVENTORY_SIZE ? Inventory.SLOT_OFFHAND : slot).boxed().toList();

	/**
	 * Utility class.
	 */
	private BlasterAmmo()
	{
	}
}
