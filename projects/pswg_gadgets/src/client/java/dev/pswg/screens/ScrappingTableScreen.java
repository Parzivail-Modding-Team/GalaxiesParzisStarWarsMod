package dev.pswg.screens;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.feature.scrapping.ScrappingTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScrappingTableScreen extends HandledScreen<ScrappingTableScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/scrapping_table.png");


	public ScrappingTableScreen(ScrappingTableScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		this.backgroundHeight = 213;
		this.backgroundWidth = 175;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = 112;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		var backgroundX = (this.width - this.backgroundWidth) / 2;
		var backgroundY = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, backgroundX, backgroundY, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

		int cutterProgress = handler.getCutterProgress() > 0 ? (int)((handler.getCutterProgress() + 2d) / 3) + 1 : 0;
		int spannerProgress = handler.getSpannerProgress() > 0 ? (int)((handler.getSpannerProgress() + 2d) / 3) + 1 : 0;
		int calibratorProgress = handler.getCalibratorProgress() > 0 ? (int)((handler.getCalibratorProgress() + 2d) / 3) + 1 : 0;

		int hoveringOverTool = -1;

		for (int i = 0; i < 3; i++)
		{
			if (handler.getToolProgress(i) >= 0)
			{
				context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 31 + backgroundX, 48 + i * 22 + backgroundY, 177, 18, 10, 10, 256, 256);
				if (mouseX > 31 + backgroundX && mouseX < 41 + backgroundX && mouseY > 48 + i * 23 + backgroundY && mouseY < 58 + i * 22 + backgroundY)
				{
					hoveringOverTool = i;
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 31 + backgroundX, 48 + i * 22 + backgroundY, 177, 29, 10, 10, 256, 256);
				}
			}
		}

		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 25 + backgroundX, 45 + backgroundY + (16 - cutterProgress + 1), 187, 16 - cutterProgress + 1, 4, cutterProgress, 256, 256);
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 25 + backgroundX, 67 + backgroundY + (16 - spannerProgress + 1), 187, 16 - spannerProgress + 1, 4, spannerProgress, 256, 256);
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 25 + backgroundX, 89 + backgroundY + (16 - calibratorProgress + 1), 187, 16 - calibratorProgress + 1, 4, calibratorProgress, 256, 256);

		var stack = handler.getInputItem();
		if (stack.contains(GadgetsItems.Components.METAL_COMPONENT))
		{
			int metalAmount = stack.get(GadgetsItems.Components.METAL_COMPONENT);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX, 55 + backgroundY, 177, 42, 1 + 13 * metalAmount, 4, 256, 256);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 45 + backgroundX, 53 + backgroundY, 192, 0, 9, 8, 256, 256);
			if (hoveringOverTool == 0)
				for (int i = 0; i < Math.min(metalAmount, 2); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (metalAmount - i - 1), 55 + backgroundY, 177, 62, 14, 4, 256, 256);
			if (hoveringOverTool == 1)
				for (int i = 0; i < Math.min(metalAmount, 1); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (metalAmount - i - 1), 55 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.contains(GadgetsItems.Components.TECH_COMPONENT))
		{
			int techAmount = stack.get(GadgetsItems.Components.TECH_COMPONENT);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX, 67 + backgroundY, 177, 47, 1 + 13 * techAmount, 4, 256, 256);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 44 + backgroundX, 65 + backgroundY, 192, 9, 10, 7, 256, 256);
			if (hoveringOverTool == 1)
				for (int i = 0; i < Math.min(techAmount, 2); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (techAmount - i - 1), 67 + backgroundY, 177, 62, 14, 4, 256, 256);
			if (hoveringOverTool == 2)
				for (int i = 0; i < Math.min(techAmount, 1); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (techAmount - i - 1), 67 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.contains(GadgetsItems.Components.PLASTIC_COMPONENT))
		{
			int plasticAmount = stack.get(GadgetsItems.Components.PLASTIC_COMPONENT);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX, 79 + backgroundY, 177, 52, 1 + 13 * plasticAmount, 4, 256, 256);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 43 + backgroundX, 77 + backgroundY, 203, 0, 12, 8, 256, 256);
			if (hoveringOverTool == 0)
				for (int i = 0; i < Math.min(plasticAmount, 1); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (plasticAmount - i - 1), 79 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.contains(GadgetsItems.Components.ENERGY_COMPONENT))
		{
			int energyAmount = stack.get(GadgetsItems.Components.ENERGY_COMPONENT);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX, 91 + backgroundY, 177, 57, 1 + 13 * energyAmount, 4, 256, 256);
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 47 + backgroundX, 89 + backgroundY, 205, 9, 6, 8, 256, 256);
			if (hoveringOverTool == 2)
				for (int i = 0; i < Math.min(energyAmount, 2); i++)
					context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, 56 + backgroundX + 13 * (energyAmount - i - 1), 91 + backgroundY, 177, 62, 14, 4, 256, 256);
		}

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		int backgroundX = (this.width - this.backgroundWidth) / 2;
		int backgroundY = (this.height - this.backgroundHeight) / 2;

		for (int i = 0; i < 3; i++)
		{
			if (mouseX > 31 + backgroundX && mouseX < 41 + backgroundX && mouseY > 48 + i * 23 + backgroundY && mouseY < 58 + i * 22 + backgroundY && this.handler.onButtonClick(this.client.player, i))
			{
				this.client.interactionManager.clickButton(this.handler.syncId, i);
				return true;
			}
		}
		//Gadgets.LOGGER.info("x: " + mouseX + " y: " + mouseY + " button: " + button + " xMinLoc: " + (30 + ((this.width - this.backgroundWidth) / 2)) + " yMinLoc: " + (70 + ((this.height - this.backgroundHeight) / 2)));
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
	}
}
