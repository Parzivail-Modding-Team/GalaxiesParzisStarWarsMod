package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.data.BlasterClientDefinitions;
import dev.pswg.data.BlasterStats;
import dev.pswg.data.SlimRegistry;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.events.ItemRenderEvents;
import dev.pswg.hud.DefaultBlasterHudRenderer;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.input.BlasterControls;
import dev.pswg.interaction.BlasterActions;
import dev.pswg.interaction.ItemInteractionTimer;
import dev.pswg.item.BlasterAmmo;
import dev.pswg.data.BlasterStanceProfile;
import dev.pswg.item.BlasterEffectiveStats;
import dev.pswg.item.component.StoredCharge;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.HasAttachmentProperty;
import dev.pswg.item.ItemTooltipHelper;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import dev.pswg.rendering.Drawables;
import dev.pswg.rendering.ItemHudRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Locale;

/**
 * The main entrypoint for PSWG client-side blaster features
 */
public class BlastersClient implements GalaxiesClientAddon
{
	/**
	 * The registry for blaster information HUD renderers
	 */
	public static final SlimRegistry<ItemHudRenderer> BLASTER_HUD_REGISTRY = new SlimRegistry<>();

	/**
	 * A translatable text with two parameters: the keybind value, and the hint text
	 */
	public static final String I18N_VENT_BLASTER = GalaxiesClient.getI18nKey(Blasters.id("vent_blaster"));

	@Override
	public void onGalaxiesClientReady()
	{
		BlasterClientDefinitions.register();
		BlasterControls.register();
		EntityRenderers.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);

		BLASTER_HUD_REGISTRY.register(Blasters.DEFAULT_HUD, new DefaultBlasterHudRenderer());
		HudRenderEvents.CROSSHAIR.register(BlastersClient::renderCrosshair);

		HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Blasters.id("crosshair"), (context, tickCounter) -> {
			var matrix = context.pose();
			matrix.pushMatrix();
			HudRenderEvents.CROSSHAIR.invoker().render(context, tickCounter);
			matrix.popMatrix();
		});

		ItemRenderEvents.STACK.register(BlastersClient::renderItemBars);

		// Add the name of the blaster in the tool tip, with a fallback
		ItemTooltipHelper.registerTooltip(Blasters.BLASTER_ITEM, BlastersClient::getTooltip);
		ItemTooltipHelper.registerTooltip(Blasters.SMALL_POWER_PACK, BlastersClient::getPackTooltip);
		ItemTooltipHelper.registerTooltip(Blasters.POWER_PACK, BlastersClient::getPackTooltip);

		ConditionalItemModelProperties.ID_MAPPER.put(Blasters.id("has_attachment"), HasAttachmentProperty.CODEC);

		// TODO: I think we can use this to generate config UIs
		//
		//		var uiElement = BlasterItem.StatsComponent.CODEC
		//				.encode(BlasterItem.StatsComponent.DEFAULT, ConfigUiOps.INSTANCE, new GroupUiElement())
		//				.getOrThrow();
		//
		//		var encodedData = BlasterItem.StatsComponent.CODEC
		//				.decode(ConfigUiOps.INSTANCE, uiElement)
		//				.resultOrPartial(Blasters.LOGGER::error)
		//				.orElseThrow();

		Blasters.LOGGER.info("Client module initialized");
	}

	/**
	 * Displays operating information by default and detailed effective statistics while Shift is held.
	 */
	private static void getTooltip(ItemStack itemStack, Item.TooltipContext ctx, TooltipFlag type, List<Component> list)
	{
		list.add(Component.translatable(itemStack.getOrDefault(BlasterItem.ID, BlasterItem.MISSING_ID).toLanguageKey()).withStyle(ChatFormatting.YELLOW));

		var level = Minecraft.getInstance().level;
		var loadout = BlasterItem.getLoadout(level, itemStack);
		if (loadout.isEmpty())
		{
			list.add(Component.translatable("tooltip.pswg_blasters.unavailable").withStyle(ChatFormatting.RED));
			return;
		}

		var effective = BlasterItem.getEffectiveStats(level, itemStack).orElseThrow();
		var stats = effective.stats();
		var resolvedLoadout = loadout.orElseThrow();
		var definition = resolvedLoadout.definition();

		list.add(Component.translatable("tooltip.pswg_blasters.mode", ItemTooltipHelper.color(BlasterActions.modeName(resolvedLoadout.selectedMode().id()), ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY));
		if (resolvedLoadout.availableModes().size() > 1)
			list.add(ItemTooltipHelper.keyHint(BlasterControls.mode, "text.pswg_blasters.cycle_mode"));

		if (stats.heat().capacity() > 0)
			list.add(ItemTooltipHelper.color(GalaxiesClient.getKeybindHint(GalaxiesKeybinds.getPrimaryAction(), Component.translatable(I18N_VENT_BLASTER)), ChatFormatting.AQUA));

		if (BlasterItem.canReload(stats))
			list.add(ItemTooltipHelper.keyHint(BlasterControls.reload, "text.pswg_blasters.reload"));

		var hasFoldedBehavior = BlasterItem.hasContextualAttachmentModifier(resolvedLoadout, true);
		var hasDeployedBehavior = BlasterItem.hasContextualAttachmentModifier(resolvedLoadout, false);

		if (hasFoldedBehavior)
			list.add(ItemTooltipHelper.keyHint(BlasterControls.fold, "key.pswg_blasters.fold"));

		if (hasDeployedBehavior)
			list.add(ItemTooltipHelper.keyHint(BlasterControls.deploy, "key.pswg_blasters.deploy"));

		if (definition.stats().configuration().fieldConversion().isPresent() || resolvedLoadout.activeConversion().isPresent())
			list.add(ItemTooltipHelper.keyHint(BlasterControls.convert, "key.pswg_blasters.convert"));

		if (BlasterItem.isDeployed(itemStack))
			list.add(Component.translatable("tooltip.pswg_blasters.deployed").withStyle(ChatFormatting.GREEN));

		if (BlasterItem.isFolded(itemStack))
			list.add(Component.translatable("tooltip.pswg_blasters.folded").withStyle(ChatFormatting.GRAY));

		var ammoCapacity = BlasterItem.getAmmoCapacity(stats.ammo());
		if (ammoCapacity > 0)
			list.add(ItemTooltipHelper.detail(
					"tooltip.pswg_blasters.ammo",
					ItemTooltipHelper.value(Long.toString(BlasterItem.getLoadedAmmo(itemStack, stats.ammo()))),
					ItemTooltipHelper.value(Integer.toString(ammoCapacity))
			));

		if (!Minecraft.getInstance().hasShiftDown())
		{
			list.add(Component.translatable("tooltip.pswg_blasters.extended_hint").withStyle(ChatFormatting.DARK_GRAY));
			return;
		}

		var hip = BlasterItem.getEffectiveStats(level, itemStack, new BlasterEffectiveStats.Context(
				BlasterStanceProfile.WeaponState.FIRING, BlasterItem.isDeployed(itemStack), false, BlasterItem.isFolded(itemStack)
		)).orElseThrow().stats().recoil();

		var aim = BlasterItem.getEffectiveStats(level, itemStack, new BlasterEffectiveStats.Context(
				BlasterStanceProfile.WeaponState.FIRING, BlasterItem.isDeployed(itemStack), true, BlasterItem.isFolded(itemStack)
		)).orElseThrow().stats().recoil();

		list.add(Component.translatable("tooltip.pswg_blasters.details").withStyle(ChatFormatting.GOLD));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.damage", ItemTooltipHelper.value(number(stats.damage()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.range", ItemTooltipHelper.value(Integer.toString(stats.range()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.fire_interval", ItemTooltipHelper.value(Integer.toString(stats.automaticRepeatDelay()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.effective_range", ItemTooltipHelper.value(number(stats.damageRange()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.zoom", ItemTooltipHelper.value(number(effective.zoom()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.spread", ItemTooltipHelper.value(number(stats.spread().hipDegrees())), ItemTooltipHelper.value(number(stats.spread().aimDegrees()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.hip_recoil", ItemTooltipHelper.value(number(hip.hipPitchDegrees())), ItemTooltipHelper.value(number(hip.hipYawDegrees()))));
		list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.aim_recoil", ItemTooltipHelper.value(number(aim.aimPitchDegrees())), ItemTooltipHelper.value(number(aim.aimYawDegrees()))));
		list.add(ItemTooltipHelper.detail(
				"tooltip.pswg_blasters.recoil_profile",
				ItemTooltipHelper.value(Integer.toString(hip.pattern().pitchStages().size())),
				ItemTooltipHelper.value(Integer.toString(hip.pattern().yawCycle().size())),
				ItemTooltipHelper.value(Integer.toString(hip.recoveryTicks()))
		));

		if (stats.heat().capacity() > 0)
		{
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.cooling", ItemTooltipHelper.value(number(stats.heat().drainSpeed()))));
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.overheat_cooling", ItemTooltipHelper.value(number(stats.heat().overheatDrainSpeed()))));
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.heat_cost", ItemTooltipHelper.value(Integer.toString(stats.heat().perRound())), ItemTooltipHelper.value(Integer.toString(stats.heat().capacity()))));
		}

		if (stats.ammo().feed() instanceof BlasterStats.MagazineFeed magazine)
		{
			var units = BlasterAmmo.unitsPerLoadedQuantity(stats.ammo());
			var room = Math.max(0, magazine.magazineSize() - BlasterItem.getLoadedRounds(itemStack));
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.units_per_round", ItemTooltipHelper.value(Long.toString(units))));
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.full_magazine_cost", ItemTooltipHelper.value(Long.toString((long)magazine.magazineSize() * units))));
			list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.top_up_cost", ItemTooltipHelper.value(Long.toString((long)room * units))));
		}

		resolvedLoadout.activeConversion().ifPresent(option -> list.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.conversion", ItemTooltipHelper.value(option.toString()))));
	}

	/**
	 * Shows the charge amount.
	 */
	private static void getPackTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines)
	{
		var charge = stack.get(StoredCharge.COMPONENT);
		if (charge != null)
			lines.add(ItemTooltipHelper.detail("tooltip.pswg_blasters.pack", ItemTooltipHelper.value(Integer.toString(charge.current())), ItemTooltipHelper.value(Integer.toString(charge.capacity()))));
	}

	/**
	 * Formats displayed effective values.
	 */
	private static String number(float value)
	{
		return String.format(Locale.ROOT, "%.2f", value);
	}

	/**
	 * Draws only the owning player's active interaction timer at the native durability-bar location.
	 */
	private static void renderItemBars(GuiGraphicsExtractor context, Font textRenderer, ItemStack stack, int x, int y)
	{
		var client = Minecraft.getInstance();
		if (!stack.is(Blasters.BLASTER_ITEM) || client.player == null || client.level == null)
			return;
		var timer = client.player.getAttached(ItemInteractionTimer.ATTACHMENT);
		var serial = stack.get(BlasterItem.SERIAL);
		if (timer == null || serial == null || serial.longValue() != timer.serial() || !timer.isActive(client.level.getGameTime()))
			return;
		var color = timer.kind() == ItemInteractionTimer.ItemInteractionKind.RELOAD ? 0x54D9FF : 0xFFD45A;
		Drawables.itemDurability(context, timer.progress(client.level.getGameTime(), GalaxiesClient.getTickDelta()), x, y, 13, color);
	}

	@Override
	public void onGalaxiesFinalizing()
	{
		BLASTER_HUD_REGISTRY.freeze();
	}

	private static void renderCrosshair(GuiGraphicsExtractor context, DeltaTracker tickCounter)
	{
		var client = Minecraft.getInstance();
		if (client.player == null)
			return;

		var stack = client.player.getMainHandItem();
		if (!stack.is(Blasters.BLASTER_ITEM))
			return;

		var attachments = BlasterItem.getAttachments(stack);
		BLASTER_HUD_REGISTRY.tryGetValue(attachments.hud())
		                    .ifPresent(hudRenderer -> hudRenderer.render(stack, context, tickCounter));
	}
}
