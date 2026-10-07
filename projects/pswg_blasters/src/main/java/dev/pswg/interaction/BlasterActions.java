package dev.pswg.interaction;

import dev.pswg.Blasters;
import dev.pswg.data.BlasterData;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.data.BlasterStats;
import dev.pswg.item.BlasterAmmo;
import dev.pswg.item.BlasterItem;
import dev.pswg.networking.BlasterInputPayload;
import dev.pswg.sound.BlasterSounds;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Logical-server controls and short-lived trigger/reload session.
 */
public final class BlasterActions
{
	/**
	 * Captured physical source, mode and definition generation; swapping or reloading data cancels the action.
	 */
	private static class Session
	{
		/**
		 * Actual held weapon object.
		 */
		private final ItemStack _stack;

		/**
		 * Captured hand.
		 */
		private final InteractionHand _hand;

		/**
		 * Captured main-hand hotbar slot.
		 */
		private final int _slot;

		/**
		 * Captured dimension instance.
		 */
		private final ServerLevel _level;

		/**
		 * Captured authored or converted definition.
		 */
		private final BlasterDatapackDefinition _definition;

		/**
		 * Captured mode ID.
		 */
		private final Identifier _mode;

		/**
		 * Captured committed definition generation.
		 */
		private final String _generation;

		/**
		 * Completion time, or the held-input lease endpoint.
		 */
		private long _until;

		/**
		 * Remaining burst shots.
		 */
		private int _remaining;

		/**
		 * Next burst emission time.
		 */
		private long _next;

		/**
		 * A released burst finishes its bounded accepted sequence without requiring held-input heartbeats.
		 */
		private boolean _released;

		/**
		 * Captures one source without duplicating persistent item state.
		 */
		private Session(ServerPlayer player, InteractionHand hand, long until)
		{
			_stack = player.getItemInHand(hand);

			if (!_stack.has(BlasterItem.SERIAL))
				_stack.set(BlasterItem.SERIAL, player.level().getRandom().nextLong());

			_hand = hand;
			_slot = player.getInventory().getSelectedSlot();
			_level = player.level();

			var loadout = BlasterItem.getLoadout(_level, _stack).orElseThrow();
			_definition = loadout.definition();
			_mode = loadout.selectedMode().id();

			_generation = BlasterData.get(_level).data().generation();
			_until = until;
		}

		/**
		 * Validates the live physical source and current server generation before any mutation.
		 */
		private boolean matches(ServerPlayer player)
		{
			return matchesSource(player) && BlasterItem.getLoadout(_level, _stack)
			                                           .map(loadout -> loadout.selectedMode().id().equals(_mode)).orElse(false);
		}

		/**
		 * Check to see if the session is for this player.
		 */
		private boolean matchesSource(ServerPlayer player)
		{
			if (!player.isAlive() || player.isSpectator() || player.level() != _level
			    || player.containerMenu != player.inventoryMenu || player.getItemInHand(_hand) != _stack
			    || (_hand == InteractionHand.MAIN_HAND && player.getInventory().getSelectedSlot() != _slot)
			    || !BlasterData.get(_level).data().generation().equals(_generation))
				return false;

			return BlasterItem.getLoadout(_level, _stack).map(loadout -> loadout.definition() == _definition).orElse(false);
		}
	}

	/**
	 * Unsaved, unsynchronized per-player server state.
	 */
	private static class InputState
	{
		/**
		 * Last accepted transport sequence.
		 */
		private long _sequence = -1;

		/**
		 * Current trigger session, if held.
		 */
		private Session _trigger;

		/**
		 * Current reload session, if waiting.
		 */
		private Session _reload;

		/**
		 * Last physical source.
		 */
		private Session _equipped;

		/**
		 * Creates an idle connection-local state.
		 */
		private InputState()
		{
		}
	}

	/**
	 * Held input expires if the client stops renewing it.
	 */
	public static final int HEARTBEAT_TIMEOUT = 15;

