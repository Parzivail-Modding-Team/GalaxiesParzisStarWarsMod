package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.item.grenades.soundGroups.FragmentationGrenadeSoundGroup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class SmokeSignalGrenadeItem extends GrenadeItem
{
	public SmokeSignalGrenadeItem(Item.Properties settings)
	{
		super(settings, GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM, 80, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.SMOKE_SIGNAL_GRENADE_ENTITY;
	}
}
