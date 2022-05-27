package com.parzivail.pswg.util;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgParticles;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.BlasterIonBoltEntity;
import com.parzivail.pswg.entity.BlasterStunBoltEntity;
import com.parzivail.util.data.PacketByteBufHelper;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.entity.PProjectileEntityDamageSource;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class BlasterUtil
{
	public static DamageSource getDamageSource(Entity projectile, Entity attacker)
	{
		return new PProjectileEntityDamageSource("pswg.blaster", projectile, attacker).setIgnoresInvulnerableFrames().setProjectile();
	}

	public static DamageSource getSlugDamageSource(Entity attacker)
	{
		return new PProjectileEntityDamageSource("pswg.blaster.slug", attacker).setIgnoresInvulnerableFrames().setProjectile();
	}

	public static void fireBolt(World world, PlayerEntity player, Vec3d fromDir, float range, float damage, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterBoltEntity(SwgEntities.Misc.BlasterBolt, player, world);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		world.spawnEntity(bolt);

		var start = new Vec3d(bolt.getX(), bolt.getY() + bolt.getHeight() / 2f, bolt.getZ());

		var hit = EntityUtil.raycastEntities(getTargetedEntityClass(), start, fromDir, range, player, new Entity[] { player });
		var blockHit = EntityUtil.raycastBlocks(start, fromDir, range, player, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY);

		var entityDistance = hit == null ? Double.MAX_VALUE : hit.hit().squaredDistanceTo(player.getPos());
		var blockDistance = blockHit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : blockHit.squaredDistanceTo(player);

		if (hit != null && entityDistance < blockDistance)
		{
			hit.entity().damage(getDamageSource(bolt, player), damage);
		}
	}

	public static void fireIon(World world, PlayerEntity player, float range, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterIonBoltEntity(SwgEntities.Misc.BlasterIonBolt, player, world);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		world.spawnEntity(bolt);
	}

	public static void handleSlugFired(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender)
	{
		var start = PacketByteBufHelper.readVec3d(buf);
		var fromDir = PacketByteBufHelper.readVec3d(buf);

		var distance = buf.readDouble();

		if (distance > 1e5)
			return;

		for (var d = 2; d < distance; d++)
		{
			var vec = start.add(fromDir.multiply(d));

			var dx = 0.01 * client.world.random.nextGaussian();
			var dy = 0.01 * client.world.random.nextGaussian();
			var dz = 0.01 * client.world.random.nextGaussian();

			client.world.addParticle(SwgParticles.SLUG_TRAIL, vec.x, vec.y, vec.z, dx, dy, dz);
		}

		var shouldScorch = buf.readBoolean();

		if (shouldScorch)
		{
			var pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			var normal = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

			createScorchParticles(client, pos, fromDir, normal, false);
		}
	}

	public static void fireSlug(World world, PlayerEntity player, Vec3d fromDir, float range, float damage)
	{
		var start = player.getEyePos();

		var hit = EntityUtil.raycastEntities(getTargetedEntityClass(), start, fromDir, range, player, new Entity[] { player });
		var blockHit = EntityUtil.raycastBlocks(start, fromDir, range, player, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE);

		var entityDistance = hit == null ? Double.MAX_VALUE : hit.hit().squaredDistanceTo(player.getPos());
		var blockDistance = blockHit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : blockHit.squaredDistanceTo(player);
		BlockPos end;

		if (hit != null && entityDistance < blockDistance)
		{
			hit.entity().damage(getSlugDamageSource(player), damage);
			end = new BlockPos(hit.hit());
		}
		else if (blockHit.getType() == HitResult.Type.BLOCK)
		{
			end = new BlockPos(blockHit.getPos());
			// TODO: smoke puff, blaster burn mark, etc server-side stuff
		}
		else
			end = player.getBlockPos();

		var distance = Math.min(entityDistance, blockDistance);

		if (hit == null && blockHit.getType() == HitResult.Type.MISS)
			distance = range;

		var passedData = WorldEvent.createBuffer(WorldEvent.SLUG_FIRED);
		PacketByteBufHelper.writeVec3d(passedData, start);
		PacketByteBufHelper.writeVec3d(passedData, fromDir);
		passedData.writeDouble(distance);
		passedData.writeBoolean(blockHit.getType() == HitResult.Type.BLOCK);

		if (blockHit.getType() == HitResult.Type.BLOCK)
		{
			var normal = new Vec3d(blockHit.getSide().getUnitVector());

			var pos = blockHit.getPos();

			PacketByteBufHelper.writeVec3d(passedData, pos);
			PacketByteBufHelper.writeVec3d(passedData, normal);
		}

		for (var trackingPlayer : PlayerLookup.tracking((ServerWorld)world, end))
			ServerPlayNetworking.send(trackingPlayer, SwgPackets.S2C.WorldEvent, passedData);
	}

	private static Class<? extends Entity> getTargetedEntityClass()
	{
		var config = Resources.CONFIG.get();
		if (config.server.allowBlasterNonlivingDamage)
			return Entity.class;

		return LivingEntity.class;
	}

	public static void fireStun(World world, PlayerEntity player, Vec3d fromDir, float range, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final var bolt = new BlasterStunBoltEntity(SwgEntities.Misc.BlasterStunBolt, player, world);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		world.spawnEntity(bolt);

		var start = new Vec3d(bolt.getX(), bolt.getY() + bolt.getHeight() / 2f, bolt.getZ());

		var hit = EntityUtil.raycastEntitiesCone(start, fromDir, 10 / 180f * Math.PI, range, player, new Entity[] { player });

		// TODO: prevent stunning through walls

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

		createScorchParticles(client, pos, incident, normal, true);
	}

	private static void createScorchParticles(MinecraftClient client, Vec3d pos, Vec3d incident, Vec3d normal, boolean energyScorch)
	{
		var blockPos = new BlockPos(pos.subtract(normal.multiply(0.1f)));

		assert client.world != null;

		var offset = 0.005 + 0.0005 * client.world.random.nextDouble();
		var heatEncodedNormal = normal.multiply(energyScorch ? 1 : 0.3f);
		client.world.addParticle(SwgParticles.SCORCH, pos.x + normal.x * offset, pos.y + normal.y * offset, pos.z + normal.z * offset, heatEncodedNormal.x, heatEncodedNormal.y, heatEncodedNormal.z);

		var reflection = normal.multiply(2 * normal.dotProduct(incident)).subtract(incident);

		reflection = reflection.multiply(-1);

		for (var i = 0; i < 16; i++)
		{
			var vx = client.world.random.nextGaussian() * 0.03;
			var vy = client.world.random.nextGaussian() * 0.03;
			var vz = client.world.random.nextGaussian() * 0.03;

			var sparkVelocity = reflection.multiply(0.3f * (client.world.random.nextDouble() * 0.5 + 0.5));
			client.world.addParticle(SwgParticles.SPARK, pos.x, pos.y, pos.z, sparkVelocity.x + vx, sparkVelocity.y + vy, sparkVelocity.z + vz);

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
