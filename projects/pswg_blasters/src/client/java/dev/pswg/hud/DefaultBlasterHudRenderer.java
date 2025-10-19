package dev.pswg.hud;

import dev.pswg.Blasters;
import dev.pswg.GalaxiesClient;
import dev.pswg.item.BlasterItem;
import dev.pswg.rendering.BlittableTexture;
import dev.pswg.rendering.ItemHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

/**
 * The default blaster HUD renderer. Renders a rectangular cooldown bar
 * with a primary and secondary bypass when overheated.
 */
public class DefaultBlasterHudRenderer implements ItemHudRenderer
{
	private static final int COOLDOWN_WIDTH = 61;
	private static final int COOLDOWN_HEIGHT = 3;

	private static final int COOLDOWN_OFFSET = 30;

	private static final int PRIMARY_BYPASS_TEX_V = 8;
	private static final int SECONDARY_BYPASS_TEX_V = 12;

	private static final BlittableTexture HUD_ELEMENTS = new BlittableTexture(
			Blasters.id("textures/gui/hud_elements.png"),
			RenderPipelines.GUI_TEXTURED,
			256, 256
	);

	private static final BlittableTexture.Patch BACKGROUND = HUD_ELEMENTS.createPatch(0, 0, COOLDOWN_WIDTH, COOLDOWN_HEIGHT);

	private static final BlittableTexture.Patch ENDCAPS = HUD_ELEMENTS.createPatch(0, 20, COOLDOWN_WIDTH, COOLDOWN_HEIGHT);

	private static final BlittableTexture.Patch PASSIVE_HEAT_BAR = HUD_ELEMENTS.createPatch(0, 4, COOLDOWN_WIDTH, COOLDOWN_HEIGHT);

	private static final BlittableTexture.Patch OVERCHARGE_BAR = HUD_ELEMENTS.createPatch(0, 12, COOLDOWN_WIDTH, COOLDOWN_HEIGHT);

	private static final BlittableTexture.Patch COOLDOWN_BACKGROUND = HUD_ELEMENTS.createPatch(0, 16, COOLDOWN_WIDTH, COOLDOWN_HEIGHT);

	private static final BlittableTexture.Patch CURSOR = HUD_ELEMENTS.createPatch(0, 24, 3, 7);

	@Override
	public void render(ItemStack stack, DrawContext context, RenderTickCounter tickCounter)
	{
		var client = MinecraftClient.getInstance();

		assert client.world != null;

		var optionalStats = BlasterItem.getStats(stack);
		if (optionalStats.isEmpty())
			return;

		var stats = optionalStats.get();

		var state = BlasterItem.getState(stack);

		var m = context.getMatrices();
		m.pushMatrix();

		var left = (int)(context.getScaledWindowWidth() / 2f);
		var top = (int)(context.getScaledWindowHeight() / 2f);

		var cooldownBarX = left - COOLDOWN_WIDTH / 2;

		BACKGROUND.blit(context, cooldownBarX, top + COOLDOWN_OFFSET, -1);

		var tickDelta = GalaxiesClient.getTickDelta();

		var overcharge = BlasterItem.getOverchargeTimeRemaining(client.world, stack, tickDelta);
		if (overcharge.isPresent())
		{
			OVERCHARGE_BAR.blit(
					context,
					cooldownBarX, top + COOLDOWN_OFFSET,
					COOLDOWN_WIDTH, COOLDOWN_HEIGHT,
					-1
			);

			// cursor
			m.pushMatrix();
			m.translate(cooldownBarX + overcharge.get() * (COOLDOWN_WIDTH - 3), 0);
			CURSOR.blit(context, 0, top + COOLDOWN_OFFSET - 2, -1);
			m.popMatrix();
		}
		else
		{
			var coolingStatus = BlasterItem.getCoolingStatus(client.world, stack, tickDelta);
			if (coolingStatus.coolingMode() == BlasterItem.CoolingMode.PASSIVE)
			{
				PASSIVE_HEAT_BAR.blit(
						context,
						cooldownBarX, top + COOLDOWN_OFFSET,
						(int)(COOLDOWN_WIDTH * coolingStatus.totalHeat() / stats.heat().capacity()), COOLDOWN_HEIGHT,
						-1
				);
			}
			else
			{
				COOLDOWN_BACKGROUND.blit(
						context,
						cooldownBarX, top + COOLDOWN_OFFSET,
						COOLDOWN_WIDTH, COOLDOWN_HEIGHT,
						-1
				);

				if (coolingStatus.coolingMode().canBypass())
				{
					var profile = stats.cooling();
					var primaryBypassStartX = (int)((profile.primaryBypassTime() - profile.primaryBypassTolerance()) * COOLDOWN_WIDTH);
					var primaryBypassWidth = (int)(2 * profile.primaryBypassTolerance() * COOLDOWN_WIDTH);
					var secondaryBypassStartX = (int)((profile.secondaryBypassTime() - profile.secondaryBypassTolerance()) * COOLDOWN_WIDTH);
					var secondaryBypassWidth = (int)(2 * profile.secondaryBypassTolerance() * COOLDOWN_WIDTH);

					// blue primary bypass
					HUD_ELEMENTS.blit(
							context,
							cooldownBarX + primaryBypassStartX, top + COOLDOWN_OFFSET,
							primaryBypassStartX, PRIMARY_BYPASS_TEX_V,
							primaryBypassWidth, COOLDOWN_HEIGHT,
							-1
					);

					// yellow secondary bypass
					HUD_ELEMENTS.blit(
							context,
							cooldownBarX + secondaryBypassStartX, top + COOLDOWN_OFFSET,
							secondaryBypassStartX, SECONDARY_BYPASS_TEX_V,
							secondaryBypassWidth, COOLDOWN_HEIGHT,
							-1
					);
				}

				var heat = coolingStatus.totalHeat() / state.lastVentingHeat();

				// cursor
				m.pushMatrix();
				m.translate(cooldownBarX + heat * (COOLDOWN_WIDTH - 3), 0);
				CURSOR.blit(context, 0, top + COOLDOWN_OFFSET - 2, -1);
				m.popMatrix();
			}
		}

		// endcaps
		ENDCAPS.blit(context, cooldownBarX, top + COOLDOWN_OFFSET, -1);

		m.popMatrix();
	}
}
