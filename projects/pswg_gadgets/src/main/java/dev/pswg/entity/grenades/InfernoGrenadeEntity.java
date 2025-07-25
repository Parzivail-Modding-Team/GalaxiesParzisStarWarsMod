package dev.pswg.entity.grenades;

import com.google.common.collect.ConcurrentHashMultiset;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.item.grenades.GrenadeItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
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
	public final float MAX_DISTANCE = 8;

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

	public void spawnScorchParticles(Vector3f unitVec, BlockPos pos)
	{
		var world = getWorld();
		SimpleParticleType scorchParticleType = pos.isWithinDistance(getPos(), MAX_DISTANCE / 2f) ? GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE : GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE;
		world.addParticle(scorchParticleType,
		                  true,
		                  true,

		                  pos.getX() + unitVec.x + (unitVec.y + unitVec.z) * (3 / 4f),
		                  pos.getY() + unitVec.y + (unitVec.x + unitVec.z) * (3 / 4f),
		                  pos.getZ() + unitVec.z + (unitVec.x + unitVec.y) * (3 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticle(scorchParticleType,
		                  true,
		                  true,

		                  pos.getX() + unitVec.x + (unitVec.y + unitVec.z) * (3 / 4f),
		                  pos.getY() + unitVec.y + (unitVec.x + unitVec.z) * (1 / 4f),
		                  pos.getZ() + unitVec.z + (unitVec.x + unitVec.y) * (1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticle(scorchParticleType,
		                  true,
		                  true,

		                  pos.getX() + unitVec.x + (unitVec.y + unitVec.z) * (1 / 4f),
		                  pos.getY() + unitVec.y + (unitVec.x + unitVec.z) * (3 / 4f),
		                  pos.getZ() + unitVec.z + (unitVec.x + unitVec.y) * (1 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		world.addParticle(scorchParticleType,
		                  true,
		                  true,

		                  pos.getX() + unitVec.x + (unitVec.y + unitVec.z) * (1 / 4f),
		                  pos.getY() + unitVec.y + (unitVec.x + unitVec.z) * (1 / 4f),
		                  pos.getZ() + unitVec.z + (unitVec.x + unitVec.y) * (3 / 4f),
		                  unitVec.x,
		                  unitVec.y,
		                  unitVec.z
		);
		for (float i = 0; i < (world.random.nextBetween(1, 4) * (MAX_DISTANCE + 1 - getPos().distanceTo(pos.toCenterPos()))); i += 1)
		{
			double offsetX = this.random.nextGaussian() * (unitVec.y + unitVec.z);
			double offsetY = this.random.nextGaussian() * (unitVec.x + unitVec.z);
			double offsetZ = this.random.nextGaussian() * (unitVec.y + unitVec.x);
			offsetX = offsetX - (offsetX) % (2 / 32f);
			offsetY = offsetY - (offsetY) % (2 / 32f);
			offsetZ = offsetZ - (offsetZ) % (2 / 32f);

			world.addParticle(ParticleTypes.SMOKE,
			                  true,
			                  true,
			                  pos.getX() + unitVec.x + offsetX,
			                  pos.getY() + unitVec.y + offsetY,
			                  pos.getZ() + unitVec.z + offsetZ,
			                  0,
			                  world.random.nextGaussian() * 0.1f,
			                  0

			);
		}
		for (float i = 0; i < (world.random.nextBetween(1, 2) * (MAX_DISTANCE + 1 - getPos().distanceTo(pos.toCenterPos()))) / 2d; i += 1)
		{
			double offsetX = this.random.nextGaussian() * (unitVec.y + unitVec.z);
			double offsetY = this.random.nextGaussian() * (unitVec.x + unitVec.z);
			double offsetZ = this.random.nextGaussian() * (unitVec.y + unitVec.x);
			offsetX = offsetX - (offsetX) % (2 / 32f);
			offsetY = offsetY - (offsetY) % (2 / 32f);
			offsetZ = offsetZ - (offsetZ) % (2 / 32f);

			world.addParticle(ParticleTypes.LARGE_SMOKE,
			                  true,
			                  true,
			                  pos.getX() + unitVec.x + offsetX,
			                  pos.getY() + unitVec.y + offsetY,
			                  pos.getZ() + unitVec.z + offsetZ,
			                  0,
			                  world.random.nextGaussian() * 0.1f,
			                  0

			);
		}
	}

	@Override
	public void tick()
	{
		if (detonationTicks > 30)
			discard();
		if (detonated)
		{
			detonationTicks++;
			setVelocity(Vec3d.ZERO);
			velocityModified = true;
		}
		var world = getWorld();
		if (detonationTicks == 1)
		{
			var oPos = getBlockPos();
			this.burntBlocks.add(oPos);
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
						List<Direction> dirList = new ArrayList<>(6);
						for (Direction dir2 : Direction.values())
						{
							var offsetPos2 = offsetPos.offset(dir2);
							var offsetState2 = world.getBlockState(offsetPos2);
							if (offsetState2.isAir() && !offsetState2.isLiquid())
							{
								dirList.add(dir2);
							}
						}
						if (!dirList.isEmpty())
						{
							burntBlocks.add(offsetPos);
							var offsetState = world.getBlockState(offsetPos);
							if (!offsetState.isAir() && !offsetState.isLiquid())
							{
								if (!offsetState.isAir())
									world.setBlockState(offsetPos, Blocks.COAL_BLOCK.getDefaultState());

								for (Direction dir3 : dirList)
								{
									var offsetPos2 = offsetPos.offset(dir3);
									var unitVec = dir3.getUnitVector().normalize();
									spawnScorchParticles(unitVec, offsetPos);

								}
							}
						}
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
