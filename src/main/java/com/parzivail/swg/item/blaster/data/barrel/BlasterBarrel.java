package com.parzivail.swg.item.blaster.data.barrel;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachmentType;

public abstract class BlasterBarrel extends BlasterAttachment
{
	public BlasterBarrel(String name, int price)
	{
		super(BlasterAttachmentType.BARREL, Resources.modDot("blaster", "barrel", name), price);
	}

	@Override
	public String getInfoText()
	{
		return "";
	}
}
