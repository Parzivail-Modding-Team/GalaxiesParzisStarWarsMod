package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class FragmentationGrenadeEntity extends ThrowableExplosive
{
	public static final int MIN_PICKUP_AGE = 30;
	public boolean IS_EXPLODING = false;
	public int EXPLOSION_TICK = 0;

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
		}
	}

	@Override
	public void tick()
	{
		super.tick();
		if (IS_EXPLODING)
		{
			this.setVelocity(Vec3d.ZERO);
		}

		if (EXPLOSION_TICK == 7)
		{
			List<LivingEntity> entities = getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(3, 3, 3), entity -> {
				return true;
			});
			for (LivingEntity entity : entities)
			{
				float x = (float)(entity.getX() - getX()) / 4f;
				float z = (float)(entity.getZ() - getZ()) / 4f;
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
