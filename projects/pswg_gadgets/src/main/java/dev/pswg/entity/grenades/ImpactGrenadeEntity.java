package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GalaxiesParticleTypes;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ImpactGrenadeEntity extends GrenadeEntity
{
	public ImpactGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world, CollisionType.BOUNCE);
		setExplosionPower(1.5f);
	}

	@Override
	protected void onInsideBlock(BlockState state)
	{
		if (isPrimed())
			explode();
		super.onInsideBlock(state);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.IMPACT_GRENADE_ITEM;
	}

	@Override
	protected void createParticles(double x, double y, double z, ServerLevel serverWorld)
	{
		float power = getExplosionPower();
		float m = power / 4;
		int m2 = (int)power * 2;
		int m3 = (int)(power / 4);
		double m4 = m3 * 1.5f;

		for (ServerPlayer serverPlayerEntity : serverWorld.players())
		{
			ColorParticleOption flashParticle = ColorParticleOption.create((power > 2 ? ParticleTypes.FLASH : GalaxiesParticleTypes.SMALL_FLASH_PARTICLE), CommonColors.WHITE);

			serverWorld.sendParticles(serverPlayerEntity, flashParticle, true, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.sendParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.sendParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 2, m4, m4, m4, 0);
		}
	}
}
