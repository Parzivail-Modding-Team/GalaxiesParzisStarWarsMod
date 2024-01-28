package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgParticles;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.Objects;

public class ThermalDetonatorEntity extends ThrowableExplosive implements IPrecisionSpawnEntity, IPrecisionVelocityEntity
{
	public int texturePhase = 0;

	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
	{
		super(type, world);
		setExplosionPower(5f);
		//		SoundHelper.playDetonatorEntitySound(this);
	}

	@Override
	public void explode()
	{
		//this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SwgSounds.Explosives.THERMAL_DETONATOR_EXPLOSION, SoundCategory.MASTER, 4.0f, 1f, true);
		getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.THERMAL_DETONATOR_EXPLOSION, SoundCategory.PLAYERS, 4f, 1f);

		super.explode();
	}

	@Override
	public void tick()
	{
		if (this.age == 1 && getWorld().isClient() && this.isPrimed())
		{
			SoundHelper.playDetonatorEntitySound(this);
		}
		if (isPrimed())
		{
			if (texturePhase < 6)
				texturePhase++;
			else
				texturePhase = 0;
		}

		this.speed = this.speed * 0.95f;
		super.tick();
	}

	@Override
	protected void createParticles(double x, double y, double z)
	{
		int m = (int)(getExplosionPower() / 2);
		if (getWorld() instanceof ServerWorld world)
		{
			/*world.spawnParticles( ParticleTypes.LARGE_SMOKE,  x, y, z, 50 * m, 1, 1, 1, 0);
			world.spawnParticles( ParticleTypes.SMOKE,  x, y, z, 45 * m, 0.75 * m, 0.75 * m, 0.75 * m, 0);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE,  x, y + 0.5f, z, 15 * m, 0.125 * m, 0.25 * m, 0.125 * m, 0.03);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE,  x, y + 0.5f, z, 15 * m, 0.25 * m, 0.125 * m, 0.125 * m, 0.025);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE,  x, y + 0.5f, z, 15 * m, 0.125 * m, 0.125 * m, 0.25 * m, 0.02);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE , x, y + 0.5f, z, 10 * m, 0.1 * m, 0.2 * m, 0.1 * m, 0.0075);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE,  x, y + 0.5f, z, 10 * m, 0.2 * m, 0.1 * m, 0.25 * m, 0.01);
			world.spawnParticles(SwgParticles.EXPLOSION_SMOKE,  x, y + 0.5f, z, 10 * m, 0.25 * m, 0.5 * m, 0.3 * m, 0.015);

			world.spawnParticles( ParticleTypes.FLAME,  x, y, z, 20 * m, 0.5 * m, 0.5 * m, 0.5 * m, 0.1);
			world.spawnParticles( ParticleTypes.SMALL_FLAME,  x, y, z, 20 * m, 1.2 * m, 1.2 * m, 1.2 * m, 0.125);
			world.spawnParticles(ParticleTypes.SMALL_FLAME,  x, y, z, 20 * m, 1.1 * m, 1.3 * m, 1.25 * m, 0.125);*/
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
			this.bounce(blockHitResult);
		}

		super.onCollision(hitResult);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (!isPrimed() && age > 30 && player.getInventory().getMainHandStack().isEmpty())
		{
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
