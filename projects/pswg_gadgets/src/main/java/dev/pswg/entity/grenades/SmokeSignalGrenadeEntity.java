package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class SmokeSignalGrenadeEntity extends GasGrenadeEntity
{

	public SmokeSignalGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world, GadgetsEntities.SMOKE_GAS, CollisionType.BOUNCE, 30);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM;
	}

}
