package dev.pswg.interaction;

import dev.pswg.Blasters;
import dev.pswg.data.BlasterStats;
import dev.pswg.item.BlasterItem;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Selects the weapons that a player can operate.
 */
public final class BlasterWield
{
	/**
	 * Registers the native and custom interaction guards.
	 */
	public static void register()
	{
		BlasterWieldState.register();
		ItemHandPermission.EVENT.register((player, hand) -> !blocksInteraction(player, hand));
		UseItemCallback.EVENT.register((player, level, hand) -> useResult(player, hand));
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> useResult(player, hand));
		UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> useResult(player, hand));
		AttackBlockCallback.EVENT.register((player, level, hand, pos, face) -> interactionResult(player, hand));
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> interactionResult(player, hand));
	}

	/**
	 * Reserves right-use for weapon controls when the player holds two one-handed blasters.
	 */
	private static InteractionResult useResult(Player player, InteractionHand hand)
	{
		if (!player.isSpectator() && dualWielding(player))
		{
			return InteractionResult.FAIL;
		}

		return interactionResult(player, hand);
	}

	/**
	 * Rejects an interaction from a reserved hand.
	 */
	private static InteractionResult interactionResult(Player player, InteractionHand hand)
	{
		if (blocksInteraction(player, hand))
		{
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	/**
	 * Uses the main weapon first. A sole offhand weapon uses the primary control.
	 */
	public static InteractionHand primaryHand(Player player)
	{
		if (hasWeapon(player, InteractionHand.MAIN_HAND))
		{
			return InteractionHand.MAIN_HAND;
		}

		return InteractionHand.OFF_HAND;
	}

	/**
	 * Checks whether both hands contain valid weapons.
	 */
	public static boolean bothHandsArmed(Player player)
	{
		return hasWeapon(player, InteractionHand.MAIN_HAND) && hasWeapon(player, InteractionHand.OFF_HAND);
	}

	/**
	 * Permits dual operation only for two one-handed weapons.
	 */
	public static boolean dualWielding(Player player)
	{
		return bothHandsArmed(player)
		       && !twoHanded(player, InteractionHand.MAIN_HAND)
		       && !twoHanded(player, InteractionHand.OFF_HAND);
	}

	/**
	 * Finds a weapon definition for the held stack.
	 */
	private static boolean hasWeapon(Player player, InteractionHand hand)
	{
		return BlasterItem.getLoadout(player.level(), player.getItemInHand(hand)).isPresent();
	}

	/**
	 * Checks whether the player's current activity reserves the hands.
	 */
	public static boolean busy(Player player)
	{
		if (player.onClimbable() || player.containerMenu != player.inventoryMenu)
		{
			return true;
		}

		var vehicle = player.getVehicle();
		if (vehicle != null && vehicle.getControllingPassenger() == player)
		{
			return true;
		}

		if (HandsOccupied.test(player))
		{
			return true;
		}

		if (player.level().isClientSide())
		{
			return player.getAttachedOrElse(BlasterWieldState.ATTACHMENT, BlasterWieldState.EMPTY).busy();
		}

		return false;
	}

	/**
	 * Reads the handling policy of the current weapon form.
	 */
	public static boolean twoHanded(Player player, InteractionHand hand)
	{
		var loadout = BlasterItem.getLoadout(player.level(), player.getItemInHand(hand));
		if (loadout.isEmpty())
		{
			return false;
		}

		return loadout.orElseThrow().definition().stats().configuration().effectiveHandling() == BlasterStats.BlasterHandling.TWO_HANDED;
	}

	/**
	 * Selects at most one active two-handed source.
	 */
	public static Optional<InteractionHand> elected(Player player)
	{
		var hand = primaryHand(player);
		if (twoHanded(player, hand))
		{
			return Optional.of(hand);
		}

		return Optional.empty();
	}

	/**
	 * Marks the selected two-handed weapon as holstered during a busy activity.
	 */
	public static boolean holstered(Player player, InteractionHand hand)
	{
		return busy(player) && elected(player).filter(hand::equals).isPresent();
	}

	/**
	 * Checks weapon selection and busy-state restrictions.
	 */
	public static boolean canWield(Player player, InteractionHand hand)
	{
		if (!player.isAlive() || player.isSpectator())
		{
			return false;
		}

		var loadout = BlasterItem.getLoadout(player.level(), player.getItemInHand(hand));
		if (loadout.isEmpty())
		{
			return false;
		}

		if (bothHandsArmed(player) && !dualWielding(player) && hand != primaryHand(player))
		{
			return false;
		}

		if (busy(player))
		{
			var archetype = loadout.orElseThrow().definition().stats().configuration().archetype();
			return !twoHanded(player, hand) && archetype.equals(Blasters.id("pistol"));
		}

		return true;
	}

	/**
	 * Blocks the alternate hand while the selected two-handed weapon is wielded.
	 */
	public static boolean blocksInteraction(Player player, InteractionHand hand)
	{
		if (player.isSpectator() || busy(player))
		{
			return false;
		}

		return elected(player).filter(source -> source != hand).isPresent();
	}

	/**
	 * Checks the movement restrictions for ADS.
	 */
	public static boolean aimEligible(Player player, InteractionHand hand)
	{
		if (!canWield(player, hand) || player.isSprinting() || player.isFallFlying() || player.getAbilities().flying)
		{
			return false;
		}

		if (!player.onGround() && !player.isPassenger() && !player.onClimbable())
		{
			return false;
		}

		var configuration = BlasterItem.getLoadout(player.level(), player.getItemInHand(hand)).orElseThrow().definition().stats().configuration();
		if (configuration.archetype().equals(Blasters.id("heavy")))
		{
			return player.getDeltaMovement().horizontalDistanceSqr() < 0.0001;
		}

		return true;
	}

	/**
	 * Utility class.
	 */
	private BlasterWield()
	{
	}
}
