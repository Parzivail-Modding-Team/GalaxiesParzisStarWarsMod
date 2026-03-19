package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class NerveGasGrenadeEntity extends GasGrenadeEntity
{

	public NerveGasGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world, GadgetsEntities.NERVE_GAS, CollisionType.BOUNCE, 30);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.NERVE_GAS_GRENADE_ITEM;
	}
}
