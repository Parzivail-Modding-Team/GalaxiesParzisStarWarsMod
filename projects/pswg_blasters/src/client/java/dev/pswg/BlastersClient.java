package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.data.SlimRegistry;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.hud.DefaultBlasterHudRenderer;
import dev.pswg.item.BlasterItem;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import dev.pswg.rendering.ItemHudRenderer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

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
		EntityRendererRegistry.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BlasterBoltEntityRenderer.MODEL_LAYER, BlasterBoltEntityRenderer.Model::getTexturedModelData);

		BLASTER_HUD_REGISTRY.register(Blasters.DEFAULT_HUD, new DefaultBlasterHudRenderer());

		HudRenderEvents.CROSSHAIR.register(BlastersClient::renderCrosshair);


		// TODO: make modular
		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
			if (!itemStack.isOf(Blasters.BLASTER_ITEM)) {
				return;
			}
			list.add(Text.of(itemStack.getOrDefault(BlasterItem.ID, BlasterItem.MISSING_ID)));
		});

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
