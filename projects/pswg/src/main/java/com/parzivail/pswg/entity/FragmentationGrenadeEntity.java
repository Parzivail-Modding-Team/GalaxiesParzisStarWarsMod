package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

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
	protected void createParticles(double x, double y, double z, ServerWorld serverWorld)
	{
		for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
		{
			serverWorld.spawnParticles(serverPlayerEntity, SwgParticleTypes.FRAGMENTATION_GRENADE, true, x, y, z, 1, 0, 0, 0, 0);
		}
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
	public void explode()
	{
		if (!IS_EXPLODING)
		{
			IS_EXPLODING = true;
		}
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers())
			{
				serverWorld.spawnParticles(serverPlayerEntity, SwgParticleTypes.FRAGMENTATION_GRENADE, true, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
			}
		}
		super.explode();
	}

	@Override
	public void tick()
	{
		super.tick();
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
