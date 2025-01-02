package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.block.GrenadeBlock;
import dev.pswg.block.ThermalDetonatorBlock;
import dev.pswg.entity.GrenadeEntity;
import dev.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class ThermalDetonatorItem extends GrenadeItem
{
	public ThermalDetonatorItem(Item.Settings settings)
	{
		super(settings, Gadgets.THERMAL_DETONATOR_ITEM, 150);
	}

	@Override
	public EntityType<ThermalDetonatorEntity> getEntity()
	{
		return Gadgets.THERMAL_DETONATOR_ENTITY;
	}

	@Override
	public ThermalDetonatorBlock getBlock()
	{
		return Gadgets.THERMAL_DETONATOR_BLOCK;
	}
}
