package dev.pswg.gametest.blasters;

import dev.pswg.Blasters;
import dev.pswg.data.BlasterData;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.component.StoredCharge;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Builds item stacks for blaster scenarios through the same factories and components production code uses.
 *
 * <p>A scenario never writes component NBT or JSON by hand. A blaster starts from its authored data-pack
 * definition via {@link BlasterItem#createStack}, and its mutable state is changed through the public
 * {@link BlasterItem} mutators that validate every change.
 */
public final class BlasterTestStacks
{
	/**
	 * The shipped development blaster with every trigger and behavior family, used for ammo, heat and cadence
	 * scenarios.
	 */
	public static final Identifier TEST_BLASTER = Blasters.id("test_blaster");

	/**
	 * The shipped one-handed pistol, used for dual-wield scenarios.
	 */
	public static final Identifier DL44 = Blasters.id("dl44");

	/**
	 * Prevents instantiation.
	 */
	private BlasterTestStacks()
	{
	}

	/**
	 * Creates a freshly authored blaster with its default mode and attachments.
	 *
	 * <p>Like a blaster crafted by a player, it starts with an empty magazine and has to be reloaded.
	 *
	 * @param level the level whose current blaster data defines the blaster
	 * @param id    the blaster definition identifier
	 *
	 * @return the new stack
	 *
	 * @throws IllegalStateException if the data pack does not define the blaster
	 */
	public static ItemStack blaster(ServerLevel level, Identifier id)
	{
		var definition = BlasterData.get(level).blasters().get(id);

		if (definition == null)
		{
			throw new IllegalStateException("The loaded data packs define no blaster " + id);
		}

		return BlasterItem.createStack(id, definition);
	}

	/**
	 * Creates a blaster whose magazine is full.
	 *
	 * @param level the level whose current blaster data defines the blaster
	 * @param id    the blaster definition identifier
	 *
	 * @return the new stack, loaded to the capacity its definition authors
	 *
	 * @throws IllegalStateException if the blaster does not exist or is not magazine-fed
	 */
	public static ItemStack loadedBlaster(ServerLevel level, Identifier id)
	{
		var stack = blaster(level, id);
		var capacity = BlasterItem.getAmmoCapacity(BlasterData.get(level).blasters().get(id).stats().ammo());

		if (!BlasterItem.setLoadedRounds(level, stack, capacity))
		{
			throw new IllegalStateException("Blaster " + id + " cannot be loaded to " + capacity + " rounds");
		}

		return stack;
	}

	/**
	 * Creates a blaster holding an exact number of loaded rounds.
	 *
	 * @param level  the level whose current blaster data defines the blaster
	 * @param id     the blaster definition identifier
	 * @param rounds the loaded rounds, within the blaster's magazine capacity
	 *
	 * @return the new stack
	 *
	 * @throws IllegalStateException if the blaster does not exist or rejects the quantity
	 */
	public static ItemStack blasterWithRounds(ServerLevel level, Identifier id, int rounds)
	{
		var stack = blaster(level, id);

		if (!BlasterItem.setLoadedRounds(level, stack, rounds))
		{
			throw new IllegalStateException("Blaster " + id + " rejected " + rounds + " loaded rounds");
		}

		return stack;
	}

	/**
	 * Creates a charged item of the given kind with an exact stored charge.
	 *
	 * @param item     the charged item, for example {@link Blasters#POWER_PACK}
	 * @param current  the stored charge units
	 * @param capacity the charge capacity, at least {@code current}
	 *
	 * @return the new stack
	 */
	public static ItemStack pack(Item item, int current, int capacity)
	{
		var stack = new ItemStack(item);
		stack.set(StoredCharge.COMPONENT, new StoredCharge(current, capacity));
		return stack;
	}

	/**
	 * Reads the charge units stored in a charged item.
	 *
	 * @param pack the charged item
	 *
	 * @return the stored units, or zero when the stack stores no charge
	 */
	public static int storedUnits(ItemStack pack)
	{
		var charge = pack.get(StoredCharge.COMPONENT);
		return charge == null ? 0 : charge.current();
	}
}
