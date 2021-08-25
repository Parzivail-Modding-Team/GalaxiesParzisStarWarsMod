package com.parzivail.pswg.client.render.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterArchetype;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.client.render.ICustomHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BlasterHudRenderer extends DrawableHelper implements ICustomHudRenderer
{
	public static final BlasterHudRenderer INSTANCE = new BlasterHudRenderer();

	private static final Identifier OVERLAY_BASIC_SCOPE = Resources.id("textures/misc/blaster_basic_scope.png");
	private static final Identifier HUD_ELEMENTS_TEXTURE = Resources.id("textures/gui/blasters.png");

	@Override
	public boolean renderCrosshair(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices)
	{
		var bd = BlasterItem.getBlasterDescriptor(player.world, stack);

		if (bd == null)
			return false;

		var client = MinecraftClient.getInstance();
		var scaledWidth = client.getWindow().getScaledWidth();
		var scaledHeight = client.getWindow().getScaledHeight();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, HUD_ELEMENTS_TEXTURE);

		var i = scaledWidth / 2;
		var j = scaledHeight / 2;
		final var cooldownWidth = 61;

		var b = (BlasterItem)stack.getItem();
		var bt = new BlasterTag(stack.getOrCreateNbt());

		var profile = bd.cooling;
		final var crosshairIdx = 0;

		/*
		 * Cooldown
		 */

		if (bt.isCooling() || bt.heat > 0)
		{
			var cooldownBarX = (scaledWidth - cooldownWidth) / 2;

			// translucent background
			this.drawTexture(matrices, cooldownBarX, j + 30, 0, 0, cooldownWidth, 3);

			final float maxHeat = bd.heat.capacity;

			if (bt.isCooling() && (bt.coolingMode == BlasterTag.COOLING_MODE_OVERHEAT || bt.coolingMode == BlasterTag.COOLING_MODE_PENALTY_BYPASS))
			{
				var cooldownTimer = (bt.coolingTimer - bd.heat.overheatDrainSpeed * client.getTickDelta()) / (maxHeat + bd.heat.overheatPenalty);

				cooldownTimer = MathHelper.clamp(cooldownTimer, 0, 0.98f);

				// cooldown background
				this.drawTexture(matrices, cooldownBarX, j + 30, 0, 16, cooldownWidth, 3);

				if (bt.canBypassCooling)
				{
					var primaryBypassStartX = (int)((profile.primaryBypassTime - profile.primaryBypassTolerance) * cooldownWidth);
					var primaryBypassWidth = (int)(2 * profile.primaryBypassTolerance * cooldownWidth);
					var secondaryBypassStartX = (int)((profile.secondaryBypassTime - profile.secondaryBypassTolerance) * cooldownWidth);
					var secondaryBypassWidth = (int)(2 * profile.secondaryBypassTolerance * cooldownWidth);

					// blue primary bypass
					this.drawTexture(matrices, cooldownBarX + primaryBypassStartX, j + 30, primaryBypassStartX, 8, primaryBypassWidth, 3);
					// yellow secondary bypass
					this.drawTexture(matrices, cooldownBarX + secondaryBypassStartX, j + 30, secondaryBypassStartX, 12, secondaryBypassWidth, 3);
				}

				// cursor
				this.drawTexture(matrices, (int)(cooldownBarX + cooldownTimer * cooldownWidth - 1), j + 28, 0, 24, 3, 7);
			}
			else
			{
				var heatPercentage = 0f;

				if (bt.isCooling())
				{
					float deltaHeat = 0;
					if (bt.passiveCooldownTimer == 0)
						deltaHeat = bd.heat.overheatDrainSpeed * client.getTickDelta();

					heatPercentage = (bt.coolingTimer - deltaHeat) / maxHeat;
				}
				else
				{
					float deltaHeat = 0;
					if (bt.passiveCooldownTimer == 0)
						deltaHeat = bd.heat.drainSpeed * client.getTickDelta();

					heatPercentage = (bt.heat - deltaHeat) / maxHeat;
				}

				this.drawTexture(matrices, cooldownBarX, j + 30, 0, 4, (int)(cooldownWidth * heatPercentage), 3);
			}

			// endcaps
			this.drawTexture(matrices, cooldownBarX, j + 30, 0, 20, cooldownWidth, 3);
		}

		/*
		 * Crosshair
		 */

		if (bt.isAimingDownSights)
		{
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			this.drawTexture(matrices, (scaledWidth - 15) / 2, (scaledHeight - 15) / 2, 62 + 16 * crosshairIdx, 0, 15, 15);
			RenderSystem.defaultBlendFunc();
		}

		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);

		return bt.isAimingDownSights;
	}

	@Override
	public void renderOverlay(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices, int scaledWidth, int scaledHeight, float tickDelta)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());

		var bd = BlasterItem.getBlasterDescriptor(player.world, stack);
		if (bd == null)
			return;

		if (bd.type != BlasterArchetype.SNIPER)
			return;

		float scale = bt.getAdsLerp();
		int opacity = (int)(bt.getAdsLerp() * (224));
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, OVERLAY_BASIC_SCOPE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float f = (float)Math.min(scaledWidth, scaledHeight);
		float h = Math.min((float)scaledWidth / f, (float)scaledHeight / f) * scale;
		float i = f * h;
		float j = f * h;
		float k = ((float)scaledWidth - i) / 2.0F;
		float l = ((float)scaledHeight - j) / 2.0F;
		float m = k + i;
		float n = l + j;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(k, n, -90.0D).color(255, 255, 255, opacity).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex(m, n, -90.0D).color(255, 255, 255, opacity).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(m, l, -90.0D).color(255, 255, 255, opacity).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(k, l, -90.0D).color(255, 255, 255, opacity).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableTexture();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0D, scaledHeight, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, scaledHeight, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(0.0D, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(0.0D, l, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, l, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, 0.0D, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(0.0D, 0.0D, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(0.0D, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(k, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(k, l, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(0.0D, l, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(m, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, n, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(scaledWidth, l, -90.0D).color(0, 0, 0, opacity).next();
		bufferBuilder.vertex(m, l, -90.0D).color(0, 0, 0, opacity).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
