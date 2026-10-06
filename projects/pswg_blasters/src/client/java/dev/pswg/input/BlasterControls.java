package dev.pswg.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pswg.Blasters;
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

/**
 * Configurable weapon controls.
 */
public final class BlasterControls
{
	/**
	 * Mode-cycle mapping, available for tooltip hints.
	 */
	public static KeyMapping mode;

	/**
	 * Reload mapping, available for tooltip hints.
	 */
	public static KeyMapping reload;

	/**
	 * Fold/extend mapping.
	 */
	public static KeyMapping fold;

	/**
	 * Ground-supported deployment mapping.
	 */
	public static KeyMapping deploy;

	/**
	 * Authored field-conversion mapping.
	 */
	public static KeyMapping convert;

	/**
	 * Connection owning the local sequence.
	 */
	private static ClientPacketListener _owner;

	/**
	 * Monotonic sequence, reset on a new play connection.
	 */
	private static long _sequence;

	/**
	 * Previous physical attack-key state.
	 */
	private static boolean _down;

	/**
	 * Whether a held intent needs a release.
	 */
	private static boolean _firing;

	/**
	 * Held-request hand.
	 */
	private static InteractionHand _hand;

	/**
	 * Last physical serial, stable through native component updates.
	 */
	private static Long _serial;

	/**
	 * Last authored base ID.
	 */
	private static Identifier _id;

	/**
	 * Last main-hand slot.
	 */
	private static int _slot;

	/**
	 * Ticks until the next held-input heartbeat.
	 */
	private static int _heartbeat;

	/**
	 * Registers this module's controls under the existing PSWG key category.
	 */
	public static void register()
	{
		mode = key("cycle_mode", InputConstants.KEY_X);
		reload = key("reload", InputConstants.KEY_R);
		fold = key("fold", InputConstants.KEY_B);
		deploy = key("deploy", InputConstants.KEY_N);
		convert = key("convert", InputConstants.KEY_G);

		ClientTickEvents.END_CLIENT_TICK.register(BlasterControls::tick);
	}

	/**
	 * Creates one control.
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
	 * Sends ordered intent using only the physical hand and action.
	 */
	private static void send(BlasterInputAction action, InteractionHand hand)
	{
		if (ClientPlayNetworking.canSend(BlasterInputPayload.TYPE))
			ClientPlayNetworking.send(new BlasterInputPayload(action, hand, ++_sequence));
	}

	/**
	 * Stops any held request before a control action or source change.
	 */
	private static void release()
	{
		if (_firing && _hand != null)
			send(BlasterInputAction.RELEASE, _hand);

		_firing = false;
	}

	/**
	 * Consumes a native key click without letting a held trigger resume after the configuration changes.
	 */
	private static void control(KeyMapping key, BlasterInputAction action, InteractionHand hand)
	{
		while (key.consumeClick())
		{
			release();
			if (hand != null)
				send(action, hand);
		}
	}

	/**
	 * Tracks connection lifetime, screens, physical source identity and input lease renewal.
	 */
	private static void tick(Minecraft client)
	{
		if (_owner != client.getConnection())
		{
			_owner = client.getConnection();
			_sequence = 0;
			_firing = false;
			_down = client.options.keyAttack.isDown();
			_hand = null;
		}

		var down = client.options.keyAttack.isDown();
		if (client.player == null || client.level == null || client.gui.screen() != null || !client.player.isAlive() || client.isPaused())
		{
			release();
			_down = down;
			return;
		}

		InteractionHand hand = client.player.getMainHandItem().is(Blasters.BLASTER_ITEM) ? InteractionHand.MAIN_HAND
		                                                                                 : client.player.getOffhandItem().is(Blasters.BLASTER_ITEM) ? InteractionHand.OFF_HAND : null;
		var stack = hand == null ? net.minecraft.world.item.ItemStack.EMPTY : client.player.getItemInHand(hand);
		var serial = stack.get(BlasterItem.SERIAL);
		var id = stack.get(BlasterItem.ID);
		var slot = client.player.getInventory().getSelectedSlot();

		if (hand != _hand || !java.util.Objects.equals(serial, _serial) || !java.util.Objects.equals(id, _id)
		    || (hand == InteractionHand.MAIN_HAND && slot != _slot))
			release();

		_hand = hand;
		_serial = serial;
		_id = id;
		_slot = slot;

		control(mode, BlasterInputAction.CYCLE_MODE, hand);
		control(reload, BlasterInputAction.RELOAD, hand);
		control(fold, BlasterInputAction.FOLD, hand);
		control(deploy, BlasterInputAction.DEPLOY, hand);
		control(convert, BlasterInputAction.CONVERT, hand);

		if (!down)
			release();
		else if (!_down && hand != null)
		{
			send(BlasterInputAction.PRESS, hand);
			_firing = true;
			_heartbeat = 0;
		}
		else if (_firing && ++_heartbeat >= 5)
		{
			send(BlasterInputAction.HEARTBEAT, hand);
			_heartbeat = 0;
		}

		_down = down;
	}

	/**
	 * Utility class.
	 */
	private BlasterControls()
	{
	}
}
