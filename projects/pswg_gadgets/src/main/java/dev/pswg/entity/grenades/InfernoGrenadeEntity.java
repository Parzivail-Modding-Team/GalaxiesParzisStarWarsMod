package dev.pswg.entity.grenades;

import com.google.common.collect.ConcurrentHashMultiset;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.tag.BlockTags;
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
			for (int i = 0; i < 8; i++)
			{
				world.addParticleClient(ParticleTypes.SMOKE,
				                  true,
				                  true,
				                  pos.getX() + world.random.nextGaussian() * 0.1f,
				                  pos.getY() + world.random.nextGaussian() * 0.1f,
				                  pos.getZ() + world.random.nextGaussian() * 0.1f,
				                  world.random.nextGaussian() * 0.05f,
				                  world.random.nextGaussian() * 0.05f,
				                  world.random.nextGaussian() * 0.05f

				);
			}
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
		else if (state.isIn(BlockTags.DIRT) && pos.isWithinDistance(getEntityPos(), INNER_AREA_DISTANCE))
		{
			world.setBlockState(pos, GadgetsBlocks.FERTILE_DIRT_BLOCK.getDefaultState());
		}
	}

	public void spawnScorchParticles(Vector3f unitVec, BlockPos pos)
	{
		var world = getEntityWorld();
		SimpleParticleType scorchParticleType = pos.isWithinDistance(getEntityPos(), INNER_AREA_DISTANCE) ? GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE : GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE;
		world.addParticleClient(scorchParticleType,
		                  true,
		                  true,
		                  pos.getX() + 0.5f + unitVec.x / 2f + (unitVec.y + unitVec.z) * (1 / 4f),
		                  pos.getY() + 0.5f + unitVec.y / 2f + (unitVec.x + unitVec.z) * (1 / 4f),
		                  pos.getZ() + 0.5f + unitVec.z / 2f + (unitVec.x + unitVec.y) * (1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticleClient(scorchParticleType,
		                  true,
		                  true,
		                  pos.getX() + 0.5f + unitVec.x / 2f + (unitVec.y + unitVec.z) * (1 / 4f),
		                  pos.getY() + 0.5f + unitVec.y / 2f + (unitVec.x + unitVec.z) * (-1 / 4f),
		                  pos.getZ() + 0.5f + unitVec.z / 2f + (unitVec.x + unitVec.y) * (-1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticleClient(scorchParticleType,
		                  true,
		                  true,
		                  pos.getX() + 0.5f + unitVec.x / 2f + (unitVec.y + unitVec.z) * (-1 / 4f),
		                  pos.getY() + 0.5f + unitVec.y / 2f + (unitVec.x + unitVec.z) * (1 / 4f),
		                  pos.getZ() + 0.5f + unitVec.z / 2f + (unitVec.x + unitVec.y) * (-1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticleClient(scorchParticleType,
		                  true,
		                  true,
		                  pos.getX() + 0.5f + unitVec.x / 2f + (unitVec.y + unitVec.z) * (-1 / 4f),
		                  pos.getY() + 0.5f + unitVec.y / 2f + (unitVec.x + unitVec.z) * (-1 / 4f),
		                  pos.getZ() + 0.5f + unitVec.z / 2f + (unitVec.x + unitVec.y) * (1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
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
