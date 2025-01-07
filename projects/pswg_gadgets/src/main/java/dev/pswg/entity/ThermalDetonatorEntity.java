package dev.pswg.entity;

import dev.pswg.Gadgets;
import dev.pswg.block.GrenadeBlock;
import dev.pswg.item.GrenadeItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ThermalDetonatorEntity extends GrenadeEntityWithBlock
{

	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
	{
		super(type, world);
		setExplosionPower(5f);
	}

	@Override
	public GrenadeItem getItem()
	{
		return Gadgets.THERMAL_DETONATOR_ITEM;
	}

	@Override
	public GrenadeBlock getBlock()
	{
		return Gadgets.THERMAL_DETONATOR_BLOCK;
	}

	@Override
	public void explode()
	{
		//getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.THERMAL_DETONATOR_EXPLOSION, SoundCategory.PLAYERS, 4f, 1f);
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
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLASH, true, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.spawnParticles(serverPlayerEntity, Gadgets.EXPLOSION_SMOKE_PARTICLE, true, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.spawnParticles(serverPlayerEntity, Gadgets.EXPLOSION_SMOKE_PARTICLE, true, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME,  true, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME,  true, true, x, y, z, m2 * 2, m4, m4, m4, 0);
		}
	}
	@Override
	public boolean canBeHitByProjectile()
	{
		return true;
	}

	@Override
	protected void onCollision(HitResult hitResult)
	{
		if (hitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			bounce(blockHitResult);
			playCollisionSound(blockHitResult);
		}
		super.onCollision(hitResult);
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower)
	{
		return true;
	}
}