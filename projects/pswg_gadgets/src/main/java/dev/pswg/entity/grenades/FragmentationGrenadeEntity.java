package dev.pswg.entity.grenades;

import dev.pswg.block.GrenadeBlock;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.item.grenades.GrenadeItem;
import dev.pswg.packet.MixerSyncS2CPayload;
import dev.pswg.packet.PreciseVelocityParticleS2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class FragmentationGrenadeEntity extends GrenadeEntityWithBlock
{
	public boolean IS_EXPLODING = false;
	public int EXPLOSION_TICK = 0;
	private boolean COLLISION_BELOW;
	public boolean SHOULD_RENDER = true;

	public FragmentationGrenadeEntity(EntityType<FragmentationGrenadeEntity> type, World world)
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
			if (getEntityWorld() instanceof ServerWorld serverWorld)
			{
				serverWorld.spawnParticles(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, false, true, getX(), getY() + 0.05d, getZ(), 1, 0, 0, 0, 0);
				var passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBoolean(true);
				passedData.writeInt(getId());
				//for (var player : PlayerLookup.tracking((ServerWorld)getEntityWorld(), this.getBlockPos()))
				//	ServerPlayNetworking.send(player, SwgPackets.S2C.FragmentationGrenadeExplode, passedData);


			}
			IS_EXPLODING = true;

			int randomNum = Random.create().nextBetween(1, 4);


			switch (randomNum){
				case 1:
					getEntityWorld().playSound(null, getBlockPos(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION1, SoundCategory.PLAYERS, 4f, 1f);
					break;
				case 2:
					getEntityWorld().playSound(null, getBlockPos(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION2, SoundCategory.PLAYERS, 4f, 1f);
					break;
				case 3:
					getEntityWorld().playSound(null, getBlockPos(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION3, SoundCategory.PLAYERS, 4f, 1f);
					break;
				case 4:
					getEntityWorld().playSound(null, getBlockPos(), GadgetsSounds.FRAGMENTATION_GRENADE_EXPLOSION4, SoundCategory.PLAYERS, 4f, 1f);
					break;
			}
		}
	}

	@Override
	public boolean shouldRender(double distance)
	{
		if (super.shouldRender(distance) && !IS_EXPLODING)
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
			this.setVelocity(Vec3d.ZERO);
			this.velocityModified = true;
		}

		if (EXPLOSION_TICK == 6)
		{
			/*if (!getEntityWorld().isClient)
			{
				var passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBoolean(false);
				PacketByteBufHelper.writeVec3d(passedData, getPos());
				passedData.writeInt(getId());
				passedData.writeBoolean(COLLISION_BELOW);
				for (var player : PlayerLookup.tracking((ServerWorld)getEntityWorld(), this.getBlockPos()))
					ServerPlayNetworking.send(player, SwgPackets.S2C.FragmentationGrenadeExplode, passedData);
			}*/
			List<LivingEntity> entities = getEntityWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(getExplosionPower() / 4f * 3f, getExplosionPower() / 4f * 3f, getExplosionPower() / 4f * 3f), entity -> true);
			for (LivingEntity entity : entities)
			{
				float x = (float)(entity.getX() - getX()) / (getExplosionPower() / 4f * 3f);
				float z = (float)(entity.getZ() - getZ()) / (getExplosionPower() / 4f * 3f);
				entity.addVelocity(-x, 0, -z);
			}
		}
		if (EXPLOSION_TICK == 7)
		{
			for (int i = 0; i < Random.create().nextBetween(70, 100); i++)
			{
				double vx = getEntityWorld().random.nextGaussian() * 0.5;
				double vz = getEntityWorld().random.nextGaussian() * 0.5;
				double vy;

				if (COLLISION_BELOW)
					vy = Math.abs(getEntityWorld().random.nextGaussian() * 0.8);
				else
					vy = getEntityWorld().random.nextGaussian() * 0.4;
				if (getEntityWorld() instanceof ServerWorld serverWorld)
					createSparkParticle(serverWorld, getX(), getY(), getZ(), vx, vy, vz);
			}
		}
		if (EXPLOSION_TICK >= 15)
		{
			super.explode(new Vec3d(getX(), getY() + 0.1d, getZ()));
		}
		if (IS_EXPLODING)
			EXPLOSION_TICK++;
	}

	private static void createSparkParticle(ServerWorld serverWorld, double x, double y, double z, double vx, double vy, double vz)
	{
		var payload = new PreciseVelocityParticleS2CPayload(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, new Vec3d(x, y, z), new Vec3d(vx, vy, vz));
		for (ServerPlayerEntity player : serverWorld.getPlayers())
			ServerPlayNetworking.send(player, payload);
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower)
	{
		return state.isIn(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY);
	}

}