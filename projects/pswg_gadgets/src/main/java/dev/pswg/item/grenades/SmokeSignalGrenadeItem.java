package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.item.FragmentationGrenadeSoundGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class SmokeSignalGrenadeItem extends GrenadeItem
{
	public SmokeSignalGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM, 80, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.SMOKE_SIGNAL_GRENADE_ENTITY;
	}
}
