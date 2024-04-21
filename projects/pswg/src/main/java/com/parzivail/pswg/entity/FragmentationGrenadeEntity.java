package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgParticleTypes;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.ExplosionSoundGroup;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FragmentationGrenadeEntity extends ThrowableExplosive
{
	public static final int MIN_PICKUP_AGE = 30;
	public boolean IS_EXPLODING = false;
	public int EXPLOSION_TICK = 0;
	private boolean COLLISION_BELOW;

	public FragmentationGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
		setExplosionPower(5f);
	}
	@Override
	public boolean canBeHitByProjectile()
	{
		return true;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (!isPrimed() && age > MIN_PICKUP_AGE && player.getInventory().getMainHandStack().isEmpty())
		{
			player.giveItemStack(new ItemStack(SwgItems.Explosives.FragmentationGrenade));

			this.discard();
		}
		return super.interact(player, hand);
	}

	@Override
	public boolean shouldRender(double distance)
	{
		if (super.shouldRender(distance) && !IS_EXPLODING)
			return true;
		return false;
	}

	@Override
	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{
	}

	@Override
	public Vec3d getVelocity()
	{
		if (IS_EXPLODING)
		{
			return Vec3d.ZERO;
		}
		return super.getVelocity();
	}

	@Override
	public void explode()
	{
		if (!IS_EXPLODING)
		{
			if (getWorld() instanceof ServerWorld serverWorld)
			{
				for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
				{
					serverWorld.spawnParticles(serverPlayerEntity, SwgParticleTypes.FRAGMENTATION_GRENADE, true, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
				}
			}
			IS_EXPLODING = true;

			int randomNum = Random.create().nextBetween(1, 4);

			if (randomNum == 4)
				getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.FRAGMENTATION_GRENADE_EXPLOSION4, SoundCategory.PLAYERS, 4f, 1f);
			else if (randomNum == 3)
				getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.FRAGMENTATION_GRENADE_EXPLOSION3, SoundCategory.PLAYERS, 4f, 1f);
			else if (randomNum == 2)
				getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.FRAGMENTATION_GRENADE_EXPLOSION2, SoundCategory.PLAYERS, 4f, 1f);
			else
				getWorld().playSound(null, getBlockPos(), SwgSounds.Explosives.FRAGMENTATION_GRENADE_EXPLOSION1, SoundCategory.PLAYERS, 4f, 1f);
		}
	}

	public void createSparkParticles()
	{
		for (int i = 0; i < Random.create().nextBetween(50, 80); i++)
		{
			double vx = getWorld().random.nextGaussian() * 0.4;
			double vz = getWorld().random.nextGaussian() * 0.4;
			double vy;
			if (COLLISION_BELOW)
				vy = Math.abs(getWorld().random.nextGaussian() * 0.4);
			else
				vy = getWorld().random.nextGaussian() * 0.4;
			getWorld().addParticle(SwgParticleTypes.FRAGMENTATION_GRENADE_SPARK, getX(), getY(), getZ(), vx, vy, vz);
		}
	}

	@Override
	public void tick()
	{
		super.tick();
		if (getWorld().isClient() && this.age == 1 && this.isPrimed())
			SoundHelper.playFragmentationEntitySound(this);
		if (IS_EXPLODING)
		{
			this.setVelocity(Vec3d.ZERO);
		}

		if (EXPLOSION_TICK == 7)
		{
			createSparkParticles();
			List<LivingEntity> entities = getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(3, 3, 3), entity -> {
				return true;
			});
			for (LivingEntity entity : entities)
			{
				float x = (float)(entity.getX() - getX()) / 3f;
				float z = (float)(entity.getZ() - getZ()) / 3f;
				entity.addVelocity(-x, 0, -z);
			}
		}
		if (EXPLOSION_TICK >= 15)
		{
			super.explode();
		}
		if (IS_EXPLODING)
			EXPLOSION_TICK++;
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
			if (blockHitResult.getSide() == Direction.UP)
			{
				COLLISION_BELOW = true;
			}
			else
			{
				COLLISION_BELOW = false;
			}
			if (getVelocity().length() > 0.01f)
				this.playSound(state.getBlock().getSoundGroup(state).getHitSound(), 1f, 1f);
		}

		super.onCollision(hitResult);
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower)
	{
		return false;
	}

	@Override
	public boolean canHit()
	{
		return true;
	}
}
