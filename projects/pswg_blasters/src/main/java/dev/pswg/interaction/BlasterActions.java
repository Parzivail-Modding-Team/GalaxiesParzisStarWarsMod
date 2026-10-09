package dev.pswg.interaction;

import dev.pswg.Blasters;
import dev.pswg.data.BlasterData;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.data.BlasterStats;
import dev.pswg.data.BlasterStanceProfile;
import dev.pswg.item.BlasterAmmo;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.BlasterLoadout;
import dev.pswg.networking.BlasterInputPayload;
import dev.pswg.sound.BlasterSounds;
import dev.pswg.world.GameTime;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;

import java.util.EnumMap;
import java.util.Optional;

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
		 * Installed attachments.
		 */
		private final BlasterItem.AttachmentsComponent _attachments;

		/**
		 * Captured committed definition generation.
		 */
		private final String _generation;

		/**
		 * Tick when the action started.
		 */
		private final long _startedAt;

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
		 * Monotonic press sequence.
		 */
		private long _inputSequence;

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
			_attachments = BlasterItem.getAttachments(_stack);

			_generation = BlasterData.get(_level).data().generation();
			_startedAt = time(player);
			_until = until;
		}

		/**
		 * Validates the live physical source and current server generation before any mutation.
		 */
		private boolean matches(ServerPlayer player)
		{
			return matchesSource(player)
			       && BlasterItem.getLoadout(_level, _stack).map(loadout -> loadout.selectedMode().id().equals(_mode)).orElse(false)
			       && BlasterItem.getAttachments(_stack).equals(_attachments);
		}

		/**
		 * Check to see if the session is for this player.
		 */
		private boolean matchesSource(ServerPlayer player)
		{
			if (!player.isAlive() || player.isSpectator() || player.level() != _level
			    || player.getItemInHand(_hand) != _stack
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
		 * Independent actions/readiness for each physical hand.
		 */
		private final EnumMap<InteractionHand, HandState> _hands = new EnumMap<>(InteractionHand.class);

		/**
		 * Last busy predicate; entering a new busy activity cancels outstanding actions.
		 */
		private boolean _busy;

		/**
		 * Last state tick.
		 */
		private long _lastTick = Long.MIN_VALUE;

		private InputState()
		{
			for (var hand : InteractionHand.values())
				_hands.put(hand, new HandState());
		}
	}

	/**
	 * Actions for one source hand.
	 */
	private static class HandState
	{

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
		 * Surface anchor, never a second weapon slot.
		 */
		private BlasterDeployment.Support _support;

		/**
		 * Explicit server carry preference, reset when a new source is drawn.
		 */
		private boolean _patrol;

		/**
		 * Creates an idle connection-local state.
		 */
		private HandState()
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
		return GameTime.now(player.level());
	}

	/**
	 * Checks the selected hand's draw, reload, and patrol state.
	 */
	public static boolean canFire(Player player, InteractionHand hand)
	{
		if (!BlasterWield.canWield(player, hand))
			return false;

		if (player instanceof ServerPlayer serverPlayer)
		{
			var root = serverPlayer.getAttachedOrCreate(INPUT);
			refreshEquipped(serverPlayer, root);
			var state = root._hands.get(hand);
			return state._equipped != null && time(serverPlayer) >= state._equipped._until
			       && state._reload == null && !state._patrol;
		}

		var projected = player.getAttachedOrElse(BlasterWieldState.ATTACHMENT, BlasterWieldState.EMPTY).weapon(hand);
		if (projected.isEmpty())
		{
			return false;
		}

		var weapon = projected.orElseThrow();

		return weapon.serial() == player.getItemInHand(hand).getOrDefault(BlasterItem.SERIAL, 0L)
		       && GameTime.now(player.level()) >= weapon.readyAt()
		       && weapon.stance() != BlasterStanceProfile.WeaponState.RELOADING
		       && !weapon.patrol();
	}

	/**
	 * ADS obeys the same draw/reload/source gate and movement policy.
	 */
	public static boolean canAim(Player player, InteractionHand hand)
	{
		return BlasterWield.aimEligible(player, hand) && canFire(player, hand);
	}

	/**
	 * Selects one camera ADS source.
	 */
	public static boolean setAim(Player player, InteractionHand hand, boolean aiming)
	{
		if (aiming && !canAim(player, hand))
			return false;

		var stack = player.getItemInHand(hand);

		if (BlasterItem.getLoadout(player.level(), stack).isEmpty() || player.level().isClientSide())
			return false;

		if (aiming)
		{
			var other = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
			if (BlasterItem.getState(other).isAiming())
				BlasterItem.setAiming(player.level(), other, false);
		}

		BlasterItem.setAiming(player.level(), stack, aiming);

		return true;
	}

	/**
	 * Cancels the trigger and its charge feedback without clearing an unrelated draw/reload timer.
	 */
	private static void clearTrigger(ServerPlayer player, InteractionHand hand, HandState state)
	{
		state._trigger = null;

		var timer = ItemInteractionTimer.get(player, hand);
		if (timer != null && timer.kind() == ItemInteractionTimer.ItemInteractionKind.CHARGE)
			ItemInteractionTimer.clear(player, hand);
	}

	/**
	 * Emits at most once from a charge session using elapsed ticks.
	 */
	private static void finishCharge(ServerPlayer player, Session session, BlasterStats.ChargeTrigger charge)
	{
		if (session._remaining == 0)
			return;

		session._remaining = 0;
		ItemInteractionTimer.clear(player, session._hand);

		var elapsed = (int)Math.clamp(time(player) - session._startedAt, 0, charge.maximumChargeTicks());
		if (elapsed >= charge.minimumChargeTicks())
			BlasterItem.tryFire(player.level(), player, session._hand, false, elapsed);
	}

	/**
	 * Refreshes draw timing once per source.
	 */
	private static void refreshEquipped(ServerPlayer player, InputState root)
	{
		var busy = BlasterWield.busy(player);

		if (busy != root._busy)
		{
			if (BlasterWield.elected(player).isPresent())
				player.sendOverlayMessage(Component.translatable(busy ? "text.pswg_blasters.holstered" : "text.pswg_blasters.redrawing"));

			for (var hand : InteractionHand.values())
			{
				var state = root._hands.get(hand);
				clearTrigger(player, hand, state);
				state._reload = null;
				ItemInteractionTimer.clear(player, hand);
			}

			root._busy = busy;
		}

		for (var hand : InteractionHand.values())
			refreshHand(player, hand, root._hands.get(hand));

		if (BlasterItem.getState(player.getMainHandItem()).isAiming() && BlasterItem.getState(player.getOffhandItem()).isAiming())
			BlasterItem.setAiming(player.level(), player.getOffhandItem(), false);
	}

	/**
	 * Keeps readiness attached to one source and restarts draw only when it becomes available again.
	 */
	private static void refreshHand(ServerPlayer player, InteractionHand hand, HandState state)
	{
		if (state._equipped != null && !state._equipped.matchesSource(player))
		{
			var previous = state._equipped._stack;
			if (BlasterItem.getState(previous).isAiming())
			{
				BlasterItem.setAiming(player.level(), previous, false);
			}

			var current = player.getItemInHand(hand);
			if (BlasterItem.getState(current).isAiming())
			{
				BlasterItem.setAiming(player.level(), current, false);
			}
		}

		if (state._support != null && !BlasterDeployment.valid(player, state._support))
		{
			BlasterItem.setDeployed(player.level(), state._support.stack(), false);
			state._support = null;
		}

		var stack = player.getItemInHand(hand);
		if (BlasterItem.isDeployed(stack) && state._support == null)
			BlasterItem.setDeployed(player.level(), stack, false);

		if (BlasterItem.getState(stack).isAiming()
		    && (!BlasterWield.aimEligible(player, hand) || state._reload != null || state._equipped == null || time(player) < state._equipped._until))
			BlasterItem.setAiming(player.level(), stack, false);

		if (state._equipped != null && state._equipped.matchesSource(player) && BlasterWield.canWield(player, hand))
			return;

		state._trigger = null;
		state._reload = null;
		state._equipped = null;

		ItemInteractionTimer.clear(player, hand);
		if (!BlasterWield.canWield(player, hand))
			return;

		var loadout = BlasterItem.getLoadout(player.level(), stack);

		if (loadout.isEmpty())
			return;

		if (!stack.has(BlasterItem.SERIAL))
			stack.set(BlasterItem.SERIAL, player.level().getRandom().nextLong());

		var duration = loadout.orElseThrow().definition().stats().configuration().drawTicks();
		state._equipped = new Session(player, hand, time(player) + duration);
		state._patrol = false;

		ItemInteractionTimer.begin(player, hand, ItemInteractionTimer.ItemInteractionKind.DRAW, stack.get(BlasterItem.SERIAL), time(player), duration);
	}

	/**
	 * Handles ordered intents.
	 */
	private static void handle(ServerPlayer player, BlasterInputPayload packet)
	{
		var root = player.getAttachedOrCreate(INPUT);
		if (packet.sequence() <= 0 || packet.sequence() <= root._sequence)
			return;

		root._sequence = packet.sequence();
		refreshEquipped(player, root);
		var state = root._hands.get(packet.hand());

		if (packet.action() == BlasterInputPayload.BlasterInputAction.CANCEL)
		{
			if (state._trigger != null && state._trigger._inputSequence == packet.session())
				clearTrigger(player, packet.hand(), state);
			return;
		}

		if (packet.action() == BlasterInputPayload.BlasterInputAction.RELEASE)
		{
			if (state._trigger != null && state._trigger._inputSequence == packet.session())
			{
				var session = state._trigger;

				if (!session.matches(player) || !BlasterWield.canWield(player, packet.hand()) || time(player) >= session._until)
				{
					clearTrigger(player, packet.hand(), state);
					return;
				}

				var trigger = BlasterItem.getLoadout(player.level(), session._stack).orElseThrow().selectedMode().trigger();
				if (trigger instanceof BlasterStats.ChargeTrigger charge
				    && (charge.releasePolicy() == BlasterStats.ReleasePolicy.FIRE_ON_RELEASE || time(player) - session._startedAt >= charge.maximumChargeTicks()))
					finishCharge(player, session, charge);

				if (trigger instanceof BlasterStats.BurstTrigger && session._remaining > 0)
					session._released = true;
				else
					clearTrigger(player, packet.hand(), state);
			}
			return;
		}

		if (packet.action() == BlasterInputPayload.BlasterInputAction.HEARTBEAT)
		{
			if (state._trigger != null
			    && state._trigger._inputSequence == packet.session()
			    && state._trigger.matches(player)
			    && time(player) < state._trigger._until)
				state._trigger._until = time(player) + HEARTBEAT_TIMEOUT;
			return;
		}

		var stack = player.getItemInHand(packet.hand());
		var loadout = BlasterItem.getLoadout(player.level(), stack);

		if (packet.action() == BlasterInputPayload.BlasterInputAction.AIM_STOP)
		{
			setAim(player, packet.hand(), false);
			return;
		}

		if (loadout.isEmpty())
		{
			return;
		}

		if (!BlasterWield.canWield(player, packet.hand()))
		{
			if (BlasterWield.bothHandsArmed(player) && !BlasterWield.dualWielding(player))
			{
				player.sendOverlayMessage(Component.translatable("text.pswg_blasters.inactive_hand"));
			}

			return;
		}

		if (packet.action() == BlasterInputPayload.BlasterInputAction.AIM_START
		    || packet.action() == BlasterInputPayload.BlasterInputAction.AIM)
		{
			if (state._patrol)
			{
				raiseWeapon(player, packet.hand(), state);
			}
			else
			{
				var aiming = packet.action() == BlasterInputPayload.BlasterInputAction.AIM_START
				             || !BlasterItem.getState(stack).isAiming();
				setAim(player, packet.hand(), aiming);
			}

			return;
		}

		player.resetLastActionTime();
		if (packet.action() == BlasterInputPayload.BlasterInputAction.PRESS)
		{
			pressTrigger(player, packet, state, loadout.orElseThrow());
			return;
		}

		clearTrigger(player, packet.hand(), state);

		if (state._reload != null)
			ItemInteractionTimer.clear(player, packet.hand());

		state._reload = null;

		switch (packet.action())
		{
			case PATROL ->
			{
				if (state._patrol)
				{
					raiseWeapon(player, packet.hand(), state);
				}
				else
				{
					state._patrol = true;
					BlasterItem.setAiming(player.level(), stack, false);
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.patrol"));
				}
			}
			case VENT -> ((BlasterItem)stack.getItem()).invokePrimaryAction(stack, player.level(), player);
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
					BlasterItem.setAiming(player.level(), stack, false);
					ItemInteractionTimer.begin(player, packet.hand(), ItemInteractionTimer.ItemInteractionKind.RELOAD, stack.get(BlasterItem.SERIAL), time(player), ticks);
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
				if (BlasterItem.hasContextualAttachmentModifier(loadout.orElseThrow(), true) && BlasterItem.setFolded(player.level(), stack, !BlasterItem.isFolded(stack)))
					player.sendOverlayMessage(Component.translatable(BlasterItem.isFolded(stack) ? "text.pswg_blasters.stock_folded" : "text.pswg_blasters.stock_extended"));
				else
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.no_stock"));
			}
			case DEPLOY ->
			{
				if (BlasterItem.isDeployed(stack))
				{
					state._support = null;
					BlasterItem.setDeployed(player.level(), stack, false);
					player.sendOverlayMessage(Component.translatable("text.pswg_blasters.bipod_stowed"));
				}
				else
				{
					var support = BlasterDeployment.find(player, stack);
					if (support.isPresent())
					{
						state._support = support.orElseThrow();
						BlasterItem.setDeployed(player.level(), stack, true);
					}
					player.sendOverlayMessage(Component.translatable(support.isPresent() ? "text.pswg_blasters.bipod_deployed" : "text.pswg_blasters.needs_ground"));
				}
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
			clearTrigger(player, session._hand, player.getAttachedOrCreate(INPUT)._hands.get(session._hand));
			return;
		}

		if (BlasterItem.getState(session._stack).coolingMode().isCooling())
			clearTrigger(player, session._hand, player.getAttachedOrCreate(INPUT)._hands.get(session._hand));

		if (trigger instanceof BlasterStats.BurstTrigger && !fired
		    && (pressed || BlasterItem.getState(session._stack).fireCooldown() <= time(player)))
			session._remaining = 0;

		if (trigger instanceof BlasterStats.BurstTrigger burst && fired)
		{
			session._remaining--;
			var minimum = (int)Math.clamp(BlasterItem.getState(session._stack).fireCooldown() - time(player), 1, Integer.MAX_VALUE);
			var interval = Math.max(minimum, burst.intervalTicks());
			session._next = time(player) + interval;
			BlasterItem.applyState(session._stack, value -> value.withFireCooldown(time(player) + interval));
			if (session._remaining == 0)
				BlasterItem.applyState(session._stack, value -> value.withFireCooldown(time(player)
				                                                                       + Math.max(minimum, burst.effectiveInterburstCooldownTicks(minimum))));
		}
	}

	/**
	 * Advances held fire and delayed reloads.
	 */
	private static void tick(ServerPlayer player)
	{
		var root = player.getAttachedOrCreate(INPUT);
		refreshEquipped(player, root);

		var now = time(player);
		if (now != root._lastTick)
		{
			root._lastTick = now;
			for (var hand : InteractionHand.values())
				tickHand(player, hand, root._hands.get(hand));
		}

		var snapshot = new BlasterWieldState(
				root._busy,
				project(player, InteractionHand.MAIN_HAND, root._hands.get(InteractionHand.MAIN_HAND)),
				project(player, InteractionHand.OFF_HAND, root._hands.get(InteractionHand.OFF_HAND))
		);

		if (!snapshot.equals(player.getAttached(BlasterWieldState.ATTACHMENT)))
			player.setAttached(BlasterWieldState.ATTACHMENT, snapshot);
	}

	/**
	 * Consumes a patrol press or starts one trigger session.
	 */
	private static void pressTrigger(ServerPlayer player, BlasterInputPayload packet, HandState state, BlasterLoadout loadout)
	{
		if (packet.session() != 0)
		{
			return;
		}

		if (state._patrol)
		{
			raiseWeapon(player, packet.hand(), state);
			return;
		}

		if (state._trigger != null || state._reload != null)
		{
			return;
		}

		if (state._equipped == null || time(player) < state._equipped._until)
		{
			return;
		}

		var session = new Session(player, packet.hand(), time(player) + HEARTBEAT_TIMEOUT);
		session._inputSequence = packet.sequence();
		state._trigger = session;

		var trigger = loadout.selectedMode().trigger();
		session._remaining = trigger instanceof BlasterStats.BurstTrigger burst ? burst.rounds() : 1;

		if (trigger instanceof BlasterStats.ChargeTrigger charge)
		{
			if (BlasterItem.tryBeginCharge(player.level(), player, packet.hand()) != BlasterItem.ShotResult.READY)
			{
				clearTrigger(player, packet.hand(), state);
				return;
			}

			ItemInteractionTimer.begin(
					player,
					packet.hand(),
					ItemInteractionTimer.ItemInteractionKind.CHARGE,
					session._stack.getOrDefault(BlasterItem.SERIAL, 0L),
					time(player),
					charge.maximumChargeTicks()
			);

			return;
		}

		attempt(player, session, true);
	}

	/**
	 * Leaves patrol and starts a draw. The request does not fire a shot.
	 */
	private static void raiseWeapon(ServerPlayer player, InteractionHand hand, HandState state)
	{
		clearTrigger(player, hand, state);
		state._patrol = false;
		var stack = player.getItemInHand(hand);
		var duration = BlasterItem.getLoadout(player.level(), stack).orElseThrow().definition().stats().configuration().drawTicks();
		state._equipped = new Session(player, hand, time(player) + duration);

		ItemInteractionTimer.begin(
				player,
				hand,
				ItemInteractionTimer.ItemInteractionKind.DRAW,
				stack.getOrDefault(BlasterItem.SERIAL, 0L),
				time(player),
				duration
		);

		player.sendOverlayMessage(Component.translatable("text.pswg_blasters.redrawing"));
	}

	/**
	 * Copies public state for the current held source.
	 */
	private static Optional<BlasterWieldState.BlasterWieldWeapon> project(ServerPlayer player, InteractionHand hand, HandState state)
	{
		var stack = player.getItemInHand(hand);
		if (!player.isAlive() || player.isSpectator() || BlasterItem.getLoadout(player.level(), stack).isEmpty())
			return Optional.empty();

		var holstered = BlasterWield.holstered(player, hand);
		var slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
		var readyAt = state._equipped == null ? Long.MAX_VALUE : state._equipped._until;

		return Optional.of(new BlasterWieldState.BlasterWieldWeapon(
				stack.getOrDefault(BlasterItem.SERIAL, 0L),
				slot,
				stance(player, stack, state, holstered),
				holstered,
				state._patrol,
				readyAt
		));
	}

	/**
	 * Selects a public stance in priority order.
	 */
	private static BlasterStanceProfile.WeaponState stance(ServerPlayer player, ItemStack stack, HandState state, boolean holstered)
	{
		if (holstered)
		{
			return BlasterStanceProfile.WeaponState.HOLSTERED;
		}

		if (state._patrol)
		{
			return BlasterStanceProfile.WeaponState.PATROL;
		}

		if (state._reload != null)
		{
			return BlasterStanceProfile.WeaponState.RELOADING;
		}

		var item = BlasterItem.getState(stack);
		if (item.coolingMode().isCooling())
		{
			return BlasterStanceProfile.WeaponState.VENTING;
		}

		if (item.isAiming())
		{
			return BlasterStanceProfile.WeaponState.ADS;
		}

		if (time(player) - item.lastFired() < 2)
		{
			return BlasterStanceProfile.WeaponState.FIRING;
		}

		if (BlasterItem.isDeployed(stack))
		{
			return BlasterStanceProfile.WeaponState.DEPLOYED;
		}

		if (BlasterItem.isFolded(stack))
		{
			return BlasterStanceProfile.WeaponState.FOLDED;
		}

		if (player.isSprinting() || player.isFallFlying() || player.getAbilities().flying)
		{
			return BlasterStanceProfile.WeaponState.PATROL;
		}

		if (!player.onGround() && !player.isPassenger() && !player.onClimbable())
		{
			return BlasterStanceProfile.WeaponState.PATROL;
		}

		return BlasterStanceProfile.WeaponState.HIP;
	}

	/**
	 * Advances only this hand's actions and owner feedback.
	 */
	private static void tickHand(ServerPlayer player, InteractionHand hand, HandState state)
	{

		var now = time(player);
		var timer = ItemInteractionTimer.get(player, hand);
		if (timer != null && !timer.isActive(now))
			ItemInteractionTimer.clear(player, hand);

		if (state._reload != null)
		{
			var session = state._reload;
			if (!session.matches(player))
			{
				state._reload = null;
				ItemInteractionTimer.clear(player, hand);
				player.sendOverlayMessage(Component.translatable("text.pswg_blasters.reload_cancelled"));
			}

			else if (now >= session._until)
			{
				var ammo = session._definition.stats().ammo();
				var amount = BlasterAmmo.reload(player.level(), player, session._stack, ammo);
				var units = player.isCreative() ? 0L : (long)amount * BlasterAmmo.unitsPerLoadedQuantity(ammo);
				player.sendOverlayMessage(Component.translatable(amount > 0 ? "text.pswg_blasters.reload_complete" : "text.pswg_blasters.cannot_reload", amount, units));
				state._reload = null;

				ItemInteractionTimer.clear(player, hand);
			}
		}

		if (state._trigger != null)
		{
			var session = state._trigger;
			if (!session.matches(player) || (!session._released && now >= session._until))
			{
				clearTrigger(player, hand, state);
				return;
			}

			var trigger = BlasterItem.getLoadout(player.level(), session._stack).orElseThrow().selectedMode().trigger();
			if (trigger instanceof BlasterStats.ChargeTrigger charge)
			{
				if (BlasterItem.getCoolingStatus(player.level(), session._stack, 0).coolingMode().isCooling())
					clearTrigger(player, hand, state);
				else if (charge.releasePolicy() == BlasterStats.ReleasePolicy.FIRE_AT_FULL && now - session._startedAt >= charge.maximumChargeTicks())
					finishCharge(player, session, charge);
			}

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
