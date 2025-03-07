package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class FragmentationGrenadeItem extends GrenadeItem
{
	public FragmentationGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.FRAGMENTATION_GRENADE_ITEM, 50, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY;
	}
}
