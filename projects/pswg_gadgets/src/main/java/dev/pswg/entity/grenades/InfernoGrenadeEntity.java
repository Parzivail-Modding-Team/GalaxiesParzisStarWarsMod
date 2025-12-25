package dev.pswg.entity.grenades;

import com.google.common.collect.ConcurrentHashMultiset;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.item.grenades.GrenadeItem;
import dev.pswg.packet.PreciseVelocityParticleS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

	public InfernoGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
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
		var world = getEntityWorld();
		if (state.isIn(GadgetsBlocks.Tags.INFERNO_CHAR))
		{
			world.setBlockState(pos, GadgetsBlocks.CHARRED_BLOCK.getDefaultState());
		}
		else if (state.isIn(GadgetsBlocks.Tags.INFERNO_DESTROY))
		{
			if (world instanceof ServerWorld serverWorld)
				createSmoke(pos, serverWorld);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
		else if (state.isIn(BlockTags.DIRT) && pos.isWithinDistance(getEntityPos(), INNER_AREA_DISTANCE))
		{
			world.setBlockState(pos, GadgetsBlocks.FERTILE_DIRT_BLOCK.getDefaultState());
		}
	}

	private static void createSmoke(BlockPos pos, ServerWorld serverWorld)
	{
		for (int i = 0; i < 8; i++)
		{
			double x = pos.getX() + serverWorld.random.nextGaussian() * 0.1f;
			double y = pos.getY() + serverWorld.random.nextGaussian() * 0.1f;
			double z = pos.getZ() + serverWorld.random.nextGaussian() * 0.1f;
			double vX = serverWorld.random.nextGaussian() * 0.05f;
			double vY = serverWorld.random.nextGaussian() * 0.05f;
			double vZ = serverWorld.random.nextGaussian() * 0.05f;
			var payload = new PreciseVelocityParticleS2CPayload(ParticleTypes.SMOKE, new Vec3d(x, y, z), new Vec3d(vX, vY, vZ));
			for (ServerPlayerEntity player : serverWorld.getPlayers())
				ServerPlayNetworking.send(player, payload);
		}
	}

	public void spawnScorchParticles(Vector3f unitVec, BlockPos pos)
	{
		if (getEntityWorld() instanceof ServerWorld serverWorld)
		{
			SimpleParticleType scorchParticleType = pos.isWithinDistance(getEntityPos(), INNER_AREA_DISTANCE) ? GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE : GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE;
			createScorchParticles(serverWorld, scorchParticleType, pos.getX(), pos.getY(), pos.getZ(), unitVec.x, unitVec.y, unitVec.z);
		}
	}

	private static void createScorchParticles(ServerWorld serverWorld, SimpleParticleType particleType, double x, double y, double z, float unitX, float unitY, float unitZ)
	{
		List<PreciseVelocityParticleS2CPayload> payloads = new ArrayList<>();
		double x1 = x + 0.5f + unitX / 2f + (unitY + unitZ) * (1 / 4f);
		double x2 = x + 0.5f + unitX / 2f + (unitY + unitZ) * (-1 / 4f);
		double y1 = y + 0.5f + unitY / 2f + (unitX + unitZ) * (1 / 4f);
		double y2 = y + 0.5f + unitY / 2f + (unitX + unitZ) * (-1 / 4f);
		double z1 = z + 0.5f + unitZ / 2f + (unitX + unitY) * (1 / 4f);
		double z2 = z + 0.5f + unitZ / 2f + (unitX + unitY) * (-1 / 4f);
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3d(x1, y1, z1), new Vec3d(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3d(x1, y2, z2), new Vec3d(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3d(x2, y1, z2), new Vec3d(unitX, unitY, unitZ)));
		payloads.add(new PreciseVelocityParticleS2CPayload(particleType, new Vec3d(x2, y2, z1), new Vec3d(unitX, unitY, unitZ)));

		for (var payload : payloads)
		{
			for (ServerPlayerEntity player : serverWorld.getPlayers())
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
			setVelocity(Vec3d.ZERO);
			velocityModified = true;
		}
		var world = getEntityWorld();
		if (detonationTicks == 1)
		{
			var oPos = getBlockPos();
			this.burntBlocks.add(oPos);
			burnBlock(world.getBlockState(oPos), oPos);
			for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(INNER_AREA_DISTANCE), livingEntity -> livingEntity.getEntityPos().distanceTo(getEntityPos()) <= INNER_AREA_DISTANCE))
			{
				if (!entity.isFireImmune())
				{
					entity.setOnFireForTicks(100);
					if (world instanceof ServerWorld serverWorld)
					{
						entity.damage(serverWorld, GadgetsDamage.create(serverWorld, DamageTypes.IN_FIRE), 8);
					}
				}
			}
		}
		if (detonationTicks == 12)
		{
			for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(MAX_DISTANCE), livingEntity -> livingEntity.getEntityPos().distanceTo(getEntityPos()) <= MAX_DISTANCE))
			{
				if (!entity.isFireImmune())
				{
					entity.setOnFireForTicks(300);
					if (world instanceof ServerWorld serverWorld)
					{
						entity.damage(serverWorld, GadgetsDamage.create(serverWorld, DamageTypes.IN_FIRE), 4);
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
					var offsetPos = pos.offset(dir);
					if (!burntBlocks.contains(offsetPos) && offsetPos.isWithinDistance(getBlockPos(), MAX_DISTANCE))
					{
						List<Direction> airSideList = new ArrayList<>(6);
						for (Direction dir2 : Direction.values())
						{
							var offsetPos2 = offsetPos.offset(dir2);
							var offsetState2 = world.getBlockState(offsetPos2);
							if (offsetState2.isAir() && !offsetState2.isLiquid())
							{
								airSideList.add(dir2);
							}
						}
						if (!airSideList.isEmpty())
						{
							burntBlocks.add(offsetPos);
							var offsetState = world.getBlockState(offsetPos);
							if (!offsetState.isLiquid())
							{
								if (!offsetState.isAir())
								{
									burnBlock(offsetState, offsetPos);

									for (Direction dir3 : airSideList)
									{
										var unitVec = dir3.getUnitVector();
										spawnScorchParticles(unitVec, offsetPos);
									}
								}
							}
						}
					}
				}
			}
		}
		var flameWaveList = burntBlocks.stream().filter(pos -> pos.isWithinDistance(getEntityPos(), (float)firewaveTicks / MAX_FIRE_WAVE_TICKS * MAX_DISTANCE + 1) && !pos.isWithinDistance(getEntityPos(), (float)firewaveTicks / MAX_FIRE_WAVE_TICKS * MAX_DISTANCE)).toList();
		for (BlockPos pos : flameWaveList)
		{
			var state = world.getBlockState(pos);
			for (Direction dir : Direction.values())
			{
				var offsetPos = pos.offset(dir);
				var offsetState = world.getBlockState(offsetPos);
				var normal = dir.getUnitVector().normalize();
				if (offsetState.isAir() && !offsetState.isLiquid() && !state.isAir())
				{
					for (int i = 0; i < 2; i++)
					{
						double offsetX = this.random.nextGaussian() * (normal.y + normal.z) / 2f;
						double offsetY = this.random.nextGaussian() * (normal.x + normal.z) / 2f;
						double offsetZ = this.random.nextGaussian() * (normal.y + normal.x) / 2f;
						world.addParticleClient(
								GadgetsParticleTypes.SHORT_FLAME_PARTICLE,
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
						world.addParticleClient(
								GadgetsParticleTypes.SMALL_SHORT_FLAME_PARTICLE,
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
		return GadgetsItems.THERMAL_DETONATOR_ITEM;
	}
}
