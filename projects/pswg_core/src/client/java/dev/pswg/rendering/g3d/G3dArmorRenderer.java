package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.Galaxies;
import dev.pswg.item.ArmorItems;
import dev.pswg.model.g3d.G3dTextureBindings;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * G3D armor renderer.
 */
public final class G3dArmorRenderer implements ArmorRenderer
{
	/**
	 * Optional behavior attached to registered armor items during client startup.
	 */
	public enum Flag
	{
		/**
		 * Hides the equipped slot's outer player-skin layers while keeping the base skin.
		 */
		HIDE_SKIN_OVERLAY
	}

	/**
	 * Registers one combined armor asset for all four items during client startup.
	 * Flags apply to each piece's equipped area; no flags keeps normal skin visibility.
	 */
	public static void register(Identifier modelId, ArmorItems armor, Flag... flags)
	{
		register(modelId, armor, G3dTextureBindings.EMPTY, flags);
	}

	/**
	 * Registers a set's material bindings without copying its model or rig.
	 */
	public static void register(Identifier modelId, ArmorItems armor, G3dTextureBindings bindings, Flag... flags)
	{
		register(
				new G3dArmorRenderer(modelId, bindings),
				flags,
				armor.helmet,
				armor.chestplate,
				armor.leggings,
				armor.boots
		);
	}

	/**
	 * Registers separate wide and slim assets when an armor set needs more than
	 * arm-geometry variants. Both assets use the same named-anchor contract.
	 */
	public static void register(Identifier wideModelId, Identifier slimModelId, ArmorItems armor, Flag... flags)
	{
		register(wideModelId, slimModelId, armor, G3dTextureBindings.EMPTY, flags);
	}

	/**
	 * Applies one appearance to separate wide/slim geometry assets.
	 */
	public static void register(Identifier wideModelId, Identifier slimModelId, ArmorItems armor, G3dTextureBindings bindings, Flag... flags)
	{
		register(
				new G3dArmorRenderer(wideModelId, slimModelId, bindings),
				flags,
				armor.helmet,
				armor.chestplate,
				armor.leggings,
				armor.boots
		);
	}

	/**
	 * Registers one piece when a set needs different behavior for different items.
	 */
	public static void register(Identifier modelId, ItemLike item, Flag... flags)
	{
		register(new G3dArmorRenderer(modelId), flags, item);
	}

	/**
	 * Registers an individual item's slot bindings and behavior.
	 */
	public static void register(Identifier modelId, ItemLike item, G3dTextureBindings bindings, Flag... flags)
	{
		register(new G3dArmorRenderer(modelId, bindings), flags, item);
	}

	/**
	 * Registers one piece with separate wide/slim assets and its own behavior flags.
	 */
	public static void register(Identifier wideModelId, Identifier slimModelId, ItemLike item, Flag... flags)
	{
		register(new G3dArmorRenderer(wideModelId, slimModelId), flags, item);
	}

	/**
	 * Registers a single piece with distinct wide/slim geometry and shared bindings.
	 */
	public static void register(Identifier wideModelId, Identifier slimModelId, ItemLike item, G3dTextureBindings bindings, Flag... flags)
	{
		register(new G3dArmorRenderer(wideModelId, slimModelId, bindings), flags, item);
	}

	/**
	 * Stores immutable registration settings after Fabric accepts the renderer.
	 */
	private static void register(ArmorRenderer renderer, Flag[] flags, ItemLike... items)
	{
		var settings = Set.copyOf(Arrays.asList(flags));
		ArmorRenderer.register(renderer, items);
		for (var item : items)
			_flags.put(item.asItem(), settings);
	}

	/**
	 * Applies equipment visibility after vanilla extracts skin options. The same
	 * snapshot drives queued player rendering and first-person hand sleeves.
	 * Only disables overlays; vanilla restores the player's choices each extraction.
	 */
	public static void applySkinVisibility(AvatarRenderState state)
	{
		if (state.isSpectator)
			return;

		if (hidesSkinOverlay(state.headEquipment))
			state.showHat = false;

		if (hidesSkinOverlay(state.chestEquipment))
		{
			state.showJacket = false;
			state.showLeftSleeve = false;
			state.showRightSleeve = false;
		}

		if (hidesSkinOverlay(state.legsEquipment) || hidesSkinOverlay(state.feetEquipment))
		{
			state.showLeftPants = false;
			state.showRightPants = false;
		}
	}

