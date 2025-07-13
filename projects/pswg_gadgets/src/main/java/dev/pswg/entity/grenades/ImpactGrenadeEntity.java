package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.item.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ImpactGrenadeEntity extends GrenadeEntity
{
	public ImpactGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world, CollisionType.EXPLODE);
		setExplosionPower(2f);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.IMPACT_GRENADE_ITEM;
	}

	@Override
	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{
		float power = getExplosionPower();
		float m = power / 4;
		int m2 = (int)power * 2;
		int m3 = (int)(power / 4);
		double m4 = m3 * 1.5f;

		for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
		{
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLASH, true, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.spawnParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.spawnParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 2, m4, m4, m4, 0);
		}
	}
}
