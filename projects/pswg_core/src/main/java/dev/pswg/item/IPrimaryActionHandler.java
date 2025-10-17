package dev.pswg.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Provides an interface for items that can have primary actions invoked on them
 */
public interface IPrimaryActionHandler
{
	/**
	 * Called when an entity requests the primary action for the item.
	 *
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 *
	 * @param stack The item stack to query
	 * @param user  The user that is holding the stack
	 * @param world The world that the user is in
	 *
	 * @return The new item stack after using the item
	 */
	default ItemStack invokePrimaryAction(ItemStack stack, World world, LivingEntity user)
	{
		return stack;
	}
}
