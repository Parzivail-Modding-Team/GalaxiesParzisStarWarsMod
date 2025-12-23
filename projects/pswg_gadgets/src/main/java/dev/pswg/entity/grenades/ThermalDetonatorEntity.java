package dev.pswg.entity.grenades;

import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.block.GrenadeBlock;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static java.awt.Color.WHITE;

public class ThermalDetonatorEntity extends GrenadeEntityWithBlock
{
	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
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
		getEntityWorld().playSound(null, getBlockPos(), GadgetsSounds.THERMAL_DETONATOR_EXPLOSION, SoundCategory.PLAYERS, 4f, 1f);
		super.explode();
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
			TintedParticleEffect flashParticleEffect = TintedParticleEffect.create(ParticleTypes.FLASH, Colors.WHITE);
			serverWorld.spawnParticles(serverPlayerEntity, flashParticleEffect, true, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.spawnParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.spawnParticles(serverPlayerEntity, GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, true, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME,  true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME,  true, true, x, y, z, m2 * 2, m4, m4, m4, 0);
		}
	}
}