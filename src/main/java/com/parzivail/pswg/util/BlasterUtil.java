package com.parzivail.pswg.util;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.EntityHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class BlasterUtil
{
	public static DamageSource getDamageSource(Entity attacker)
	{
		return new EntityDamageSource("pswg.blaster", attacker).setProjectile();
	}

	public static void fireBolt(World world, PlayerEntity player, Vec3d fromDir, float range, float damage, Consumer<BlasterBoltEntity> entityInitializer)
	{
		final BlasterBoltEntity bolt = new BlasterBoltEntity(SwgEntities.Misc.BlasterBolt, player, world);
		entityInitializer.accept(bolt);
		bolt.setRange(range);

		world.spawnEntity(bolt);

		Vec3d start = new Vec3d(bolt.getX(), bolt.getY() + bolt.getHeight() / 2f, bolt.getZ());

		EntityHitResult hit = EntityUtil.raycastEntities(start, fromDir, range, player, new Entity[] { player });
		BlockHitResult blockHit = EntityUtil.raycastBlocks(start, fromDir, range, player);

		double entityDistance = hit == null ? Double.MAX_VALUE : hit.hit.squaredDistanceTo(player.getPos());
		double blockDistance = blockHit == null ? Double.MAX_VALUE : blockHit.squaredDistanceTo(player);

		if (hit != null && entityDistance < blockDistance)
		{
			hit.entity.damage(getDamageSource(player), damage);
		}
		else if (blockHit != null)
		{
			// TODO: smoke puff
		}
	}
}
