package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.LightsaberForgeScreenHandler;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class LightsaberForgeScreen extends HandledScreen<LightsaberForgeScreenHandler> implements ScreenHandlerListener
{
	private static final Identifier TEXTURE = Resources.identifier("textures/gui/container/lightsaber_forge.png");

	public LightsaberForgeScreen(LightsaberForgeScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 256;
		backgroundHeight = 241;
	}

	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 48;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
		this.handler.addListener(this);
	}

	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	@Override
	public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks)
	{
		this.onSlotUpdate(handler, 0, handler.getSlot(0).getStack());
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0:
			{
				Lumberjack.debug(stack);
				break;
			}
		}
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		this.renderBackground(matrices);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
	{
		this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 4210752);
		this.textRenderer.draw(matrices, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
	}
}
