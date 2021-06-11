package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MoistureVaporatorScreen extends HandledScreen<MoistureVaporatorScreenHandler>
{
	private static final Identifier TEXTURE = Resources.id("textures/gui/container/moisture_vaporator.png");
	private static final Text TEXT_IDLE = new TranslatableText(Resources.container("moisture_vaporator_gx8.idle"));

	public MoistureVaporatorScreen(MoistureVaporatorScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 166;
	}

	protected void init()
	{
		super.init();

		//		this.playerInventoryTitleX = 44;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);

		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		if (mouseX > i + 103 && mouseX < i + 112 && mouseY > j + 28 && mouseY < j + 58)
		{
			int timer = this.handler.getCollectionTimer();
			if (timer == -1)
			{
				this.renderTooltip(matrices, TEXT_IDLE, mouseX, mouseY);
			}
			else
			{
				int timerLength = this.handler.getCollectionTimerLength();
				if (timerLength <= 0)
					timerLength = 1;
				this.renderTooltip(matrices, new LiteralText(String.format("%s%%", (int)((1 - timer / (float)timerLength) * 100))), mouseX, mouseY);
			}
		}
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		int timer = this.handler.getCollectionTimer();
		int timerLength = this.handler.getCollectionTimerLength();
		int height = timerLength <= 0 ? 30 : (int)((1 - timer / (float)timerLength) * 30);
		this.drawTexture(matrices, i + 103, j + 28 + 30 - height, 176, 30 - height, 9, height);
	}
}
