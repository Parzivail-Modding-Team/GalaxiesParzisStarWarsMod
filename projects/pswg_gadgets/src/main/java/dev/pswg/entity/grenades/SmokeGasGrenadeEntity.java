package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class SmokeGasGrenadeEntity extends GasGrenadeEntity
{

	public SmokeGasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world, GadgetsEntities.SMOKE_GAS, CollisionType.BOUNCE, 30);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.SMOKE_GRENADE_ITEM;
	}

}
