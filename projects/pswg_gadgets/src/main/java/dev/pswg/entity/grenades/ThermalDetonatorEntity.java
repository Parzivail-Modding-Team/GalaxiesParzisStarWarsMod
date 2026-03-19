package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.block.GrenadeBlock;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import static java.awt.Color.WHITE;

public class ThermalDetonatorEntity extends GrenadeEntityWithBlock
{
	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, Level world)
	{
		super(type, world, CollisionType.BOUNCE);
		setExplosionPower(5f);
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.THERMAL_DETONATOR_ITEM;
	}

	@Override
	public GrenadeBlock getBlock()
	{
		return GadgetsBlocks.THERMAL_DETONATOR_BLOCK;
	}

	@Override
	public void explode()
	{
		level().playSound(null, blockPosition(), GadgetsSounds.THERMAL_DETONATOR_EXPLOSION, SoundSource.PLAYERS, 4f, 1f);
		super.explode();
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
			ColorParticleOption flashParticleEffect = ColorParticleOption.create(ParticleTypes.FLASH, CommonColors.WHITE);
			serverWorld.sendParticles(serverPlayerEntity, flashParticleEffect, true, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.sendParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.sendParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.FLAME,  true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.sendParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME,  true, true, x, y, z, m2 * 2, m4, m4, m4, 0);
		}
	}
}