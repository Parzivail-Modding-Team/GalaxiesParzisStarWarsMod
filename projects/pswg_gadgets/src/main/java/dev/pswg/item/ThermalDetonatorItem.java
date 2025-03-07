package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class ThermalDetonatorItem extends GrenadeItem
{
	public ThermalDetonatorItem(Item.Settings settings)
	{
		super(settings, Gadgets.THERMAL_DETONATOR_ITEM, 150, new ThermalDetonatorSoundGroup());
	}

	@Override
	public EntityType<ThermalDetonatorEntity> getEntityType()
	{
		return Gadgets.THERMAL_DETONATOR_ENTITY;
	}
}
