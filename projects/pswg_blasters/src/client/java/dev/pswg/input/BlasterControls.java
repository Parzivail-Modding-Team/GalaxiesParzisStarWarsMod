package dev.pswg.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pswg.Blasters;
import dev.pswg.interaction.BlasterWield;
import dev.pswg.interaction.GalaxiesEntityItemActionClientManager;
import dev.pswg.item.BlasterItem;
import dev.pswg.networking.BlasterInputPayload;
import dev.pswg.networking.BlasterInputPayload.BlasterInputAction;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Sends hand-specific weapon controls.
 */
public final class BlasterControls
{
	/**
	 * Stores the source and trigger state for one hand.
	 */
	private static class HandInput
	{
		/**
		 * Previous trigger state.
		 */
		private boolean _down;

		/**
		 * True while this hand needs a release packet.
		 */
		private boolean _firing;

		/**
		 * Serial of the last held stack.
		 */
		private Long _serial;

		/**
		 * Base ID of the last held stack.
		 */
		private Identifier _id;

		/**
		 * Last selected hotbar slot.
		 */
		private int _slot;

		/**
		 * Ticks since the last heartbeat.
		 */
		private int _heartbeat;

		/**
		 * Sequence of the accepted press.
		 */
		private long _session;
	}

	/**
	 * Fires the one-handed offhand weapon.
	 */
	public static KeyMapping offhandFire;

	/**
	 * Routes configuration controls to the offhand.
	 */
	public static KeyMapping offhandModifier;

	/**
	 * Toggles ADS for the selected weapon.
	 */
	public static KeyMapping aim;

	/**
	 * Toggles patrol carry for the selected weapon.
	 */
	public static KeyMapping patrol;

	/**
	 * Cycles the selected weapon's modes.
	 */
	public static KeyMapping mode;

	/**
	 * Reloads the selected weapon.
	 */
	public static KeyMapping reload;

	/**
	 * Folds the selected weapon's stock.
	 */
	public static KeyMapping fold;

	/**
	 * Deploys the selected weapon's bipod.
	 */
	public static KeyMapping deploy;

	/**
	 * Converts the selected weapon's form.
	 */
	public static KeyMapping convert;

	/**
	 * Stores each hand's trigger state.
	 */
	private static final EnumMap<InteractionHand, HandInput> _hands = new EnumMap<>(InteractionHand.class);

	/**
	 * Owns the current input sequence.
	 */
	private static ClientPacketListener _owner;

	/**
	 * Last packet sequence.
	 */
	private static long _sequence;

	/**
	 * Previous attack control state.
	 */
	private static boolean _primaryDown;

	/**
	 * Previous secondary fire control state.
	 */
	private static boolean _secondaryDown;

	/**
	 * Previous primary item-action control state.
	 */
	private static boolean _ventDown;

	/**
	 * Previous vanilla use control state.
	 */
	private static boolean _useDown;

