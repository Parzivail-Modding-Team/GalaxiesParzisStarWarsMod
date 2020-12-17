package com.parzivail.pswg.client.item.render.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.BlasterItem;
import com.parzivail.pswg.item.data.BlasterTag;
import com.parzivail.util.item.ICustomHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class BlasterHudRenderer extends DrawableHelper implements ICustomHudRenderer
{
	public static final BlasterHudRenderer INSTANCE = new BlasterHudRenderer();

	private static final Identifier HUD_ELEMENTS_TEXTURE = Resources.identifier("textures/gui/blasters.png");

	@Override
	public boolean renderCustomHUD(PlayerEntity playerEntity, Hand hand, ItemStack stack, MatrixStack matrices)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(HUD_ELEMENTS_TEXTURE);
		int i = scaledWidth / 2;
		int j = scaledHeight / 2;
		final int cooldownWidth = 61;

		BlasterItem b = (BlasterItem)stack.getItem();
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());

		// TODO: move these to the BT and make them functional
		final float primaryBypass = 0.7f;
		final float primaryBypassTolerance = 0.15f;
		final float secondaryBypass = 0.25f;
		final float secondaryBypassTolerance = 0.1f;
		final int crosshairIdx = 0;

		/*
		 * Cooldown
		 */

		int cooldownBarX = (scaledWidth - cooldownWidth) / 2;

		// background
		this.drawTexture(matrices, cooldownBarX, j + 30, 0, 0, cooldownWidth, 3);

		if (bt.isCoolingDown())
		{
			float cooldownTimer = (bt.cooldownTimer - client.getTickDelta()) / 80f;

			int primaryBypassStartX = (int)((primaryBypass - primaryBypassTolerance) * cooldownWidth);
			int primaryBypassWidth = (int)(2 * primaryBypassTolerance * cooldownWidth);
			int secondaryBypassStartX = (int)((secondaryBypass - secondaryBypassTolerance) * cooldownWidth);
			int secondaryBypassWidth = (int)(2 * secondaryBypassTolerance * cooldownWidth);

			// red
			this.drawTexture(matrices, cooldownBarX, j + 30, 0, 16, cooldownWidth, 3);
			// blue
			this.drawTexture(matrices, cooldownBarX + primaryBypassStartX, j + 30, primaryBypassStartX, 8, primaryBypassWidth, 3);
			// yellow
			this.drawTexture(matrices, cooldownBarX + secondaryBypassStartX, j + 30, secondaryBypassStartX, 12, secondaryBypassWidth, 3);

			// cursor
			this.drawTexture(matrices, (int)(cooldownBarX + cooldownTimer * cooldownWidth - 1), j + 28, 0, 24, 3, 7);
		}
		else
		{
			float heatPercentage = (bt.heat - client.getTickDelta()) / b.getMaxHeat(stack, playerEntity);
			this.drawTexture(matrices, cooldownBarX, j + 30, 0, 4, (int)(cooldownWidth * heatPercentage), 3);
		}

		// endcaps
		this.drawTexture(matrices, cooldownBarX, j + 30, 0, 20, cooldownWidth, 3);

		/*
		 * Crosshair
		 */

		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		this.drawTexture(matrices, (scaledWidth - 15) / 2, (scaledHeight - 15) / 2, 62 + 16 * crosshairIdx, 0, 15, 15);
		RenderSystem.defaultBlendFunc();

		client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_TEXTURE);

		return true;
	}
}
