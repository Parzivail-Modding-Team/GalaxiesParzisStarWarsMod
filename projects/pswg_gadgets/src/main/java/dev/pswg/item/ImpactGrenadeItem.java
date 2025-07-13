package dev.pswg.item;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.ImpactGrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class ImpactGrenadeItem extends GrenadeItem
{
	public ImpactGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.IMPACT_GRENADE_ITEM, 400, new ThermalDetonatorSoundGroup());
	}

	@Override
	public EntityType<ImpactGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.IMPACT_GRENADE_ENTITY;
	}
}
