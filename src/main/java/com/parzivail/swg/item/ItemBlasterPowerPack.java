package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.swg.item.blaster.data.powerpack.BlasterPowerPack;

import java.util.HashMap;

public abstract class ItemBlasterPowerPack extends PItem
{
	public static HashMap<ItemBlasterPowerPack, BlasterPowerPack> itemStackAttachmentTypes = new HashMap<>();

	public ItemBlasterPowerPack(String name, Class<? extends BlasterPowerPack> packClass)
	{
		super(Resources.dot("pack", name));

		for (BlasterAttachment a : BlasterAttachments.POWERPACKS)
			if (packClass == a.getClass())
				itemStackAttachmentTypes.putIfAbsent(this, (BlasterPowerPack)a);
	}
}
