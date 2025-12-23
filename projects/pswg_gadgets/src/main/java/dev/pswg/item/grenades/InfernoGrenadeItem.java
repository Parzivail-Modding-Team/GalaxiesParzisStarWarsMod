package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.InfernoGrenadeEntity;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.item.ThermalDetonatorSoundGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class InfernoGrenadeItem extends GrenadeItem
{
	public InfernoGrenadeItem(Item.Settings settings)
	{
		super(settings, GadgetsItems.INFERNO_GRENADE_ITEM, 80, new ThermalDetonatorSoundGroup());
	}

	@Override
	public EntityType<InfernoGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.INFERNO_GRENADE_ENTITY;
	}
}
