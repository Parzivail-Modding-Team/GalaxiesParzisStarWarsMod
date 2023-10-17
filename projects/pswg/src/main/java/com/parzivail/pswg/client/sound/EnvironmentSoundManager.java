package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

public class EnvironmentSoundManager
{
	public static void tick(MinecraftClient mc)
	{
		if (mc.player == null || mc.world == null)
			return;

		var pos = mc.player.getEyePos();

		var box = Box.of(mc.player.getPos(), 16, 16, 16);
		for (var entity : mc.world.getEntitiesByClass(BlasterBoltEntity.class, box, e -> true))
		{
			var entityDirection = entity.getVelocity();

			var newEntityPos = entity.getLerpedPos(1);

			var oldPlayerDelta = entity.getLerpedPos(0).subtract(pos);
			var newPlayerDelta = newEntityPos.subtract(pos);

			var oldPlayerDot = entityDirection.dotProduct(oldPlayerDelta);
			var newPlayerDot = entityDirection.dotProduct(newPlayerDelta);

			if (oldPlayerDot >= 0 && newPlayerDot < 0)
			{
				// Entity transitioned from moving towards the
				// player (i.e. pos-player vector and direction
				// vector face the same direction) to away from
				// the player (i.e. vectors point opposite
				// directions)

				mc.world.playSound(newEntityPos.x, newEntityPos.y, newEntityPos.z, SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.PLAYERS, 1, 1, true);
			}
		}
	}
}
