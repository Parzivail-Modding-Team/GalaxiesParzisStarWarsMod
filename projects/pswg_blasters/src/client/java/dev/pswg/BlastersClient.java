package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.events.HudRenderEvents;
import dev.pswg.item.BlasterItem;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * The main entrypoint for PSWG client-side blaster features
 */
public class BlastersClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		EntityRendererRegistry.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BlasterBoltEntityRenderer.MODEL_LAYER, BlasterBoltEntityRenderer.Model::getTexturedModelData);

		HudRenderEvents.CROSSHAIR.register(BlastersClient::renderCrosshair);

		Blasters.LOGGER.info("Client module initialized");
	}

	private static final Identifier HUD_ELEMENTS_TEXTURE = Blasters.id("textures/gui/hud_elements.png");

	private static void renderCrosshair(DrawContext context, RenderTickCounter tickCounter)
	{
		var client = MinecraftClient.getInstance();
		if (client.player == null)
			return;

		var stack = client.player.getMainHandStack();
		if (!stack.isOf(Blasters.BLASTER_ITEM))
			return;

		var stats = BlasterItem.getStats(stack);
		var state = BlasterItem.getState(stack);
		var coolingStatus = BlasterItem.getCoolingStatus(client.world, stack, tickCounter.getTickDelta(false));

		var m = context.getMatrices();
		m.push();

		var left = (int)(context.getScaledWindowWidth() / 2f);
		var top = (int)(context.getScaledWindowHeight() / 2f);

		final var cooldownWidth = 61;
		var cooldownOffset = 30;
		var cooldownBarX = left - cooldownWidth / 2;

		// translucent background
		context.drawTexture(
				RenderLayer::getGuiTexturedOverlay,
				HUD_ELEMENTS_TEXTURE,
				cooldownBarX, top + cooldownOffset,
				0, 0,
				cooldownWidth, 3,
				256, 256,
				-1
		);

		if (coolingStatus.coolingMode() == BlasterItem.CoolingMode.PASSIVE)
		{
			// passive heat accumulator
			context.drawTexture(
					RenderLayer::getGuiTexturedOverlay,
					HUD_ELEMENTS_TEXTURE,
					cooldownBarX, top + cooldownOffset,
					0, 4,
					(int)(cooldownWidth * coolingStatus.totalHeat() / stats.heat().capacity()), 3,
					256, 256,
					-1
			);
		}
		else
		{
			// cooldown background
			context.drawTexture(
					RenderLayer::getGuiTexturedOverlay,
					HUD_ELEMENTS_TEXTURE,
					cooldownBarX, top + cooldownOffset,
					0, 16,
					cooldownWidth, 3,
					256, 256,
					-1
			);

			if (coolingStatus.coolingMode().canBypass())
			{
				var profile = stats.cooling();
				var primaryBypassStartX = (int)((profile.primaryBypassTime() - profile.primaryBypassTolerance()) * cooldownWidth);
				var primaryBypassWidth = (int)(2 * profile.primaryBypassTolerance() * cooldownWidth);
				var secondaryBypassStartX = (int)((profile.secondaryBypassTime() - profile.secondaryBypassTolerance()) * cooldownWidth);
				var secondaryBypassWidth = (int)(2 * profile.secondaryBypassTolerance() * cooldownWidth);

				// blue primary bypass
				context.drawTexture(
						RenderLayer::getGuiTexturedOverlay,
						HUD_ELEMENTS_TEXTURE,
						cooldownBarX + primaryBypassStartX, top + cooldownOffset,
						primaryBypassStartX, 8,
						primaryBypassWidth, 3,
						256, 256,
						-1
				);

				// yellow secondary bypass
				context.drawTexture(
						RenderLayer::getGuiTexturedOverlay,
						HUD_ELEMENTS_TEXTURE,
						cooldownBarX + secondaryBypassStartX, top + cooldownOffset,
						secondaryBypassStartX, 12,
						secondaryBypassWidth, 3,
						256, 256,
						-1
				);
			}

			var heat = coolingStatus.totalHeat() / state.lastVentingHeat();

			// cursor
			m.push();
			m.translate(cooldownBarX + heat * (cooldownWidth - 3), 0, 0);
			context.drawTexture(
					RenderLayer::getGuiTexturedOverlay,
					HUD_ELEMENTS_TEXTURE,
					0, top + cooldownOffset - 2,
					0, 24,
					3, 7,
					256, 256,
					-1
			);
			m.pop();
		}

		// endcaps
		context.drawTexture(
				RenderLayer::getGuiTexturedOverlay,
				HUD_ELEMENTS_TEXTURE,
				cooldownBarX, top + cooldownOffset,
				0, 20,
				cooldownWidth, 3,
				256, 256,
				-1
		);

		m.pop();
	}
}
