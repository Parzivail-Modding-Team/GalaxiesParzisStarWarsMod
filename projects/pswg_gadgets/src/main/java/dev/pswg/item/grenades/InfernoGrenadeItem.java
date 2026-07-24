package dev.pswg.item.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.InfernoGrenadeEntity;
import dev.pswg.item.grenades.soundGroups.ThermalDetonatorSoundGroup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class InfernoGrenadeItem extends GrenadeItem
{
	public InfernoGrenadeItem(Item.Properties settings)
	{
		super(settings, GadgetsItems.INFERNO_GRENADE_ITEM, 80, new ThermalDetonatorSoundGroup());
	}

	@Override
	public EntityType<InfernoGrenadeEntity> getEntityType()
	{
		return GadgetsEntities.INFERNO_GRENADE_ENTITY;
	}
}
