package dev.pswg.interaction;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Permission-only guard for PSWG's custom left-use and primary-item action paths.
 * These paths do not invoke vanilla right-use callbacks, so code-owned hand reservations are checked explicitly.
 */
@FunctionalInterface
public interface ItemHandPermission
{
	/** Returns whether a custom item action may use this hand in the current live context. */
	boolean mayInteract(Player player, InteractionHand hand);

	/** All declared guards must allow the interaction; this callback never performs item actions itself. */
	Event<ItemHandPermission> EVENT = EventFactory.createArrayBacked(ItemHandPermission.class, listeners -> (player, hand) -> {
		for (var listener : listeners)
			if (!listener.mayInteract(player, hand))
				return false;
		return true;
	});
}
