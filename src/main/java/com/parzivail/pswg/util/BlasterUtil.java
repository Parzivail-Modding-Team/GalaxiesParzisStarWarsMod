package com.parzivail.pswg.util;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.BlasterStunBoltEntity;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.entity.PProjectileEntityDamageSource;
import io.netty.buffer.Unpooled;
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

		var hit = EntityUtil.raycastEntities(start, fromDir, range, player, new Entity[] { player });
		var blockHit = EntityUtil.raycastBlocks(start, fromDir, range, player, RaycastContext.FluidHandling.ANY);

		var entityDistance = hit == null ? Double.MAX_VALUE : hit.hit().squaredDistanceTo(player.getPos());
		var blockDistance = blockHit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : blockHit.squaredDistanceTo(player);

		if (hit != null && entityDistance < blockDistance)
		{
			hit.entity().damage(getDamageSource(bolt, player), damage);
		}
		else if (blockHit.getType() == HitResult.Type.BLOCK)
		{
			// TODO: smoke puff, blaster burn mark, etc server-side stuff
		}
	}

	public static void handleSlugFired(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender)
	{
		var sx = packetByteBuf.readDouble();
		var sy = packetByteBuf.readDouble();
		var sz = packetByteBuf.readDouble();
		var vx = packetByteBuf.readDouble();
		var vy = packetByteBuf.readDouble();
		var vz = packetByteBuf.readDouble();
		var distance = packetByteBuf.readDouble();

		var start = new Vec3d(sx, sy, sz);
		var fromDir = new Vec3d(vx, vy, vz);

		if (distance > 1e100)
			return;

		for (var d = 0; d < distance; d++)
		{
			var vec = start.add(fromDir.multiply(d));
			minecraftClient.world.addParticle(ParticleTypes.SMOKE, vec.x, vec.y, vec.z, fromDir.x, fromDir.y, fromDir.z);
		}
	}

	public static void fireSlug(World world, PlayerEntity player, Vec3d fromDir, float range, float damage)
	{
		var start = player.getEyePos();

		var hit = EntityUtil.raycastEntities(start, fromDir, range, player, new Entity[] { player });
		var blockHit = EntityUtil.raycastBlocks(start, fromDir, range, player, RaycastContext.FluidHandling.ANY);

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

		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeDouble(start.x);
		passedData.writeDouble(start.y);
		passedData.writeDouble(start.z);
		passedData.writeDouble(fromDir.x);
		passedData.writeDouble(fromDir.y);
		passedData.writeDouble(fromDir.z);
		passedData.writeDouble(distance);

		for (var trackingPlayer : PlayerLookup.tracking((ServerWorld)world, end))
			ServerPlayNetworking.send(trackingPlayer, SwgPackets.S2C.PacketWorldEventSlugFired, passedData);
	}

	public static void fireStun(World world, PlayerEntity player, Vec3d fromDir, float range, float damage, Consumer<BlasterBoltEntity> entityInitializer)
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
}
