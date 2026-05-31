package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.item.grenades.soundGroups.FragmentationGrenadeSoundGroup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class NerveGasGrenadeItem extends GrenadeItem
{
	public NerveGasGrenadeItem(Item.Properties settings)
	{
		super(settings, GadgetsItems.NERVE_GAS_GRENADE_ITEM, 60, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.NERVE_GAS_GRENADE_ENTITY;
	}
}
