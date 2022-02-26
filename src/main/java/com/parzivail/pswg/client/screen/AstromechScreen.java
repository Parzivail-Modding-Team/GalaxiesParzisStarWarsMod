package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.screen.AstromechScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AstromechScreen extends HandledScreen<AstromechScreenHandler>
{
	private static final Identifier TEXTURE = Resources.id("textures/gui/container/astromech.png");
	private final AstromechEntity entity;
	private float mouseX;
	private float mouseY;

	public AstromechScreen(AstromechScreenHandler handler, PlayerInventory inventory, AstromechEntity entity)
	{
		super(handler, inventory, entity.getDisplayName());
		this.entity = entity;
		this.passEvents = false;
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		// saddle
		this.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);

		// armor
		this.drawTexture(matrices, i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}
