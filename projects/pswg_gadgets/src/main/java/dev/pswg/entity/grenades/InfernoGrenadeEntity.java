package dev.pswg.entity.grenades;

import com.google.common.collect.ConcurrentHashMultiset;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GalaxiesParticleTypes;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.item.grenades.GrenadeItem;
import dev.pswg.networking.PreciseVelocityParticleS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class InfernoGrenadeEntity extends GrenadeEntity
{
	public ConcurrentHashMultiset<BlockPos> burntBlocks;
	public boolean detonated = false;
	public int detonationTicks = 0;
	public int firewaveTicks = 0;
	public final float INNER_AREA_DISTANCE = 4;
	public final float MAX_DISTANCE = 8;
	public final float MAX_DETONATION_TICKS = 20;
	public final float MAX_FIRE_WAVE_TICKS = 15;

	public InfernoGrenadeEntity(EntityType<? extends ThrowableProjectile> entityType, Level world)
	{
		super(entityType, world, CollisionType.BOUNCE);
		burntBlocks = ConcurrentHashMultiset.create();
	}

	@Override
	public void explode()
	{
		this.setVisible(false);
		detonated = true;
	}

	public void burnBlock(BlockState state, BlockPos pos)
	{
		var world = level();
		if (state.is(GadgetsBlocks.Tags.INFERNO_CHAR))
		{
			world.setBlockAndUpdate(pos, GadgetsBlocks.CHARRED_BLOCK.defaultBlockState());
		}
		else if (state.is(GadgetsBlocks.Tags.INFERNO_DESTROY))
		{
			if (world instanceof ServerLevel serverWorld)
				createSmoke(pos, serverWorld);
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
		else if (state.is(BlockTags.DIRT) && pos.closerToCenterThan(position(), INNER_AREA_DISTANCE))
		{
			world.setBlockAndUpdate(pos, GadgetsBlocks.FERTILE_DIRT_BLOCK.defaultBlockState());
		}
	}

	private static void createSmoke(BlockPos pos, ServerLevel serverWorld)
	{
		for (int i = 0; i < 8; i++)
		{
			double x = pos.getX() + serverWorld.getRandom().nextGaussian() * 0.1f;
			double y = pos.getY() + serverWorld.getRandom().nextGaussian() * 0.1f;
			double z = pos.getZ() + serverWorld.getRandom().nextGaussian() * 0.1f;
			double vX = serverWorld.getRandom().nextGaussian() * 0.05f;
			double vY = serverWorld.getRandom().nextGaussian() * 0.05f;
			double vZ = serverWorld.getRandom().nextGaussian() * 0.05f;
			var payload = new PreciseVelocityParticleS2CPayload(ParticleTypes.SMOKE, new Vec3(x, y, z), new Vec3(vX, vY, vZ));
			for (ServerPlayer player : serverWorld.players())
				ServerPlayNetworking.send(player, payload);
		}
	}

	public void spawnScorchParticles(Vector3f unitVec, BlockPos pos)
	{
		if (level() instanceof ServerLevel serverWorld)
		{
			SimpleParticleType scorchParticleType = pos.closerToCenterThan(position(), INNER_AREA_DISTANCE) ? GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE : GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE;
			createScorchParticles(serverWorld, scorchParticleType, pos.getX(), pos.getY(), pos.getZ(), unitVec.x, unitVec.y, unitVec.z);
		}
	}

	private static void createScorchParticles(ServerLevel serverWorld, SimpleParticleType particleType, double x, double y, double z, float unitX, float unitY, float unitZ)
	{
		List<PreciseVelocityParticleS2CPayload> payloads = new ArrayList<>();
		double x1 = x + 0.5f + unitX / 2f + (unitY + unitZ) * (1 / 4f);
		double x2 = x + 0.5f + unitX / 2f + (unitY + unitZ) * (-1 / 4f);
		double y1 = y + 0.5f + unitY / 2f + (unitX + unitZ) * (1 / 4f);
		double y2 = y + 0.5f + unitY / 2f + (unitX + unitZ) * (-1 / 4f);
		double z1 = z + 0.5f + unitZ / 2f + (unitX + unitY) * (1 / 4f);
		double z2 = z + 0.5f + unitZ / 2f + (unitX + unitY) * (-1 / 4f);
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3(x1, y1, z1), new Vec3(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3(x1, y2, z2), new Vec3(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3(x2, y1, z2), new Vec3(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3(x2, y2, z1), new Vec3(unitX, unitY, unitZ)));

		for (var payload : payloads)
		{
			for (ServerPlayer player : serverWorld.players())
			{
				ServerPlayNetworking.send(player, payload);
			}
		}
	}

	@Override
	public void tick()
	{
		if (detonationTicks > MAX_DETONATION_TICKS && firewaveTicks > MAX_FIRE_WAVE_TICKS)
			discard();
		if (detonated)
		{
			firewaveTicks++;
			detonationTicks++;
			setDeltaMovement(Vec3.ZERO);
			hurtMarked = true;
		}
		var world = level();
		if (detonationTicks == 1)
		{
			var oPos = blockPosition();
			this.burntBlocks.add(oPos);
			burnBlock(world.getBlockState(oPos), oPos);
			for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(INNER_AREA_DISTANCE), livingEntity -> livingEntity.position().distanceTo(position()) <= INNER_AREA_DISTANCE))
			{
				if (!entity.fireImmune())
				{
					entity.igniteForTicks(100);
					if (world instanceof ServerLevel serverWorld)
					{
						entity.hurtServer(serverWorld, GadgetsDamage.create(serverWorld, DamageTypes.IN_FIRE), 8);
					}
				}
			}
		}
		if (detonationTicks == 12)
		{
			for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(MAX_DISTANCE), livingEntity -> livingEntity.position().distanceTo(position()) <= MAX_DISTANCE))
			{
				if (!entity.fireImmune())
				{
					entity.igniteForTicks(300);
					if (world instanceof ServerLevel serverWorld)
					{
						entity.hurtServer(serverWorld, GadgetsDamage.create(serverWorld, DamageTypes.IN_FIRE), 4);
					}
				}
			}
		}
		if (detonated && detonationTicks % 2 == 0)
		{
			for (BlockPos pos : burntBlocks)
			{
				for (Direction dir : Direction.values())
				{
					var offsetPos = pos.relative(dir);
					if (!burntBlocks.contains(offsetPos) && offsetPos.closerThan(blockPosition(), MAX_DISTANCE))
					{
						List<Direction> airSideList = new ArrayList<>(6);
						for (Direction dir2 : Direction.values())
						{
							var offsetPos2 = offsetPos.relative(dir2);
							var offsetState2 = world.getBlockState(offsetPos2);
							if (offsetState2.isAir() && !offsetState2.liquid())
							{
								airSideList.add(dir2);
							}
						}
						if (!airSideList.isEmpty())
						{
							burntBlocks.add(offsetPos);
							var offsetState = world.getBlockState(offsetPos);
							if (!offsetState.liquid())
							{
								if (!offsetState.isAir())
								{
									burnBlock(offsetState, offsetPos);

									for (Direction dir3 : airSideList)
									{
										var unitVec = dir3.step();
										spawnScorchParticles(unitVec, offsetPos);
									}
								}
							}
						}
					}
				}
			}
		}
		var flameWaveList = burntBlocks.stream().filter(pos -> pos.closerToCenterThan(position(), (float)firewaveTicks / MAX_FIRE_WAVE_TICKS * MAX_DISTANCE + 1) && !pos.closerToCenterThan(position(), (float)firewaveTicks / MAX_FIRE_WAVE_TICKS * MAX_DISTANCE)).toList();
		for (BlockPos pos : flameWaveList)
		{
			var state = world.getBlockState(pos);
			for (Direction dir : Direction.values())
			{
				var offsetPos = pos.relative(dir);
				var offsetState = world.getBlockState(offsetPos);
				var normal = dir.step().normalize();
				if (offsetState.isAir() && !offsetState.liquid() && !state.isAir())
				{
					for (int i = 0; i < 2; i++)
					{
						double offsetX = this.random.nextGaussian() * (normal.y + normal.z) / 2f;
						double offsetY = this.random.nextGaussian() * (normal.x + normal.z) / 2f;
						double offsetZ = this.random.nextGaussian() * (normal.y + normal.x) / 2f;
						world.addParticle(
								GalaxiesParticleTypes.SHORT_FLAME_PARTICLE,
								pos.getX() + 0.5 + normal.x + offsetX,
								pos.getY() + 0.5 + normal.y + offsetY,
								pos.getZ() + 0.5 + normal.z + offsetZ,
								0,
								0,
								0
						);
					}
					for (int i = 0; i < 3; i++)
					{
						double offsetX = this.random.nextGaussian() * (normal.y + normal.z);
						double offsetY = this.random.nextGaussian() * (normal.x + normal.z);
						double offsetZ = this.random.nextGaussian() * (normal.y + normal.x);
						world.addParticle(
								GalaxiesParticleTypes.SMALL_SHORT_FLAME_PARTICLE,
								pos.getX() + 0.5 + normal.x + offsetX,
								pos.getY() + 0.5 + normal.y + offsetY,
								pos.getZ() + 0.5 + normal.z + offsetZ,
								0,
								0,
								0
						);
					}
				}
			}
		}


		super.tick();
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.INFERNO_GRENADE_ITEM;
	}
}