	/**
	 * Checks a worn item without retaining the wearer or reading live entity state.
	 */
	private static boolean hidesSkinOverlay(ItemStack stack)
	{
		return !stack.isEmpty() && _flags.getOrDefault(stack.getItem(), Set.of()).contains(Flag.HIDE_SKIN_OVERLAY);
	}

	/**
	 * Detects Alex-style player skins; other humanoid wearers use the wide model.
	 */
	public static boolean isSlim(HumanoidRenderState state)
	{
		return state instanceof AvatarRenderState avatar && avatar.skin.model() == PlayerModelType.SLIM;
	}

	/**
	 * Item behavior survives resource reload and is shared by wide/slim renderers.
	 */
	private static final Map<Item, Set<Flag>> _flags = new HashMap<>();

	/**
	 * Default model for Steve-style players and other humanoid entities.
	 */
	private final Identifier _wideModelId;

	/**
	 * Model for Alex-style players; may be the same combined asset.
	 */
	private final Identifier _slimModelId;

	/**
	 * This registration's immutable appearance overrides; model defaults fill gaps.
	 */
	private final G3dTextureBindings _bindings;

	/**
	 * Wide rig binding, replaced when the model manager publishes a new asset.
	 */
	private @Nullable G3dArmorPose _widePose;

	/**
	 * Slim rig binding, independent of the wide model and of the current wearer.
	 */
	private @Nullable G3dArmorPose _slimPose;

	/**
	 * Missing assets are diagnosed once, rather than crashing or logging every frame.
	 */
	private final Set<Identifier> _missingModels = new HashSet<>();

	/**
	 * Creates an armor renderer for a combined asset.
	 */
	public G3dArmorRenderer(Identifier modelId)
	{
		this(modelId, modelId);
	}

	/**
	 * Creates a renderer with optional separate Steve/Alex assets. A missing slim
	 * asset falls back to the wide asset from the same resource-reload snapshot.
	 */
	public G3dArmorRenderer(Identifier wideModelId, Identifier slimModelId)
	{
		this(wideModelId, slimModelId, G3dTextureBindings.EMPTY);
	}

	/**
	 * Creates a sampled appearance over one shared geometry asset.
	 */
	public G3dArmorRenderer(Identifier modelId, G3dTextureBindings bindings)
	{
		this(modelId, modelId, bindings);
	}

	/**
	 * Creates a set's sampled appearance over either native player model variant.
	 */
	public G3dArmorRenderer(Identifier wideModelId, Identifier slimModelId, G3dTextureBindings bindings)
	{
		_wideModelId = wideModelId;
		_slimModelId = slimModelId;
		_bindings = bindings;
		G3dClientModels.registerSampled(wideModelId);
		G3dClientModels.registerSampled(slimModelId);
	}

	/**
	 * Captures the context model's current pose before queuing geometry. Armor stays
	 * visible on invisible wearers, as in vanilla, and retains native light/outline
	 * and enchantment glint. Material tint slot zero receives the item's dye color.
	 */
	@Override
	public void render(
			PoseStack stack,
			SubmitNodeCollector collector,
			ItemStack item,
			HumanoidRenderState state,
			EquipmentSlot slot,
			int light,
			HumanoidModel<HumanoidRenderState> contextModel
	)
	{
		boolean slim = isSlim(state);
		var id = slim ? _slimModelId : _wideModelId;
		var renderer = G3dClientModels.getSampled(id, _bindings).orElse(null);

		if (renderer == null && slim && !id.equals(_wideModelId))
		{
			renderer = G3dClientModels.getSampled(_wideModelId, _bindings).orElse(null);
			slim = false;
		}

		if (renderer == null)
		{
			if (_missingModels.add(id))
				Galaxies.LOGGER.warn("Missing G3D armor model {}", id);

			return;
		}

		var pose = slim ? _slimPose : _widePose;
		if (pose == null || pose.rig() != renderer.model().rig())
		{
			pose = new G3dArmorPose(renderer.model().rig());
			if (slim)
				_slimPose = pose;
			else
				_widePose = pose;
		}

		renderer.submitArmor(
				pose.capture(contextModel, slot, slim),
				stack,
				collector,
				light,
				new int[]{DyedItemColor.getOrDefault(item, -1)},
				item.hasFoil(),
				state.outlineColor
		);
	}

	/**
	 * The custom helmet supplies the worn appearance, including for addon items
	 * that have no vanilla equipment asset and would otherwise render a head item.
	 */
	@Override
	public boolean shouldRenderDefaultHeadItem(LivingEntity entity, ItemStack stack)
	{
		return false;
	}
}
