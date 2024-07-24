package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgParticleTypes;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ThermalDetonatorEntity extends ThrowableExplosive implements IPrecisionSpawnEntity, IPrecisionVelocityEntity
{
	public static final int MIN_PICKUP_AGE = 30;

	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
	{
		super(type, world);
		setExplosionPower(5f);
	}

	@Override
	public void explode()
	{
		getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.THERMAL_DETONATOR_EXPLOSION, SoundCategory.PLAYERS, 4f, 1f);
		super.explode();
	}

	@Override
	public void tick()
	{
		if (getWorld().isClient() && this.age == 1 && this.isPrimed())
				SoundHelper.playDetonatorEntitySound(this);

		this.speed = this.speed * 0.95f;
		super.tick();
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
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLASH, true, x, y, z, 1, 0, 0, 0, 0);
			serverWorld.spawnParticles(serverPlayerEntity, SwgParticleTypes.EXPLOSION_SMOKE, true, x, y, z, m2 * 6, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, true, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, true, x, y, z, m2 * 3, m4, m4, m4, 0);

			serverWorld.spawnParticles(serverPlayerEntity, SwgParticleTypes.EXPLOSION_SMOKE, false, x, y, z, m2 * 4, m, m, m, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.FLAME, false, x, y, z, m2 * 2, m3, m3, m3, 0);
			serverWorld.spawnParticles(serverPlayerEntity, ParticleTypes.SMALL_FLAME, false, x, y, z, m2 * 2, m4, m4, m4, 0);
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
			var pos = blockHitResult.getBlockPos();
			var state = getWorld().getBlockState(pos);
			this.bounce(blockHitResult);

			if (getVelocity().length() > 0.01f)
				this.playSound(state.getBlock().getSoundGroup(state).getHitSound(), 1f, 1f);
		}

		super.onCollision(hitResult);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (!isPrimed() && age > MIN_PICKUP_AGE && player.getInventory().getMainHandStack().isEmpty())
		{
			if (getWorld() instanceof ServerWorld)
				player.giveItemStack(new ItemStack(SwgItems.Explosives.ThermalDetonator));
			this.discard();
		}
		return super.interact(player, hand);
	}

	@Override
	public boolean canHit()
	{
		return true;
	}
}
