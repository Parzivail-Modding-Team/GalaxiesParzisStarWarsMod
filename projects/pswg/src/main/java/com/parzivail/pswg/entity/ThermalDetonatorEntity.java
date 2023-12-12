package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgParticles;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import com.parzivail.util.math.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ThermalDetonatorEntity extends ThrowableExplosive implements IPrecisionSpawnEntity, IPrecisionVelocityEntity
{
	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
	{
		super(type, world);
		setExplosionPower(5f);
	}

	@Override
	public void tick()
	{

		this.speed = this.speed * 0.95f;
		super.tick();
	}

	@Override
	protected void createParticles(ServerWorld world, double x, double y, double z)
	{
		int m = (int)explosionPower / 2;
		for (int i = 0; i < world.getPlayers().size(); ++i)
		{
			ServerPlayerEntity player = world.getPlayers().get(i);
			world.spawnParticles(player, ParticleTypes.FLASH, true, x, y, z, 1, 0, 0, 0, 0);

			world.spawnParticles(player, ParticleTypes.LARGE_SMOKE, true, x, y, z, 50 * m, 1, 1, 1, 0);
			world.spawnParticles(player, ParticleTypes.SMOKE, true, x, y, z, 45 * m, 0.75 * m, 0.75 * m, 0.75 * m, 0);

			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 15 * m, 0.125 * m, 0.25 * m, 0.125 * m, 0.03);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 15 * m, 0.25 * m, 0.125 * m, 0.125 * m, 0.025);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 15 * m, 0.125 * m, 0.125 * m, 0.25 * m, 0.02);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 10 * m, 0.1 * m, 0.2 * m, 0.1 * m, 0.0075);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 10 * m, 0.2 * m, 0.1 * m, 0.25 * m, 0.01);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y + 0.5f, z, 10 * m, 0.25 * m, 0.5 * m, 0.3 * m, 0.015);

			world.spawnParticles(player, ParticleTypes.FLAME, true, x, y, z, 20 * m, 0.5 * m, 0.5 * m, 0.5 * m, 0.1);
			world.spawnParticles(player, ParticleTypes.SMALL_FLAME, true, x, y, z, 20 * m, 1.2 * m, 1.2 * m, 1.2 * m, 0.125);
			world.spawnParticles(player, ParticleTypes.SMALL_FLAME, true, x, y, z, 20 * m, 1.1 * m, 1.3 * m, 1.25 * m, 0.125);
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
			if (hitResult.squaredDistanceTo(this) < 0.01)
			{
				this.setVelocity(0, 0, 0);
			}
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;

			Vec3d velocity = this.getVelocity();
			Vec3d pos = hitResult.getPos();

			double modX = 0.9f;
			double modY = 0.9f;
			double modZ = 0.9f;
			if (blockHitResult.getSide() == Direction.UP)
			{
				modY = -0.2f;
			}
			else if (blockHitResult.getSide() == Direction.DOWN)
			{
				modY = -1.2f;
			}
			else
			{
				if (this.getX() - pos.getX() > 0 && this.getZ() - pos.getZ() > 0)
				{
					if (blockHitResult.getSide() == Direction.EAST)
					{
						modX = modX * -1;
					}
					else
					{
						modZ = modZ * -1;
					}
				}
				else if (this.getX() - pos.getX() < 0 && this.getZ() - pos.getZ() > 0)
				{
					if (blockHitResult.getSide() == Direction.SOUTH)
					{
						modZ = modZ * -1;
					}
					else
					{
						modX = modX * -1;
					}
				}
				else if (this.getX() - pos.getX() < 0 && this.getZ() - pos.getZ() < 0)
				{
					if (blockHitResult.getSide() == Direction.WEST)
					{
						modX = modX * -1;
					}
					else
					{
						modZ = modZ * -1;
					}
				}
				else if (this.getX() - pos.getX() > 0 && this.getZ() - pos.getZ() < 0)
				{
					if (blockHitResult.getSide() == Direction.NORTH)
					{
						modZ = modZ * -1;
					}
					else
					{
						modX = modX * -1;
					}
				}
			}
			this.setVelocity(velocity.x * modX, velocity.y * modY, velocity.z * modZ);
		}
		super.onCollision(hitResult);
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower)
	{
		if (state.isIn(SwgTags.Blocks.DETONATOR_EXPLODE))
		{
			return true;
		}
		return false;
	}
}
