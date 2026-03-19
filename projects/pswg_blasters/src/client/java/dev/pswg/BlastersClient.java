package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.data.*;
import dev.pswg.data.SlimRegistry;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.events.ItemRenderEvents;
import dev.pswg.hud.DefaultBlasterHudRenderer;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.HasAttachmentProperty;
import dev.pswg.item.ItemTooltipHelper;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import dev.pswg.rendering.Drawables;
import dev.pswg.rendering.ItemHudRenderer;
import dev.pswg.rendering.models.GalaxiesModelBakery;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;
import java.util.Optional;

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
		EntityRenderers.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BlasterBoltEntityRenderer.MODEL_LAYER, BlasterBoltEntityRenderer.Model::getTexturedModelData);

		BLASTER_HUD_REGISTRY.register(Blasters.DEFAULT_HUD, new DefaultBlasterHudRenderer());

		HudRenderEvents.CROSSHAIR.register(BlastersClient::renderCrosshair);

		ItemRenderEvents.STACK.register(BlastersClient::renderItemBars);

		// Add the name of the blaster in the tool tip, with a fallback
		ItemTooltipHelper.registerTooltip(Blasters.BLASTER_ITEM, BlastersClient::getTooltip);

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

	private static void getTooltip(ItemStack itemStack, Item.TooltipContext ctx, TooltipFlag type, List<Component> list)
	{
		list.add(Component.translatable(itemStack.getOrDefault(BlasterItem.ID, BlasterItem.MISSING_ID).toLanguageKey()));
		list.add(GalaxiesClient.getKeybindHint(GalaxiesKeybinds.getPrimaryAction(), Component.translatable(I18N_VENT_BLASTER)));
	}

	private static void renderItemBars(GuiGraphics context, Font textRenderer, ItemStack stack, int x, int y)
	{
		if (stack.is(Blasters.BLASTER_ITEM))
		{
			var client = Minecraft.getInstance();
			assert client.level != null;

			// TODO: better visual
			BlasterItem.getFireCooldownProgress(client.level, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(context, value, x, y - 13, 13, 0x0000FF));

			var optionalStats = BlasterItem.getStats(stack);
			if (optionalStats.isEmpty())
				return;

			var stats = optionalStats.get();

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

	private static void renderCrosshair(GuiGraphics context, DeltaTracker tickCounter)
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
