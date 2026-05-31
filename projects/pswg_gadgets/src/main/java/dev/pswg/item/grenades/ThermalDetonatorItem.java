package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.item.grenades.soundGroups.ThermalDetonatorSoundGroup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ThermalDetonatorItem extends GrenadeItem
{
	public ThermalDetonatorItem(Item.Properties settings)
	{
		super(settings, GadgetsItems.THERMAL_DETONATOR_ITEM, 150, new ThermalDetonatorSoundGroup());
	}

	@Override
	public EntityType<ThermalDetonatorEntity> getEntityType()
	{
		return GadgetsEntities.THERMAL_DETONATOR_ENTITY;
	}
}
