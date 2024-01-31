package com.parzivail.pswg.features.blasters;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgParticleTypes;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.BlasterIonBoltEntity;
import com.parzivail.pswg.entity.BlasterStunBoltEntity;
import com.parzivail.util.data.PacketByteBufHelper;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Function;

public class BlasterUtil
{
	public static void fireBolt(World world, PlayerEntity player, Vec3d fromDir, float range, Function<Double, Double> damage, boolean ignoreWater, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterBoltEntity(SwgEntities.Misc.BlasterBolt, player, world, ignoreWater);
		entityInitializer.accept(bolt);
		bolt.setRange(range);
		bolt.setDamageFunction(damage);

		world.spawnEntity(bolt);
	}

	public static void fireIon(World world, PlayerEntity player, float range, boolean ignoreWater, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterIonBoltEntity(SwgEntities.Misc.BlasterIonBolt, player, world, ignoreWater);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		// TODO: ion effects

		world.spawnEntity(bolt);
	}

	public static void fireStun(World world, PlayerEntity player, Vec3d fromDir, float range, boolean ignoreWater, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterStunBoltEntity(SwgEntities.Misc.BlasterStunBolt, player, world, ignoreWater);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		world.spawnEntity(bolt);

		var start = new Vec3d(bolt.getX(), bolt.getY() + bolt.getHeight() / 2f, bolt.getZ());

		var hit = EntityUtil.raycastEntitiesCone(start, fromDir, 10 / 180f * Math.PI, range, player, new Entity[] { player });

		// TODO: prevent stunning through walls
		// TODO: ignore water

		for (var e : hit)
		{
			if (e instanceof LivingEntity le)
			{
				le.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 3, true, false), player);
				le.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 7 * 20, 0, true, false), player);
				le.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 4 * 20, 0, true, false), player);
				le.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 6 * 20, 1, true, false), player);
			}
		}
	}

	public static void handleBoltHit(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var pos = PacketByteBufHelper.readVec3d(buf);
		var incident = PacketByteBufHelper.readVec3d(buf);
		var normal = PacketByteBufHelper.readVec3d(buf);

		client.execute(() -> createScorchParticles(client, pos, incident, normal, true));
	}

	private static void createScorchParticles(MinecraftClient client, Vec3d pos, Vec3d incident, Vec3d normal, boolean energyScorch)
	{
		var blockPos = new BlockPos(MathUtil.floorInt(pos.subtract(normal.multiply(0.1f))));

		assert client.world != null;

		var offset = 0.005 + 0.0005 * client.world.random.nextDouble();
		var heatEncodedNormal = normal.multiply(energyScorch ? 1 : 0.3f);
		client.world.addParticle(SwgParticleTypes.SCORCH, pos.x + normal.x * offset, pos.y + normal.y * offset, pos.z + normal.z * offset, heatEncodedNormal.x, heatEncodedNormal.y, heatEncodedNormal.z);

		var reflection = MathUtil.reflect(incident, normal);

		for (var i = 0; i < 16; i++)
		{
			var vx = client.world.random.nextGaussian() * 0.03;
			var vy = client.world.random.nextGaussian() * 0.03;
			var vz = client.world.random.nextGaussian() * 0.03;

			var sparkVelocity = reflection.multiply(0.3f * (client.world.random.nextDouble() * 0.5 + 0.5));
			client.world.addParticle(SwgParticleTypes.SPARK, pos.x, pos.y, pos.z, sparkVelocity.x + vx, sparkVelocity.y + vy, sparkVelocity.z + vz);

			if (i % 3 == 0)
			{
				var debrisVelocity = reflection.multiply(0.15f * (client.world.random.nextDouble() * 0.5 + 0.5));
				client.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, client.world.getBlockState(blockPos)), pos.x, pos.y, pos.z, debrisVelocity.x + vx, debrisVelocity.y + vy, debrisVelocity.z + vz);
			}

			if (i % 2 == 0)
			{
				var smokeVelocity = reflection.multiply(0.08f * (client.world.random.nextDouble() * 0.5 + 0.5));
				client.world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, smokeVelocity.x + vx, smokeVelocity.y + vy, smokeVelocity.z + vz);
			}
		}
	}
}