	/**
	 * Registers the controls and the client tick handler.
	 */
	public static void register()
	{
		mode = key("cycle_mode", InputConstants.KEY_X);
		reload = key("reload", InputConstants.KEY_R);
		fold = key("fold", InputConstants.KEY_B);
		deploy = key("deploy", InputConstants.KEY_N);
		convert = key("convert", InputConstants.KEY_G);
		aim = key("aim", InputConstants.KEY_Z);
		patrol = key("patrol", InputConstants.KEY_H);
		offhandModifier = key("offhand_modifier", InputConstants.KEY_LALT);
		offhandFire = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.pswg_blasters.offhand_fire",
				InputConstants.Type.MOUSE,
				1,
				GalaxiesKeybinds.CATEGORY
		));

		resetHands();

		GalaxiesEntityItemActionClientManager.HAND_SELECTION.register(player -> {
			var hand = offhandModifier.isDown() ? InteractionHand.OFF_HAND : BlasterWield.primaryHand(player);
			if (player.getItemInHand(hand).is(Blasters.BLASTER_ITEM))
			{
				return hand;
			}
			return null;
		});
		ClientTickEvents.END_CLIENT_TICK.register(BlasterControls::tick);
	}

	/**
	 * Registers a keyboard control.
	 */
	private static KeyMapping key(String name, int code)
	{
		return KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.pswg_blasters." + name,
				InputConstants.Type.KEYBOARD,
				code,
				GalaxiesKeybinds.CATEGORY
		));
	}

	/**
	 * Sends a control that does not refer to a held trigger.
	 */
	private static void send(BlasterInputAction action, InteractionHand hand)
	{
		send(action, hand, 0);
	}

	/**
	 * Sends a trigger packet and returns its sequence.
	 */
	private static long send(BlasterInputAction action, InteractionHand hand, long session)
	{
		if (!ClientPlayNetworking.canSend(BlasterInputPayload.TYPE))
		{
			return 0;
		}

		var packet = new BlasterInputPayload(
				action,
				hand,
				++_sequence,
				session
		);
		ClientPlayNetworking.send(packet);

		return _sequence;
	}

	/**
	 * Releases or cancels the trigger for one hand.
	 */
	private static void release(InteractionHand hand, boolean cancelled)
	{
		var input = _hands.get(hand);

		if (input._firing)
		{
			var action = cancelled ? BlasterInputAction.CANCEL : BlasterInputAction.RELEASE;
			send(action, hand, input._session);
		}

		input._firing = false;
	}

	/**
	 * Resets input state for a new connection.
	 */
	private static void resetHands()
	{
		for (var hand : InteractionHand.values())
		{
			_hands.put(hand, new HandInput());
		}
	}

	/**
	 * Records the physical controls after processing a tick.
	 */
	private static void rememberButtons(Minecraft client)
	{
		_primaryDown = client.options.keyAttack.isDown();
		_secondaryDown = offhandFire.isDown();
		_ventDown = GalaxiesKeybinds.getPrimaryAction().isDown();
		_useDown = client.options.keyUse.isDown();
	}

	/**
	 * Cancels input when the player cannot use weapon controls.
	 */
	private static void cancelInput(Minecraft client)
	{
		for (var hand : InteractionHand.values())
		{
			release(hand, true);
			_hands.get(hand)._down = triggerDown(client, hand);
		}

		rememberButtons(client);
	}

	/**
	 * Processes source changes, controls, aim, and trigger input.
	 */
	private static void tick(Minecraft client)
	{
		if (_owner != client.getConnection())
		{
			_owner = client.getConnection();
			_sequence = 0;
			resetHands();
			rememberButtons(client);
		}

		if (client.player == null || client.level == null || client.gui.screen() != null
		    || !client.player.isAlive() || client.isPaused())
		{
			cancelInput(client);
			return;
		}

		var hand = offhandModifier.isDown() ? InteractionHand.OFF_HAND : BlasterWield.primaryHand(client.player);
		processControls(client, hand);
		processUseAim(client, hand);
		for (var source : InteractionHand.values())
		{
			processTrigger(client, source);
		}

		rememberButtons(client);
	}

	/**
	 * Routes configuration controls to the selected hand.
	 */
	private static void processControls(Minecraft client, InteractionHand hand)
	{
		control(mode, BlasterInputAction.CYCLE_MODE, hand);
		control(reload, BlasterInputAction.RELOAD, hand);
		control(fold, BlasterInputAction.FOLD, hand);
		control(deploy, BlasterInputAction.DEPLOY, hand);
		control(convert, BlasterInputAction.CONVERT, hand);
		control(aim, BlasterInputAction.AIM, hand);
		control(patrol, BlasterInputAction.PATROL, hand);

		if (
				GalaxiesKeybinds.getPrimaryAction().isDown()
				&& !_ventDown
				&& client.player.getItemInHand(hand).is(Blasters.BLASTER_ITEM)
		)
		{
			release(hand, true);
			send(BlasterInputAction.VENT, hand);
		}
	}

	/**
	 * Sends each control click once.
	 */
	private static void control(KeyMapping key, BlasterInputAction action, InteractionHand hand)
	{
		while (key.consumeClick())
		{
			release(hand, true);
			send(action, hand);
		}
	}

	/**
	 * Uses the secondary control only for an eligible one-handed source.
	 */
	private static boolean secondaryDown(Minecraft client)
	{
		if (client.player == null || !offhandFire.isDown())
		{
			return false;
		}

		if (!BlasterWield.canWield(client.player, InteractionHand.OFF_HAND)
		    || BlasterWield.twoHanded(client.player, InteractionHand.OFF_HAND))
		{
			return false;
		}

		return !offhandFire.same(client.options.keyUse) || BlasterWield.dualWielding(client.player);
	}

	/**
	 * Reads the trigger that belongs to this hand.
	 */
	private static boolean triggerDown(Minecraft client, InteractionHand hand)
	{
		if (client.player == null)
		{
			return false;
		}

		if (hand == BlasterWield.primaryHand(client.player) && client.options.keyAttack.isDown())
		{
			return true;
		}

		return hand == InteractionHand.OFF_HAND && secondaryDown(client);
	}

	/**
	 * Toggles ADS on a fresh use press.
	 */
	private static void processUseAim(Minecraft client, InteractionHand hand)
	{
		if (!client.options.keyUse.isDown() || _useDown || secondaryDown(client))
		{
			return;
		}

		if (BlasterWield.canWield(client.player, hand))
		{
			send(BlasterInputAction.AIM, hand);
		}
	}

	/**
	 * Checks whether this tick has a new physical trigger press.
	 */
	private static boolean freshPress(Minecraft client, InteractionHand hand)
	{
		if (hand == BlasterWield.primaryHand(client.player) && client.options.keyAttack.isDown() && !_primaryDown)
		{
			return true;
		}

		return hand == InteractionHand.OFF_HAND && secondaryDown(client) && !_secondaryDown;
	}

	/**
	 * Cancels stale sources and renews the current hand's trigger lease.
	 */
	private static void processTrigger(Minecraft client, InteractionHand hand)
	{
		var input = _hands.get(hand);
		var stack = client.player.getItemInHand(hand);
		var serial = stack.get(BlasterItem.SERIAL);
		var id = stack.get(BlasterItem.ID);
		var slot = client.player.getInventory().getSelectedSlot();
		var eligible = BlasterWield.canWield(client.player, hand);
		var sourceChanged = !Objects.equals(serial, input._serial) || !Objects.equals(id, input._id)
		                    || hand == InteractionHand.MAIN_HAND && slot != input._slot;
		if (!eligible || sourceChanged)
		{
			release(hand, true);
		}

		input._serial = serial;
		input._id = id;
		input._slot = slot;

		var down = triggerDown(client, hand);
		if (!down)
		{
			release(hand, false);
		}
		else if (!input._down && eligible && freshPress(client, hand))
		{
			input._session = send(BlasterInputAction.PRESS, hand, 0);
			input._firing = input._session > 0;
			input._heartbeat = 0;
		}
		else if (input._firing && ++input._heartbeat >= 5)
		{
			send(BlasterInputAction.HEARTBEAT, hand, input._session);
			input._heartbeat = 0;
		}

		input._down = down;
	}

	/**
	 * Prevents construction of this utility class.
	 */
	private BlasterControls()
	{
	}
}
