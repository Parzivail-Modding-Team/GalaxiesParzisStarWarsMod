package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.item.grenades.soundGroups.FragmentationGrenadeSoundGroup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class FragmentationGrenadeItem extends GrenadeItem
{
	public FragmentationGrenadeItem(Item.Properties settings)
	{
		super(settings, GadgetsItems.FRAGMENTATION_GRENADE_ITEM, 50, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY;
	}
}
