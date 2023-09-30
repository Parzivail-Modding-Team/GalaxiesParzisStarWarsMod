package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.screen.AstromechScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
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
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		// saddle
		context.drawTexture(TEXTURE, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);

		// armor
		context.drawTexture(TEXTURE, i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(context, mouseX, mouseY, delta);
		this.mouseX = (float)mouseX;
		this.mouseY = (float)mouseY;
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}
}
