package com.parzivail.pswg.features.grenades;

import com.parzivail.pswg.container.SwgParticleTypes;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import com.parzivail.util.data.PacketByteBufHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class GrenadeUtil
{
	@Environment(EnvType.CLIENT)
	public static void createSparkParticles(World world, Vec3d pos, Entity entity, boolean yCollision)
	{
		for (int i = 0; i < Random.create().nextBetween(50, 80); i++)
		{
			double vx = world.random.nextGaussian() * 0.5;
			double vz = world.random.nextGaussian() * 0.5;
			double vy;

			if (yCollision)
				vy = Math.abs(world.random.nextGaussian() * 0.8);
			else
				vy = world.random.nextGaussian() * 0.4;
			world.addParticle(SwgParticleTypes.FRAGMENTATION_GRENADE_SPARK, pos.x, pos.y, pos.z, vx, vy, vz);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void handleFragmentationGrenadeExplosion(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		boolean isStart = buf.readBoolean();
		if (isStart)
		{
			var entityId = buf.readInt();
			var world = client.world;
			var entity = world.getEntityById(entityId);
			if (entity instanceof FragmentationGrenadeEntity fragmentationGrenade)
				fragmentationGrenade.SHOULD_RENDER = false;
		}
		else
		{
			var world = client.world;
			var pos = PacketByteBufHelper.readVec3d(buf);
			var entityId = buf.readInt();
			var yCollision = buf.readBoolean();
			client.execute(() -> createSparkParticles(world, pos, world.getEntityById(entityId), yCollision));
		}
	}
}
