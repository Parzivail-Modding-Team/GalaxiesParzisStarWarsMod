package dev.pswg.interaction;

import dev.pswg.item.BlasterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Surface-backed deployment for installed stability attachments; support never relocates the weapon.
 */
public final class BlasterDeployment
{
	/**
	 * Captured physical weapon, dimension, top face, and placement pose. Unsaved server state.
	 */
	public record Support(
			ItemStack stack,
			ServerLevel level,
			BlockPos block,
			BlockState surface,
			Vec3 hit,
			Vec3 playerPosition,
			float yaw
	)
	{
	}

	/**
	 * Deploys only an installed deployed-context attachment on a reachable, sturdy upward face.
	 */
	public static Optional<Support> find(ServerPlayer player, ItemStack stack)
	{
		var loadout = BlasterItem.getLoadout(player.level(), stack);

		if (loadout.isEmpty()
		    || !BlasterItem.hasContextualAttachmentModifier(loadout.orElseThrow(), false)
		    || !player.onGround()
		    || player.isSprinting()
		    || BlasterWield.busy(player)
		)
			return Optional.empty();

		var origin = player.getEyePosition();
		var hit = player.level().clipIncludingBorder(new ClipContext(origin, origin.add(player.getViewVector(1).scale(3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

		if (hit.getType() != HitResult.Type.BLOCK
		    || hit.getDirection() != Direction.UP
		    || !player.level().getBlockState(hit.getBlockPos()).isFaceSturdy(player.level(), hit.getBlockPos(), Direction.UP)
		)
			return Optional.empty();

		return Optional.of(new Support(stack, player.level(), hit.getBlockPos().immutable(), player.level().getBlockState(hit.getBlockPos()), hit.getLocation(), player.position(), player.getYRot()));
	}

	/**
	 * Rechecks support, source, movement and occlusion every tick, even when not firing.
	 */
	public static boolean valid(ServerPlayer player, Support support)
	{
		if (!player.isAlive()
		    || player.isSpectator()
		    || player.level() != support.level()
		    || !player.onGround()
		    || player.isSprinting()
		    || BlasterWield.busy(player)
		    || player.getMainHandItem() != support.stack() && player.getOffhandItem() != support.stack()
		    || !BlasterItem.isDeployed(support.stack()) || player.position().distanceToSqr(support.playerPosition()) > 0.25
		    || Math.abs(Mth.wrapDegrees(player.getYRot() - support.yaw())) > 60
		    || player.level().getBlockState(support.block()) != support.surface()
		    || !player.level().getBlockState(support.block()).isFaceSturdy(player.level(), support.block(), Direction.UP)
		)
			return false;

		var loadout = BlasterItem.getLoadout(player.level(), support.stack());
		if (loadout.isEmpty() || !BlasterItem.hasContextualAttachmentModifier(loadout.orElseThrow(), false))
			return false;

		var origin = player.getEyePosition();
		if (origin.distanceToSqr(support.hit()) > 9)
			return false;

		var hit = player.level().clipIncludingBorder(new ClipContext(origin, support.hit().add(0, -0.02, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		return hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(support.block()) && hit.getDirection() == Direction.UP;
	}

	/**
	 * Utility class.
	 */
	private BlasterDeployment()
	{
	}
}
