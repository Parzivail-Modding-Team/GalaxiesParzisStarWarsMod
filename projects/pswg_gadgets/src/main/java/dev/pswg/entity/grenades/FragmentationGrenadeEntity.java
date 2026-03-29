package dev.pswg.entity.grenades;

import dev.pswg.block.GrenadeBlock;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.item.grenades.GrenadeItem;
import dev.pswg.networking.PreciseVelocityParticleS2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class FragmentationGrenadeEntity extends GrenadeEntityWithBlock
{
	public boolean IS_EXPLODING = false;
	public int EXPLOSION_TICK = 0;
	private boolean COLLISION_BELOW;
	public boolean SHOULD_RENDER = true;

	public FragmentationGrenadeEntity(EntityType<FragmentationGrenadeEntity> type, Level world)
	{
		super(type, world, CollisionType.BOUNCE);
		setExplosionPower(4f);
	}

	@Override
	public GrenadeBlock getBlock()
	{
		return GadgetsBlocks.FRAGMENTATION_GRENADE_BLOCK;
	}

	@Override
	public GrenadeItem getItem()
	{
		return GadgetsItems.FRAGMENTATION_GRENADE_ITEM;
	}

	@Override
	public void explode()
	{
		if (!IS_EXPLODING)
		{
			if (level() instanceof ServerLevel serverWorld)
			{
				serverWorld.sendParticles(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, false, true, getX(), getY() + 0.05d, getZ(), 1, 0, 0, 0, 0);
			}
			IS_EXPLODING = true;

			int randomNum = RandomSource.create().nextIntBetweenInclusive(1, 4);


			switch (randomNum){
				case 1:
					level().playSound(null, blockPosition(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION1, SoundSource.PLAYERS, 4f, 1f);
					break;
				case 2:
					level().playSound(null, blockPosition(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION2, SoundSource.PLAYERS, 4f, 1f);
					break;
				case 3:
					level().playSound(null, blockPosition(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION3, SoundSource.PLAYERS, 4f, 1f);
					break;
				case 4:
					level().playSound(null, blockPosition(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION4, SoundSource.PLAYERS, 4f, 1f);
					break;
			}
		}
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance)
	{
		if (super.shouldRenderAtSqrDistance(distance) && !IS_EXPLODING)
			return true;
		return false;
	}

	@Override
	public void tick()
	{
		super.tick();
		//if (getEntityWorld().isClient() && this.age == 1 && this.isPrimed())
		//	SoundHelper.playFragmentationEntitySound(this);
		if (IS_EXPLODING)
		{
			this.setDeltaMovement(Vec3.ZERO);
			this.hurtMarked = true;
		}

		if (EXPLOSION_TICK == 6)
		{
			List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(getExplosionPower() / 4f * 3f, getExplosionPower() / 4f * 3f, getExplosionPower() / 4f * 3f), entity -> true);
			for (LivingEntity entity : entities)
			{
				float x = (float)(entity.getX() - getX()) / (getExplosionPower() / 4f * 3f);
				float z = (float)(entity.getZ() - getZ()) / (getExplosionPower() / 4f * 3f);
				entity.push(-x, 0, -z);
			}
		}
		if (EXPLOSION_TICK == 7)
		{
			for (int i = 0; i < RandomSource.create().nextIntBetweenInclusive(70, 100); i++)
			{
				double vx = level().getRandom().nextGaussian() * 0.5;
				double vz = level().getRandom().nextGaussian() * 0.5;
				double vy;

				if (COLLISION_BELOW)
					vy = Math.abs(level().getRandom().nextGaussian() * 0.8);
				else
					vy = level().getRandom().nextGaussian() * 0.4;
				if (level() instanceof ServerLevel serverWorld)
					createSparkParticle(serverWorld, getX(), getY(), getZ(), vx, vy, vz);
			}
		}
		if (EXPLOSION_TICK >= 15)
		{
			super.explode(new Vec3(getX(), getY() + 0.1d, getZ()));
		}
		if (IS_EXPLODING)
			EXPLOSION_TICK++;
	}

	private static void createSparkParticle(ServerLevel serverWorld, double x, double y, double z, double vx, double vy, double vz)
	{
		var payload = new PreciseVelocityParticleS2CPayload(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, new Vec3(x, y, z), new Vec3(vx, vy, vz));
		for (ServerPlayer player : serverWorld.players())
			ServerPlayNetworking.send(player, payload);
	}

	@Override
	public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float explosionPower)
	{
		return state.is(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY);
	}

}
