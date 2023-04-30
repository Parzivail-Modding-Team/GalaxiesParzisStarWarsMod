package com.parzivail.pswg.features.blasters.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.component.PlayerData;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.BlasterWield;
import com.parzivail.pswg.features.blasters.data.BlasterArchetype;
import com.parzivail.pswg.features.blasters.data.BlasterAttachmentFunction;
import com.parzivail.pswg.features.blasters.data.BlasterDescriptor;
import com.parzivail.pswg.features.blasters.data.BlasterTag;
import com.parzivail.util.client.render.ICustomHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.HashMap;

public class BlasterHudRenderer extends DrawableHelper implements ICustomHudRenderer
{
	public static final BlasterHudRenderer INSTANCE = new BlasterHudRenderer();

	private static final Identifier OVERLAY_BASIC_SCOPE = Resources.id("textures/misc/blaster_basic_scope.png");
	private static final Identifier HUD_ELEMENTS_TEXTURE = Resources.id("textures/gui/blasters.png");
	private static final HashMap<BlasterAttachmentFunction, Integer> CROSSHAIR_ATTACHMENT_MAP = Util.make(new HashMap<>(), (h) -> {
		h.put(BlasterAttachmentFunction.SNIPER_SCOPE, 2);
		h.put(BlasterAttachmentFunction.REDUCE_RECOIL, 5);
		h.put(BlasterAttachmentFunction.REDUCE_SPREAD, 9);
		h.put(BlasterAttachmentFunction.WATERPROOF_FIRING, 4);
	});

	@Override
	public boolean renderCrosshair(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices)
	{
		matrices.push();
		var bd = BlasterItem.getBlasterDescriptor(stack);

		if (bd == null)
			return false;

		var client = MinecraftClient.getInstance();
		var scaledWidth = client.getWindow().getScaledWidth();
		var scaledHeight = client.getWindow().getScaledHeight();

		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, HUD_ELEMENTS_TEXTURE);

		var left = scaledWidth / 2;
		var top = scaledHeight / 2;
		final var cooldownWidth = 61;
		var cooldownOffset = 30;

		var isDual = BlasterItem.getWield(player) == BlasterWield.Dual;
		var isOff = hand == Hand.OFF_HAND;

		if (isDual)
		{
			matrices.translate(left, top, 0);
			matrices.multiply(new Quaternionf().rotationZ(-90 * MathHelper.RADIANS_PER_DEGREE));
			matrices.translate(-left, -top, 0);

			cooldownOffset = 0;
			matrices.translate(-top + 10 + cooldownWidth / 2f, (isOff ? -1 : 1) * 130, 0);
		}

		var b = (BlasterItem)stack.getItem();
		var bt = new BlasterTag(stack.getOrCreateNbt());

		var profile = bd.cooling;
		var crosshairIdx = getCrosshairIndex(bd, bt.attachmentBitmask);

		var tickDelta = Client.getTickDelta();

		/*
		 * Cooldown
		 */

		if (bt.isCooling() || bt.heat > 0 || bt.overchargeTimer > 0 || bt.readyTimer > 0)
		{
			var cooldownBarX = (scaledWidth - cooldownWidth) / 2;

			// translucent background
			drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 0, cooldownWidth, 3);

			final float maxHeat = bd.heat.capacity;

			if (bt.isCooling() && (bt.coolingMode == BlasterTag.COOLING_MODE_OVERHEAT || bt.coolingMode == BlasterTag.COOLING_MODE_PENALTY_BYPASS))
			{
				var cooldownTimer = (bt.ventingHeat - bd.heat.overheatDrainSpeed * tickDelta) / (maxHeat + bd.heat.overheatPenalty);

				cooldownTimer = MathHelper.clamp(cooldownTimer, 0, 0.98f);

				// cooldown background
				drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 16, cooldownWidth, 3);

