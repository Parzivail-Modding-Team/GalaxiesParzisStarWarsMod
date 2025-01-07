package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.entity.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class FragmentationGrenadeItem extends GrenadeItem
{
	public FragmentationGrenadeItem(Item.Settings settings)
	{
		super(settings, Gadgets.FRAGMENTATION_GRENADE_ITEM, 50);
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return Gadgets.FRAGMENTATION_GRENADE_ENTITY;
	}
}
