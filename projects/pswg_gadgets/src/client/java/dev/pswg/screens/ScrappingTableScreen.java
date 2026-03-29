package dev.pswg.screens;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GalaxiesItems;
import dev.pswg.feature.scrapping.table.ScrappingTableScreenHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ScrappingTableScreen extends AbstractContainerScreen<ScrappingTableScreenHandler>
{
	private static final Identifier TEXTURE = Gadgets.id("textures/gui/container/scrapping_table.png");


	public ScrappingTableScreen(ScrappingTableScreenHandler handler, Inventory inventory, Component title)
	{
		super(handler, inventory, title, 175, 213);
		this.inventoryLabelX = 8;
		this.inventoryLabelY = 112;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
	{
		super.extractBackground(context, mouseX, mouseY, delta);
		var backgroundX = (this.width - this.imageWidth) / 2;
		var backgroundY = (this.height - this.imageHeight) / 2;
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, backgroundX, backgroundY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

		int cutterProgress = menu.getCutterProgress() > 0 ? (int)((menu.getCutterProgress() + 20d) / 30) + 1 : 0;
		int spannerProgress = menu.getSpannerProgress() > 0 ? (int)((menu.getSpannerProgress() + 20d) / 30) + 1 : 0;
		int calibratorProgress = menu.getCalibratorProgress() > 0 ? (int)((menu.getCalibratorProgress() + 20d) / 30) + 1 : 0;

		int hoveringOverTool = -1;

		for (int i = 0; i < 3; i++)
		{
			if (menu.getToolProgress(i) >= 0)
			{
				context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 31 + backgroundX, 48 + i * 22 + backgroundY, 177, 18, 10, 10, 256, 256);
				if (mouseX > 31 + backgroundX && mouseX < 41 + backgroundX && mouseY > 48 + i * 22 + backgroundY && mouseY < 58 + i * 22 + backgroundY)
				{
					hoveringOverTool = i;
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 31 + backgroundX, 48 + i * 22 + backgroundY, 177, 29, 10, 10, 256, 256);
				}
			}
		}

		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 25 + backgroundX, 45 + backgroundY + (16 - cutterProgress + 1), 187, 16 - cutterProgress + 1, 4, cutterProgress, 256, 256);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 25 + backgroundX, 67 + backgroundY + (16 - spannerProgress + 1), 187, 16 - spannerProgress + 1, 4, spannerProgress, 256, 256);
		context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 25 + backgroundX, 89 + backgroundY + (16 - calibratorProgress + 1), 187, 16 - calibratorProgress + 1, 4, calibratorProgress, 256, 256);

		var stack = menu.getInputItem();
		if (stack.has(GalaxiesItems.Components.METAL_COMPONENT))
		{
			int metalAmount = stack.get(GalaxiesItems.Components.METAL_COMPONENT);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX, 55 + backgroundY, 177, 42, 1 + 13 * metalAmount, 4, 256, 256);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 45 + backgroundX, 53 + backgroundY, 192, 0, 9, 8, 256, 256);
			if (hoveringOverTool == 0)
				for (int i = 0; i < Math.min(metalAmount, 2); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (metalAmount - i - 1), 55 + backgroundY, 177, 62, 14, 4, 256, 256);
			if (hoveringOverTool == 1)
				for (int i = 0; i < Math.min(metalAmount, 1); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (metalAmount - i - 1), 55 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.has(GalaxiesItems.Components.TECH_COMPONENT))
		{
			int techAmount = stack.get(GalaxiesItems.Components.TECH_COMPONENT);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX, 67 + backgroundY, 177, 47, 1 + 13 * techAmount, 4, 256, 256);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 44 + backgroundX, 65 + backgroundY, 192, 9, 10, 7, 256, 256);
			if (hoveringOverTool == 1)
				for (int i = 0; i < Math.min(techAmount, 2); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (techAmount - i - 1), 67 + backgroundY, 177, 62, 14, 4, 256, 256);
			if (hoveringOverTool == 2)
				for (int i = 0; i < Math.min(techAmount, 1); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (techAmount - i - 1), 67 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.has(GalaxiesItems.Components.PLASTIC_COMPONENT))
		{
			int plasticAmount = stack.get(GalaxiesItems.Components.PLASTIC_COMPONENT);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX, 79 + backgroundY, 177, 52, 1 + 13 * plasticAmount, 4, 256, 256);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 43 + backgroundX, 77 + backgroundY, 203, 0, 12, 8, 256, 256);
			if (hoveringOverTool == 0)
				for (int i = 0; i < Math.min(plasticAmount, 1); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (plasticAmount - i - 1), 79 + backgroundY, 177, 62, 14, 4, 256, 256);
		}
		if (stack.has(GalaxiesItems.Components.ENERGY_COMPONENT))
		{
			int energyAmount = stack.get(GalaxiesItems.Components.ENERGY_COMPONENT);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX, 91 + backgroundY, 177, 57, 1 + 13 * energyAmount, 4, 256, 256);
			context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 47 + backgroundX, 89 + backgroundY, 205, 9, 6, 8, 256, 256);
			if (hoveringOverTool == 2)
				for (int i = 0; i < Math.min(energyAmount, 2); i++)
					context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 56 + backgroundX + 13 * (energyAmount - i - 1), 91 + backgroundY, 177, 62, 14, 4, 256, 256);
		}

	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled)
	{
		int mouseX = (int)click.x();
		int mouseY = (int)click.y();
		int backgroundX = (this.width - this.imageWidth) / 2;
		int backgroundY = (this.height - this.imageHeight) / 2;

		for (int i = 0; i < 3; i++)
		{
			if (mouseX > 31 + backgroundX && mouseX < 41 + backgroundX && mouseY > 48 + i * 23 + backgroundY && mouseY < 58 + i * 22 + backgroundY && this.menu.clickMenuButton(this.minecraft.player, i))
			{
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
				return true;
			}
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
	{
		super.extractRenderState(context, mouseX, mouseY, delta);
	}
}
