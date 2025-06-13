package dev.pswg.item;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class NerveGasGrenadeItem extends GrenadeItem
{
	public NerveGasGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.NERVE_GAS_GRENADE_ITEM, 60, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.NERVE_GAS_GRENADE_ENTITY;
	}
}
