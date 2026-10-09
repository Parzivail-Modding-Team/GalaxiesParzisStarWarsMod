package dev.pswg.interaction;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Code-addon capability for an active use or activity that genuinely reserves the hands.
 * Items may implement this interface; addons may register independent live activity predicates.
 */
@FunctionalInterface
public interface HandsOccupied
{
	/** Returns whether this entity's current activity commits its hands. */
	boolean occupiesHands(LivingEntity entity, ItemStack activeUse);

	/** Code-owned activity predicates; data packs cannot supply executable predicates. */
	Event<HandsOccupied> EVENT = EventFactory.createArrayBacked(HandsOccupied.class, listeners -> (entity, stack) -> {
		for (var listener : listeners)
			if (listener.occupiesHands(entity, stack))
				return true;
		return false;
	});

	/** Ordinary right clicks are not busy; only a declared capability or activity reserves hands. */
	static boolean test(LivingEntity entity)
	{
		var active = entity.isUsingItem() ? entity.getUseItem() : ItemStack.EMPTY;
		var reservedUse = !active.isEmpty() && (active.getItem() instanceof HandsOccupied item ? item.occupiesHands(entity, active)
				: active.getUseAnimation() != net.minecraft.world.item.ItemUseAnimation.NONE);
		if (reservedUse)
			return true;
		if (entity instanceof ILeftClickingEntity left && left.pswg$isLeftUsingItem())
		{
			var leftActive = left.pswg$getLeftActiveItemStack();
			if (!leftActive.isEmpty() && leftActive.getItem() instanceof HandsOccupied item && item.occupiesHands(entity, leftActive))
				return true;
		}
		return EVENT.invoker().occupiesHands(entity, active);
	}
}
