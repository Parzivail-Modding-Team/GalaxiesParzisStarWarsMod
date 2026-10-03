package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.Galaxies;
import dev.pswg.item.ArmorItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * G3D armor renderer.
 */
public final class G3dArmorRenderer implements ArmorRenderer
{
	/**
	 * Registers one combined armor asset for all four items during client startup.
	 */
	public static void register(Identifier modelId, ArmorItems armor)
	{
		ArmorRenderer.register(
				new G3dArmorRenderer(modelId),
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
	public static void register(Identifier wideModelId, Identifier slimModelId, ArmorItems armor)
	{
		ArmorRenderer.register(
				new G3dArmorRenderer(wideModelId, slimModelId),
				armor.helmet,
				armor.chestplate,
				armor.leggings,
				armor.boots
		);
	}

	/**
	 * Detects Alex-style player skins; other humanoid wearers use the wide model.
	 */
	public static boolean isSlim(HumanoidRenderState state)
	{
		return state instanceof AvatarRenderState avatar && avatar.skin.model() == PlayerModelType.SLIM;
	}

	/**
	 * Default model for Steve-style players and other humanoid entities.
	 */
	private final Identifier _wideModelId;

	/**
	 * Model for Alex-style players; may be the same combined asset.
	 */
	private final Identifier _slimModelId;

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
		_wideModelId = wideModelId;
		_slimModelId = slimModelId;
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
		var renderer = G3dClientModels.get(id).orElse(null);

		if (renderer == null && slim && !id.equals(_wideModelId))
		{
			renderer = G3dClientModels.get(_wideModelId).orElse(null);
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
