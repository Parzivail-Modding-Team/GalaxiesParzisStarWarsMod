package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.swg.item.blaster.data.powerpack.BlasterPowerPack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public abstract class ItemBlasterPowerPack extends PItem
{
	private static final HashMap<ItemBlasterPowerPack, BlasterPowerPack> itemStackAttachmentTypes = new HashMap<>();

	public ItemBlasterPowerPack(String name, Class<? extends BlasterPowerPack> packClass)
	{
		super(Resources.dot("pack", name));

		for (BlasterAttachment a : BlasterAttachments.POWERPACKS)
			if (packClass == a.getClass())
				itemStackAttachmentTypes.putIfAbsent(this, (BlasterPowerPack)a);
	}

	public static BlasterPowerPack getPackType(ItemStack stack)
	{
		if (stack == null)
			return null;

		Item item = stack.getItem();
		if (!(item instanceof ItemBlasterPowerPack))
			return null;

		if (!itemStackAttachmentTypes.containsKey(item))
			return null;

		return itemStackAttachmentTypes.get(item);
	}
}
