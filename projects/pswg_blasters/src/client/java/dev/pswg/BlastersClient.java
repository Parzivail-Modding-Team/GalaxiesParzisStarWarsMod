package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.data.BlasterClientDefinitions;
import dev.pswg.data.SlimRegistry;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.events.ItemRenderEvents;
import dev.pswg.hud.DefaultBlasterHudRenderer;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.input.BlasterControls;
import dev.pswg.interaction.BlasterActions;
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
	 * Displays the current resolved form, effective statistics, and item-owned ammo/configuration state.
	 */
	private static void getTooltip(ItemStack itemStack, Item.TooltipContext ctx, TooltipFlag type, List<Component> list)
	{
		list.add(Component.translatable(itemStack.getOrDefault(BlasterItem.ID, BlasterItem.MISSING_ID).toLanguageKey()));
		list.add(GalaxiesClient.getKeybindHint(GalaxiesKeybinds.getPrimaryAction(), Component.translatable(I18N_VENT_BLASTER)));
		list.add(GalaxiesClient.getKeybindHint(BlasterControls.mode, Component.translatable("text.pswg_blasters.cycle_mode")));
		list.add(GalaxiesClient.getKeybindHint(BlasterControls.reload, Component.translatable("text.pswg_blasters.reload")));

		var level = Minecraft.getInstance().level;
		var loadout = BlasterItem.getLoadout(level, itemStack);
		if (loadout.isEmpty())
		{
			list.add(Component.translatable("tooltip.pswg_blasters.unavailable"));
			return;
		}

		var effective = BlasterItem.getEffectiveStats(level, itemStack).orElseThrow();
		var stats = effective.stats();

		list.add(Component.translatable("tooltip.pswg_blasters.mode", BlasterActions.modeName(loadout.orElseThrow().selectedMode().id())));
		list.add(Component.translatable("tooltip.pswg_blasters.stats", number(stats.damage()), stats.range(), stats.automaticRepeatDelay(), number(stats.damageRange())));

		var hip = BlasterItem.getEffectiveStats(level, itemStack, new BlasterEffectiveStats.Context(
				BlasterStanceProfile.WeaponState.FIRING, BlasterItem.isDeployed(itemStack), false, BlasterItem.isFolded(itemStack)
		)).orElseThrow().stats().recoil();

		var aim = BlasterItem.getEffectiveStats(level, itemStack, new BlasterEffectiveStats.Context(
				BlasterStanceProfile.WeaponState.FIRING, BlasterItem.isDeployed(itemStack), true, BlasterItem.isFolded(itemStack)
		)).orElseThrow().stats().recoil();

		list.add(Component.translatable("tooltip.pswg_blasters.handling", number(effective.zoom()), number(stats.spread().hipDegrees())));
		list.add(Component.translatable("tooltip.pswg_blasters.recoil", number(hip.hipPitchDegrees()), number(hip.hipYawDegrees()), number(aim.aimPitchDegrees()), number(aim.aimYawDegrees()), hip.recoveryTicks()));
		list.add(Component.translatable("tooltip.pswg_blasters.cooling", number(stats.heat().drainSpeed()), number(stats.heat().overheatDrainSpeed())));

		if (BlasterItem.getAmmoCapacity(stats.ammo()) > 0)
			list.add(Component.translatable("tooltip.pswg_blasters.ammo", BlasterItem.getLoadedAmmo(itemStack, stats.ammo()), BlasterItem.getAmmoCapacity(stats.ammo())));

		loadout.orElseThrow().activeConversion().ifPresent(option -> list.add(Component.translatable("tooltip.pswg_blasters.conversion", option.toString())));

		if (BlasterItem.isDeployed(itemStack))
			list.add(Component.translatable("tooltip.pswg_blasters.deployed"));

		if (BlasterItem.isFolded(itemStack))
			list.add(Component.translatable("tooltip.pswg_blasters.folded"));
	}

	/**
	 * Shows the charge amount.
	 */
	private static void getPackTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> lines)
	{
		var charge = stack.get(StoredCharge.COMPONENT);
		if (charge != null)
			lines.add(Component.translatable("tooltip.pswg_blasters.pack", charge.current(), charge.capacity()));
	}

	/**
	 * Formats displayed effective values without changing their gameplay precision.
	 */
	private static String number(float value)
	{
		return String.format(Locale.ROOT, "%.2f", value);
	}

	private static void renderItemBars(GuiGraphicsExtractor context, Font textRenderer, ItemStack stack, int x, int y)
	{
		if (stack.is(Blasters.BLASTER_ITEM))
		{
			var client = Minecraft.getInstance();
			assert client.level != null;

			// TODO: better visual
			BlasterItem.getFireCooldownProgress(client.level, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(context, value, x, y - 13, 13, 0x0000FF));

			var optionalStats = BlasterItem.getStats(client.level, stack);
			if (optionalStats.isEmpty())
				return;

			var stats = optionalStats.get();
			if (stats.heat().capacity() <= 0)
			{
				return;
			}

			var state = BlasterItem.getState(stack);
			if (state.coolingMode() == BlasterItem.CoolingMode.PASSIVE)
			{
				BlasterItem.getAccumulatedHeat(client.level, stack, GalaxiesClient.getTickDelta())
				           .ifPresent(heat -> {
					           Drawables.itemDurability(context, heat / stats.heat().capacity(), x, y - 10, 13, 0x30FF00);
				           });
			}
			else
			{
				BlasterItem.getVentingHeat(client.level, stack, GalaxiesClient.getTickDelta())
				           .ifPresent(heat -> {
					           Drawables.itemDurability(context, heat / stats.heat().capacity(), x, y - 10, 13, 0xFF3000);
				           });
			}

			BlasterItem.getOverchargeTimeRemaining(client.level, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(context, value, x, y - 7, 13, 0xFFFF00));
		}
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
