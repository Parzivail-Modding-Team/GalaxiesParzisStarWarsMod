package dev.pswg.item;

import dev.pswg.item.component.StoredCharge;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An ordinary powered item displaying its native charge component as an inventory bar.
 */
public class ChargedItem extends Item
{
	/**
	 * Creates an item.
	 */
	public ChargedItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		var charge = stack.get(StoredCharge.COMPONENT);
		return charge != null && charge.current() < charge.capacity();
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		var charge = stack.get(StoredCharge.COMPONENT);
		return charge == null ? 0 : Mth.clamp(Math.round(13f * charge.current() / charge.capacity()), 0, 13);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0x54D9FF;
	}
}
