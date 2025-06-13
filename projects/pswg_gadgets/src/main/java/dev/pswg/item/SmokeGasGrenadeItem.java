package dev.pswg.item;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class SmokeGasGrenadeItem extends GrenadeItem
{
	public SmokeGasGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.SMOKE_GRENADE_ITEM, 80, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return GadgetsEntities.SMOKE_GAS_GRENADE_ENTITY;
	}
}