	/**
	 * Ephemeral state has neither a persistent codec nor a synchronization channel.
	 */
	private static final AttachmentType<InputState> INPUT = AttachmentRegistry.create(
			Blasters.id("input_state"),
			builder -> builder.initializer(InputState::new)
	);

	/**
	 * Registers native intent networking and one server-owned scheduler.
	 */
	public static void register()
	{
		PayloadTypeRegistry.serverboundPlay().register(BlasterInputPayload.TYPE, BlasterInputPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(BlasterInputPayload.TYPE, (packet, context) ->
				context.server().execute(() -> handle(context.player(), packet)));
		ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(BlasterActions::tick));
	}

	/**
	 * Gets the current server time.
	 */
	private static long time(ServerPlayer player)
	{
		return player.level().getServer().overworld().getGameTime();
	}

	/**
	 * Refreshes draw timing once per source.
	 */
	private static void refreshEquipped(ServerPlayer player, InputState state)
	{
		if (state._equipped != null && state._equipped.matchesSource(player))
			return;

		state._trigger = null;
		state._reload = null;
		state._equipped = null;

		ItemInteractionTimer.clear(player);
		if (!player.isAlive() || player.isSpectator() || player.containerMenu != player.inventoryMenu)
			return;

		var hand = player.getMainHandItem().is(Blasters.BLASTER_ITEM) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		var stack = player.getItemInHand(hand);
		var loadout = BlasterItem.getLoadout(player.level(), stack);

		if (loadout.isEmpty())
			return;

		if (!stack.has(BlasterItem.SERIAL))
			stack.set(BlasterItem.SERIAL, player.level().getRandom().nextLong());

		var duration = loadout.orElseThrow().definition().stats().configuration().drawTicks();
		state._equipped = new Session(player, hand, time(player) + duration);

		ItemInteractionTimer.begin(player, ItemInteractionTimer.ItemInteractionKind.DRAW, stack.get(BlasterItem.SERIAL), time(player), duration);
	}

