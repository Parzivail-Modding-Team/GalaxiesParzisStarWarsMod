package com.parzivail.pswg.features.blasters.data;

public enum BlasterAttachmentCategory
{
	NONE(0),
	BARREL(1),
	SCOPE(2),
	STOCK(3),
	INTERNAL_ORDNANCE_CONFIG(4),
	INTERNAL_TARGETING(5),
	INTERNAL_COOLING(6),
	;

	private final int id;

	BlasterAttachmentCategory(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}
