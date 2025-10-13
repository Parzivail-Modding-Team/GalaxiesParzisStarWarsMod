package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.data.SlimRegistry;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.events.ItemRenderEvents;
import dev.pswg.hud.DefaultBlasterHudRenderer;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.ItemTooltipHelper;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import dev.pswg.rendering.Drawables;
import dev.pswg.rendering.ItemHudRenderer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

/**
 * The main entrypoint for PSWG client-side blaster features
 */
public class BlastersClient implements GalaxiesClientAddon
{
	/**
	 * The registry for blaster information HUD renderers
	 */
	public static final SlimRegistry<ItemHudRenderer> BLASTER_HUD_REGISTRY = new SlimRegistry<>();

	@Override
	public void onGalaxiesClientReady()
	{
		// TODO: recoil rendering

		EntityRendererFactories.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BlasterBoltEntityRenderer.MODEL_LAYER, BlasterBoltEntityRenderer.Model::getTexturedModelData);

		BLASTER_HUD_REGISTRY.register(Blasters.DEFAULT_HUD, new DefaultBlasterHudRenderer());

		HudRenderEvents.CROSSHAIR.register(BlastersClient::renderCrosshair);

		ItemRenderEvents.STACK.register(BlastersClient::renderItemBars);

		// Add the name of the blaster in the tool tip, with a fallback
		ItemTooltipHelper.registerTooltip(Blasters.BLASTER_ITEM, BlastersClient::getTooltip);

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

	private static void getTooltip(ItemStack itemStack, Item.TooltipContext ctx, TooltipType type, List<Text> list)
	{
		list.add(Text.translatable(itemStack.getOrDefault(BlasterItem.ID, BlasterItem.MISSING_ID).toTranslationKey()));
	}

	private static void renderItemBars(DrawContext context, TextRenderer textRenderer, ItemStack stack, int x, int y)
	{
		if (stack.isOf(Blasters.BLASTER_ITEM))
		{
			var client = MinecraftClient.getInstance();
			assert client.world != null;

			// TODO: better visual
			BlasterItem.getFireCooldownProgress(client.world, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(context, value, x, y - 13, 13, 0x0000FF));

			var stats = BlasterItem.getStats(stack);
			var state = BlasterItem.getState(stack);
			if (state.coolingMode() == BlasterItem.CoolingMode.PASSIVE)
			{
				BlasterItem.getAccumulatedHeat(client.world, stack, GalaxiesClient.getTickDelta())
				           .ifPresent(heat -> {
					           Drawables.itemDurability(context, heat / stats.heat().capacity(), x, y - 10, 13, 0x30FF00);
				           });
			}
			else
			{
				BlasterItem.getVentingHeat(client.world, stack, GalaxiesClient.getTickDelta())
				           .ifPresent(heat -> {
					           Drawables.itemDurability(context, heat / stats.heat().capacity(), x, y - 10, 13, 0xFF3000);
				           });
			}

			BlasterItem.getOverchargeTimeRemaining(client.world, stack, GalaxiesClient.getTickDelta())
			           .ifPresent(value -> Drawables.itemDurability(context, value, x, y - 7, 13, 0xFFFF00));
		}
	}

	@Override
	public void onGalaxiesFinalizing()
	{
		BLASTER_HUD_REGISTRY.freeze();
	}

	private static void renderCrosshair(DrawContext context, RenderTickCounter tickCounter)
	{
		var client = MinecraftClient.getInstance();
		if (client.player == null)
			return;

		var stack = client.player.getMainHandStack();
		if (!stack.isOf(Blasters.BLASTER_ITEM))
			return;

		var attachments = BlasterItem.getAttachments(stack);
		BLASTER_HUD_REGISTRY.tryGetValue(attachments.hud())
		                    .ifPresent(hudRenderer -> hudRenderer.render(stack, context, tickCounter));
	}
}