	/**
	 * Handles ordered intents.
	 */
	private static void handle(ServerPlayer player, BlasterInputPayload packet)
	{
		var state = player.getAttachedOrCreate(INPUT);
		if (packet.sequence() < 0 || packet.sequence() <= state._sequence)
			return;

		state._sequence = packet.sequence();
		refreshEquipped(player, state);
		if (packet.action() == BlasterInputPayload.BlasterInputAction.RELEASE)
		{
			if (state._trigger != null && state._trigger._hand == packet.hand())
			{
				var session = state._trigger;
				var burst = session.matches(player) && BlasterItem.getLoadout(player.level(), session._stack).orElseThrow()
				                                                  .selectedMode().trigger() instanceof BlasterStats.BurstTrigger;
				if (burst && session._remaining > 0)
					session._released = true;
				else
					state._trigger = null;
			}
			return;
		}

		if (packet.action() == BlasterInputPayload.BlasterInputAction.HEARTBEAT)
		{
			if (state._trigger != null && state._trigger._hand == packet.hand() && state._trigger.matches(player))
				state._trigger._until = time(player) + HEARTBEAT_TIMEOUT;
			return;
		}

		var stack = player.getItemInHand(packet.hand());
		var loadout = BlasterItem.getLoadout(player.level(), stack);
		if (loadout.isEmpty() || !player.isAlive() || player.isSpectator() || player.containerMenu != player.inventoryMenu)
			return;

		player.resetLastActionTime();
		if (packet.action() == BlasterInputPayload.BlasterInputAction.PRESS)
		{
			if (state._trigger != null || state._reload != null || (state._equipped != null && time(player) < state._equipped._until))
				return;

			var session = new Session(player, packet.hand(), time(player) + HEARTBEAT_TIMEOUT);
			state._trigger = session;
			var trigger = loadout.orElseThrow().selectedMode().trigger();
			session._remaining = trigger instanceof BlasterStats.BurstTrigger burst ? burst.rounds() : 1;
			attempt(player, session, true);

			return;
		}

		state._trigger = null;

		if (state._reload != null)
			ItemInteractionTimer.clear(player);

		state._reload = null;

		switch (packet.action())
		{
			case CYCLE_MODE ->
			{
				var modes = loadout.orElseThrow().availableModes();
				var index = modes.indexOf(loadout.orElseThrow().selectedMode());
				var next = modes.get((index + 1) % modes.size());

				if (BlasterItem.selectMode(player.level(), stack, next.id()))
					player.sendOverlayMessage(Component.translatable("tooltip.pswg_blasters.mode", modeName(next.id())));
			}
			case RELOAD ->
			{
				if (state._equipped != null && time(player) < state._equipped._until)
					return;

				var ammo = loadout.orElseThrow().definition().stats().ammo();
				var current = BlasterItem.getLoadedAmmo(stack, ammo);

				var ticks = switch (ammo.feed())
				{
					case BlasterStats.PerShotFeed ignored -> 0;
					case BlasterStats.MagazineFeed magazine -> magazine.reloadTicks();
					case BlasterStats.ChargeStoreFeed charge -> charge.reloadTicks();
				};

				var room = BlasterItem.getAmmoCapacity(ammo) - current;
				var plan = player.isCreative() ? java.util.Optional.<dev.pswg.item.InventoryResourceTransfer.Transfer>empty()
				                               : BlasterAmmo.prepare(player.getInventory(), stack, ammo, room);

				if (ticks > 0 && room > 0 && (player.isCreative() || plan.isPresent()))
				{
					state._reload = new Session(player, packet.hand(), time(player) + ticks);
					ItemInteractionTimer.begin(player, ItemInteractionTimer.ItemInteractionKind.RELOAD, stack.get(BlasterItem.SERIAL), time(player), ticks);
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), BlasterSounds.RELOAD, SoundSource.PLAYERS, 1, 1);

					var quantity = player.isCreative() ? room : plan.orElseThrow().quantity();
					var units = player.isCreative() ? 0L : plan.orElseThrow().debitedUnits();
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.reload_cost", quantity, units));
				}
				else
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.cannot_reload"));
			}
			case FOLD ->
			{
				if (BlasterItem.setFolded(player.level(), stack, !BlasterItem.isFolded(stack)))
					player.sendOverlayMessage(Component.translatable(BlasterItem.isFolded(stack) ? "text.pswg_blasters.stock_folded" : "text.pswg_blasters.stock_extended"));
			}
			case DEPLOY ->
			{
				if (BlasterItem.isDeployed(stack) || player.onGround())
				{
					BlasterItem.setDeployed(player.level(), stack, !BlasterItem.isDeployed(stack));
					player.sendOverlayMessage(Component.translatable(BlasterItem.isDeployed(stack) ? "text.pswg_blasters.bipod_deployed" : "text.pswg_blasters.bipod_stowed"));
				}
				else
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.needs_ground"));
			}
			case CONVERT ->
			{
				if (loadout.orElseThrow().activeConversion().isPresent())
				{
					BlasterItem.clearConversion(player.level(), stack);
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.base_form"));
				}
				else
				{
					var options = BlasterItem.getDefinition(player.level(), stack).orElseThrow().stats().configuration().fieldConversion();
					var activated = false;
					if (options.isPresent())
						for (var option : options.orElseThrow().options())
							if (BlasterItem.activateConversion(player.level(), stack, option.id()))
							{
								player.sendOverlayMessage(Component.translatable("tooltip.pswg_blasters.conversion", option.id().toString()));
								activated = true;
								break;
							}
					if (!activated)
						player.sendOverlayMessage(Component.translatable("text.pswg_blasters.no_conversion"));
				}
			}
			default ->
			{
			}
		}
	}

	/**
	 * Localized mode IDs use a conventional key with the raw ID as an addon fallback.
	 */
	public static Component modeName(Identifier id)
	{
		return Component.translatableWithFallback("mode." + id.toLanguageKey().replace('/', '.'), id.toString());
	}

	/**
	 * Attempts at most one emission per scheduler tick; a semi press never queues another emission.
	 */
	private static void attempt(ServerPlayer player, Session session, boolean pressed)
	{
		var loadout = BlasterItem.getLoadout(player.level(), session._stack).orElseThrow();
		var trigger = loadout.selectedMode().trigger();
		if (trigger instanceof BlasterStats.ChargeTrigger)
			return;

		var result = BlasterItem.tryFire(player.level(), player, session._hand, pressed);
		var fired = result == BlasterItem.ShotResult.FIRED;

		if (result == BlasterItem.ShotResult.COOLING_HANDLED)
		{
			player.getAttachedOrCreate(INPUT)._trigger = null;
			return;
		}

		if (BlasterItem.getState(session._stack).coolingMode().isCooling())
			player.getAttachedOrCreate(INPUT)._trigger = null;

		if (trigger instanceof BlasterStats.BurstTrigger && !fired
		    && (pressed || BlasterItem.getState(session._stack).fireCooldown() <= player.level().getGameTime()))
			session._remaining = 0;

		if (trigger instanceof BlasterStats.BurstTrigger burst && fired)
		{
			session._remaining--;
			session._next = time(player) + burst.intervalTicks();
			BlasterItem.applyState(session._stack, value -> value.withFireCooldown(player.level().getGameTime() + burst.intervalTicks()));
			if (session._remaining == 0)
				BlasterItem.applyState(session._stack, value -> value.withFireCooldown(player.level().getGameTime()
				                                                                       + burst.effectiveInterburstCooldownTicks(loadout.definition().stats().automaticRepeatDelay())));
		}
	}

	/**
	 * Advances held fire and delayed reloads.
	 */
	private static void tick(ServerPlayer player)
	{
		var state = player.getAttachedOrCreate(INPUT);
		refreshEquipped(player, state);

		var now = time(player);
		var timer = player.getAttached(ItemInteractionTimer.ATTACHMENT);
		if (timer != null && !timer.isActive(now))
			ItemInteractionTimer.clear(player);

		if (state._reload != null)
		{
			var session = state._reload;
			if (!session.matches(player))
			{
				state._reload = null;
				ItemInteractionTimer.clear(player);
				player.sendOverlayMessage(Component.translatable("text.pswg_blasters.reload_cancelled"));
			}

			else if (now >= session._until)
			{
				var ammo = session._definition.stats().ammo();
				var amount = BlasterAmmo.reload(player.level(), player, session._stack, ammo);
				var units = player.isCreative() ? 0L : (long)amount * BlasterAmmo.unitsPerLoadedQuantity(ammo);
				player.sendOverlayMessage(Component.translatable(amount > 0 ? "text.pswg_blasters.reload_complete" : "text.pswg_blasters.cannot_reload", amount, units));
				state._reload = null;

				ItemInteractionTimer.clear(player);
			}
		}

		if (state._trigger != null)
		{
			var session = state._trigger;
			if (!session.matches(player) || (!session._released && now >= session._until))
			{
				state._trigger = null;
				return;
			}

			var trigger = BlasterItem.getLoadout(player.level(), session._stack).orElseThrow().selectedMode().trigger();
			if (BlasterItem.isDeployed(session._stack) && !player.onGround())
				BlasterItem.setDeployed(player.level(), session._stack, false);

			if (trigger instanceof BlasterStats.AutoTrigger
			    || trigger instanceof BlasterStats.BurstTrigger && session._remaining > 0 && now >= session._next)
				attempt(player, session, false);

			if (session._released && session._remaining == 0)
				state._trigger = null;
		}
	}

	/**
	 * Utility class.
	 */
	private BlasterActions()
	{
	}
}