				if (bt.canBypassCooling)
				{
					var primaryBypassStartX = (int)((profile.primaryBypassTime - profile.primaryBypassTolerance) * cooldownWidth);
					var primaryBypassWidth = (int)(2 * profile.primaryBypassTolerance * cooldownWidth);
					var secondaryBypassStartX = (int)((profile.secondaryBypassTime - profile.secondaryBypassTolerance) * cooldownWidth);
					var secondaryBypassWidth = (int)(2 * profile.secondaryBypassTolerance * cooldownWidth);

					// blue primary bypass
					drawTexture(matrices, cooldownBarX + primaryBypassStartX, top + cooldownOffset, primaryBypassStartX, 8, primaryBypassWidth, 3);
					// yellow secondary bypass
					drawTexture(matrices, cooldownBarX + secondaryBypassStartX, top + cooldownOffset, secondaryBypassStartX, 12, secondaryBypassWidth, 3);
				}

				// cursor
				matrices.push();
				matrices.translate(cooldownBarX + cooldownTimer * cooldownWidth - 1, 0, 0);
				drawTexture(matrices, 0, top + cooldownOffset - 2, 0, 24, 3, 7);
				matrices.pop();
			}
			else if (bt.readyTimer > 0)
			{
				var readyTimer = (bt.readyTimer - tickDelta) / bd.quickdrawDelay;

				readyTimer = MathHelper.clamp(readyTimer, 0, 0.98f);

				// cooldown background
				drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 32, cooldownWidth, 3);

				// cursor
				matrices.push();
				matrices.translate(cooldownBarX + readyTimer * cooldownWidth - 1, 0, 0);
				drawTexture(matrices, 0, top + cooldownOffset - 2, 0, 24, 3, 7);
				matrices.pop();
			}
			else
			{
				var heatPercentage = 0f;

				if (bt.isCooling())
				{
					float deltaHeat = 0;
					if (bt.passiveCooldownTimer == 0)
						deltaHeat = bd.heat.overheatDrainSpeed * tickDelta;

					heatPercentage = (bt.ventingHeat - deltaHeat) / maxHeat;
				}
				else if (bt.overchargeTimer > 0)
				{
					heatPercentage = (bt.overchargeTimer - tickDelta) / bd.heat.overchargeBonus;
				}
				else
				{
					float deltaHeat = 0;
					if (bt.passiveCooldownTimer == 0)
						deltaHeat = bd.heat.drainSpeed * tickDelta;

					heatPercentage = (bt.heat - deltaHeat) / maxHeat;
				}

				if (bt.overchargeTimer > 0)
					drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 12, (int)(cooldownWidth * heatPercentage), 3);
				else
					drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 4, (int)(cooldownWidth * heatPercentage), 3);
			}

			// endcaps
			drawTexture(matrices, cooldownBarX, top + cooldownOffset, 0, 20, cooldownWidth, 3);
		}

		/*
		 * Crosshair
		 */

		var cancelCrosshair = false;
		if (bt.isAimingDownSights)
		{
			drawCrosshair(matrices, scaledWidth, scaledHeight, crosshairIdx);
			cancelCrosshair = true;
		}
		else
		{
			var data = PlayerData.getVolatilePublic(player);
			if (data.isPatrolPosture())
			{
				drawCrosshair(matrices, scaledWidth, scaledHeight, 11);
				cancelCrosshair = true;
			}
		}

		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		matrices.pop();

		return cancelCrosshair;
	}

	private static void drawCrosshair(MatrixStack matrices, int scaledWidth, int scaledHeight, int crosshairIdx)
	{
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		drawTexture(matrices, (scaledWidth - 15) / 2, (scaledHeight - 15) / 2, 62 + 16 * crosshairIdx, 0, 15, 15);
		RenderSystem.defaultBlendFunc();
	}

	public static int getCrosshairIndex(BlasterDescriptor bd, int attachmentBitmask)
	{
		return bd.mapWithAttachment(attachmentBitmask, CROSSHAIR_ATTACHMENT_MAP).orElse(bd.defaultCrosshair);
	}

	@Override
	public void renderOverlay(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices, int scaledWidth, int scaledHeight, float tickDelta)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());

		var bd = BlasterItem.getBlasterDescriptor(stack);
		if (bd == null)
			return;

		if (bd.type != BlasterArchetype.SNIPER)
			return;

		float scale = 1; // TODO: bt.getAdsLerp();
		int opacity = 1; // (int)(bt.getAdsLerp() * (224));
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
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
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		// TODO: review possibility of using fill()
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
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
